package com.zi.search.utils;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.zi.search.constants.PropertyConstants;

/**
 * HTablePool工具类，管理Htable实例
 * 
 * @author 刘星 2016年1月18日
 */

public class HTableClientUtils {
	private static Connection connection;
	private static Admin hBaseAdmin;
	private static final Log logger = LogFactory.getLog(HTableClientUtils.class);

	static {
		logger.info("init Connection and Admin...");
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.quorum",
				PropertyUtils.getSystemProperties(PropertyConstants.ZOOKEEPER_CONNECT));
		try {
			connection = ConnectionFactory.createConnection(configuration);
			hBaseAdmin = connection.getAdmin();
		} catch (Exception e) {
			logger.error("init Connection and Admin failure...");
			e.printStackTrace();
		}
	}

	/**
	 * 创建连接池
	 */
	public static void createPool() {
		logger.info(" init hbase pool ...");
		if (connection == null || hBaseAdmin == null) {
			Configuration configuration;
			configuration = HBaseConfiguration.create();
			configuration.set("hbase.zookeeper.quorum",
					PropertyUtils.getSystemProperties(PropertyConstants.ZOOKEEPER_CONNECT));
			try {
				connection = ConnectionFactory.createConnection(configuration);
				hBaseAdmin = connection.getAdmin();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取表实例
	 * 
	 * @param tableName
	 *            表名
	 * @return
	 */
	public static Table getHTable(String tableName) {
		logger.info("hbase get HTable...tableName:" + tableName);
		if (connection == null) {
			createPool();
		}
		Table table = null;
		try {
			table = connection.getTable(TableName.valueOf(tableName));
		} catch (IOException e) {
			logger.error("hbase get HTable failure...tableName:" + tableName);
			e.printStackTrace();
		}
		return table;
	}

	/**
	 * 关闭连接
	 */
	public static void closePool() {
		logger.info("close resource...");
		CloseableUtils.close(connection);
		CloseableUtils.close(hBaseAdmin);
	}

	/**
	 * 通过表名获取hbase表
	 * 
	 * @param tableName
	 *            表名
	 * @return
	 */
	public static boolean tableExist(String tableName) {
		logger.info("check table if exist...tableName:" + tableName);
		if (connection == null) {
			createPool();
		}
		try {
			return hBaseAdmin.tableExists(TableName.valueOf(tableName));
		} catch (IOException e) {
			logger.error("check table if exist failure...tableName:" + tableName);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建表
	 * 
	 * @param table
	 *            表描述
	 */
	public static void createTable(HTableDescriptor table) {
		logger.info("create table...table:" + table);
		if (hBaseAdmin == null) {
			createPool();
		}
		try {
			hBaseAdmin.createTable(table);
		} catch (IOException e) {
			logger.error("create table failure...table" + table);
			e.printStackTrace();
		}
	}

	/**
	 * 通过预分区建表
	 * 
	 * @param tableName
	 *            表名
	 * @param cfs
	 *            列族
	 * @param splitkeys
	 *            预分区
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static void createTable(String tableName, String[] cfs, byte[][] splitkeys) throws IOException {
		logger.info("create table through splitkeys...tableName:" + tableName + "--->cfs:" + cfs);
		try {
			if (hBaseAdmin == null) {
				createPool();
			}
			HTableDescriptor tableDesc = new HTableDescriptor(tableName);
			for (int i = 0; i < cfs.length; i++) {
				tableDesc.addFamily(new HColumnDescriptor(cfs[i]));
			}
			hBaseAdmin.createTable(tableDesc, splitkeys);
		} catch (IOException e) {
			logger.error("create table through splitkeys failure...tableName:" + tableName + "--->cfs:" + cfs);
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 添加列族
	 * 
	 * @param tableName
	 *            表名
	 * @param family
	 *            列族
	 * @throws IOException
	 */
	public static void addColumnFamily(String tableName, String family) throws IOException {
		logger.info("add ColumnFamily...tableName:" + tableName + "--->family:" + family);
		TableName tName = TableName.valueOf(tableName);
		Table table = getHTable(tableName);
		HTableDescriptor descriptor = new HTableDescriptor(table.getTableDescriptor());
		descriptor.addFamily(new HColumnDescriptor(Bytes.toBytes(family)));
		hBaseAdmin.disableTable(tName);
		hBaseAdmin.modifyTable(tName, descriptor);
		hBaseAdmin.enableTable(tName);
		table.close();
	}

	/**
	 * 删除表
	 * 
	 * @param tableName
	 *            表名
	 */
	public static void dropTable(String tableName) {
		logger.info("drop Table..." + tableName);
		if (hBaseAdmin == null) {
			createPool();
		}
		try {
			TableName tName = TableName.valueOf(tableName);
			hBaseAdmin.disableTable(tName);
			hBaseAdmin.deleteTable(tName);
		} catch (Exception e) {
			logger.error("drop Table failure..." + tableName);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println(HTableClientUtils.tableExist("question"));
		try {
			int splitNum = 20;
			byte[][] splitkeys = new byte[splitNum - 1][];
			for (int i = 1; i < splitNum; i++) {
				splitkeys[i - 1] = Bytes.toBytes(String.format("%3d", i).replaceAll(" ", "0"));
			}
			createTable("quedddstion", new String[] { "attr", "feature" }, splitkeys);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
