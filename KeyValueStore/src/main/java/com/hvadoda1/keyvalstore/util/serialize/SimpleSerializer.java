package com.hvadoda1.keyvalstore.util.serialize;

import java.io.IOException;

public class SimpleSerializer<T> implements ISerializer<T> {
	
	protected final BasicObjectSerializer serializer;

	public SimpleSerializer(BasicObjectSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public String serialize(T obj) throws IOException {
		return serializer.serialize(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(String serialized) throws IOException, ClassNotFoundException {
		return (T) serializer.deserialize(serialized);
	}

}
