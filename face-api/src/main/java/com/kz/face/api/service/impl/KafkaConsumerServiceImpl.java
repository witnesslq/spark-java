package com.kz.face.api.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kz.face.api.constant.Constant;
import com.kz.face.api.service.KafkaConsumerService;
import com.kz.face.api.utils.KryoSerializerUtils;
import com.kz.face.api.utils.PropertiesUtil;
import com.kz.face.pojo.Event;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class KafkaConsumerServiceImpl implements KafkaConsumerService {
  private static final Log logger = LogFactory.getLog(KafkaConsumerServiceImpl.class);
  // 主题
  private String topic = "";
  // 消费者个数
  private int numThreads = 1;
  // 配置
  private Properties props;
  // 消费者
  private ConsumerConnector consumer;
  // 线程管理
  private ExecutorService executor;

  /**
   * 实例化
   * 
   * @param topic 消息主题
   */
  public KafkaConsumerServiceImpl(String topic) {
    this(1);
  }

  /**
   * 实例化
   * 
   * @param topic 消息主题
   * @param numThreads 线程数(该个数在程序启动后设定,不能动态修改)
   */
  public KafkaConsumerServiceImpl(int numThreads) {
    logger.info(new Date()+" new KafkaConsumerServiceImpl() with "+numThreads+" thread");
    props = new Properties();
    props.put("zookeeper.connect", PropertiesUtil.getValue(Constant.ZKCONNECT));
    props.put("group.id", PropertiesUtil.getValue(Constant.KAFKAGROUP));
    props.put("zookeeper.session.timeout.ms", PropertiesUtil.getValue(Constant.KAFKAZKTIMEOUT));
    String topic = PropertiesUtil.getValue(Constant.KAFKATOPIC);
    this.topic = topic;
    this.numThreads = numThreads;
  }

  @Override
  public void consume() {
    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    
    // 一次从主题中获取的数据个数,跟线程数保持一致
    topicCountMap.put(topic, new Integer(numThreads));
    
    // 加载配置获取链接,得到topic中的
    ConsumerConfig consumerConfig = new ConsumerConfig(props);
    consumer = Consumer.createJavaConsumerConnector(consumerConfig);
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
    List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
    
    // 开启多个线程
    executor = Executors.newFixedThreadPool(numThreads);
    for (final KafkaStream<byte[], byte[]> stream : streams) {
      executor.submit(new Runnable() {
        public void run() {
          for (MessageAndMetadata<byte[], byte[]> msgAndMetadata : stream) {
            // 实时更新offset保证进程宕后，不会重复消费(同一个group组中)
            consumer.commitOffsets();
            byte[] faceBuffer = msgAndMetadata.message();
            Event face = KryoSerializerUtils.deserialization(faceBuffer, Event.class);

            // TODO 人脸比对业务...

            System.out.println(
                Thread.currentThread().getName() + " - partition:" + msgAndMetadata.partition());
            System.out.println("name:" + face.getSession_id());
            System.out.println();
          }
        }
      });
    }

  }
  
  @Override
  public void shutdown() {
    // 关闭线程
    if (executor != null) {
      executor.shutdown();
      executor = null;
    }
    // 关闭消费
    if (consumer != null) {
      consumer.shutdown();
      consumer = null;
    }
  }

  public static void main(String[] args) {
    new KafkaConsumerServiceImpl(1).consume();
  }
}
