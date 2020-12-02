package com.hvadoda1.keyvalstore.starter;

import java.util.Map;

import com.hvadoda1.keyvalstore.Config;
import com.hvadoda1.keyvalstore.IKeyValueStoreServer;
import com.hvadoda1.keyvalstore.INode;

public interface IKeyValueStoreServiceStarter<N extends INode, Exc extends Exception, Serv extends IKeyValueStoreServer<?, ?, N, ?, ?, Exc>> {

	void start(int port) throws Exc;

	N createNode(String ip, int port);

	Serv createServerHandler(String ip, int port) throws Exc;

	Config initConfig(Map<String, String> args);

}
