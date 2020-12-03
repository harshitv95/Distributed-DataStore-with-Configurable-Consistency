package com.hvadoda1.keyvalstore.frontend.starter;

import com.hvadoda1.keyvalstore.frontend.cmdline.AbstractCmdLineClient;
import com.hvadoda1.keyvalstore.frontend.cmdline.ThriftCmdLineClient;
import com.hvadoda1.keyvalstore.util.CommonUtils;

public class StartKeyValueStoreFrontEnd {
	public static void main(String[] args) {
		AbstractCmdLineClient<?, ?, ?, ?> client = new ThriftCmdLineClient(CommonUtils.parseArgsMap(args));
		client.start();
	}
}
