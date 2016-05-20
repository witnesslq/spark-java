package com.zi.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.zi.search.service.HbaseServer;
import com.zi.search.utils.CloseableUtils;
import com.zi.search.utils.HTableClientUtils;
import com.zi.search.utils.PropertyUtils;
/**
 * hbase的实现类
 * @author liuxing
 *
 */
public class HbaseServerImpl implements HbaseServer {
	private static final Log logger = LogFactory.getLog(HbaseServerImpl.class);
	static Random r = new Random();
	static String hbaseTable = PropertyUtils.getSystemProperties("hbase.tableName");
	static String[] cfs = PropertyUtils.getSystemProperties("hbase.cf").split(",");
	static int splitNum = Integer.parseInt(PropertyUtils.getSystemProperties("hbase.regionNum"));

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
		}
	}
	@Override
	public void insertData(Map<String, List<String>> hbaseMap) {
		Table table = null;
		try {
			// 1.需要插入到hbase的列和列族
            String [] colnums = PropertyUtils.getSystemProperties("mysql.colnum").split(",");
            String cf = PropertyUtils.getSystemProperties("hbase.cf");
			// 2.将结果放到hbase中
			table = HTableClientUtils.getHTable(hbaseTable);
			Iterator<Entry<String, List<String>>>  iterator = hbaseMap.entrySet().iterator();
			// 3.put集合
			List<Put> list = new ArrayList<Put>();
			// 4.迭代集合
			while (iterator.hasNext()) {
				Entry<String, List<String>> entry = iterator.next();
				String rowKey = entry.getKey();
				List<String> colValList = entry.getValue();
				Put put = new Put(Bytes.toBytes(rowKey));
				
				int size = colValList.size();
				if(size !=0 && size == colnums.length){
					for(int i = 0 ; i < size ; i++){
						put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(colnums[i]), Bytes.toBytes(StringUtils.isBlank(colValList.get(i))?"":colValList.get(i)));
					}
				}
				list.add(put);
			}
			table.put(list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("insert data to hbase table failure...");
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
	public static void main(String[] args) {
		HbaseServerImpl hbaseServerImpl = new HbaseServerImpl();
		Result result = 
				hbaseServerImpl.getData("0096dc3589c-c358-45f3-8da3-92662ecd7fa6");
		String [] colnums = PropertyUtils.getSystemProperties("mysql.colnum").split(",");
		for(int i = 0 ; i < colnums.length ; i++){
			System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("bank"), Bytes.toBytes(colnums[i]))));
		}
	}
}
