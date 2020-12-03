package com.hvadoda1.keyvalstore.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
	private static final SimpleDateFormat logFileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss"),
			logDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public static String logFileNameDateString() {
		return logFileNameFormat.format(new Date());
	}

	public static String logDateTimeString() {
		return logDateTimeFormat.format(new Date());
	}

	public static long currentTimestamp() {
		return System.currentTimeMillis();
	}

}
