package com.hvadoda1.keyvalstore;

import java.util.HashMap;
import java.util.Map;

import static com.hvadoda1.keyvalstore.util.ValueUtils.shouldOverwrite;

public class AbstractHintedHandoffs<K, V> implements IHintedHandoffs<K, V> {

	protected final Map<INode<K>, Map<K, IValue<V>>> missedWrites = new HashMap<>();

	@Override
	public void saveMissedWrite(INode<K> node, K key, IValue<V> value) {
		if (!missedWrites.containsKey(node))
			missedWrites.put(node, new HashMap<>());
		if (!missedWrites.get(node).containsKey(key) || shouldOverwrite(missedWrites.get(node).get(key), value))
			missedWrites.get(node).put(key, value);
	}

	@Override
	public Map<K, IValue<V>> getMissedWrites(INode<K> node) {
		return !missedWrites.containsKey(node) ? null : missedWrites.get(node);
	}

}
