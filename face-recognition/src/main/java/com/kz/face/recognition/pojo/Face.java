package com.kz.face.recognition.pojo;

import java.io.Serializable;

/**
 * 人脸数据，用于RDD内存装载
 * 
 * @author huanghaiyang 2016年1月19日
 */
public class Face implements Serializable {
  private static final long serialVersionUID = 2563597526162260931L;
  private String faceId;
  private String attr1;
  private String attr2;
  private byte[] feature;

  public String getFaceId() {
    return faceId;
  }

  public void setFaceId(String faceId) {
    this.faceId = faceId;
  }

  public String getAttr1() {
    return attr1;
  }

  public void setAttr1(String attr1) {
    this.attr1 = attr1;
  }

  public String getAttr2() {
    return attr2;
  }

  public void setAttr2(String attr2) {
    this.attr2 = attr2;
  }

  public byte[] getFeature() {
    return feature;
  }

  public void setFeature(byte[] feature) {
    this.feature = feature;
  }
}
