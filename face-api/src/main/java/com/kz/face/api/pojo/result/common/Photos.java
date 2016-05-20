package com.kz.face.api.pojo.result.common;

public class Photos {
  private int id;
  private String tag ="";
  public void setId(int id) {
       this.id = id;
   }
   public int getId() {
       return id;
   }

  public void setTag(String tag) {
       this.tag = tag;
   }
   public String getTag() {
       return tag;
   }
}
