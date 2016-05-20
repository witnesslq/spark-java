package com.kz.face.recognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import com.kz.face.recognition.constants.PropertyConstants;
import com.kz.face.recognition.function.FaceFeatureLoadFunction;
import com.kz.face.recognition.mq.KafkaConsumer;
import com.kz.face.recognition.pojo.Face;
import com.kz.face.recognition.pojo.GroupInfo;
import com.kz.face.recognition.service.GroupService;
import com.kz.face.recognition.service.impl.GroupServiceImpl;
import com.kz.face.recognition.utils.HTableClientUtils;
import com.kz.face.recognition.utils.PropertyUtils;

/**
 * 人脸识别 Context
 * 
 * @author huanghaiyang 2016年1月18日
 */
public class FaceRecognitionContext {
  private final static Log logger = LogFactory.getLog(FaceRecognitionContext.class);
  // groupservice
  private static GroupService groupService = new GroupServiceImpl();
  // kafka mq consumer
  private static KafkaConsumer consumer;
  // sparkContext
  private static JavaSparkContext sparkContext;
  // 加载内存的人脸地库数据，以人脸库库名作为key，cache的RDD为value
  private static Map<String, JavaRDD<Face>> cacheFeatures;
  // 删除的人脸列表，为以人脸库库名作为key , Broadcast对象为value ，Broadcast传播的是face id列表
  private static Map<String, Broadcast<List<String>>> deleteFaces;
  // 新增的人脸列表，为以人脸库库名作为key , Broadcast对象为value, Broadcast传播的是face对象列表
  private static Map<String, Broadcast<List<Face>>> addFaces;
  /**
   * 获取缓存数据的RDD
   * @return
   */
  public static Map<String, JavaRDD<Face>> getCacheFeatures() {
    return cacheFeatures;
  }
  /**
   * 获取sparkContext对象
   * @return JavaSparkContext
   */
  public static JavaSparkContext getSparkContext() {
    if(sparkContext == null){
      initAndStart();
    }
    return sparkContext;
  }
  /**
   * 获取删除的face
   * @return
   */
  public static Map<String, Broadcast<List<String>>> getDeleteFaces() {
    return deleteFaces;
  }
  /**
   * 获取增加的face
   * @return
   */
  public static Map<String, Broadcast<List<Face>>> getAddFaces() {
    return addFaces;
  }
  /**
   * 初始化，主要包含： 初始化sparkContext 加载并cache人脸库特征值数据 启动mq消费者接受消息
   */
  public static void initAndStart() {
    // 初始化hbase连接池
    HTableClientUtils.createPool();
    cacheFeatures = new HashMap<>();
    deleteFaces = new HashMap<>();
    addFaces = new HashMap<>();
    // 初始化sparkContext
    initSparkContext();
    // 加载并cache人脸库特征值数据
    loadFeature();
    // 启动mq消费者接受消息
    logger.info("start kafka consumer.");
    consumer = new KafkaConsumer();
    consumer.start();
  }

  /**
   * 销毁，主要包含： 关闭sparkContext、 移除cache人脸库特征值数据 、关闭mq消费者接受消息
   */
  public static void destroy() {
    if (sparkContext != null) {
      sparkContext.close();
    }
    // 清空cache
    sparkContext = null;
    cacheFeatures = null;
    deleteFaces = null;
    addFaces = null;
    // 关闭mq消费者
    if (consumer != null) {
      consumer.shutdown();
      consumer = null;
    }
    // 关闭hbase连接池
    HTableClientUtils.closePool();
  }

  /**
   * 初始化sparkContext
   */
  private static void initSparkContext() {
    logger.info("initSparkContext.");
    SparkConf conf = new SparkConf();
    conf.setMaster(PropertyUtils.getSystemProperties(PropertyConstants.SPARK_MASTER));
    conf.setAppName(PropertyUtils.getSystemProperties(PropertyConstants.SPARK_APP_NAME));
    conf.set("spark.executor.memory",
        PropertyUtils.getSystemProperties(PropertyConstants.SPARK_EXECUTOR_MEMORY));
    sparkContext = new JavaSparkContext(conf);
  }

  /**
   * 加载并cache人脸库特征值数据
   */
  private static void loadFeature() {
    // logger.info("get all groupInfos.");
    // List<GroupInfo> groups = groupService.getAllGroupInfo();
    // TODO 测试
    List<GroupInfo> groups = new ArrayList<>();
    GroupInfo groupInfo = new GroupInfo();
    groupInfo.setGroupName("group_test_1000W");
    groupInfo.setTableName("group_test_1000W");
    groupInfo.setCreateTime("2016-01-19 12:00:05");
    groups.add(groupInfo);
    // 遍历加载人脸底库
    for (GroupInfo g : groups) {
      logger.info("load featrue [" + g.getGroupName() + "]   tableName[" + g.getTableName() + "]");
      Configuration configuration = HBaseConfiguration.create();
      configuration.set("hbase.zookeeper.quorum",
          PropertyUtils.getSystemProperties(PropertyConstants.ZOOKEEPER_CONNECT));
      configuration.set(TableInputFormat.INPUT_TABLE, g.getTableName());
      Scan scan = new Scan();
      scan.addFamily(Bytes.toBytes("attr"));
      scan.addFamily(Bytes.toBytes("feature"));
      scan.setCacheBlocks(false);
      scan.setCaching(1000);
      try {
        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        String scanToString = Base64.encodeBytes(proto.toByteArray());
        configuration.set(TableInputFormat.SCAN, scanToString);
      } catch (IOException e) {
        e.printStackTrace();
      }
      JavaPairRDD<ImmutableBytesWritable, Result> rdd = sparkContext.newAPIHadoopRDD(configuration,
          TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
      JavaRDD<Face> groupRdd = rdd.map(new FaceFeatureLoadFunction());
      // 加载到内存
      logger.info("cache featrue [" + g.getGroupName() + "]   tableName[" + g.getTableName() + "]");
      JavaRDD<Face> cachedGroup = groupRdd.cache();
      long conunt = cachedGroup.count();
      logger.info("cache featrue [" + g.getGroupName() + "]   tableName[" + g.getTableName() + "] : "+ conunt);
      cacheFeatures.put(g.getGroupName(), cachedGroup);
      // 创建Broadcast
      logger.info("create deleteBroadcast [" + g.getGroupName() + "]   tableName["
          + g.getTableName() + "]");
      List<String> deleteList = new ArrayList<>();
      Broadcast<List<String>> deleteBroadcast = sparkContext.broadcast(deleteList);
      deleteFaces.put(g.getGroupName(), deleteBroadcast);
      logger.info(
          "create addBroadcast [" + g.getGroupName() + "]   tableName[" + g.getTableName() + "]");
      List<Face> addList = new ArrayList<>();
      Broadcast<List<Face>> addBroadcast = sparkContext.broadcast(addList);
      addFaces.put(g.getGroupName(), addBroadcast);
    }
  }
}
