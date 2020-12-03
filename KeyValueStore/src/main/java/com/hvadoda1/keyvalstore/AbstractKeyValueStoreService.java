package com.hvadoda1.keyvalstore;

import static com.hvadoda1.keyvalstore.util.ValueUtils.shouldOverwrite;
import static com.hvadoda1.keyvalstore.util.ValueUtils.valueToStr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hvadoda1.keyvalstore.util.FileUtils;
import com.hvadoda1.keyvalstore.util.Logger;
import com.hvadoda1.keyvalstore.util.NodeUtils;
import com.hvadoda1.keyvalstore.util.SerializerFactory;
import com.hvadoda1.keyvalstore.util.partitioning.IPartitioner;
import com.hvadoda1.keyvalstore.util.serialize.ISerializer;
import com.hvadoda1.keyvalstore.util.serialize.SerializerException;

public abstract class AbstractKeyValueStoreService<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Exc extends Exception>
		implements IKeyValueStoreService<K, V, N, Val, Con, Exc>, IKeyValueStoreServer<K, V, N, Val, Con, Exc> {

	protected Map<K, Val> memTable = new HashMap<>();
	protected int entryCount = 0;
	protected final ISerializer<Entry<K, Val>> serializer;

	protected final String backupsDir, backupFilename, tempBackupFilename, nodesListFilename;

	protected final N node;

	protected final IHintedHandoffs<K, V, Val, N> missedWrites = new HintedHandoffs<>();

	protected List<N> nodes = null;
	protected IPartitioner<K> partitioner;

//	protected final Map<N, IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc>> connCache = new HashMap<>();

	protected IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc> connCache;

	protected final FileWriter writeAheadLogger;

	protected boolean recovered = false;

	public AbstractKeyValueStoreService(N node) {
		this.node = node;
		this.backupsDir = Config.backupsDir(node);
		this.backupFilename = this.backupsDir + "keyvalstore.bak";
		this.tempBackupFilename = this.backupsDir + "keyvalstore.temp.bak";
		this.nodesListFilename = this.backupsDir + "nodeslist.bak";

		this.serializer = SerializerFactory.getSimpleSerializer();

		if (Config.args().containsKey("nodes")) {
			Logger.debugLow("Nodes filename found: [" + Config.getArg("nodes") + "]");
			try {
				setNeighborList(NodeUtils.getNodesFromFile(new File(Config.getArg("nodes")),
						(ip, port) -> createNode(ip, port)));
				Logger.debugLow("Nodes read from nodes file: ["
						+ nodes.stream().map(n -> NodeUtils.nodeAddress(n)).collect(Collectors.joining(", ")) + "]");
				recovered = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (nodes == null)
			recoverLastSavedState();

		try {
			this.writeAheadLogger = FileUtils.fileAppender(new File(backupFilename), false);
		} catch (IOException e) {
			throw new RuntimeException(
					"Failed to initialize write-ahead-logging. (Log filename: [" + backupFilename + "])", e);
		}
	}

	public void recoverLastSavedState() {
		File backup = new File(backupFilename);
		if (backup.exists() && backup.isFile()) {
			try (BufferedReader fr = FileUtils.fileReader(backup);) {
				String line;
				Entry<K, Val> entry;
				while ((line = fr.readLine()) != null) {
					entry = serializer.deserialize(line);
					if (entry == null)
						throw new RuntimeException(
								"Backed up data seems corrupt, failed to properly restore backups.\nBackup file: ["
										+ backup.getAbsolutePath() + "]");
					entryCount++;

					memTable.put(entry.getKey(), entry.getValue());
				}
			} catch (ClassNotFoundException | IOException e) {
				throw new RuntimeException("Failed to recover previously backed up data", e);
			}
		}

		backup = new File(nodesListFilename);
		if (backup.isFile()) {
			ISerializer<List<N>> listSerializer = SerializerFactory.getSimpleSerializer(true);
			try {
				String contents = FileUtils.readFile(backup);
				if (contents == null || contents.isEmpty())
					return;
				this.nodes = listSerializer.deserialize(contents);
			} catch (ClassNotFoundException | IOException e) {
				throw new RuntimeException("Failed to recover previously backed up Nodes list", e);
			} catch (SerializerException e) {
				throw new RuntimeException("Backed-up list of nodes is corrupt", e);
			}

			Map<K, Val> vals;
			IKeyValueStoreClient<K, V, N, Val, Con, Exc> client;
			for (N node : nodes) {
				try {
					client = getClientConnection(node);
					vals = client.getMissedWrites(this.node);
					if (vals != null) {
						vals.forEach((k, v) -> {
							try {
								this.write(k, v);
							} catch (Exception e) {
								System.err.println("Failed to save hinted entry.\nCause:" + e.getMessage());
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			recovered = true;
		}

	}

	@Override
	public V get(K key, Con level) throws Exc {
		try {
			Logger.info("Attempt to GET key [" + key + "] with level [" + level + "]");
			Objects.requireNonNull(key, "Key was null, cannot GET");
			Objects.requireNonNull(level, "Consistency level was null, cannot GET value mapped to key [" + key + "]");

			int replicaCount = level.readReplicas();
			if (replicaCount <= 0 || replicaCount > Config.numReplicas()) {
				Logger.debugLow("Level [" + level + "] had replicaCount: [" + replicaCount + "]");
				throw new RuntimeException("Consistency Level [" + level + "] was not configured correctly");
			}

			Logger.info("Contacting [" + replicaCount + "] replicas for key [" + key + "] and level [" + level + "]");
			return readFromReplicas(key, replicaCount);
		} catch (Exception e) {
			if (!shouldWrapException(e))
				throw e;
			throw createException("An error occurred while GETting value for key [" + key + "]", e);
		}
	}

	protected V readFromReplicas(K key, int numReplicasToContact) throws Exc {
		int idx = getPartitioner().indexOfResponsibleNode(key);
		Logger.debugLow("indexOfPrimaryReplica(" + key + ") : [" + idx + "]");
		if (idx == -1) {
			throw new RuntimeException("Invalid key [" + key + "]");
		}
		Val v = null, v1;

		Map<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replicas = new HashMap<>();
		List<IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc>> conns = new ArrayList<>();
		int maxConfiguredReplicaCount = Config.numReplicas();
		String nodeAddress;
		while (maxConfiguredReplicaCount-- > 0 && replicas.size() < numReplicasToContact) {
			nodeAddress = NodeUtils.nodeAddress(nodes.get(idx));
			try {
				Logger.debugLow("Connecting to replica [" + nodeAddress + "]");
				replicas.put(nodes.get(idx), this.getClientConnection(nodes.get(idx)));
				conns.add(connCache);
				Logger.debugLow("Replica [" + nodeAddress + "] available");
			} catch (Exception e) {
				Logger.debugLow("Replica [" + nodeAddress + "] NOT available");
			}
			connCache = null;
			idx++;
			idx %= nodes.size();
		}

		if (replicas.size() < numReplicasToContact)
			throw new RuntimeException("Failed to process read request, only [" + replicas.size()
					+ "] out of the minimum required [" + numReplicasToContact + "] replicas were available");

		for (Map.Entry<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replica : replicas.entrySet()) {
			try {
				v1 = replica.getValue().read(key);
			} catch (Exception e) {
				v1 = null;
			}
			Logger.debugLow(
					"Client [" + replica.getValue().getRemoteNode() + "] [" + key + "]->[" + valueToStr(v1) + "]");
			if (shouldOverwrite(v, v1))
				v = v1;
		}
		for (IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc> conn : conns)
			if (conn != null)
				try {
					conn.close();
				} catch (IOException e) {
					Logger.error("Exception while closing connection to client [" + conn.getRemoteNode() + "]", e);
				}
		Logger.info("Final [" + key + "]->[" + valueToStr(v) + "]");
		if (v == null)
			throw createException("Key [" + key + "] not found");
		return v.getValue();
	}

	@Override
	public void put(K key, V value, Con level) throws Exc {
		try {
			Logger.info("Attempt to PUT (" + key + ", " + value + ") with level [" + level + "]");
			Objects.requireNonNull(key, "Key was null, cannot PUT");
			Objects.requireNonNull(value, "Value was null, cannot PUT");
			Objects.requireNonNull(level, "Consistency level was null, cannot PUT value mapped to key [" + key + "]");

			int minReplicas = level.minWriteReplicas(), maxReplicas = level.maxWriteReplicas();
			if (maxReplicas <= 0 || maxReplicas > Config.numReplicas() || minReplicas <= 0
					|| minReplicas > Config.numReplicas())
				throw new RuntimeException("Consistency Level [" + level + "] was not configured correctly");

			Logger.info("Contacting between [" + minReplicas + "] & [" + maxReplicas + "] replicas to store (" + key
					+ ", " + value + ") and level [" + level + "]");
			writeToReplicas(key, value, minReplicas, maxReplicas, level.shouldSaveMissedUpdates());
		} catch (Exception e) {
			if (!shouldWrapException(e))
				throw e;
			throw createException("An error occurred while PUTting (" + key + ", " + value + ")", e);
		}
	}

	protected void writeToReplicas(K key, V value, int minReplicas, int maxReplicas, boolean shouldSaveHints)
			throws Exc {
		Val valueWrpr = createValue(value);
		int idx = getPartitioner().indexOfResponsibleNode(key);
		Logger.debugLow("indexOfPrimaryReplica(" + key + ") : [" + idx + "]");
		Logger.info("Primary Replica for Key [" + key + "] : [" + NodeUtils.nodeAddress(nodes.get(idx)) + "]");
		if (idx == -1)
			throw new RuntimeException("Invalid key [" + key + "]");

		Map<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replicas = new HashMap<>();
		List<IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc>> conns = new ArrayList<>();
		int availableReplicas = 0;
		IKeyValueStoreClient<K, V, N, Val, Con, Exc> client;
		int maxConfiguredReplicaCount = Config.numReplicas();
		String nodeAddress;
		while (maxConfiguredReplicaCount-- > 0 && availableReplicas < maxReplicas) {
			nodeAddress = NodeUtils.nodeAddress(nodes.get(idx));
			try {
				Logger.debugLow("Connecting to replica [" + nodeAddress + "]");
				client = this.getClientConnection(nodes.get(idx));
				availableReplicas++;
				conns.add(connCache);
				Logger.debugLow("Replica [" + nodeAddress + "] available");
			} catch (Exception e) {
				client = null;
				Logger.debugLow("Replica [" + nodeAddress + "] NOT available");
			}
			connCache = null;
			if (client != null || shouldSaveHints)
				replicas.put(nodes.get(idx), client);
			idx++;
			idx %= nodes.size();
		}

		if (availableReplicas < minReplicas)
			throw new RuntimeException("Failed to process write request, only [" + availableReplicas
					+ "] out of the minimum required [" + minReplicas + "] replicas were available");

		for (Map.Entry<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replica : replicas.entrySet())
			if (replica.getValue() == null)
				missedWrites.saveMissedWrite(replica.getKey(), key, valueWrpr);
			else
				replica.getValue().write(key, valueWrpr);

		for (IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc> conn : conns)
			if (conn != null)
				try {
					conn.close();
				} catch (IOException e) {
					Logger.error("Exception while closing connection to client [" + conn.getRemoteNode() + "]", e);
				}
	}

	@Override
	public Val read(K key) throws Exc {
		Logger.info("Fetching value for [" + key + "] from MemTable");
		try {
//			if (!memTable.containsKey(key)) {
//				Logger.error("Key [" + key + "] not found", null);
//				return null;
//			}
			if (!memTable.containsKey(key))
				throw new RuntimeException("Key [" + key + "] not found");
			return memTable.get(key);
		} catch (Exception e) {
			throw createException("An error occurred while trying to read value for key [" + key + "] in MemTable", e);
		}
	}

	@Override
	public void write(K key, Val value) throws Exc {
		Logger.info("Attempting to write (" + key + ", " + value.getValue() + ") to MemTable");
		try {
			Objects.requireNonNull(value, "Value mapped to Key [" + key + "] was null");
			if (!memTable.containsKey(key) || shouldOverwrite(memTable.get(key), value)) {
				writeAheadLog(new Entry<>(key, value));
				memTable.put(key, value);
			} else
				Logger.info("Rejecting write of (" + key + ", " + value.getValue()
						+ ") because value with newer timestamp exists");
		} catch (Exception e) {
			throw createException("An error occurred while attempting to write to MemTable", e);
		}
	}

	protected void writeAheadLog(Entry<K, Val> entry) throws Exc {
		try {
			this.writeAheadLogger.write(serializer.serialize(entry));
			this.writeAheadLogger.write(System.lineSeparator());
			this.writeAheadLogger.flush();
			entryCount++;
		} catch (IOException e) {
			throw new RuntimeException("Failed to write/update value, due to failure in write-ahead-logging", e);
		}
	}

	@Override
	public Map<K, Val> getMissedWrites(N node) throws Exc {
		try {
			return missedWrites.getMissedWrites(node);
		} catch (Exception e) {
			throw createException("An error occurred while getting hinted handoffs", e);
		}
	}

	@Override
	public void setNeighborList(List<N> nodes) throws Exc {
		if (this.nodes != null)
			throw new RuntimeException(
					"Cluster nodes list already set, attempt to reset the list (Please restart the cluster to set list of nodes again)");
		if (nodes == null)
			return;
		try (FileWriter fw = FileUtils.fileAppender(new File(nodesListFilename));) {
			ISerializer<List<N>> listSerializer = SerializerFactory.getSimpleSerializer(true);
			fw.write(listSerializer.serialize(nodes));
			fw.flush();
		} catch (IOException e) {
			throw new RuntimeException("Failed to set nodes list, due to failure in logging the list");
		}
		this.nodes = nodes;
	}

	protected IKeyValueStoreClient<K, V, N, Val, Con, Exc> getClientConnection(N node) {
		if (node.equals(this.node))
			return this;
		if (connCache != null && connCache.isOpen())
			try {
				connCache.close();
			} catch (IOException e) {
			}

		Logger.debugHigh("Connecting to client [" + node + "]");
		connCache = createConnection(node);
		return connCache.getClient();
	}

	protected IPartitioner<K> getPartitioner() throws Exc {
		if (partitioner == null) {
			if (nodes == null)
				throw new NullPointerException("Neighboring Nodes list not initialized");
			partitioner = createPartitioner();
		}
		return partitioner;
	}

	@Override
	public void close() {
		FileUtils.deleteDirectory(new File(backupsDir), true);
		System.exit(0);
	}

	protected abstract Exc createException(String message);

	protected abstract Exc createException(String message, Exception cause);

	protected abstract boolean shouldWrapException(Exception e);

	protected abstract IPartitioner<K> createPartitioner();

	protected abstract IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc> createConnection(N node);

	protected abstract Val createValue(V value);

	protected abstract N createNode(String ip, int port);

}
