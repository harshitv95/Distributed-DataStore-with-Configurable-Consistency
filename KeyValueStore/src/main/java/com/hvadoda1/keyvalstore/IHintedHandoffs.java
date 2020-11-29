package com.hvadoda1.keyvalstore;

import java.util.Map;

public interface IHintedHandoffs<K, V, Val extends IValue<V>, N extends INode> {

	void saveMissedWrite(N node, K key, Val value);

	Map<K, Val> getMissedWrites(N node);

}
