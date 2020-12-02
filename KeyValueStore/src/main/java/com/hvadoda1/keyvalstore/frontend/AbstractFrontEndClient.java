package com.hvadoda1.keyvalstore.frontend;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

import com.hvadoda1.keyvalstore.IConsistencyLevel;
import com.hvadoda1.keyvalstore.IKeyValueStoreClientConnection;
import com.hvadoda1.keyvalstore.IKeyValueStoreService;
import com.hvadoda1.keyvalstore.INode;
import com.hvadoda1.keyvalstore.IValue;

public abstract class AbstractFrontEndClient<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Ex extends Exception>
		implements IKeyValueStoreService<K, V, N, Val, Con, Ex>, Closeable {

	protected final IKeyValueStoreClientConnection<K, V, N, Val, Con, Ex> connection;

	public AbstractFrontEndClient(N node) throws Ex {
		this.connection = Objects.requireNonNull(getConnection(node), "Connection was null");
	}

	@Override
	public V get(K key, Con level) throws Ex {
		return connection.getClient().get(key, level);
	}

	@Override
	public void put(K key, V value, Con level) throws Ex {
		connection.getClient().put(key, value, level);
	}

	@Override
	public void close() throws IOException {
		if (connection.isOpen())
			connection.close();
	}

	public N getRemoteNode() {
		return this.connection.getRemoteNode();
	}

	protected abstract IKeyValueStoreClientConnection<K, V, N, Val, Con, Ex> getConnection(N node) throws Ex;

}
