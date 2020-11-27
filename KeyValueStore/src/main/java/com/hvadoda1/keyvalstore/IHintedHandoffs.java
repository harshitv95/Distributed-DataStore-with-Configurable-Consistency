package com.hvadoda1.keyvalstore;

import java.util.Map;

public interface IHintedHandoffs<K, V> {

	void saveMissedWrite(INode<K> node, K key, IValue<V> value);

	Map<K, IValue<V>> getMissedWrites(INode<K> node);

}
