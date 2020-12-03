package com.hvadoda1.keyvalstore.frontend.cmdline;

import java.util.Map;

import org.apache.thrift.TException;

import com.hvadoda1.keyvalstore.frontend.AbstractFrontEndClient;
import com.hvadoda1.keyvalstore.frontend.ThriftFrontEndClient;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ConsistencyLevel;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Value;
import com.hvadoda1.keyvalstore.util.NodeUtils;

public class ThriftCmdLineClient extends AbstractCmdLineClient<Node, Value, ConsistencyLevel, TException> {

	public ThriftCmdLineClient(Map<String, String> params) {
		super(params);
	}

	@Override
	protected ConsistencyLevel[] getLevels() {
		return ConsistencyLevel.values();
	}

	@Override
	protected Node createNode(String ip, int port) {
		return new Node(ip, port);
	}

	@Override
	protected AbstractFrontEndClient<Integer, String, Node, Value, ConsistencyLevel, TException> getFrontEndClient(
			Node node) {
		try {
			return new ThriftFrontEndClient(node);
		} catch (TException e) {
			throw new RuntimeException("Could not connect to node [" + NodeUtils.nodeAddress(node) + "]", e);
		}
	}

}
