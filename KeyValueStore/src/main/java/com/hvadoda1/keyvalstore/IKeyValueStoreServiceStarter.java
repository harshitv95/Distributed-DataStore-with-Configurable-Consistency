package com.hvadoda1.keyvalstore;

public interface IKeyValueStoreServiceStarter<N extends INode, Exc extends Exception, Serv extends IKeyValueStoreServer<?, ?, N, ?, ?, Exc>> {

	void start(int port) throws Exc;

	N createNode(String ip, int port);

	Serv createServerHandler(String ip, int port) throws Exc;

}
