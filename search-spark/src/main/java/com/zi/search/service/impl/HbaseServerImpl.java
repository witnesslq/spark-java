package com.zi.search.service.impl;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.zi.search.service.HbaseServer;
import com.zi.search.utils.HTableClientUtils;
/**
 * hbase的实现类
 * @author liuxing
 *
 */
public class HbaseServerImpl implements HbaseServer {
	private static final Log logger = LogFactory.getLog(HbaseServerImpl.class);
	private Table table ;
	
	@Override
	public void getTable(String tableName) {
		if(tableName!=null && !"".equals(tableName)){
			table = HTableClientUtils.getHTable(tableName);
		}else{
			logger.error("tableName can not empty");
		}
	}
	@Override
	public void insertData(String cf, String rowkey, String qual, String value) {
		try {
			Put put = new Put(Bytes.toBytes(rowkey));
			put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(qual), Bytes.toBytes(value));
			table.put(put);
		} catch (IOException e) {
			logger.error("htable insert data error");
			e.printStackTrace();
		}
	}
	@Override
	public void closeTable() {
		if(table!=null){
			try {
				table.close();
			} catch (IOException e) {
				logger.error("htable close error");
				e.printStackTrace();
			}
		}
	}
}
