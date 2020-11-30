package com.hvadoda1.keyvalstore.starter;

import static com.hvadoda1.keyvalstore.util.CommonUtils.isInt;
import static com.hvadoda1.keyvalstore.util.CommonUtils.parseArgsMap;

import java.util.Map;

public class StartKeyValueStoreService {

	public static void main(String[] args) {
		Map<String, String> paramArgs = parseArgsMap(args);

		if (!paramArgs.containsKey("port") || !isInt(paramArgs.get("port")))
			throw new RuntimeException("Port number is required");

		IKeyValueStoreServiceStarter<?, ?, ?> starter = KeyValueStoreServiceStarterFactory
				.factory(KeyValueStoreImpls.THRIFT);

		try {
			starter.start(Integer.parseInt(paramArgs.get("port")));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
