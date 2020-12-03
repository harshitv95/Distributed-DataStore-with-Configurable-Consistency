package com.hvadoda1.keyvalstore.frontend.cmdline;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.hvadoda1.keyvalstore.IConsistencyLevel;
import com.hvadoda1.keyvalstore.INode;
import com.hvadoda1.keyvalstore.IValue;
import com.hvadoda1.keyvalstore.frontend.AbstractFrontEndClient;

public abstract class AbstractCmdLineClient<N extends INode, Val extends IValue<String>, Con extends IConsistencyLevel, Ex extends Exception> {

	@SuppressWarnings("serial")
	List<CmdLineAction> actions = new ArrayList<>() {
		{
//			add(new CmdLineAction("get", actionTitle, task));
		}
	};

	final static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public AbstractCmdLineClient() {

	}

	public void start() {
		new CmdLineAction("main", new ArrayList<>() {
			private static final long serialVersionUID = 1L;
			{
				add(new GetInput<Integer>("action", "Select any one from:\n1 GET\n2 PUT", str -> {
					try {
						int n = Integer.parseInt(str);
						if (n < 1 || n > 2)
							throw new RuntimeException("Enter a valid number among 1 and 2");
						return n;
					} catch (NumberFormatException e) {
						throw new RuntimeException(str + " is not a valid number");
					}
				}));
			}
		}, inputs -> {
			clientActions.get(((int) inputs.get("action")) - 1).run();
			return false;
		}).run();
	}

	protected static class GetInput<T> implements Supplier<T> {
		protected final String name, dispText;
		protected final Function<String, T> transform;

		public GetInput(String name, String dispText, Function<String, T> transform) {
			this.name = name;
			this.dispText = dispText;
			this.transform = transform;
		}

		public String name() {
			return name;
		}

		@Override
		public T get() {
			while (true) {
				System.out.println(dispText);
				try {
					return transform.apply(br.readLine());
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}

	}

	protected static class CmdLineAction implements Runnable {

		protected final String actionName;
		protected final Function<Map<String, Object>, Boolean> task;
		protected final List<GetInput<?>> inputGetters;

		public CmdLineAction(String actionName, List<GetInput<?>> inputGetters,
				Function<Map<String, Object>, Boolean> task) {
			this.actionName = actionName;
			this.inputGetters = inputGetters;
			this.task = task;
		}

		@Override
		public void run() {
			if (task != null) {
				try {
					while (!task.apply(inputs()))
						;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}

		protected Map<String, Object> inputs() {
			Map<String, Object> inputs = new HashMap<>();
			for (GetInput<?> getIn : inputGetters)
				inputs.put(getIn.name(), getIn.get());
			return inputs;
		}

	}

	final GetInput<Con> getConsistencyLevel = new GetInput<>("level",
			"Select any one from:\n" + consistencyLevelPrompts(), str -> {
				Con[] levels = getLevels();
				try {
					int n = Integer.parseInt(str);
					if (n < 1 || n > levels.length)
						throw new RuntimeException("Enter a valid number between 1 and " + levels.length);
					return levels[n];
				} catch (NumberFormatException e) {
					throw new RuntimeException(str + " is not a valid number");
				}
			});

	final GetInput<N> getNode = new GetInput<N>("node", "Enter address (ip:port) of coordinator node to connect:",
			str -> {
				String spl[] = str.split(":");
				if (spl.length != 2)
					throw new RuntimeException(str + " is not a valid IP:port string");
				int port;
				try {
					port = Integer.parseInt(spl[1]);
					if (port < 0 || port > 65535)
						throw new RuntimeException(spl[1] + " is not a valid port number");
				} catch (NumberFormatException e) {
					throw new RuntimeException(spl[1] + " is not a valid port number");
				}
				return createNode(spl[0], port);
			});

	@SuppressWarnings("unchecked")
	List<CmdLineAction> clientActions = new ArrayList<>() {
		private static final long serialVersionUID = 1L;

		{
			add(new CmdLineAction("get", new ArrayList<>() {
				private static final long serialVersionUID = 1L;

				{
					add(new GetInput<Integer>("key", "Key:", (str) -> {
						try {
							int n = Integer.parseInt(str);
							return n;
						} catch (NumberFormatException e) {
							throw new RuntimeException(str + " is not a valid number");
						}
					}));

					add(getConsistencyLevel);

					add(getNode);

				}
			}, params -> {
				try (AbstractFrontEndClient<Integer, String, N, Val, Con, Ex> client = getFrontEndClient(
						(N) params.get("node"));) {
					System.out.println("Value: " + client.get((int) params.get("key"), (Con) params.get("level")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}));

			add(new CmdLineAction("put", new ArrayList<>() {
				private static final long serialVersionUID = 1L;

				{
					add(new GetInput<Integer>("key", "Key:", (str) -> {
						try {
							int n = Integer.parseInt(str);
							return n;
						} catch (NumberFormatException e) {
							throw new RuntimeException(str + " is not a valid number");
						}
					}));

					add(new GetInput<String>("value", "Value:", str -> str));

					add(getConsistencyLevel);

					add(getNode);

				}
			}, params -> {
				try (AbstractFrontEndClient<Integer, String, N, Val, Con, Ex> client = getFrontEndClient(
						(N) params.get("node"));) {
					client.put((int) params.get("key"), (String) params.get("value"), (Con) params.get("level"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}));

		}
	};

	protected String consistencyLevelPrompts() {
		IConsistencyLevel[] levels = getLevels();
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (IConsistencyLevel level : levels)
			sb.append(i++).append(" ").append(level.name()).append(System.lineSeparator());
		return sb.toString();
	}

	protected abstract Con[] getLevels();

	protected abstract N createNode(String ip, int port);

	protected abstract AbstractFrontEndClient<Integer, String, N, Val, Con, Ex> getFrontEndClient(N node);

}
