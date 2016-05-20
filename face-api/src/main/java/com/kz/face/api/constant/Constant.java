package com.kz.face.api.constant;

/**
 * 常量类
 * 
 * @author liuxing
 *
 */
public class Constant {
  /**
   * 人脸底库信息表前缀
   */
  public static String FACE_ = "face_";
  /**
   * 时间的格式化
   */
  public static String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
  ////////////// zk配置/////////////
  /**
   * kafka zk集群地址
   */
  public static String ZKCONNECT = "zookeeper.connect";

  ///////////// kafka生产配置//////////////
  /**
   * kafka集群地址
   */
  public static String KAFKABROKERS = "kafka.broker";
  /**
   * kafka topic
   */
  public static String KAFKATOPIC = "kafka.topic";
  /**
   * kafka自定义对象key
   */
  public static String KAFKAKEYSERIALIZER = "kafka.key.serializer";
  /**
   * kafka自定义对象
   */
  public static String KAFKASERIALIZER = "kafka.serializer";
  /**
   * kafka自定义分区
   */
  public static String KAFKAPARTITION = "kafka.partitioner";
  /**
   * kafka容错机制
   */
  public static String KAFKAACKS = "kafka.acks";

  ///////////// kafka消费配置//////////////
  /**
   * kafka消费组id
   */
  public static String KAFKAGROUP = "kafka.group";
  /**
   * kafka zk超时时间
   */
  public static String KAFKAZKTIMEOUT = "kafka.zookeeper.timeout";
  
  /////////////////hbase配置///////////////////
  /**
   * hhbase预分区个数
   */
  public static String HBASESPLITNUM = "hbase.splitnum";
  /**
   * hhbase列族
   */
  public static String HBASECFS= "hbase.cf";
  /**
   * hhbase元数据表
   */
  public static String HBASEMATETABLE = "hbase.matedata.table";
  /**
   * hhbase元数据表的列族
   */
  public static String HBASEMATETABLECF = "hbase.matedata.table.cf";
  /**
   * hhbase存放用户结果信息的表
   */
  public static String HBASERESULTTABLE = "hbase.face_result_info";
  /**
   * hhbase存放用户结果信息的表的列族
   */
  public static String HBASERESULTTABLECF = "hbase.face_result_info.cf";
  /**
   * hhbase中获取face_result_info结果超时时间
   */
  public static String HBASERESULTTABLETIMEOUT = "hbase.face_result_info.timeout";
  /**
   * hhbase中存放入库失败图片的表
   */
  public static String HBASEFAILURETABLE = "hbase.face_failure.table";
  /**
   * hhbase中存放入库失败图片的表的列族
   */
  public static String HBASEFAILURETABLECF = "hbase.face_failure.table.cf";
}
