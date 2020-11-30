package com.hvadoda1.keyvalstore;

import java.io.File;
import java.util.Map;

public class Config {

	public final static String USR_DIR = System.getProperty("user.dir") + File.separatorChar;

	public static String backupsDir(INode node) {
		return USR_DIR + node.getIp() + File.separatorChar + node.getPort() + File.separatorChar;
	}

	protected static int numWriteReplicas = 3;
	protected static int numReadReplicas = 2;

	protected static Config instance;

	Config(Map<String, String> params) {
		if (instance != null)
			throw new RuntimeException("Config already initialized");
		numWriteReplicas = Integer.parseInt(params.getOrDefault("replicas", "3"));
		instance = this;
	}

	public static int getMaxNumWriteReplicas() {
		return numWriteReplicas;
	}

	public static int getMaxNumReadReplicas() {
		return numReadReplicas;
	}

}
