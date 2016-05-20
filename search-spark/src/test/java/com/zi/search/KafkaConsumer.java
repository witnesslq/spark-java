package com.zi.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zi.search.utils.PropertyUtils;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class KafkaConsumer {
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
	 * @param topic
	 *            消息主题
	 */
	public KafkaConsumer(String topic) {
		this(1);
	}

	/**
	 * 实例化
	 * 
	 * @param topic
	 *            消息主题
	 * @param numThreads
	 *            线程数(该个数在程序启动后设定,不能动态修改)
	 */
	private KafkaConsumer(int numThreads) {
		props = new Properties();
		// zookeeper 配置
		props.put("zookeeper.connect", PropertyUtils.getSystemProperties("zookeeper.connect"));
		// group 代表一个消费组
		props.put("group.id", "content");
		// zk连接超时
		props.put("zookeeper.session.timeout.ms", "4000");
		String topic = PropertyUtils.getSystemProperties("kafka.topic");
		this.topic = topic;
		this.numThreads = numThreads;
	}

	void consume() {
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
			            
						System.out.println(
								Thread.currentThread().getName() 
								+ " - partition:" + msgAndMetadata.partition()
								+ " - message:"+new String(msgAndMetadata.message()));
						System.out.println();
					}
				}
			});
		}
	}

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
		new KafkaConsumer(1).consume();
	}
}
