package com.hvadoda1.keyvalstore.rpc.thrift;

import java.util.Objects;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.hvadoda1.keyvalstore.IKeyValueStoreClientConnection;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ConsistencyLevel;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.KeyValueStore;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Value;
import com.hvadoda1.keyvalstore.util.Logger;
import com.hvadoda1.keyvalstore.util.NodeUtils;

public class ClientConnectionThrift
		implements IKeyValueStoreClientConnection<Integer, String, Node, Value, ConsistencyLevel, TException> {

	protected final Node node;
	protected TTransport transport;
	protected final ClientThrift client;

	public ClientConnectionThrift(Node node) throws TTransportException {
		this.node = Objects.requireNonNull(node, "Node cannot be null; Failed to connect to neighbor");

		client = new ClientThrift(node, new KeyValueStore.Client(setupTransport()));
	}

	@Override
	public ClientThrift getClient() {
		return client;
	}

	protected TProtocol setupTransport() throws TTransportException {
		transport = new TSocket(node.getIp(), node.getPort(), 5000);
		if (!transport.isOpen())
			transport.open();
		return new TBinaryProtocol(transport);
	}

	@Override
	public void close() {
		try {
			Logger.debugHigh("Closing connection to [" + NodeUtils.nodeAddress(node) + "]");
			transport.close();
		} catch (Exception e) {
			Logger.error("Exception while closing connection to client [" + NodeUtils.nodeAddress(node) + "]", e);
		}
	}

	@Override
	public boolean isOpen() {
		return transport.isOpen();
	}

}
