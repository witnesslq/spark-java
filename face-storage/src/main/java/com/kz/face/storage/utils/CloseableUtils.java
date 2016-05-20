package com.kz.face.storage.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 资源释放工具类
 * 
 * @author liuxing 2016年1月18日
 *
 */
public class CloseableUtils {
  /**
   * 关闭资源
   * @param object 持有资源的对象
   */
  public static void close(Closeable object) {
    if (object == null)
      return;
    try {
      object.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
