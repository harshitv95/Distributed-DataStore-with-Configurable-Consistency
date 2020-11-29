package com.hvadoda1.keyvalstore;

public interface IKeyValueStoreClientConnection<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Exc extends Exception> {

	boolean isOpen();
	
	IKeyValueStoreClient<K, V, N, Val, Con, Exc> getClient();

}
