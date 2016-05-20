package com.kz.face.pojo;

import java.io.Serializable;

public class DetectParams implements Serializable{
  private static final long serialVersionUID = 1L;
  private byte[] image;// 人脸图片数据
  private boolean crop;// 返回裁剪人脸图像
  public byte[] getImage() {
    return image;
  }
  public void setImage(byte[] image) {
    this.image = image;
  }
  public boolean isCrop() {
    return crop;
  }
  public void setCrop(boolean crop) {
    this.crop = crop;
  }
}
