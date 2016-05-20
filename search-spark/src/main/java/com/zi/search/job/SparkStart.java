package com.zi.search.job;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function0;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.zi.search.constants.PropertyConstants;
import com.zi.search.function.InsertHbaseAndEsFunction;
import com.zi.search.utils.ElasticUtils;
import com.zi.search.utils.HTableClientUtils;
import com.zi.search.utils.PropertyUtils;

import kafka.serializer.StringDecoder;
import scala.Tuple2;

public class SparkStart{
	private static String brokers = PropertyUtils.getSystemProperties("kafka.broker");
	private static String topics = PropertyUtils.getSystemProperties("kafka.topic");
	
	private static String tableName = PropertyUtils.getSystemProperties("hbase.tableName");
	private static String cf = PropertyUtils.getSystemProperties("hbase.cf");
	private static String num = PropertyUtils.getSystemProperties("hbase.regionNum");
	
	private static String index = PropertyUtils.getSystemProperties("elastic.index");
	private static String type = PropertyUtils.getSystemProperties("elastic.type");
	
	public static Map<String,Broadcast<String>> broadcastMap = null;

	@SuppressWarnings("deprecation")
	private static JavaStreamingContext createContext(String checkpointDirectory) {
		// start config
		SparkConf conf = new SparkConf();
	    conf.setMaster(PropertyUtils.getSystemProperties(PropertyConstants.SPARK_MASTER));
	    conf.setAppName(PropertyUtils.getSystemProperties(PropertyConstants.SPARK_APP_NAME));
	    conf.set("spark.executor.memory",
	        PropertyUtils.getSystemProperties(PropertyConstants.SPARK_EXECUTOR_MEMORY));
		JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(2000));
		
		// broadcast tableName、cf、index、type
		broadcastMap = new HashMap<String,Broadcast<String>>();
		final Broadcast<String> broadcastTableName = jssc.sparkContext().broadcast(tableName);
		final Broadcast<String> broadcastCF = jssc.sparkContext().broadcast(cf);
		final Broadcast<String> broadcastIndex = jssc.sparkContext().broadcast(index);
		final Broadcast<String> broadcastType = jssc.sparkContext().broadcast(type);
		broadcastMap.put(PropertyConstants.TABLENAME, broadcastTableName);
		broadcastMap.put(PropertyConstants.CF, broadcastCF);
		broadcastMap.put(PropertyConstants.INDEX, broadcastIndex);
		broadcastMap.put(PropertyConstants.TYPE, broadcastType);
		    
		// offerset insert checkpoint dir
//		jssc.checkpoint(checkpointDirectory);
		Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", brokers);
		kafkaParams.put("auto.offset.reset", "smallest");
		JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);

		// init env（hbase elastic）
		init();
		
		// if exist data
		//if (!jssc.sparkContext().emptyRDD().isEmpty())
		JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String call(Tuple2<String, String> tuple2) {
				return tuple2._2();
			}
		});

		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Iterable<String> call(String x) throws Exception {
				return Arrays.asList(x.split(" "));
			}
		});

		JavaPairDStream<String, Integer> wordCount = words.mapToPair(new PairFunction<String, String, Integer>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<String, Integer> call(String t) throws Exception {
				return new Tuple2<String, Integer>(t, 1);
			}
		});

		JavaPairDStream<String, Integer> wordCounts = wordCount.reduceByKey(new Function2<Integer, Integer, Integer>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1 + v2;
			}
		});
		
		// don't checkpoint every second
		// wordCounts.checkpoint(new Duration(10000));
		wordCounts.foreachRDD(new InsertHbaseAndEsFunction());
		
		return jssc;
	}
	/**
	 * 建立hbase表和elasticSearch索引
	 */
	public static void init() {
		// 1.建立hbase表
		if (!HTableClientUtils.tableExist(tableName)) {
			try {
				int splitNum = Integer.parseInt(num);
				byte[][] splitkeys = new byte[splitNum - 1][];
				for (int i = 1; i < splitNum; i++) {
					splitkeys[i - 1] = Bytes.toBytes(String.format("%3d", i).replaceAll(" ", "0"));
				}
				HTableClientUtils.createTable(tableName, new String[] { cf }, splitkeys);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 2.建立es索引
		ElasticUtils.createIndex(index, type);
	}
	
	public static void main(String[] args) {
		String checkpointDirectory = "./checkpoint/";
		Function0<JavaStreamingContext> createContextFunc = new Function0<JavaStreamingContext>() {
			private static final long serialVersionUID = 1L;

			@Override
			public JavaStreamingContext call() {
				return createContext(checkpointDirectory);
			}
		};
		JavaStreamingContext jssc = JavaStreamingContext.getOrCreate(checkpointDirectory, createContextFunc);
		jssc.start();
		jssc.awaitTermination();
	}
}
