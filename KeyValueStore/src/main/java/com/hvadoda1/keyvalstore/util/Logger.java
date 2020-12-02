package com.hvadoda1.keyvalstore.util;

import static com.hvadoda1.keyvalstore.util.CommonUtils.last;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Simple Logging utility that lets users print logs, to a console and to an
 * output file.
 * 
 * This is a Singleton class, and will only be initialized once.
 * 
 * @author Harshit Vadodaria
 *
 */
public class Logger implements AutoCloseable {

	protected static Logger instance = null;
	protected final Level level;
	protected final Set<Level> logtoFileForLevel;
	protected final FileWriter fw;
	protected final String basePackageName;
	protected final boolean detailsOnConsole;
	protected String hostAddress;

	/**
	 * Initializes the logger with the provided level and log file. Call this
	 * function only once, and either pass the instance around, or use the static
	 * {@link #getInstance()} method to get the Logger's instance, and call the
	 * {@link #log(Level, String, Object...)} method. A better approach would be to
	 * call the various static logging methods available for each level:
	 * <ul>
	 * <li>{@link #error(String, Throwable, Object...)}</li>
	 * <li>{@link #warn(String, Object...)}</li>
	 * <li>{@link #info(String, Object...)}</li>
	 * <li>{@link #config(String, Object...)}</li>
	 * <li>{@link #debugLow(String, Object...)}</li>
	 * <li>{@link #debugMed(String, Object...)}</li>
	 * <li>{@link #debugHigh(String, Object...)}</li>
	 * </ul>
	 * 
	 * @param level             Level to log at (Refer {@link Level})
	 * @param logFilePath       Name of log file
	 * @param logtoFileForLevel If this is not empty, only the logs printed at any
	 *                          of these levels will be printed to the log file,
	 *                          else all the logs (with levels equal to or less than
	 *                          the constructor parameter {@code level}) will be
	 *                          printed to the log file. Regardless of this
	 *                          parameter, all the logs are printed to the console.
	 * @throws IOException
	 */
	public Logger(Level level, String logFilePath, String hostAddress, boolean detailsOnConsole,
			Level... logtoFileForLevel) throws IOException {
		this(level, Logger.class.getPackageName().split("\\.")[0], logFilePath, hostAddress, detailsOnConsole,
				logtoFileForLevel);
	}

	public Logger(Level level, String basePackageName, String logFilePath, String hostAddress, boolean detailsOnConsole,
			Level... logtoFileForLevel) throws IOException {
		if (instance != null)
			throw new RuntimeException("Logger already initialized, cannot initialize it again");
		this.level = level != null ? level : Level.INFO;

		File logFile = new File(logFilePath);
		if (!logFile.getParentFile().exists())
			logFile.getParentFile().mkdirs();
		this.fw = new FileWriter(logFilePath);

		this.detailsOnConsole = detailsOnConsole;
		this.logtoFileForLevel = new HashSet<>(Arrays.asList(
				(logtoFileForLevel == null || logtoFileForLevel.length == 0) ? Level.values() : logtoFileForLevel));
		this.basePackageName = basePackageName;
		if (level == null)
			Logger.warn("Invalid Log Level value provided, defaulting to level INFO");

		this.hostAddress = Objects.requireNonNullElseGet(hostAddress, () -> {
			try {
				return InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return "-";
			}
		});

		Logger.instance = this;
	}

	/**
	 * Returns the singleton instance of this Logger
	 * 
	 * @return instance of this Logger
	 */
	public static Logger getInstance() {
		if (Logger.instance == null)
			throw new RuntimeException("Logger not initialized. Initialize it using the constructor new Logger(...)");
		return Logger.instance;
	}

	public void log(Level level, String msg, Object... args) {
		if (shouldPrintLevel(level)) {
			String logMsgPrefix = logMessagePrefix(level), logMsg = String.format("%s %s", msg, printAll(args)).trim();
			if (level == Level.ERROR)
				System.err.println(logMsgPrefix + logMsg);
			else
				System.out.println((this.detailsOnConsole ? logMsgPrefix : "") + logMsg);
			if (this.logtoFileForLevel == null || this.logtoFileForLevel.contains(level)) {
				try {
					this.fw.write(logMsgPrefix + logMsg + System.lineSeparator());
					this.fw.flush();
				} catch (IOException e) {
					System.err.println("Failed to write to log file");
					e.printStackTrace();
				}
			}
		}
	}

	protected boolean shouldPrintLevel(Level level) {
		return level == Level.ERROR || level.toInt() <= this.level.toInt();
	}

	protected String logMessagePrefix(Level level) {
		String caller = getCaller();
		return String.format("[%s][%s][%s][%s] ", new Date(), level, this.hostAddress, caller != null ? caller : "-");
	}

	protected String printAll(Object[] args) {
		if (args == null || args.length == 0)
			return "";
		if (args.length == 1)
			return args[0].toString();
		StringBuilder sb = new StringBuilder("(");
		for (Object o : args)
			sb.append(o).append(", ");
		if (sb.length() > 0)
			sb.delete(sb.length() - 2, sb.length());
		return sb.append(')').toString();
	}

	public static void debugHigh(String msg, Object... args) {
		Logger.getInstance().log(Level.DEBUG_HIGH, msg, args);
	}

	public static void debugMed(String msg, Object... args) {
		Logger.getInstance().log(Level.DEBUG_MED, msg, args);
	}

	public static void debugLow(String msg, Object... args) {
		Logger.getInstance().log(Level.DEBUG_LOW, msg, args);
	}

	public static void config(String msg, Object... args) {
		Logger.getInstance().log(Level.CONFIG, msg, args);
	}

	public static void info(String msg, Object... args) {
		Logger.getInstance().log(Level.INFO, msg, args);
	}

	public static void warn(String msg, Object... args) {
		Logger.getInstance().log(Level.WARN, msg, args);
	}

	public static void error(String msg, Throwable t, Object... args) {
		Logger.getInstance().log(Level.ERROR, msg, args);
		if (t != null)
			t.printStackTrace();
	}

	public static void setHost(String host) {
		getInstance().hostAddress = host;
	}

	protected String getCaller() {
		if (basePackageName == null || basePackageName.trim().equals(""))
			return null;
		for (StackTraceElement st : Thread.currentThread().getStackTrace())
			if (!st.getClassName().equals(this.getClass().getName())
//					&& st.getClassName().split("\\.")[0].equals(basePackageName))
					&& st.getClassName().startsWith(basePackageName))
				return last(st.getClassName().split("\\.")) + "." + st.getMethodName() + ":" + st.getLineNumber();
		return null;
	}

	public void close() {
		try {
			if (this.fw != null)
				this.fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeLogger() {
		getInstance().close();
	}

	public static enum Level {
		NONE(0), ERROR(1), WARN(2), INFO(3), CONFIG(4), DEBUG_LOW(5), DEBUG_MED(6), DEBUG_HIGH(7),;

		private final int levelNum;

		Level(int levelNum) {
			this.levelNum = levelNum;
		}

		public int toInt() {
			return this.levelNum;
		}

		public static Level from(int levelInt) {
			for (Level level : Level.values())
				if (level.toInt() == levelInt)
					return level;
			return null;
//			throw new RuntimeException("Invalid log level number: [" + levelInt + "]");
		}
	}
}
