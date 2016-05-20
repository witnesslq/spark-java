package com.kz.face.recognition.mq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kz.face.recognition.constants.PropertyConstants;
import com.kz.face.recognition.utils.HTableClientUtils;
import com.kz.face.recognition.utils.PropertyUtils;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConsumer {
  // 主题
  private String topic = null;
  // 消费者个数
  private int numThreads = 1;
  // 配置
  private Properties props;

  private ConsumerConnector consumer;
  private ExecutorService executor;

  public KafkaConsumer() {
    this(1);
  }

  /**
   * 实例化
   * 
   * @param numThreads 线程数(该个数在程序启动后设定,不能动态修改)
   */
  public KafkaConsumer(int numThreads) {
    props = new Properties();
    props.put("zookeeper.connect",
        PropertyUtils.getSystemProperties(PropertyConstants.ZOOKEEPER_CONNECT));
    props.put("group.id", PropertyUtils.getSystemProperties(PropertyConstants.KAFKA_GROUP));
    props.put("zookeeper.session.timeout.ms",
        PropertyUtils.getSystemProperties(PropertyConstants.KAFKA_ZOOKEEPER_TIMEOUT));
    props.put("fetch.message.max.bytes",
        PropertyUtils.getSystemProperties(PropertyConstants.KAFKA_FETCH_MESSAGE_MAX_BYTES));
    this.topic = PropertyUtils.getSystemProperties(PropertyConstants.KAFKA_TOPIC);
    this.numThreads = numThreads;
  }

  public void start() {
    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    // 一次从主题中获取的数据个数,跟线程数保持一致
    topicCountMap.put(topic, new Integer(numThreads));
    ConsumerConfig consumerConfig = new ConsumerConfig(props);
    consumer = Consumer.createJavaConsumerConnector(consumerConfig);
    executor = Executors.newFixedThreadPool(numThreads);
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
        consumer.createMessageStreams(topicCountMap);
    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
    // 开启多个线程
    for (final KafkaStream<byte[], byte[]> stream : streams) {
      executor.submit(new MessageProcessor(stream, consumer));
    }
  }

  public void shutdown() {
    if (executor != null) {
      executor.shutdown();
      executor = null;
    }
    if (consumer != null) {
      consumer.shutdown();
      consumer = null;
    }
  }
  
  public static void main(String[] args) {
    HTableClientUtils.createPool();
    new KafkaConsumer().start();
  }
}
