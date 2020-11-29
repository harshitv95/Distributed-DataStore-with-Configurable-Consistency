package com.hvadoda1.keyvalstore;

public interface IConsistencyLevel {

	int numWriteReplicas();
	
	int numReadReplicas();

}
