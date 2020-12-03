package com.hvadoda1.keyvalstore.util;

import com.hvadoda1.keyvalstore.IValue;

public class ValueUtils {

	public static boolean shouldOverwrite(IValue<?> oldVal, IValue<?> newVal) {
		return oldVal == null || (newVal != null && oldVal.getMeta().getTimestamp() < newVal.getMeta().getTimestamp());
	}

	public static <T> String valueToStr(IValue<T> value) {
		return valueToStr(value, true);
	}

	public static <T> String valueToStr(IValue<T> value, boolean ts) {
		if (value == null)
			return "null";
		if (ts)
			return "{val:" + value.getValue() + ", ts: " + value.getMeta().getTimestamp() + "}";
		else
			return value.getValue().toString();
	}

}
