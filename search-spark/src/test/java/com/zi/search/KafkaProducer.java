package com.zi.search;

import java.util.Properties;

import com.zi.search.utils.PropertyUtils;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducer {
	private static final String topic = PropertyUtils.getSystemProperties("kafka.topic");
	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("metadata.broker.list", PropertyUtils.getSystemProperties("kafka.broker"));
	    props.put("key.serializer.class", PropertyUtils.getSystemProperties("kafka.key.serializer"));
	    props.put("serializer.class", "kafka.serializer.StringEncoder");
	    props.put("partitioner.class", PropertyUtils.getSystemProperties("kafka.partitioner"));
	    props.put("request.required.acks", PropertyUtils.getSystemProperties("kafka.acks"));

		ProducerConfig config = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(config);
		for(int i = 1 ; i <= 1000000  ;i++){
			KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(topic, i+"", "xxx"+i); 
			producer.send(keyedMessage);
		}
		producer.close();
	}
}
