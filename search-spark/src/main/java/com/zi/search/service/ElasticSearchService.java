package com.zi.search.service;

/**
 * 操作es的接口
 * 
 * @author liuxing
 *
 */
public interface ElasticSearchService {
	/**
	 * 单条插入数据
	 * 
	 * @param index
	 *            索引名
	 * @param type
	 *            类型名
	 * @param rowkey
	 *            hbase中的rowkey
	 * @param value
	 *            数据
	 */
	void indexRecord(String index, String type,String rowkey, String value);
}
