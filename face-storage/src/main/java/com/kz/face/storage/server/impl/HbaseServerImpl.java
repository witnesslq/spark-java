package com.kz.face.storage.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.storage.server.HbaseServer;
import com.kz.face.storage.utils.CloseableUtils;
import com.kz.face.storage.utils.HTableClientUtils;
import com.kz.face.storage.utils.PropertiesUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;

public class HbaseServerImpl implements HbaseServer {
  private static final Log logger = LogFactory.getLog(HbaseServerImpl.class);
  static Random r = new Random();
  static String hbaseTable = PropertiesUtil.getValue("tableName");
  static String[] cfs = PropertiesUtil.getValue("cf").split(",");
  static int splitNum = Integer.parseInt(PropertiesUtil.getValue("regionNum"));

  static {
    logger.info("create hbase table...");
    // 如果表不存在,则建立(预分区)表
    try {
      if (!HTableClientUtils.tableExist(hbaseTable)) {
        logger.info("hbase table not exist createing...");
        byte[][] splitkeys = new byte[splitNum - 1][];
        for (int i = 1; i < splitNum; i++) {
          splitkeys[i - 1] = Bytes.toBytes(String.format("%3d", i).replaceAll(" ", "0"));
        }
        HTableClientUtils.createTable(hbaseTable, cfs, splitkeys);
      }
      logger.info("hbase table existed skip create...");
    } catch (IOException e) {
      logger.error("create hbase table failure...");
      e.printStackTrace();
    }
  }

  @Override
  public String insertData(FormDataContentDisposition fileDetail, InputStream fileContent) {
    logger.info("insert data into hbase table...fileDetail:" + fileDetail + "--->fileContent:"
        + fileContent);
    Table table = null;
    try {
      // 存在则直接将数据放入
      // 1.生产rowkey和准备数据
      String rowKey = String.format("%3d", r.nextInt(splitNum)).replaceAll(" ", "0") + UUID.randomUUID().toString();
      byte[] buffer = IOUtils.toByteArray(fileContent);
      // 2.将结果放到hbase中
      table = HTableClientUtils.getHTable(hbaseTable);
      Put put = new Put(Bytes.toBytes(rowKey));
      put.addColumn(Bytes.toBytes(cfs[0]), Bytes.toBytes("fileName"),
          Bytes.toBytes(fileDetail.getFileName()));
      put.addColumn(Bytes.toBytes(cfs[1]), Bytes.toBytes("fileContent"), buffer);
      table.put(put);
      return rowKey;
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("insert data to hbase table failure...");
      return null;
    } finally {
      CloseableUtils.close(table);
    }
  }

  @Override
  public boolean deleteData(String rowKey) {
    logger.info("delete data...rowkey:" + rowKey);
    Table table = HTableClientUtils.getHTable(hbaseTable);
    byte[] rowKeyByte = Bytes.toBytes(rowKey);
    try {
      // 如果不存在则false
      if (getData(rowKey).size() == 0) {
        return false;
      }
      Delete delete = new Delete(rowKeyByte);
      table.delete(delete);
      return true;
    } catch (Exception e) {
      logger.error("delete data failure...rowkey:" + rowKey);
      return false;
    } finally {
      CloseableUtils.close(table);
    }
  }

  @Override
  public Result getData(String rowKey) {
    logger.info("get data...rowkey:" + rowKey);
    Table table = HTableClientUtils.getHTable(hbaseTable);
    byte[] rowKeyByte = Bytes.toBytes(rowKey);
    try {
      Get get = new Get(rowKeyByte);
      return table.get(get);
    } catch (Exception e) {
      logger.error("get data failure...rowkey:" + rowKey);
      return null;
    } finally {
      CloseableUtils.close(table);
    }
  }
}
