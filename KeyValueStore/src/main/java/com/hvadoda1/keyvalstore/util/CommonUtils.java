package com.hvadoda1.keyvalstore.util;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {
	public static Map<String, String> parseArgsMap(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		String[] argSplit;
		String key, val;
		StringBuilder sb;
		int i;
		for (String arg : args) {
			argSplit = arg.split("=");
			if (argSplit.length > 1) {
				sb = new StringBuilder();
				for (i = 1; i < argSplit.length; i++)
					sb.append(argSplit[i]).append('=');
				if (sb.length() > 0)
					sb.deleteCharAt(sb.length() - 1);
				val = sb.toString();
			} else
				continue;
//				val = argSplit[0];
			key = argSplit[0];

			map.put(key, val);
		}

		return map;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static <T> T last(T[] arr) {
		if (arr == null || arr.length == 0)
			return null;
		return arr[arr.length - 1];
	}

}
