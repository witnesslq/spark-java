package com.kz.face.recognition.mq;

import com.kz.face.pojo.Event;
/**
 * 事件处理接口
 * @author huanghaiyang
 * 2016年1月21日
 */
public interface EventProcessor {
  void handEvent(Event e);
}
