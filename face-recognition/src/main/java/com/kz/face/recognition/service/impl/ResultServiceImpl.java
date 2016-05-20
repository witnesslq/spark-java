package com.kz.face.recognition.service.impl;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.recognition.pojo.ResultInfo;
import com.kz.face.recognition.service.ResultService;
import com.kz.face.recognition.utils.CloseableUtils;
import com.kz.face.recognition.utils.HTableClientUtils;
/**
 * 结果输出实现类
 * @author huanghaiyang
 * 2016年1月21日
 */
public class ResultServiceImpl implements ResultService{

  @Override
  public void outputResult(String session_id, String jsonResult) {
    Table table = HTableClientUtils.getHTable(ResultInfo.TABLE_NAME);
    byte[] family = Bytes.toBytes("attr");
    try {
      Put put = new Put(Bytes.toBytes(session_id));
      put.addColumn(family, Bytes.toBytes("value"), Bytes.toBytes(jsonResult));
      table.put(put);
    } catch (Exception e) {
    }finally {
      CloseableUtils.close(table);
    }
  }

}
