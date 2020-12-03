package com.hvadoda1.keyvalstore.util.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class BasicObjectSerializer implements ISerializer<Object> {

	@Override
	public String serialize(Object obj) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos);) {
			os.writeObject(obj);
//			return bos.toString();
			return Base64.getEncoder().encodeToString(bos.toByteArray());
		}
	}

	@Override
	public Object deserialize(String serialized) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(serialized));
				ObjectInputStream ois = new ObjectInputStream(bis);) {
			return ois.readObject();
		}
	}

}
