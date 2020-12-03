package com.hvadoda1.keyvalstore;

import java.io.Serializable;

public class Entry<K, V> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -191160979270409831L;

	private final K key;
	private final V value;

	public Entry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

}
