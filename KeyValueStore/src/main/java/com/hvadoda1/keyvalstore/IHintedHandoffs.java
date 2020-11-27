package com.hvadoda1.keyvalstore;

public interface IHintedHandoffs<K, V> {

	void saveMissedWrite(INode<K> node, K key, V value);

}
