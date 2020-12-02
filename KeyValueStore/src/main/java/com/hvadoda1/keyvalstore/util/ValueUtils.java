package com.hvadoda1.keyvalstore.util;

import com.hvadoda1.keyvalstore.IValue;

public class ValueUtils {

	public static boolean shouldOverwrite(IValue<?> oldVal, IValue<?> newVal) {
		return oldVal == null || oldVal.getMeta().getTimestamp() < newVal.getMeta().getTimestamp();
	}

}
