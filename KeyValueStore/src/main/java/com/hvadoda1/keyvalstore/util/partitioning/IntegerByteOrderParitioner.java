package com.hvadoda1.keyvalstore.util.partitioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hvadoda1.keyvalstore.INode;

import static com.hvadoda1.keyvalstore.util.NodeUtils.nodeAddress;

public class IntegerByteOrderParitioner extends ByteOrderPartitioner<Integer> {

	private final int keyMin, keyMax;
	private final List<INode<Integer>> nodes;
	private final int startKeys[];

	public IntegerByteOrderParitioner(int keyMin, int keyMax, List<INode<Integer>> nodes) {
		this.keyMin = keyMin;
		this.keyMax = keyMax;
		this.nodes = new ArrayList<INode<Integer>>(nodes);
		Collections.sort(this.nodes, (node1, node2) -> nodeAddress(node1).compareTo(nodeAddress(node2)));
		this.startKeys = new int[nodes.size()];
		int incFactor = (keyMax - keyMin) / nodes.size();
		for (int i = 0; i < keyMax; i++) {
			startKeys[i] = keyMin + (i * incFactor);
		}
	}

	@Override
	public INode<Integer> getResponsibleNode(Integer key) {
		int idx = indexOfNode(key);
		if (idx == -1 || idx >= nodes.size())
			return null;
		return nodes.get(idx);
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
