package com.hvadoda1.keyvalstore.util.partitioning;

import com.hvadoda1.keyvalstore.INode;

public interface IPartitioner<K> {

	INode<K> getResponsibleNode(K key);

}
