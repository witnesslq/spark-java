package com.kz.face.api.pojo.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * rest返回的人脸入库失败result消息
 * @author liuxing
 *
 */
@XmlRootElement
public class FailureBean implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String group;
  private String imageId;
  public String getImageId() {
    return imageId;
  }
  public void setImageId(String imageId) {
    this.imageId = imageId;
  }
  public String getGroup() {
    return group;
  }
  public void setGroup(String group) {
    this.group = group;
  }
}
