package com.hvadoda1.keyvalstore;

import java.util.HashMap;
import java.util.Map;

public class AbstractHintedHandoffs<K, V> implements IHintedHandoffs<K, V> {

	protected final Map<INode<K>, Map<K, V>> missedWrites = new HashMap<INode<K>, Map<K, V>>();

	@Override
	public void saveMissedWrite(INode<K> node, K key, V value) {
		if (!missedWrites.containsKey(node))
			missedWrites.put(node, new HashMap<K, V>());
	}

}
