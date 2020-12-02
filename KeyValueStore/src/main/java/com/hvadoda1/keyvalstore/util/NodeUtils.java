package com.hvadoda1.keyvalstore.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.hvadoda1.keyvalstore.INode;

public class NodeUtils {
	public static String nodeAddress(INode node) {
		return node.getIp() + ":" + node.getPort();
	}

	public static <N extends INode> List<N> getNodesFromFile(File file, BiFunction<String, Integer, N> createNode)
			throws IOException {
		if (!file.isFile())
			throw new FileNotFoundException(
					"File [" + file.getAbsolutePath() + "] not found, cannot get list of Nodes");

		List<N> nodes = new ArrayList<>();

		String line;
		String split[];
		try (BufferedReader br = FileUtils.fileReader(file)) {
			while ((line = br.readLine()) != null) {
				split = line.split(":");
				if (split.length != 2 || !CommonUtils.isInt(split[1]))
					throw new RuntimeException(
							"Nodes are expected in the format: <host/ip>:<port>, invalid format: " + line);
				nodes.add(createNode.apply(split[0], Integer.parseInt(split[1])));
			}
		}
		return nodes;
	}
}
