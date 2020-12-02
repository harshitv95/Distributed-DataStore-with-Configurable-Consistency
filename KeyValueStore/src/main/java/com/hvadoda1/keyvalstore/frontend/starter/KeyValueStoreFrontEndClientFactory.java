//package com.hvadoda1.keyvalstore.frontend.starter;
//
//import com.hvadoda1.keyvalstore.frontend.AbstractFrontEndClient;
//import com.hvadoda1.keyvalstore.rpc.thrift.ThriftFrontEndClient;
//import com.hvadoda1.keyvalstore.starter.KeyValueStoreImpls;
//
//public class KeyValueStoreFrontEndClientFactory {
//
//	public AbstractFrontEndClient<?, ?, ?, ?, ?, ?> factory(KeyValueStoreImpls impls) {
//		switch(impls) {
//		case THRIFT:
//			return new ThriftFrontEndClient(node);
//		}
//	}
//	
//}
