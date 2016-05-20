package com.kz.face.pojo;

import java.io.Serializable;
/**
 * 人脸验证
 * @author liuxing
 *
 */
public class VerifyParams implements Serializable{
  private static final long serialVersionUID = 1L;
  private String group; //库名
  private String photo; //图片id，以,分割
  private byte[] image;// 人脸图片数据
  private String image_rect;
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
  public byte[] getImage() {
    return image;
  }
  public void setImage(byte[] image) {
    this.image = image;
  }
  public String getImage_rect() {
    return image_rect;
  }
  public void setImage_rect(String image_rect) {
    this.image_rect = image_rect;
  }
}
