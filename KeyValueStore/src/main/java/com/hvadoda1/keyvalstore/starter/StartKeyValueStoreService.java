package com.hvadoda1.keyvalstore.starter;

import static com.hvadoda1.keyvalstore.util.CommonUtils.parseArgsMap;

import java.util.Map;
import java.util.Objects;

import com.hvadoda1.keyvalstore.Config;

public class StartKeyValueStoreService {

	public static void main(String[] args) {
		Map<String, String> paramArgs = parseArgsMap(args);

		IKeyValueStoreServiceStarter<?, ?, ?> starter = KeyValueStoreServiceStarterFactory
				.factory(KeyValueStoreImpls.THRIFT);

		try {
			Objects.requireNonNull(starter.initConfig(paramArgs), "Config was not initialized");
			starter.start(Config.port());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
