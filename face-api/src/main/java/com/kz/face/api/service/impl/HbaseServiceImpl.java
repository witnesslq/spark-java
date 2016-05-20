package com.kz.face.api.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.api.constant.Constant;
import com.kz.face.api.pojo.result.GroupBean;
import com.kz.face.api.service.HbaseService;
import com.kz.face.api.utils.HTableClientUtils;
import com.kz.face.api.utils.PropertiesUtil;

@SuppressWarnings("deprecation")
public class HbaseServiceImpl implements HbaseService {
  private static final Log logger = LogFactory.getLog(HbaseServiceImpl.class);

  @Override
  public void writeRow(String tableName, String rowkey, String cf, String colnum, String value) throws IOException {
    logger.info(new Date()+"  insert data to hbase table:" + tableName+" and cf:"+cf+""
        + " and rowkey:"+rowkey+" and colmun:"+colnum+" and values:"+value+"");
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      Put put = new Put(Bytes.toBytes(rowkey));
      put.add(Bytes.toBytes(cf), Bytes.toBytes(colnum), Bytes.toBytes(value));
      table.put(put);
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  @Override
  public void writeRow(String tableName, String rowkey, String cf, String[] column,
      String[] value) throws IOException {
    logger.info(new Date()+"  insert data to hbase table:" + tableName+" and cf:"+cf+""
        + " and rowkey:"+rowkey+" and colmun.size:"+column.length+" and values:"+value.length+"");
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      Put put = new Put(Bytes.toBytes(rowkey));
      for (int j = 0; j < column.length; j++) {
        put.add(Bytes.toBytes(cf), Bytes.toBytes(column[j]), Bytes.toBytes(value[j]));
      }
      table.put(put);
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void deleteRows(String tableName, String... rowkeys) throws IOException {
    logger.info(new Date()+"  delete data from hbase table:" + tableName+" and rowkeys:"+rowkeys+"");
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      List<Delete> list = null;
      for (String rowkey : rowkeys) {
        list = new ArrayList<Delete>();
        Delete delete = new Delete(rowkey.getBytes());
        list.add(delete);
      }
      table.delete(list);
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  @Override
  public void deleteByValue(String tableName, String value) throws IOException {
    logger.info(new Date()+"  delete data from hbase table:" + tableName+" and value:"+value+"");
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      ResultScanner rs = table.getScanner(new Scan());
      for (Result r : rs) {
        String rowkey = new String(r.getRow());
        for (KeyValue keyValue : r.raw()) {
          String cf = new String(keyValue.getFamily());
          String colnum = new String(keyValue.getQualifier());
          String colValue = new String(keyValue.getValue());
          // 元数据列族名
          String mateTableCf = PropertiesUtil.getValue(Constant.HBASEMATETABLECF);
          // 元数据列族对应的列名
          if(mateTableCf.equals(cf) && ("name".equals(colnum))
              && (colValue.equals(value))){
            // 删除该条数据
            deleteRows(tableName, rowkey);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
  }

  @Override
  public Result selectRow(String tableName, String rowkey) throws IOException {
    logger.info(new Date()+"  select data from hbase table:" + tableName+" and rowkey:"+rowkey+"");
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      Get get =new Get(rowkey.getBytes());
      Result results = table.get(get);
      return results;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
  }
  
  @Override
  public void update(String tableName, String rowkey, String cf, String colnum, String value)
      throws IOException {
    // 1.先删除
    deleteRows(tableName, rowkey);
    // 2.后添加
    writeRow(tableName, rowkey, cf, colnum, value);
  }
  
  @Override
  public Result selectByValue(String tableName, String value) throws IOException {
    logger.info(new Date()+"  select data from hbase table:" + tableName+" and value:"+value+"");
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      ResultScanner rs = table.getScanner(new Scan());
      for (Result r : rs) {
        String rowkey = new String(r.getRow());
        for (KeyValue keyValue : r.raw()) {
          String cf = new String(keyValue.getFamily());
          String colnum = new String(keyValue.getQualifier());
          String colValue = new String(keyValue.getValue());
          // 元数据列族名
          String mateTableCf = PropertiesUtil.getValue(Constant.HBASEMATETABLECF);
          // 元数据列族对应的列名
          if(mateTableCf.equals(cf) && ("group_name".equals(colnum))
              && (colValue.equals(value))){
            // 返回该条数据
           return  selectRow(tableName, rowkey);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
    return null;
  }
  
  @Override
  public Result[] selectRows(String tableName, String... rowkeys) throws IOException {
    logger.info(new Date()+"  select data from hbase table:" + tableName+" and rowkeys:"+rowkeys+"");
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      List<Get> list = new ArrayList<Get>();
      for (String rowkey : rowkeys) {
        Get get =new Get(rowkey.getBytes());
        list.add(get);
      }
      Result[] results = table.get(list);
      return results;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
  }
  
  @Override
  public Result[] selectRows(String tableName, String start_id, int size,String filter) throws IOException {
    logger.info(new Date()+"  select data from hbase table:" + tableName+" and start row:"+start_id+" "
        + "  and size:"+size);
    HTableInterface table = null;
    ResultScanner scanner = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      
      FilterList filterList = new FilterList();
      SingleColumnValueFilter valueFilter =  new  SingleColumnValueFilter(
          Bytes.toBytes("attr"),
          Bytes.toBytes("group_name"),
          CompareOp.EQUAL,
          Bytes.toBytes(filter)
      );
      filterList.addFilter(valueFilter);
      filterList.addFilter(new PageFilter(size));
      
      Scan scan = new Scan();
      scan.setFilter(filterList);
      scan.setStartRow(start_id.getBytes());
      scanner = table.getScanner(scan);
      
      List<Get> list = new ArrayList<Get>();
      for (Result r : scanner) {
        Get get =new Get(r.getRow());
        list.add(get);
      }
      
      Result[] results = table.get(list);
      return results;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
  }
  
  @Override
  public Result[] selectAllRow(String tableName) throws IOException {
    logger.info(new Date()+"  select all data from hbase table:" + tableName);
    HTableInterface table = null;
    try {
      table = HTableClientUtils.getTable(tableName);
      ResultScanner rs = table.getScanner(new Scan());
      List<Get> list = new ArrayList<Get>();
      for (Result r : rs) {
        Get get =new Get(r.getRow());
        list.add(get);
      }
      Result[] results = table.get(list);
      return results;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (table != null) {
        try {
          table.close();
        } catch (IOException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
  }
  
  @Override
  public List<Map<String, String>> packRow2Map(Result[] results
      ,String cf) {
    // 所有的数据放到一个list中
    List<Map<String, String>> eventList = null;
    if (results.length > 0) {
      eventList = new ArrayList<Map<String, String>>();
      for (Result result : results) {
        // 每条记录放到一个map中
        Map<String, String> eventMap = new LinkedHashMap<String, String>();
        Map<byte[], byte[]> cfMap = result.getFamilyMap(Bytes.toBytes(cf));
        for (byte[] key : cfMap.keySet()) {
          byte[] value = cfMap.get(key);
          eventMap.put(Bytes.toString(key), Bytes.toString(value));
        }
        eventList.add(eventMap);
      }
    }
    return eventList;
  }
  
  @Override
  public List<Object> packRow2Result(String table,Result[] results
      ,String cf) {
    // 所有的数据放到一个list中
    List<Object> responseResultList = null;
    if (results.length > 0) {
      responseResultList = new ArrayList<Object>();
      for (Result result : results) {
        // 1.找出需要封装的表
        GroupBean groupBean  = new GroupBean();
        // 2.迭代元数据表，封装到ResponseResult对象中
        Map<byte[], byte[]> cfMap = result.getFamilyMap(Bytes.toBytes(cf));
        String rowKey =Bytes.toString(result.getRow());
        for (byte[] key : cfMap.keySet()) {
          byte[] value = cfMap.get(key);
          String strKey = Bytes.toString(key);
          // 元数据实体的封装
          groupBean.setId(rowKey);
          if ("name".equals(strKey)) {
            String strValue = Bytes.toString(value);
            groupBean.setName(strValue);
          }
          if ("syncing".equals(strKey)) {
            String strValue = Bytes.toString(value);
            if("false".equals(strValue)){
              groupBean.setSyncing(false);
            }else{
              groupBean.setSyncing(true);
            }
          }
          if ("slave".equals(strKey)) {
            String strValue = Bytes.toString(value);
            if("false".equals(strValue)){
              groupBean.setSlave(false);
            }else{
              groupBean.setSlave(true);
            }
          }
          if ("version".equals(strKey)) {
            String strValue = Bytes.toString(value);
            groupBean.setVersion(Integer.parseInt(strValue));
          }
        }
         responseResultList.add(groupBean);
      }
    }
    return responseResultList;
  }
  
  public static void main(String[] args) throws IOException {
    HbaseServiceImpl service = new HbaseServiceImpl();
    for(int i=1 ; i<=10 ; i++){
      service.writeRow("face_group_meta_info", "000000"+i, "attr", 
          new String[]{"name","syncing","slave","version"}, 
          new String[]{"test","true","false","1"});
    }
//    service.deleteRow("hbase_test", "001");
//    service.selectRows("face_import_error_info", "0000001",10 , "test");
    
//    service.packRow2Result("face_group_meta_info",
//        service.selectAllRow("face_group_meta_info"),"attr");
//    
//   service.packRowMap(service.selectRow("face_group_meta_info", 
//       new String[]{"145318383368818abe0","14531838462745bf5d9"}),"cf1");
    
//    service.deleteByValue("group_mate", "test");
  }
}
