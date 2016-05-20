package com.kz.face.pojo;

import java.io.Serializable;

public class DelFaceParams implements Serializable{
  private static final long serialVersionUID = 1L;
  private String group;// 人脸底库名
  private String photo;// 要删除的人脸id
  public String getGroup() {
    return group;
  }
  public void setGroup(String group) {
    this.group = group;
  }
  public String getPhoto() {
    return photo;
  }
  public void setPhoto(String photo) {
    this.photo = photo;
  }
}
