package com.kz.face.api.pojo.result;

import com.kz.face.api.pojo.result.common.Face;

public class StoreBean {
  private String id = "";
  private Face face;
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public Face getFace() {
    return face;
  }
  public void setFace(Face face) {
    this.face = face;
  }
}
