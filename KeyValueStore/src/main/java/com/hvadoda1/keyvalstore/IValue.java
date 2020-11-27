package com.hvadoda1.keyvalstore;

public interface IValue<V> {

	/**
	 * @return Value type
	 */
	V getValue();

	/**
	 * @return instanceof {@link IValueMeta} representing metadata of current Value
	 */
	IValueMeta getMeta();

}
