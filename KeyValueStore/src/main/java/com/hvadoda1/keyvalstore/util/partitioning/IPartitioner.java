package com.hvadoda1.keyvalstore.util.partitioning;

public interface IPartitioner<K> {

//	N getResponsibleNode(K key);

	int indexOfResponsibleNode(K key);

}
