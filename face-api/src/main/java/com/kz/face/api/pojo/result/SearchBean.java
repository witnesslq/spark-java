package com.kz.face.api.pojo.result;

import java.util.List;

import com.kz.face.api.pojo.result.common.Face;
import com.kz.face.api.pojo.result.common.Groups;

public class SearchBean {
  private Face face;
  private List<Groups> groups;
  public void setFace(Face face) {
       this.face = face;
   }
   public Face getFace() {
       return face;
   }

  public void setGroups(List<Groups> groups) {
       this.groups = groups;
   }
   public List<Groups> getGroups() {
       return groups;
   }
}
