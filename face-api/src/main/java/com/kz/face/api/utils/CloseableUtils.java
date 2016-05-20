package com.kz.face.api.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 资源释放工具类
 * @author liuxing
 *
 */
public class CloseableUtils {
  // 关闭资源
  public static void close(Closeable object) {
    if (object == null)
      return ;
    try {
      object.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
