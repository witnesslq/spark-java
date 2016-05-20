package com.kz.face.storage.server;

import java.io.InputStream;
import java.util.List;
/**
 * hadoop文件系统访问接口
 * @author liuxing
 *
 */
public interface HdfsServer {
  /**
   * 列出目录下所有的文件和目录
   * @param folder    文件系统下的目录
   * @return List<String> 返回文件系统所有文件和目录
   */
  public List<String> list(String folder);
  /**
   * 在文件系统中新建目录
   * @param folder 文件系统下的目录
   * @return int  
   *          0:成功  
   *         -1:目录存在
   *         -2:文件系统异常
   */
  public int create(String folder);
  /**
   * 移除文件系统中的目录或者文件
   * @param folder 目录或者文件
   * @return int  
   *          0:成功  
   *         -1:目录或者文件不存在
   *         -2:文件系统异常
   */
  public int delete(String folder);
  /**
   * 文件上传(可以上传重复的文件)
   * @param in    上传的文件流
   * @param remote    文件系统的绝对路径
   * @return int  
   *          0:成功  
   *         -1:文件系统的绝对路径存在
   *         -2:文件系统异常
   */
  public int upload(InputStream in, String remote);
  /**
   * 将文件系统中的文件下载到本地
   * @param remote    文件系统的绝对路径
   * @return 字节流
   */
  public InputStream download(String remote);
}
