package com.kz.face.pojo;

import java.io.Serializable;

public class AddFaceParams implements Serializable{
  private static final long serialVersionUID = 1L;
  private byte[] image;// 人脸图片数据
  private String group_name;// 人脸底库名
  private String tag;// 图片注释
  private boolean crop;// 返回裁剪人脸图像
  /**
   * 人脸位置信息，不传则由算法自动解析
   *  json格式 { "left": 2, "top": 49, "width": 97, "height": 98 }
   */
  private String image_rect;

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public String getGroup_name() {
    return group_name;
  }

  public void setGroup_name(String group_name) {
    this.group_name = group_name;
  }

  public String getImage_rect() {
    return image_rect;
  }

  public void setImage_rect(String image_rect) {
    this.image_rect = image_rect;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public boolean isCrop() {
    return crop;
  }

  public void setCrop(boolean crop) {
    this.crop = crop;
  }
}
