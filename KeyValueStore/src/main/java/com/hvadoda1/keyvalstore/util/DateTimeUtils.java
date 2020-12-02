package com.hvadoda1.keyvalstore.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
	private static final SimpleDateFormat logFileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

	public static String getLogFileNameDateString() {
		return logFileNameFormat.format(new Date());
	}

	public static long currentTimestamp() {
		return System.currentTimeMillis();
	}

}
