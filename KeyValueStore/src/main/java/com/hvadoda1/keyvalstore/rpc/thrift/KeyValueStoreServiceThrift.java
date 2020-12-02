package com.hvadoda1.keyvalstore.rpc.thrift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.hvadoda1.keyvalstore.AbstractKeyValueStoreService;
import com.hvadoda1.keyvalstore.Config;
import com.hvadoda1.keyvalstore.IKeyValueStoreClientConnection;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ConsistencyLevel;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.KeyValueStore;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Node;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.SystemException;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.Value;
import com.hvadoda1.keyvalstore.rpc.thrift.generated.ValueMetadata;
import com.hvadoda1.keyvalstore.util.FileUtils;
import com.hvadoda1.keyvalstore.util.partitioning.IPartitioner;
import com.hvadoda1.keyvalstore.util.partitioning.IntegerByteOrderParitioner;

public class KeyValueStoreServiceThrift
		extends AbstractKeyValueStoreService<Integer, String, Node, Value, ConsistencyLevel, TException>
		implements KeyValueStore.Iface {

	protected static int KEY_MIN = 0, KEY_MAX = 255;
	protected static int[] START_KEYS = null;

	public KeyValueStoreServiceThrift(Node node) throws IOException {
		super(node);

		if (recovered && Config.args().containsKey("range")) {
			try {
				readRangeFromFile(new File(Config.getArg("range")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void readRangeFromFile(File file) throws FileNotFoundException, IOException {
		try (BufferedReader br = FileUtils.fileReader(file)) {
			String line1 = br.readLine();
			if (line1 == null)
				return;
			String line2 = br.readLine();
			if (line2 == null)
				return;
			String spl1[] = line1.split(","), spl2[] = line2.split(",");
			if (spl2.length != nodes.size())
				return;
			START_KEYS = new int[spl2.length];
			KEY_MIN = Integer.parseInt(spl1[0]);
			KEY_MIN = Integer.parseInt(spl1[1]);
			int i = 0;
			for (String s : spl2)
				START_KEYS[i++] = Integer.parseInt(s);
		}
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

	@Override
	public Node getRemoteNode() {
		return this.node;
	}

	@Override
	protected Node createNode(String ip, int port) {
		return new Node(ip, port);
	}

}