package com.kz.face.storage.server;

import java.io.InputStream;

import org.apache.hadoop.hbase.client.Result;

import com.sun.jersey.core.header.FormDataContentDisposition;

/**
 * hbase的数据的curd操作
 * @author liuxing
 *
 */
public interface HbaseServer {
  /**
   * 往数据库表中插入数据
   * 
   * @param fileName 文件名称
   * @param fileContent 文件内容
   * @return 返回null表示插入失败,否则返回文件的ID
   */
  String insertData(FormDataContentDisposition fileDetail,InputStream fileContent);
  /**
   * 通过rowkey删除数据
   * 
   * @param rowKey rowkey
   * @return 成功true，失败false
   */
  boolean deleteData(String rowKey);
  /**
   * 通过rowkey获取数据
   * 
   * @param rowKey rowkey
   * @return Result结果集合
   */
  Result getData(String rowKey);
}
