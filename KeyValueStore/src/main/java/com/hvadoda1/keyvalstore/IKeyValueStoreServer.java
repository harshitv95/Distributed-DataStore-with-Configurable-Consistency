package com.hvadoda1.keyvalstore;

import java.util.List;

public interface IKeyValueStoreServer<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Exc extends Exception>
		extends IKeyValueStoreClient<K, V, N, Val, Con, Exc> {

	void setNeighborList(List<N> nodes);

}
