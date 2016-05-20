package com.kz.face.recognition.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

import com.kz.face.recognition.pojo.GroupInfo;
import com.kz.face.recognition.service.GroupService;
import com.kz.face.recognition.utils.CloseableUtils;
import com.kz.face.recognition.utils.HTableClientUtils;

/**
 * 人脸底库相关操作服务类
 * 
 * @author huanghaiyang 2016年1月19日
 */
public class GroupServiceImpl implements GroupService {
  private final Log logger = LogFactory.getLog(getClass());

  @Override
  public List<GroupInfo> getAllGroupInfo() {

    List<GroupInfo> list = new ArrayList<>();
    Table table = HTableClientUtils.getHTable(GroupInfo.TABLE_NAME);
    Scan scan = new Scan();
    byte[] family = Bytes.toBytes("attr");
    scan.addFamily(family);
    try {
      ResultScanner rs = table.getScanner(scan);
      if (rs == null) {
        return list;
      }
      for (Result r : rs) {
        GroupInfo groupInfo = new GroupInfo();
        // 设置属性
        for (Cell cell : r.rawCells()) {
          String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
          if ("group_name".equals(qualifier)) {
            groupInfo.setGroupName(Bytes.toString(CellUtil.cloneValue(cell)));
          }
          if ("table_name".equals(qualifier)) {
            groupInfo.setTableName(Bytes.toString(CellUtil.cloneValue(cell)));
          }
          if ("description".equals(qualifier)) {
            groupInfo.setDescription(Bytes.toString(CellUtil.cloneValue(cell)));
          }
          if ("c_time".equals(qualifier)) {
            groupInfo.setCreateTime(Bytes.toString(CellUtil.cloneValue(cell)));
          }
        }
        list.add(groupInfo);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      CloseableUtils.close(table);
    }
    return list;
  }

  @Override
  public GroupInfo getGroupInfo(String groupName) {
    GroupInfo groupInfo = null;
    Table table = HTableClientUtils.getHTable(GroupInfo.TABLE_NAME);
    Scan scan = new Scan();
    byte[] family = Bytes.toBytes("attr");
    scan.addFamily(family);
    SingleColumnValueFilter filter =
        new SingleColumnValueFilter(family, Bytes.toBytes("group_name"),
            CompareOp.EQUAL, Bytes.toBytes(groupName));
    scan.setFilter(filter);
    try {
      ResultScanner rs = table.getScanner(scan);
      if (rs == null) {
        return null;
      }
      for (Result r : rs) {
        // 设置属性
        groupInfo = new GroupInfo();
        for (Cell cell : r.rawCells()) {
          String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
          if ("group_name".equals(qualifier)) {
            groupInfo.setGroupName(Bytes.toString(CellUtil.cloneValue(cell)));
          }
          if ("table_name".equals(qualifier)) {
            groupInfo.setTableName(Bytes.toString(CellUtil.cloneValue(cell)));
          }
          if ("description".equals(qualifier)) {
            groupInfo.setDescription(Bytes.toString(CellUtil.cloneValue(cell)));
          }
          if ("c_time".equals(qualifier)) {
            groupInfo.setCreateTime(Bytes.toString(CellUtil.cloneValue(cell)));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      CloseableUtils.close(table);
    }
    return groupInfo;
  }

}
