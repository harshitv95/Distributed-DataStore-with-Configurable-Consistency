package com.hvadoda1.keyvalstore;

import java.io.Closeable;

public interface IKeyValueStoreClientConnection<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Exc extends Exception>
		extends Closeable {

	boolean isOpen();

	IKeyValueStoreClient<K, V, N, Val, Con, Exc> getClient();

	default N getRemoteNode() {
		return getClient().getRemoteNode();
	}

}
