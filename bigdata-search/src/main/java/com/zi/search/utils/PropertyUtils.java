package com.zi.search.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * 获取系统system.properties的配置信息
 * 
 * @author 刘星 
 */
public class PropertyUtils {

  private static Properties pro = null;

  private static void initProperties() {
    if (pro == null) {
      pro = new Properties();
      try {
        pro.load(PropertyUtils.class.getResourceAsStream("/system.properties"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static String getSystemProperties(String key) {
    initProperties();
    return pro.getProperty(key);
  }

  public static String getSystemProperties(String key, String defaultValue) {
    initProperties();
    return pro.getProperty(key, defaultValue);
  }

}
