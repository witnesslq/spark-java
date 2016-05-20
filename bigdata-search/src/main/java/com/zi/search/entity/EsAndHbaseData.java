package com.zi.search.entity;

import java.util.List;
import java.util.Map;
/**
 * es和hbase集合
 * @author 刘星
 *	
 */
public class EsAndHbaseData {
	 //存放索引数据
    Map<String, String> dataMap ;
    //存放hbase数据
    Map<String, List<String>> hbaseMap;
    
	public Map<String, String> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}
	public Map<String, List<String>> getHbaseMap() {
		return hbaseMap;
	}
	public void setHbaseMap(Map<String, List<String>> hbaseMap) {
		this.hbaseMap = hbaseMap;
	}
}
