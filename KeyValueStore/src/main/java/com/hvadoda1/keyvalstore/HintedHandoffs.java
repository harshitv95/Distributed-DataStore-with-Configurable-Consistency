package com.hvadoda1.keyvalstore;

import static com.hvadoda1.keyvalstore.util.ValueUtils.shouldOverwrite;

import java.util.HashMap;
import java.util.Map;

public class HintedHandoffs<K, V, Val extends IValue<V>, N extends INode> implements IHintedHandoffs<K, V, Val, N> {

	protected final Map<INode, Map<K, Val>> missedWrites = new HashMap<>();

	@Override
	public void saveMissedWrite(N node, K key, Val value) {
		if (!missedWrites.containsKey(node))
			missedWrites.put(node, new HashMap<>());
		if (!missedWrites.get(node).containsKey(key) || shouldOverwrite(missedWrites.get(node).get(key), value))
			missedWrites.get(node).put(key, value);
	}

	@Override
	public Map<K, Val> getMissedWrites(N node) {
		return !missedWrites.containsKey(node) ? null : missedWrites.get(node);
	}

}
