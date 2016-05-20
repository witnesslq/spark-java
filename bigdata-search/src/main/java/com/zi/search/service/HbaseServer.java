package com.zi.search.service;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;

/**
 * hbase的数据的curd操作
 * 
 * @author liuxing
 *
 */
public interface HbaseServer {
	/**
	 * 数据插入
	 * 
	 * @param hbaseMap
	 *            mysq中的原始数据
	 */
	void insertData(Map<String, List<String>> hbaseMap);

	/**
	 * 通过rowkey删除数据
	 * 
	 * @param rowKey
	 *            rowkey
	 * @return 成功true，失败false
	 */
	boolean deleteData(String rowKey);

	/**
	 * 通过rowkey获取数据
	 * 
	 * @param rowKey
	 *            rowkey
	 * @return Result结果集合
	 */
	Result getData(String rowKey);
}
