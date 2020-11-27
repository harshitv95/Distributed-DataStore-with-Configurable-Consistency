package com.hvadoda1.keyvalstore;

import java.util.Map;

public interface IKeyValueStoreService<K, V, Exc extends Exception> {

	/**
	 * <b>PUBLIC API, to be used by clients.</b> <br>
	 * Returns the Value mapped to the provided {@code key}, based on following
	 * consistency level-related conditions:<br>
	 * 1. If level is {@link ConsistencyLevel ONE}, then the value contained at the
	 * Node responsible for this key is returned<br>
	 * 2. If level is {@link ConsistencyLevel QUORUM}, then the value with the
	 * latest timestamp in all Nodes in a Quorum, is returned
	 * 
	 * @param key
	 * @param level
	 * @return Value mapped to the key based on consistency-level
	 * @throws Exc
	 */
	V get(K key, ConsistencyLevel level) throws Exc;

	/**
	 * <b>PUBLIC API, to be used by clients.</b> <br>
	 * Maps the provided {@code value} mapped to the provided {@code key}, based on
	 * following consistency level-related conditions:<br>
	 * 1. If level is {@link ConsistencyLevel ONE}, then the {@code value} is just
	 * stored at the Node responsible for the provided {@code key}, and not
	 * replicated<br>
	 * 2. If level is {@link ConsistencyLevel QUORUM}, then the {@code value} is
	 * stored at the Node responsible for the provided {@code key}, as well as
	 * replicated at Nodes within its Quorum.
	 * 
	 * @param key
	 * @param level
	 * @return Value mapped to the key based on consistency-level
	 * @throws Exc
	 */
	void put(K key, V value, ConsistencyLevel level) throws Exc;

	/**
	 * @param key
	 * @return Value mapped to provided {@code key} if the mapping exists in the
	 *         current Node
	 * @throws Exc if mapping is not found in current Node
	 */
	IValue<V> read(K key) throws Exc;

	void write(K key, V value) throws Exc;

	void setRange(Map<INode<K>, IRange<K>> ranges) throws Exc;

	Map<K, V> getMissedUpdates(INode<K> node) throws Exc;

}
