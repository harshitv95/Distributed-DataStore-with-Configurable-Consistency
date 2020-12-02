package com.hvadoda1.keyvalstore.frontend.starter;

import com.hvadoda1.keyvalstore.frontend.cmdline.AbstractCmdLineClient;
import com.hvadoda1.keyvalstore.frontend.cmdline.ThriftCmdLineClient;

public class StartKeyValueStoreFrontEnd {
	public static void main(String[] args) {
		AbstractCmdLineClient<?, ?, ?, ?> client = new ThriftCmdLineClient();
		client.start();
	}
}
