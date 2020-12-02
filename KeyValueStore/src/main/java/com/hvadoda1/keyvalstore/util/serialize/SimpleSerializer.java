package com.hvadoda1.keyvalstore.util.serialize;

import java.io.IOException;

public class SimpleSerializer<T> implements ISerializer<T> {

	protected final BasicObjectSerializer serializer;
	protected final boolean strict;

	public SimpleSerializer(BasicObjectSerializer serializer, boolean strict) {
		this.serializer = serializer;
		this.strict = strict;
	}

	@Override
	public String serialize(T obj) throws IOException {
		return serializer.serialize(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(String serialized) throws IOException, ClassNotFoundException {
		T ret = (T) serializer.deserialize(serialized);
		if (strict && ret == null)
			throw new SerializerException("Failed to deserialize to object");
		return ret;
	}

}
