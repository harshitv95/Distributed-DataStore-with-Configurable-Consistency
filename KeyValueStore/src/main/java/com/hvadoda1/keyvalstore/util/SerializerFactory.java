package com.hvadoda1.keyvalstore.util;

import com.hvadoda1.keyvalstore.util.serialize.BasicObjectSerializer;
import com.hvadoda1.keyvalstore.util.serialize.ISerializer;
import com.hvadoda1.keyvalstore.util.serialize.SimpleSerializer;

public class SerializerFactory {

	private static final BasicObjectSerializer basicSerializer = new BasicObjectSerializer();

	public static <T> ISerializer<T> getSimpleSerializer() {
		return getSimpleSerializer(false);
	}
	
	public static <T> ISerializer<T> getSimpleSerializer(boolean strict) {
		return new SimpleSerializer<T>(basicSerializer, strict);
	}

	public static ISerializer<Object> getBasicObjectSerializer() {
		return basicSerializer;
	}

}
