package com.kz.face.recognition.leader;


/**
 * leader选举事件处理
 * 
 * @author huanghaiyang 2016年1月18日
 */
public interface FaceLeaderEvent {

  // 事件类型
  public enum EventTypeEnum {
    BecomeLeader, // 成为leader事件
    CancelLeader; // 取消leader事件
  }

  // 获取事件类型
  public EventTypeEnum GetEventType();

  // 处理事件
  public void Excute() throws Exception;

}
