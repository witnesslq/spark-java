package com.kz.face.api.pojo.result;

import com.kz.face.api.pojo.result.common.Face;

public class DetectBean {
  private Face faces;

  public Face getFaces() {
    return faces;
  }

  public void setFaces(Face faces) {
    this.faces = faces;
  }
}
