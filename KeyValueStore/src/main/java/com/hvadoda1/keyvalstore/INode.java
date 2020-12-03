package com.hvadoda1.keyvalstore;

import java.io.Serializable;

public interface INode extends Serializable {

	/**
	 * @return IP Address of current Node in String format
	 */
	String getIp();

	/**
	 * @return Port number (int) of current Node
	 */
	int getPort();

}
