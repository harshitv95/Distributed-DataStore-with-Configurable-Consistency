package com.hvadoda1.keyvalstore;

public interface IRange<K> {

	/**
	 * Get start of the range (inclusive)
	 * 
	 * @return instanceof V, start of the range
	 */
	K getStart();

	/**
	 * Get end of the range (exclusive)
	 * 
	 * @return instanceof V, end of the range
	 */
	K getEnd();

}
