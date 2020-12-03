package com.hvadoda1.keyvalstore;

import static com.hvadoda1.keyvalstore.util.ValueUtils.shouldOverwrite;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.hvadoda1.keyvalstore.util.Logger;
import com.hvadoda1.keyvalstore.util.NodeUtils;
import com.hvadoda1.keyvalstore.util.ValueUtils;

public class HintedHandoffs<K, V, Val extends IValue<V>, N extends INode> implements IHintedHandoffs<K, V, Val, N> {

	protected final Map<INode, Map<K, Val>> missedWrites = new HashMap<>();
	protected final Map<K, Val> emptyMap = new HashMap<>();

	@Override
	public void saveMissedWrite(N node, K key, Val value) {
		Objects.requireNonNull(node, "HintedHandoffs: node cannot be null");
		Logger.info("Saving hint for [" + NodeUtils.nodeAddress(node) + "], (" + key + ", "
				+ ValueUtils.valueToStr(value) + ")");

		if (!missedWrites.containsKey(node))
			missedWrites.put(node, new HashMap<>());
		if (!missedWrites.get(node).containsKey(key) || shouldOverwrite(missedWrites.get(node).get(key), value))
			missedWrites.get(node).put(key, value);
	}

	@Override
	public Map<K, Val> getMissedWrites(N node) {
		if (!missedWrites.containsKey(node))
			return emptyMap;

		try {
			return missedWrites.get(node);
		} finally {
			missedWrites.remove(node);
		}
	}

}
