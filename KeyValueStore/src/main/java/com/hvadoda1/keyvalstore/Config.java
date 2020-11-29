package com.hvadoda1.keyvalstore;

import java.io.File;
import java.util.Map;

public class Config {

	public final static String USR_DIR = System.getProperty("user.dir") + File.separatorChar;

	public static String backupsDir(String fullHostAddr) {
		return USR_DIR + fullHostAddr + File.separatorChar;
	}

	protected static int numReplicas = 3;
	protected static Config instance;

	Config(Map<String, String> params) {
		if (instance != null)
			throw new RuntimeException("Config already initialized");
		numReplicas = Integer.parseInt(params.getOrDefault("replicas", "3"));
		instance = this;
	}

	public static int getMaxNumReplicas() {
		return numReplicas;
	}

}
