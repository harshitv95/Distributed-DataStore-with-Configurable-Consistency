package com.hvadoda1.keyvalstore;

public interface IConsistencyLevel {

	int minWriteReplicas();

	int maxWriteReplicas();

	int readReplicas();

	boolean shouldSaveMissedUpdates();

	String name();

}
