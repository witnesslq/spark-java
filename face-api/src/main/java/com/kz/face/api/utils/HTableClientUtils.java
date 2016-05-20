package com.kz.face.api.utils;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.api.constant.Constant;

@SuppressWarnings("deprecation")
public class HTableClientUtils {
	private static final Log logger = LogFactory.getLog(HTableClientUtils.class);
	private static Configuration conf = null;
	private static HConnection conn = null;
	private static Admin admin;

	/**
	 * 初始化配置
	 * @throws IOException 
	 */
	 public static void createPool() throws IOException{
		logger.info("loading...... configure,create Connection");
		try {
		  if (conn == null || admin == null) {
		    conf = HBaseConfiguration.create();
		    conf.set("hbase.zookeeper.quorum", PropertiesUtil.getValue(Constant.ZKCONNECT));
		    conn = HConnectionManager.createConnection(conf);
		    admin = conn.getAdmin();
		  }
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 通过表名来获取表对象
	 * @param tableName 表名
	 * @return HTableInterface
	 * @throws IOException 
	 */
	public static HTableInterface getTable(String tableName) throws IOException {
		logger.info(new Date()+"  get HTableInterface by tableName:"+tableName+"");
		if (!StringUtils.isEmpty(tableName)) {
			try {
			  if (conn == null) {
			      createPool();
			    }
				return conn.getTable(tableName);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
		return null;
	}

	/**
     * 建表
     * @param tableName 表名
     * @param cfs 列族
     * @throws IOException
     */
    public static void createTable(String tableName, String[] cfs) throws IOException {
        logger.info(new Date()+"  create table by tableName:"+tableName+" and cfs:"+cfs+"");
        try {
            if (admin == null) {
              createPool();
            }
            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
            for (int i = 0; i < cfs.length; i++) {
                tableDesc.addFamily(new HColumnDescriptor(cfs[i]));
            }
            admin.createTable(tableDesc);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
	/**
	 * 通过预分区建表
	 * @param tableName 表名
	 * @param cfs 列族
	 * @param splitkeys 预分区
	 * @throws IOException
	 */
	public static void createTable(String tableName, String[] cfs,byte[][] splitkeys) throws IOException {
	    logger.info(new Date()+"  create table by tableName:"+tableName+" and cfs:"+cfs+" and splitkeys:"+splitkeys+"");
		try {
  		    if (admin == null) {
              createPool();
            }
			HTableDescriptor tableDesc = new HTableDescriptor(tableName);
			for (int i = 0; i < cfs.length; i++) {
				tableDesc.addFamily(new HColumnDescriptor(cfs[i]));
			}
			admin.createTable(tableDesc,splitkeys);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
     * 判断表是否存在
     * @param tableName 表名
     * @throws IOException
     */
    public static boolean tableExist(String tableName) throws IOException {
        logger.info(new Date()+"  find table by tableName:"+tableName+"");
        try {
            if (admin == null) {
              createPool();
            }
            if (admin.tableExists(TableName.valueOf(tableName))) {
              return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }
	
	/**
	 * 删除表
	 * @param tableName 表名
	 * @throws IOException
	 */
	public static void dropTable(String tableName) throws IOException {
		logger.info(new Date()+"  drop table by tableName:"+tableName+"");
		try {
  		    if (admin == null) {
              createPool();
            }
			if (admin.tableExists(TableName.valueOf(tableName))) {
				admin.disableTable(TableName.valueOf(tableName));
				admin.deleteTable(TableName.valueOf(tableName));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	public static void main(String[] args) {
	  try {
	    int splitNum = 10 ;
        byte[][] splitkeys = new byte[splitNum-1][];
        for (int i = 1; i < splitNum; i++) {
          splitkeys[i - 1] = Bytes.toBytes(String.format("%3d", i).replaceAll(" ", "0"));
        }
	    createTable("group_test_1000W", new String[]{"attr","feature"},splitkeys);
//        dropTable("ddd");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    }
}
