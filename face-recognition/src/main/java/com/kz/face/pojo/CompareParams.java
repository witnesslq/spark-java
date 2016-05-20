package com.kz.face.pojo;

import java.io.Serializable;

public class CompareParams implements Serializable{
  private static final long serialVersionUID = 1L;
  private byte[] image1;// 第1张人脸图片数据
  /**
   * 第1张人脸位置信息，不传则由算法自动解析
   * json格式 { "left": 2, "top": 49, "width": 97, "height": 98 }
   */
  private String image1_rect;
  private byte[] image2;// 第2张人脸图片数据
  /**
   * 第2张人脸位置信息，不传则由算法自动解析
   * json格式 { "left": 2, "top": 49, "width": 97, "height": 98 }
   */
  private String image2_rect;
  public byte[] getImage1() {
    return image1;
  }
  public void setImage1(byte[] image1) {
    this.image1 = image1;
  }
  public String getImage1_rect() {
    return image1_rect;
  }
  public void setImage1_rect(String image1_rect) {
    this.image1_rect = image1_rect;
  }
  public byte[] getImage2() {
    return image2;
  }
  public void setImage2(byte[] image2) {
    this.image2 = image2;
  }
  public String getImage2_rect() {
    return image2_rect;
  }
  public void setImage2_rect(String image2_rect) {
    this.image2_rect = image2_rect;
  }
}
