package com.kz.face.recognition.leader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kz.face.recognition.FaceRecognitionContext;


/**
 * 取消leader事件处理
 * 
 * @author huanghaiyang 2016年1月19日
 */
public class CancelLeaderEvent implements FaceLeaderEvent {
  private final Log logger = LogFactory.getLog(getClass());

  @Override
  public EventTypeEnum GetEventType() {
    return FaceLeaderEvent.EventTypeEnum.CancelLeader;
  }

  @Override
  public void Excute() throws Exception {
    // 成为leader，则进行初始化
    logger.info("CancelLeaderEvent Excute .");
    FaceRecognitionContext.destroy();
  }
}
