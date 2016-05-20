package com.zi.search;


import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * 自定义分区
 * 
 * @author liuxing
 *
 */
public class EventPartitioner<Face> implements Partitioner {
  public EventPartitioner(VerifiableProperties verifiableProperties) {}

  @Override
  public int partition(Object key, int partitionCount) {
    return Integer.valueOf((String) key) % partitionCount;
  }
}
