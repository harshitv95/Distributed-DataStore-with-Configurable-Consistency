package com.hvadoda1.keyvalstore.rpc.thrift;

import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.hvadoda1.keyvalstore.AbstractKeyValueStoreService;
import com.hvadoda1.keyvalstore.IKeyValueStoreClientConnection;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ConsistencyLevel;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.KeyValueStore;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.SystemException;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Value;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ValueMetadata;
import com.hvadoda1.keyvalstore.util.partitioning.IPartitioner;
import com.hvadoda1.keyvalstore.util.partitioning.IntegerByteOrderParitioner;

public class KeyValueStoreServiceThrift
		extends AbstractKeyValueStoreService<Integer, String, Node, Value, ConsistencyLevel, TException>
	implements KeyValueStore.Iface {

	public KeyValueStoreServiceThrift(Node node) throws IOException {
		super(node);
	}

	@Override
	protected TException createException(String message) {
		SystemException se = new SystemException();
		se.message = message;
		return se;
	}

	@Override
	protected TException createException(String message, Exception cause) {
		SystemException se = new SystemException();
		se.message = message;
		se.initCause(cause);
		return se;
	}

	@Override
	protected IPartitioner<Integer> createPartitioner() {
		return new IntegerByteOrderParitioner(0, 255, nodes.size());
	}

	@Override
	protected IKeyValueStoreClientConnection<Integer, String, Node, Value, ConsistencyLevel, TException> createConnection(
			Node node) {
		try {
			return new ClientConnectionThrift(node);
		} catch (TTransportException e) {
			throw new RuntimeException("Failed to connect to Node [" + node + "]");
		}
	}

	@Override
	protected Value createValue(String value) {
		Value v = new Value();
		v.setValue(value);
		v.setMeta(new ValueMetadata(System.currentTimeMillis()));
		return v;
	}

}