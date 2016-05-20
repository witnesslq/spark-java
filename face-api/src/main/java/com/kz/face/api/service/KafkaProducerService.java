package com.kz.face.api.service;

import com.kz.face.pojo.Event;

/**
 * kafka消息生产者
 * 
 * @author liuxing
 *
 */
public interface KafkaProducerService {
  /**
   * 操作topic(包括对topic的增、删、改、查...)
   * 
   * @param options 命令信息
   */
  void operatorTopic(String[] options);

  /**
   * 往kafka发送消息
   * 
   * @param face 需要序列化的对象
   * @throws Exception 
   */
  void produceMessage(Event face) throws Exception;
}
