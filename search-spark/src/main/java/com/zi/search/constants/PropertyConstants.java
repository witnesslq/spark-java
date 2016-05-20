package com.zi.search.constants;
/**
 * 常量类
 * @author 刘星
 *
 */
public class PropertyConstants {
  // ===================zookeeper
  public static final String ZOOKEEPER_CONNECT = "zookeeper.connect";
  // ===================spark
  public static final String SPARK_MASTER = "spark.master";
  public static final String SPARK_APP_NAME = "spark.app.name";
  public static final String SPARK_EXECUTOR_MEMORY = "spark.executor.memory";
  // ===================kafka
  public static final String KAFKA_GROUP = "kafka.group";
  public static final String KAFKA_TOPIC = "kafka.topic";
  public static final String KAFKA_ZOOKEEPER_TIMEOUT = "kafka.zookeeper.timeout";
  public static final String KAFKA_FETCH_MESSAGE_MAX_BYTES = "kafka.fetch.message.max.bytes";
  
  // ===================recognition
  public static final String RECOGNITION_LIMIT_DEFAULT = "recognition.limit.default";
  public static final Integer COUNT = 5000;

  // ===================recognition
  public static final String TABLENAME = "tableName";
  public static final String CF = "cf";
  public static final String INDEX = "index";
  public static final String TYPE= "type";
  
}
