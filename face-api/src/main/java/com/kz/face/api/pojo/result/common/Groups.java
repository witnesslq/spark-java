package com.kz.face.api.pojo.result.common;

import java.util.List;

public class Groups {
  private String group = "";
  private List<Photos> photos;
  public void setGroup(String group) {
       this.group = group;
   }
   public String getGroup() {
       return group;
   }

  public void setPhotos(List<Photos> photos) {
       this.photos = photos;
   }
   public List<Photos> getPhotos() {
       return photos;
   }
}
