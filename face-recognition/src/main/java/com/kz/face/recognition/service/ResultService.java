package com.kz.face.recognition.service;

/**
 * 结果返回服务类
 * 
 * @author huanghaiyang 2016年1月20日
 */
public interface ResultService {
  /**
   * 输出结果
   * 
   * @param session_id
   * @param jsonResult
   */
  void outputResult(String session_id, String jsonResult);
}
