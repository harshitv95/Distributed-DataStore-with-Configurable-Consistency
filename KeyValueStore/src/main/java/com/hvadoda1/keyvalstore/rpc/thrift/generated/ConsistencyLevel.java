package com.hvadoda1.keyvalstore.rpc.thrift.generated;

import com.hvadoda1.keyvalstore.Config;
import com.hvadoda1.keyvalstore.IConsistencyLevel;

/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 * 
 * @generated
 */

@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-11-26")
public enum ConsistencyLevel implements org.apache.thrift.TEnum, IConsistencyLevel {
	ONE(0, 1, 1, 1), QUORUM(1, -1, -1, -1);

	private final int value;
	private final int minWriteReplicas, numReadReplicas, maxWriteReplicas;

	private ConsistencyLevel(int value, int minWriteReplicas, int maxWriteReplicas, int numReadReplicas) {
		this.value = value;

		this.minWriteReplicas = minWriteReplicas;
		this.maxWriteReplicas = maxWriteReplicas;
		this.numReadReplicas = numReadReplicas;
	}

	/**
	 * Get the integer value of this enum value, as defined in the Thrift IDL.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Find a the enum type by its integer value, as defined in the Thrift IDL.
	 * 
	 * @return null if the value is not found.
	 */
	@org.apache.thrift.annotation.Nullable
	public static ConsistencyLevel findByValue(int value) {
		switch (value) {
		case 0:
			return ONE;
		case 1:
			return QUORUM;
		default:
			return null;
		}
	}

	@Override
	public int maxWriteReplicas() {
		return maxWriteReplicas < 0 ? Config.numReplicas() : maxWriteReplicas;
	}

	@Override
	public int minWriteReplicas() {
		if (minWriteReplicas < 0)
			return 1 + (Config.numReplicas() / 2);
		return minWriteReplicas;
	}

	@Override
	public int readReplicas() {
		if (numReadReplicas < 0)
			return 1 + (Config.numReplicas() / 2);
		return numReadReplicas;
	}
}
