package com.kz.face.recognition.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.recognition.constants.PropertyConstants;

/**
 * HTablePool工具类，管理Htable实例
 * 
 * @author huanghaiyang 2016年1月18日
 */

public class HTableClientUtils {
  private static Connection connection;
  private static Admin hBaseAdmin;
  private static final Log logger = LogFactory.getLog(HTableClientUtils.class);

  public static void createPool() {
    logger.info(" init hbase pool ...");
    if (connection == null || hBaseAdmin == null) {
      Configuration configuration;
      configuration = HBaseConfiguration.create();
      configuration.set("hbase.zookeeper.quorum",
          PropertyUtils.getSystemProperties(PropertyConstants.ZOOKEEPER_CONNECT));
      // HTablePool提供了几种方式：ReusablePool，RoundRobinPool，ThreadLocalPool。
      // 使用ThreadLocal的Pool，这样多线程写入时分别取自己线程的Pool，这样互不影响，写入的效率也会比较高。
      try {
        connection = ConnectionFactory.createConnection(configuration);
        hBaseAdmin = connection.getAdmin();
      } catch (Exception e) {
        e.printStackTrace();
      }
      // 初始化一些htable
      try {
      } catch (Exception e) {
      }
    }

  }

  /**
   * @Title: getHTable @Description: 获取htable实例 @param @param tableName @param @return @return
   *         HTable 返回类型 @throws
   */
  public static Table getHTable(String tableName) {
    if (connection == null) {
      createPool();
    }
    Table table = null;
    try {
      table = connection.getTable(TableName.valueOf(tableName));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return table;
  }

  /**
   * @Title: closePool @Description: 销毁pool @param @return void 返回类型 @throws
   */
  public static void closePool() {
    CloseableUtils.close(connection);
    CloseableUtils.close(hBaseAdmin);
  }

  /**
   * 创建表
   */
  public static void createTable(HTableDescriptor table) {
    if (hBaseAdmin == null) {
      createPool();
    }
    try {
      hBaseAdmin.createTable(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void addColumnFamily(String tableName, String family) throws IOException {
    TableName tName = TableName.valueOf(tableName);
    Table table = getHTable(tableName);
    HTableDescriptor descriptor = new HTableDescriptor(table.getTableDescriptor());
    descriptor.addFamily(new HColumnDescriptor(Bytes.toBytes(family)));
    hBaseAdmin.disableTable(tName);
    hBaseAdmin.modifyTable(tName, descriptor);
    hBaseAdmin.enableTable(tName);
    table.close();
  }

  /**
   * 删除表
   */
  public static void dropTable(String tableName) {
    if (hBaseAdmin == null) {
      createPool();
    }
    try {
      TableName tName = TableName.valueOf(tableName);
      hBaseAdmin.disableTable(tName);
      hBaseAdmin.deleteTable(tName);
    } catch (MasterNotRunningException e) {
      e.printStackTrace();
    } catch (ZooKeeperConnectionException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("deprecation")
  public static void main(String[] args) {
    Table table = getHTable("group_test_1000W");
    Random  r=new Random();
    List<Put> list = new ArrayList<Put>();
    for (int i = 0; i < 20000000; i++) {
      Put put = new Put((String.format("%3d", r.nextInt(10)).replaceAll(" ", "0")+UUID.randomUUID().toString()).getBytes());
      put.add("attr".getBytes(), "attr1".getBytes(), "1111111111".getBytes());
      put.add("attr".getBytes(), "attr2".getBytes(), "222222222222".getBytes());
      put.add("feature".getBytes(), "feature".getBytes(), "33333333333333".getBytes());
      list.add(put);
      try {
        if(i%100000 == 0){
          table.put(list);
          list.clear();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
