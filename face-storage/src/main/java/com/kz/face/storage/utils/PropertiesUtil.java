package com.kz.face.storage.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读写类
 * @author liuxing
 *
 */
public class PropertiesUtil {
  private static Properties prop = null;
  private static String filePath = "system.properties";

  static {
      prop = new Properties();
      InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath);
      try {
          prop.load(in);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  /**
   * 通过key得到value
   * 
   * @param key
   * @return value
   */
  public static String getValue(String key) {
      return prop.getProperty(key).trim();
  }
}
