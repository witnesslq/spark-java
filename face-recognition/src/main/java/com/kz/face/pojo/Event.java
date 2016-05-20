package com.kz.face.pojo;

import java.io.Serializable;

/**
 * 事件pojo
 * @author huanghaiyang
 * 2016年1月20日
 */
public class Event implements Serializable{
  private static final long serialVersionUID = 1L;
  private String session_id;// 会话ID，每个会话都是唯一的
  private int type;// 事件类型 参考EventType
  private Object params;// 参数对象，不同事件对应不同的参数对象

  public String getSession_id() {
    return session_id;
  }

  public void setSession_id(String session_id) {
    this.session_id = session_id;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public Object getParams() {
    return params;
  }

  public void setParams(Object params) {
    this.params = params;
  }
}
