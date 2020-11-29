package com.hvadoda1.keyvalstore;

import static com.hvadoda1.keyvalstore.util.NodeUtils.nodeAddress;
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
import com.hvadoda1.keyvalstore.util.SerializerFactory;
import com.hvadoda1.keyvalstore.util.partitioning.IPartitioner;
import com.hvadoda1.keyvalstore.util.serialize.ISerializer;

public abstract class AbstractKeyValueStoreService<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Exc extends Exception>
		implements IKeyValueStoreService<K, V, N, Val, Con, Exc>, IKeyValueStoreServer<K, V, N, Val, Con, Exc> {

	protected Map<K, Val> memTable = new HashMap<>();
	protected int entryCount = 0;
	protected final ISerializer<Entry<K, Val>> serializer;

	protected final String backupFilename, tempBackupFilename;

	protected final N node;

	protected final IHintedHandoffs<K, V, Val, N> missedWrites = new HintedHandoffs<>();

	protected List<N> nodes;
	protected final IPartitioner<K> partitioner;

	protected final IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc> connection;

	protected final FileWriter writeAheadLogger;

	public AbstractKeyValueStoreService(N node) throws IOException {
		this.node = node;
		this.backupFilename = Config.backupsDir(nodeAddress(node)) + "keyvalstore.bak";
		this.tempBackupFilename = Config.backupsDir(nodeAddress(node)) + "keyvalstore.temp.bak";

		this.connection = createConnection();
		this.partitioner = createPartitioner();
		this.serializer = SerializerFactory.getSimpleSerializer();

		recoverLastSavedState();

		this.writeAheadLogger = FileUtils.fileAppender(new File(backupFilename));
	}

	public void recoverLastSavedState() {
		File backup = new File(backupFilename);
		if (!backup.exists() || !backup.isFile())
			return;
		try (BufferedReader fr = FileUtils.fileReader(backup);) {
			String line;
			Entry<K, Val> entry;
			while ((line = fr.readLine()) != null) {
				entry = serializer.deserialize(line);
				if (entry == null)
					throw new RuntimeException("Backed up data seems corrupt, failed to properly restore backups");
				entryCount++;

				memTable.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to recover previously backed up data", e);
		}
	}

	@Override
	public V get(K key, Con level) throws Exc {
		Objects.requireNonNull(key, "Key was null, cannot GET");
		Objects.requireNonNull(level, "Consistency level was null, cannot GET value mapped to key [" + key + "]");
		int replicaCount = Math.min(level.numReplicas(), Config.getMaxNumReplicas());
		if (replicaCount <= 0)
			throw createException("Consistency Level [" + level + "] was not configured correctly");

		return readFromReplicas(key, replicaCount);
	}

	protected V readFromReplicas(K key, int numReplicas) throws Exc {
		int idx = partitioner.indexOfResponsibleNode(key);
		Val v = this.connection.getClient(nodes.get(idx)).read(key);
		Val v1;
		while (numReplicas-- > 1) {
			++idx;
			idx %= nodes.size();
			v1 = this.connection.getClient(nodes.get(idx)).read(key);
			if (shouldOverwrite(v, v1))
				v = v1;
		}
		return v.getValue();
	}

	@Override
	public void put(K key, V value, Con level) throws Exc {
		Objects.requireNonNull(key, "Key was null, cannot GET");
		Objects.requireNonNull(level, "Consistency level was null, cannot GET value mapped to key [" + key + "]");
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
	public void setNeighborList(List<N> nodes) {
		this.nodes = nodes;
	}

	protected abstract Exc createException(String message);

	protected abstract Exc createException(String message, Exception cause);

	protected abstract IPartitioner<K> createPartitioner();

	protected abstract IKeyValueStoreClientConnection<K, V, N, Val, Con, Exc> createConnection();

}
