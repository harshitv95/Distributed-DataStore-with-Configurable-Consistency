package com.hvadoda1.keyvalstore.rpc.thrift;

import static com.hvadoda1.keyvalstore.util.NodeUtils.ipAddr;

import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import com.hvadoda1.keyvalstore.Config;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.KeyValueStore;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.starter.IKeyValueStoreServiceStarter;
import com.hvadoda1.keyvalstore.util.Logger;

public class ThriftServiceStarter
		implements IKeyValueStoreServiceStarter<Node, TException, KeyValueStoreServiceThrift> {

	protected KeyValueStore.Processor<KeyValueStoreServiceThrift> processor;

	@Override
	public void start(int port) throws TException {
		String ip = ipAddr();
		Logger.info("Starting Thrift server at address:\n" + ip + ":" + port);
		try {
			processor = new KeyValueStore.Processor<>(createServerHandler(ip, port));
			TServer server = setupServer(port);
			server.serve();
		} catch (Exception e) {
			throw new RuntimeException("Failed to start KeyValueStore (Thrift)", e);
		}
	}

	protected TServer setupServer(int port) throws TTransportException {
		return new TThreadPoolServer(new TThreadPoolServer.Args(getTransport(port)).processor(processor));
	}

	protected TServerTransport getTransport(int port) throws TTransportException {
		return new TServerSocket(port);
	}

	@Override
	public Node createNode(String ip, int port) {
		return new Node(ip, port);
	}

	@Override
	public KeyValueStoreServiceThrift createServerHandler(String ip, int port) throws TException {
		try {
			return new KeyValueStoreServiceThrift(createNode(ip, port));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to start KeyValueStore listener service");
		}
	}

	@Override
	public Config initConfig(Map<String, String> args) {
		return new Config(args);
	}
}
