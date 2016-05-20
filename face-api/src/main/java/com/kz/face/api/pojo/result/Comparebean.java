package com.kz.face.api.pojo.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * rest返回的人脸对比result消息
 * @author liuxing
 *
 */
@XmlRootElement
public class Comparebean implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Object face1;
  private Object face2;
  private float score;
  public Object getFace1() {
    return face1;
  }
  public void setFace1(Object face1) {
    this.face1 = face1;
  }
  public Object getFace2() {
    return face2;
  }
  public void setFace2(Object face2) {
    this.face2 = face2;
  }
  public Float getScore() {
    return score;
  }
  public void setScore(Float score) {
    this.score = score;
  }
}
