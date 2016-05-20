package com.kz.face.recognition.pojo;

public class ResultInfo {
  public static String TABLE_NAME = "face_result_info";
  private String session_id;
  private String value;
  
  public String getSession_id() {
    return session_id;
  }
  public void setSession_id(String session_id) {
    this.session_id = session_id;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
}
