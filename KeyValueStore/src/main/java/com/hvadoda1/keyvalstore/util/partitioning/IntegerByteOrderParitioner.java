package com.hvadoda1.keyvalstore.util.partitioning;

import java.util.Arrays;

import com.hvadoda1.keyvalstore.util.Logger;

public class IntegerByteOrderParitioner extends ByteOrderPartitioner<Integer> {

	private final int keyMin, keyMax;
	private final int startKeys[];

	public IntegerByteOrderParitioner(int keyMin, int keyMax, int numNodes) {
		this(keyMin, keyMax, createStartKeys(keyMin, keyMax, numNodes));
	}

	protected static int[] createStartKeys(int keyMin, int keyMax, int numNodes) {
		int[] startKeys = new int[numNodes];
		int incFactor = (keyMax + 1 - keyMin) / numNodes;
		for (int i = 0; i < numNodes; i++)
			startKeys[i] = keyMin + (i * incFactor);
		return startKeys;
	}

	public IntegerByteOrderParitioner(int keyMin, int keyMax, int[] startKeys) {
		this.keyMin = keyMin;
		this.keyMax = keyMax;
		this.startKeys = startKeys;
		Logger.debugHigh("Initialized IntByteOrdPart: {min: " + keyMin + ", max: " + keyMax + ", startKeys: "
				+ Arrays.toString(startKeys) + "}");
	}

	@Override
	public int indexOfResponsibleNode(Integer key) {
		return indexOfNode(key);
	}

	protected int indexOfNode(int key) {
		if (key < keyMin || key > keyMax)
			return -1;
		int s = 0, e = startKeys.length - 1;
		int mid;
		while (s < e + 1) {
			mid = (s + e) / 2;

			if (startKeys[mid] == key)
				return mid;
			if (startKeys[s] == key)
				return s;
			if (startKeys[e] == key)
				return e;

			s++;
			e--;

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
