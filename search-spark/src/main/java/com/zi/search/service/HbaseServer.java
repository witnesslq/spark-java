package com.zi.search.service;

/**
 * hbase的数据的curd操作
 * 
 * @author liuxing
 *
 */
public interface HbaseServer {
	/**
	 * 获取连接
	 * @param tableName
	 */
	void getTable(String tableName);
	/**
	 * 插入数据
	 * @param cf 列族
	 * @param rowkey 行建
	 * @param qual 列
	 * @param value 值
	 */
	void insertData(String cf,String rowkey ,String qual,String value);
	/**
	 * 关闭连接
	 */
	void closeTable();
}
