package com.hvadoda1.keyvalstore;

import java.io.Closeable;
import java.util.List;

public interface IKeyValueStoreServer<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Exc extends Exception>
		extends IKeyValueStoreClient<K, V, N, Val, Con, Exc>, Closeable {

	void setNeighborList(List<N> nodes) throws Exc;

}
