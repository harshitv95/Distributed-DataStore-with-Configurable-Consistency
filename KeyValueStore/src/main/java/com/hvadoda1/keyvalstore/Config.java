package com.hvadoda1.keyvalstore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hvadoda1.keyvalstore.util.CommonUtils;
import com.hvadoda1.keyvalstore.util.DateTimeUtils;
import com.hvadoda1.keyvalstore.util.Logger;
import com.hvadoda1.keyvalstore.util.Logger.Level;

public class Config {

	protected final static List<String> requiredParams = new ArrayList<>() {
		private static final long serialVersionUID = 1L;
		{
			add("port");
		}

	};

	public final static String USR_DIR = System.getProperty("user.dir") + File.separatorChar;

	public static String backupsDir(INode node) {
		return USR_DIR + node.getIp() + File.separatorChar + node.getPort() + File.separatorChar;
	}

	protected static int numReplicas = 3;
	protected static int port;

	protected static Config instance;

	protected final Map<String, String> args;

	protected final boolean isServer;

	public Config(Map<String, String> params) {
		this(params, true);
	}

	public Config(Map<String, String> params, boolean server) {
		if (instance != null)
			throw new RuntimeException("Config already initialized");
		this.args = params;
		this.isServer = server;
		instance = this;
		this.init();
	}

	@SuppressWarnings("resource")
	protected void init() {
		if (this.isServer) {
			String missingParam;
			if ((missingParam = requiredParams.stream().filter(param -> !args.containsKey(param)).findAny()
					.orElse(null)) != null)
				throw new RuntimeException("Required argument [" + missingParam + "] was not provided");
		}

		int n;
		if (!CommonUtils.isInt(args.get("port")) || ((n = Integer.parseInt(args.get("port"))) < 0 || n > 65535))
			throw new RuntimeException("Invalid port number: [" + args.get("port") + "]");
		port = n;

		if (args.containsKey("replicas")) {
			if (!CommonUtils.isInt(args.get("replicas")) || (n = Integer.parseInt(args.get("replicas"))) < 0)
				throw new RuntimeException("Invalid number of replicas : [" + args.get("replicas") + "]");
			numReplicas = n;
		}

		Level logLevel = Level.from(Integer.parseInt(args.getOrDefault("level", "3")));
		String logFilename = "logs" + File.separator + "log_" + (DateTimeUtils.logFileNameDateString()) + ".txt";
		try {
			new Logger(logLevel, "com.hvadoda1.keyvalstore", logFilename, args.get("port"), true);
		} catch (IOException e) {
			throw new RuntimeException("Failed to initialize Logger", e);
		}
		Logger.debugHigh("Command Line Args", args);
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

	public static int port() {
		return port;
	}

}
