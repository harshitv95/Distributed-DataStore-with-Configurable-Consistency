package com.hvadoda1.keyvalstore;

public interface INode<K> {

	/**
	 * Returns the ID assigned to a Node
	 * 
	 * @return instanceof K (Key's type), id of current Node
	 */
	K getId();

	/**
	 * @return IP Address of current Node in String format
	 */
	String getIp();

	/**
	 * @return Port number (int) of current Node
	 */
	int getPort();

}
