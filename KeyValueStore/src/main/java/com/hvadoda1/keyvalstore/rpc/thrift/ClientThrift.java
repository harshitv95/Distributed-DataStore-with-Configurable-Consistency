package com.hvadoda1.keyvalstore.rpc.thrift;

import java.util.Map;

import org.apache.thrift.TException;

import com.hvadoda1.keyvalstore.IKeyValueStoreClient;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ConsistencyLevel;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.KeyValueStore;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Value;

public class ClientThrift implements IKeyValueStoreClient<Integer, String, Node, Value, ConsistencyLevel, TException> {

	protected final KeyValueStore.Client clientConn;

	public ClientThrift(KeyValueStore.Client clientConn) {
		this.clientConn = clientConn;
	}

	@Override
	public Value read(Integer key) throws TException {
		return clientConn.read(key);
	}

	@Override
	public void write(Integer key, Value value) throws TException {
		clientConn.write(key, value);
	}

	@Override
	public Map<Integer, Value> getMissedWrites(Node node) throws TException {
		return clientConn.getMissedWrites(node);
	}

}
