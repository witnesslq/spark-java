package com.kz.face.pojo;

import java.io.Serializable;

public class SearchParams implements Serializable{
  private static final long serialVersionUID = 1L;
  private String group;// 人脸底库名，多个以逗号隔开
  private byte[] image;// 图片
  private String image_rect;// 人脸矩形
  private int limit;//返回最相似的前几条记录
  private String filter;//结果过滤条件
  private boolean crop;//返回裁剪人脸图像
  public String getGroup() {
    return group;
  }
  public void setGroup(String group) {
    this.group = group;
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
  public int getLimit() {
    return limit;
  }
  public void setLimit(int limit) {
    this.limit = limit;
  }
  public String getFilter() {
    return filter;
  }
  public void setFilter(String filter) {
    this.filter = filter;
  }
  public boolean isCrop() {
    return crop;
  }
  public void setCrop(boolean crop) {
    this.crop = crop;
  }
}
