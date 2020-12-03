package com.hvadoda1.keyvalstore.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Base64;
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

//	public static String convertToByteString(Object object) throws IOException {
//		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
//			out.writeObject(object);
//			final byte[] byteArray = bos.toByteArray();
//			return Base64.getEncoder().encodeToString(byteArray);
//		}
//	}
//
//	public static Object convertFromByteString(String byteString) throws IOException, ClassNotFoundException {
//		final byte[] bytes = Base64.getDecoder().decode(byteString);
//		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
//			return in.readObject();
//		}
//	}

}
