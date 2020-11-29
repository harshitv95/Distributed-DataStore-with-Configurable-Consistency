package com.hvadoda1.keyvalstore.rpc.thrift;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import com.hvadoda1.keyvalstore.IKeyValueStoreServiceStarter;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.KeyValueStore;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;

public class ThriftServiceStarter
		implements IKeyValueStoreServiceStarter<Node, TException, KeyValueStoreServiceThrift> {

	protected KeyValueStore.Processor<KeyValueStoreServiceThrift> processor;

//	public ThriftServerController(int port) throws TException {
//		this.port = port;
//		this.processor = new FileStore.Processor<ThriftServer>(createServerHandler());
//	}
	@Override
	public void start(int port) throws TException {
		try {
			processor = new KeyValueStore.Processor<>(createServerHandler(getIp(), port));
			TServer server = setupServer(port);
			server.serve();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}

	protected String getIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Failed to start server", e);
		}
	}

	protected TServer setupServer(int port) throws TTransportException {
		return new TSimpleServer(new TServer.Args(getTransport(port)).processor(processor));
	}

	protected TServerTransport getTransport(int port) throws TTransportException {
//		TSSLTransportParameters params = new TSSLTransportParameters();
//		params.setKeyStore(
//					ThriftConfig.getKeyStorePath(),
//					ThriftConfig.getKeyStorePassword(),
//					ThriftConfig.getKeyStoreManager(),
//					ThriftConfig.getKeyStoreType()
//				);
//		return TSSLTransportFactory.getServerSocket(port, 0, null, params);
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
}
