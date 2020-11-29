package com.hvadoda1.keyvalstore.util.partitioning;

public class IntegerByteOrderParitioner extends ByteOrderPartitioner<Integer> {

	private final int keyMin, keyMax;
//	private final List<N> nodes;
	private final int startKeys[];

	public IntegerByteOrderParitioner(int keyMin, int keyMax, int numNodes/*, List<N> nodes*/) {
		this.keyMin = keyMin;
		this.keyMax = keyMax;
//		this.nodes = new ArrayList<>(nodes);
//		Collections.sort(this.nodes, (node1, node2) -> nodeAddress(node1).compareTo(nodeAddress(node2)));
		this.startKeys = new int[numNodes];
		int incFactor = (keyMax - keyMin) / numNodes;
		for (int i = 0; i < keyMax; i++) {
			startKeys[i] = keyMin + (i * incFactor);
		}
	}

//	@Override
//	public N getResponsibleNode(Integer key) {
//		int idx = indexOfNode(key);
//		if (idx == -1 || idx >= nodes.size())
//			return null;
//		return nodes.get(idx);
//	}

	@Override
	public int indexOfResponsibleNode(Integer key) {
		return indexOfNode(key);
	}

	protected int indexOfNode(int key) {
		if (key < keyMin || key >= keyMax)
			return -1;
		int s = 0, e = startKeys.length - 1;
		int mid;
		while (s < e + 1) {
			mid = (s + e) / 2;
			if (startKeys[mid] == key)
				return mid;
			if (startKeys[mid] > key) {
				if (mid == 0)
					return -1;
				if (startKeys[mid - 1] <= key)
					return mid - 1;
				e = mid - 1;
				continue;
			}
			if (startKeys[mid] < key) {
				if (mid == startKeys.length - 1)
					return startKeys.length - 1;
				if (startKeys[mid + 1] > key)
					return mid;
				s = mid + 1;
				continue;
			}
		}
		return s;
	}

}
