package com.hvadoda1.keyvalstore.rpc.thrift;

import org.apache.thrift.TException;

import com.hvadoda1.keyvalstore.IKeyValueStoreClientConnection;
import com.hvadoda1.keyvalstore.frontend.AbstractFrontEndClient;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ConsistencyLevel;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Value;

public class ThriftFrontEndClient
		extends AbstractFrontEndClient<Integer, String, Node, Value, ConsistencyLevel, TException> {

	public ThriftFrontEndClient(Node node) throws TException {
		super(node);
	}

	@Override
	protected IKeyValueStoreClientConnection<Integer, String, Node, Value, ConsistencyLevel, TException> getConnection(
			Node node) throws TException {
		return new ClientConnectionThrift(node);
	}

}
