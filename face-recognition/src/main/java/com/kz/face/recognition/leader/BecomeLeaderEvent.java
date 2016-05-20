package com.kz.face.recognition.leader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kz.face.recognition.FaceRecognitionContext;


/**
 * 选举为leader事件处理器
 * 
 * @author huanghaiyang 2016年1月18日
 */
public class BecomeLeaderEvent implements FaceLeaderEvent {
  private final Log logger = LogFactory.getLog(getClass());

  @Override
  public EventTypeEnum GetEventType() {
    return FaceLeaderEvent.EventTypeEnum.BecomeLeader;
  }

  @Override
  public void Excute() throws Exception {
    // 成为leader，则进行初始化
    logger.info("BecomeLeaderEvent Excute .");
    FaceRecognitionContext.initAndStart();
  }
}
