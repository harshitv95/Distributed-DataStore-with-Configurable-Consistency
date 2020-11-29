package com.hvadoda1.keyvalstore.rpc.thrift;

import java.util.Map;

import org.apache.thrift.TException;

import com.hvadoda1.keyvalstore.IKeyValueStoreService;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ConsistencyLevel;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.KeyValueStore;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.SystemException;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Value;

public class KeyValueStoreService implements KeyValueStore.Iface,
		IKeyValueStoreService<Integer, String, Node, Value, ConsistencyLevel, TException> {

	@Override
	public String get(Integer key, ConsistencyLevel level) throws SystemException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(Integer key, String value, ConsistencyLevel level) throws SystemException, TException {
		// TODO Auto-generated method stub

	}

	@Override
	public Value read(Integer key) throws SystemException, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(Integer key, Value value) throws SystemException, TException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<Integer, Value> getMissedWrites(Node node) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
