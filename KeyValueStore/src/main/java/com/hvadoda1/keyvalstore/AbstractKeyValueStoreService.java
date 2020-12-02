package com.hvadoda1.keyvalstore;

import static com.hvadoda1.keyvalstore.util.ValueUtils.shouldOverwrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.hvadoda1.keyvalstore.util.FileUtils;
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

	protected final Map<N, IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc>> connCache = new HashMap<>();

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
			try {
				nodes = NodeUtils.getNodesFromFile(new File(Config.getArg("nodes")),
						(ip, port) -> createNode(ip, port));
				recovered = true;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		if (!recovered)
			recoverLastSavedState();

		try {
			this.writeAheadLogger = FileUtils.fileAppender(new File(backupFilename));
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
		if (backup.exists() && backup.isFile()) {
			ISerializer<List<N>> listSerializer = SerializerFactory.getSimpleSerializer(true);
			try {
				String contents = FileUtils.readFile(backup);
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
					vals.forEach((k, v) -> {
						try {
							this.write(k, v);
						} catch (Exception e) {
							System.err.println("Failed to save hinted entry.\nCause:" + e.getMessage());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}

	}

	@Override
	public V get(K key, Con level) throws Exc {
		Objects.requireNonNull(key, "Key was null, cannot GET");
		Objects.requireNonNull(level, "Consistency level was null, cannot GET value mapped to key [" + key + "]");

		int replicaCount = level.readReplicas();
		if (replicaCount <= 0 || replicaCount > Config.numReplicas())
			throw createException("Consistency Level [" + level + "] was not configured correctly");

		return readFromReplicas(key, replicaCount);
	}

	protected V readFromReplicas(K key, int numReplicas) throws Exc {
		int idx = getPartitioner().indexOfResponsibleNode(key);
		Val v = this.getClientConnection(nodes.get(idx)).read(key);
		Val v1;

		Map<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replicas = new HashMap<>();
		int availableReplicas = 0;
		int maxConfiguredReplicaCount = Config.numReplicas();
		while (maxConfiguredReplicaCount-- > 0 && availableReplicas < numReplicas) {
			try {
				replicas.put(nodes.get(idx), this.getClientConnection(nodes.get(idx)));
				availableReplicas++;
			} catch (Exception e) {
			}
			idx++;
			idx %= nodes.size();
		}

		if (availableReplicas < numReplicas)
			throw createException("Failed to process read request, only [" + availableReplicas
					+ "] out of the minimum required [" + numReplicas + "] replicas were available");

		for (Map.Entry<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replica : replicas.entrySet()) {
			v1 = replica.getValue().read(key);
			if (shouldOverwrite(v, v1))
				v = v1;
		}

		return v.getValue();
	}

	@Override
	public void put(K key, V value, Con level) throws Exc {
		Objects.requireNonNull(key, "Key was null, cannot PUT");
		Objects.requireNonNull(value, "Value was null, cannot PUT");
		Objects.requireNonNull(level, "Consistency level was null, cannot PUT value mapped to key [" + key + "]");

		int minReplicas = level.minWriteReplicas(), maxReplicas = level.maxWriteReplicas();
		if (maxReplicas <= 0 || maxReplicas > Config.numReplicas() || minReplicas <= 0
				|| minReplicas > Config.numReplicas())
			throw createException("Consistency Level [" + level + "] was not configured correctly");

		writeToReplicas(key, value, minReplicas, maxReplicas);
	}

	protected void writeToReplicas(K key, V value, int minReplicas, int maxReplicas) throws Exc {
		Val valueWrpr = createValue(value);
		int idx = getPartitioner().indexOfResponsibleNode(key);

		Map<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replicas = new HashMap<>();
		int availableReplicas = 0;
		IKeyValueStoreClient<K, V, N, Val, Con, Exc> client;
		int maxConfiguredReplicaCount = Config.numReplicas();
		while (maxConfiguredReplicaCount-- > 0 && availableReplicas < maxReplicas) {
			try {
				client = this.getClientConnection(nodes.get(idx));
				availableReplicas++;
			} catch (Exception e) {
				client = null;
			}
			replicas.put(nodes.get(idx), client);
			idx++;
			idx %= nodes.size();
		}

		if (availableReplicas < minReplicas)
			throw createException("Failed to process write request, only [" + availableReplicas
					+ "] out of the minimum required [" + minReplicas + "] replicas were available");

		for (Map.Entry<N, IKeyValueStoreClient<K, V, N, Val, Con, Exc>> replica : replicas.entrySet())
			if (replica.getValue() == null)
				missedWrites.saveMissedWrite(replica.getKey(), key, valueWrpr);
			else
				replica.getValue().write(key, valueWrpr);
	}

	@Override
	public Val read(K key) throws Exc {
		if (!memTable.containsKey(key))
			throw createException("Key [" + key + "] not found");
		return memTable.get(key);
	}

	@Override
	public void write(K key, Val value) throws Exc {
		Objects.requireNonNull(value, "Value mapped to Key [" + key + "] was null");
		writeAheadLog(new Entry<>(key, value));
		if (!memTable.containsKey(key) || shouldOverwrite(memTable.get(key), value))
			memTable.put(key, value);
	}

	protected void writeAheadLog(Entry<K, Val> entry) throws Exc {
		try {
			this.writeAheadLogger.write(serializer.serialize(entry));
		} catch (IOException e) {
			throw createException("Failed to write/update value, due to failure in write-ahead-logging");
		}
	}

	@Override
	public Map<K, Val> getMissedWrites(N node) throws Exc {
		return missedWrites.getMissedWrites(node);
	}

	@Override
	public void setNeighborList(List<N> nodes) throws Exc {
		if (this.nodes != null)
			throw createException(
					"Cluster nodes list already set, attempt to reset the list (Please restart the cluster to set list of nodes again)");
		try {
			FileUtils.fileAppender(new File(nodesListFilename));
		} catch (IOException e) {
			throw createException("Failed to set nodes list, due to failure in logging the list");
		}
		this.nodes = nodes;
	}

	protected IKeyValueStoreClient<K, V, N, Val, Con, Exc> getClientConnection(N node) {
		if (node.equals(this.node))
			return this;
		if (!connCache.containsKey(node))
			connCache.put(node, createConnection(node));
		return connCache.get(node).getClient();
	}

	protected IPartitioner<K> getPartitioner() throws Exc {
		if (partitioner == null) {
			if (nodes == null)
				throw createException("Neighboring Nodes list not initialized");
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

	protected abstract IPartitioner<K> createPartitioner();

	protected abstract IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc> createConnection(N node);

	protected abstract Val createValue(V value);

	protected abstract N createNode(String ip, int port);

}
