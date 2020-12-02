package com.hvadoda1.keyvalstore;

import java.io.File;
import java.util.Map;

public class Config {

	public final static String USR_DIR = System.getProperty("user.dir") + File.separatorChar;

	public static String backupsDir(INode node) {
		return USR_DIR + node.getIp() + File.separatorChar + node.getPort() + File.separatorChar;
	}

	protected static int numReplicas = 3;

	protected static Config instance;

	protected final Map<String, String> args;

	Config(Map<String, String> params) {
		if (instance != null)
			throw new RuntimeException("Config already initialized");
		numReplicas = Integer.parseInt(params.getOrDefault("replicas", "3"));
		instance = this;
		args = params;
	}

	public static Map<String, String> args() {
		return instance.args;
	}

	public static String getArg(String name) {
		return instance.args.get(name);
	}

	public static int numReplicas() {
		return numReplicas;
	}

}
