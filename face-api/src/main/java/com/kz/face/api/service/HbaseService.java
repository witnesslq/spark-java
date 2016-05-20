package com.kz.face.api.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;

public interface HbaseService {
  /**
   * 插入一条数据
   * 
   * @param tableName 表名
   * @param rowkey 行健
   * @param cf 列族
   * @param colnum 列
   * @param value 值
   * @throws IOException
   */
  void writeRow(String tableName, String rowkey, String cf, String colnum, String value)
      throws IOException;

  /**
   * 插入一条数据(往一个cf中插入多个colnum)
   * 
   * @param tableName 表名
   * @param rowkey 行健
   * @param cf 列族
   * @param colnums 列
   * @param values 值
   * @throws IOException
   */
  void writeRow(String tableName, String rowkey, String cf, String[] columns, String[] values)
      throws IOException;

  /**
   * 根据rowkeys删除多行记录
   * 
   * @param tableName 表名
   * @param rowkeys 行健
   * @throws IOException
   */
  void deleteRows(String tableName, String... rowkeys) throws IOException;

  /**
   * 根据rowkey查询一行记录
   * 
   * @param tableName 表名
   * @param rowkey 行健
   * @return 集合数据
   * @throws IOException
   */
  Result selectRow(String tableName, String rowkey) throws IOException;

  /**
   * 根据value查询一行记录
   * 
   * @param tableName 表名
   * @param value 行健
   * @return 集合数据
   * @throws IOException
   */
  Result selectByValue(String tableName, String value) throws IOException;

  /**
   * 根据rowkeys查询多行记录
   * 
   * @param tableName 表名
   * @param rowkeys 行健
   * @return 集合数据
   * @throws IOException
   */
  Result[] selectRows(String tableName, String... rowkeys) throws IOException;

  /**
   * hbase分页
   * 
   * @param tableName 表名
   * @param start_id 开始行健
   * @param size 获取多少条
   * @param filter 过滤字段
   * @return 集合数据
   * @throws IOException
   */
  Result[] selectRows(String tableName, String start_id, int size, String filter)
      throws IOException;

  /**
   * 查询表中的所有记录
   * 
   * @param tableName 表名
   * @return 数据集合
   * @throws IOException
   */
  Result[] selectAllRow(String tableName) throws IOException;

  /**
   * 通过value删除表中的记录
   * 
   * @param tableName 表名
   * @param value 值
   * @return 集合数据
   * @throws IOException
   */
  void deleteByValue(String tableName, String value) throws IOException;
  
  /**
   * 修改数据
   * 
   * @param tableName 表名
   * @param rowkey 行健
   * @param cf 列族
   * @param colnum 列
   * @param value 值
   * @throws IOException
   */
  void update(String tableName, String rowkey ,String cf ,String colnum ,String value) throws IOException;

  /**
   * 将hbase中某一列族中的数据封装到一个map集合 所有的map集合数据放到list中返回
   * 
   * @param results hbase中的数据集
   * @param cf hbase中的列族
   * @return
   */
  List<Map<String, String>> packRow2Map(Result[] results, String cf);

  /**
   * 将hbase表中某一列族中的数据封装到一个ResponseResult 所有的ResponseResult数据放到list中返回
   * 
   * @param table 表名
   * @param results hbase中的数据集
   * @param cf hbase中的列族
   * @return
   */
  List<Object> packRow2Result(String table, Result[] results, String cf);
}
