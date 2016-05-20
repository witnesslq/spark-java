package com.zi.search.service;

import java.util.List;
import java.util.Map;

/**
 * 操作es的接口
 * 
 * @author liuxing
 *
 */
public interface SearchService {
	/**
	 * 初始化连接
	 */
	void init();

	/**
	 * 创建索引
	 * 
	 * @param index
	 *            索引名称
	 */
	boolean createIndex(String index);

	/**
	 * 创建映射
	 * 
	 * @param index
	 *            索引名
	 * @param type
	 *            类型名
	 */
	void createMapping(String index, String type);
	/**
	 * 插入数据
	 * 
	 * @param index
	 *            索引名
	 * @param type
	 *            类型名
	 * @param dataList
	 * 			  数据,必须与建立的映射格式对应,否则数据无法插入
	 */
	void createData(String index, String type ,List<String> dataList);
	/**
	 * 插入数据(批量)
	 * 
	 * @param index
	 *            索引名
	 * @param type
	 *            类型名
	 * @param dataList
	 * 			  数据,必须与建立的映射格式对应,否则数据无法插入
	 */
	void createDataBatch(String index, String type ,List<String> dataList);
	/**
	 * 插入数据(批量)
	 * 
	 * @param index
	 *            索引名
	 * @param type
	 *            类型名
	 * @param dataMap
	 * 			  数据,必须与建立的映射格式对应,否则数据无法插入
	 */
	void createDataBatch(String index, String type ,Map<String,String> dataMap);

	/**
	 * 查询索引
	 * 
	 * @param index
	 *            索引名称
	 * @param type
	 *            类型名称
	 * @param field
	 *            查询的字段
	 * @param start
	 *            开始位置
	 * @param size
	 *            获取个数
	 * @return String返回json数据
	 */
	String queryData(String index, String type, String field, int start, int size);
}
