package com.hvadoda1.keyvalstore.util.serialize;

import java.io.IOException;

public interface ISerializer<T> {
	String serialize(T obj) throws IOException;

	T deserialize(String serialized) throws IOException, ClassNotFoundException;
}
