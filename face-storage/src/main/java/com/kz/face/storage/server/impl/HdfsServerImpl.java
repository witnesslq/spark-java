package com.kz.face.storage.server.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import com.kz.face.storage.constant.Constant;
import com.kz.face.storage.server.HdfsServer;
import com.kz.face.storage.utils.PropertiesUtil;

public class HdfsServerImpl implements HdfsServer {
  private static final Log logger = LogFactory.getLog(HdfsServerImpl.class);
  // 配置
  static Configuration conf = new Configuration();
  static FileSystem fs = null;

  static {
    logger.info("hdfs installing...");;
    try {
      conf.set("fs.defaultFS", PropertiesUtil.getValue("hdfscluster"));
      conf.set("dfs.nameservices", PropertiesUtil.getValue("hdfsclustername"));
      conf.set("dfs.ha.namenodes.kzfscluster", PropertiesUtil.getValue("hdfsha"));
      conf.set("dfs.namenode.rpc-address.kzfscluster.nn0",
          PropertiesUtil.getValue("hdfsnn0address"));
      conf.set("dfs.namenode.rpc-address.kzfscluster.nn1",
          PropertiesUtil.getValue("hdfsnn1address"));
      conf.set("dfs.client.failover.proxy.provider.kzfscluster",
          PropertiesUtil.getValue("hdfsprovider"));
      fs = FileSystem.get(conf);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<String> list(String folder) {
    List<String> fileNameList = null;
    try {
      if (!folder.isEmpty()) {
        FileStatus[] fileList = fs.listStatus(new Path(folder));
        if ((fileList != null) && (fileList.length != 0)) {
          fileNameList = new ArrayList<String>();
          for (FileStatus file : fileList) {
            fileNameList.add(file.getPath().getName());
          }
          return fileNameList;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return fileNameList;
    }
    return fileNameList;
  }

  @Override
  public int create(final String folder) {
    if (!folder.isEmpty()) {
      try {
        Path path = new Path(folder);
        if (!fs.exists(path)) {
          fs.mkdirs(path);
          return Constant.CREATEOK;
        } else {
          return Constant.CREATEERROR;
        }
      } catch (IOException e) {
        e.printStackTrace();
        return Constant.HDFSERROR;
      }
    }
    return Constant.CREATEERROR;
  }

  @SuppressWarnings("deprecation")
  @Override
  public int delete(String folder) {
    logger.info("delete file...folder:" + folder);
    if (!folder.isEmpty()) {
      try {
        Path path = new Path(folder);
        if (fs.delete(path)) {
          return Constant.DELETEOK;
        } else {
          logger.error("delete file failure...folder:" + folder);
          return Constant.DELETEERROR;
        }
      } catch (IOException e) {
        logger.error("hdfs system crashed...folder:" + folder);
        e.printStackTrace();
        return Constant.HDFSERROR;
      }
    }
    logger.error("folder is empty...folder:" + folder);
    return Constant.DELETEERROR;
  }

  @Override
  public int upload(InputStream in, String remote) {
    logger.info("upload file...remote:" + remote);
    if (in != null && !remote.isEmpty()) {
      try {
        FSDataOutputStream out = fs.create(new Path(remote), null);
        IOUtils.copyBytes(in, out, 8192, true);
        return Constant.UPLOADOK;
      } catch (IOException e) {
        logger.error("hdfs system crashed...remote:" + remote);
        e.printStackTrace();
        return Constant.HDFSERROR;
      }
    }
    logger.error("upload file failure...remote:" + remote);
    return Constant.UPLOADERROR;
  }

  @Override
  public InputStream download(String remote) {
    logger.info("download file...remote:" + remote);
    FSDataInputStream in = null;
    if (!remote.isEmpty()) {
      try {
        Path remotePath = new Path(remote);
        in = fs.open(remotePath);
        return in;
      } catch (IOException e) {
        logger.error("hdfs system crashed...remote:" + remote);
        e.printStackTrace();
        return null;
      }
    }
    logger.error("download file failure...remote:" + remote);
    return null;
  }

  public static void main(String[] args) throws Exception {
    HdfsServer hdfsDao = new HdfsServerImpl();
    // List<String> fileName = hdfsDao.list("/");
    // for (String name : fileName) {
    // System.out.println(name);
    // }

    // System.out.println(hdfsDao.create("/aa/bb"));

    // System.out.println(hdfsDao.delete("/aa/rj_yg1979.exee"));

    long time = System.currentTimeMillis();
    hdfsDao.delete("/liuxing/59c69f1b56294aaaa9b01c88d898c76c%_Wildlife.wmv");

    InputStream in = new FileInputStream("D:\\1.jpg");
    System.out.println(hdfsDao.upload(in, "/liuxing/Tulips.jpg"));
    System.out.println(System.currentTimeMillis() - time);

    // InputStream in = hdfsDao.download("/aa/Desert.jpg");
    // OutputStream out = new FileOutputStream("D:/Desert.jpg");
    // byte buff [] = new byte[1024];
    // int len = 0;
    // while((len = in.read(buff))!=-1){
    // out.write(buff, 0, len);
    // }
    // out.flush();
    // in.close();
    // out.close();
  }
}
