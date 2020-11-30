package com.hvadoda1.keyvalstore.starter;

import com.hvadoda1.keyvalstore.rpc.thrift.ThriftServiceStarter;

public class KeyValueStoreServiceStarterFactory {

	public static IKeyValueStoreServiceStarter<?, ?, ?> factory(KeyValueStoreImpls impl) {
		switch (impl) {
		case THRIFT:
			return new ThriftServiceStarter();
		}
		throw new RuntimeException("Invalid Service Starter: [" + null + "]");
	}

}
