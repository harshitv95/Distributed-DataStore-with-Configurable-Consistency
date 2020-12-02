package com.hvadoda1.keyvalstore;

import java.util.Map;

public interface IKeyValueStoreClient<K, V, N extends INode, Val extends IValue<V>, Con extends IConsistencyLevel, Exc extends Exception>
		extends IKeyValueStoreService<K, V, N, Val, Con, Exc> {
	/**
	 * @param key
	 * @return Value mapped to provided {@code key} if the mapping exists in the
	 *         current Node
	 * @throws Exc if mapping is not found in current Node
	 */
	Val read(K key) throws Exc;

	void write(K key, Val value) throws Exc;

//	void setRange(Map<INode, IRange<K>> ranges) throws Exc;

	/**
	 * Hinted Handoff.<br>
	 * Returns the
	 * 
	 * @param node
	 * @return
	 * @throws Exc
	 */
	Map<K, Val> getMissedWrites(N node) throws Exc;

	N getRemoteNode();

}
