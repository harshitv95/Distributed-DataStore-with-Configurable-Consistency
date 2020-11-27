package com.hvadoda1.keyvalstore.util;

import com.hvadoda1.keyvalstore.INode;

public class NodeUtils {
	public static String nodeAddress(INode<?> node) {
		return node.getIp() + ":" + node.getPort();
	}
}
