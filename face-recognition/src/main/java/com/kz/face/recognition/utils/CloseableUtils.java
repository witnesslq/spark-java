package com.kz.face.recognition.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 资源释放工具类
 * 
 * @author huanghaiyang 2016年1月18日
 *
 */
public class CloseableUtils {
  // 关闭资源
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
