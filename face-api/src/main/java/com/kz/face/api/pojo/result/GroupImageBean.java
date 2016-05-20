package com.kz.face.api.pojo.result;

import java.util.List;

import com.kz.face.api.pojo.result.common.Photos;

public class GroupImageBean {
  private String name ="";
  private int total_photos;
  private int next_cursor;
  private List<Photos> photos;
  public void setName(String name) {
       this.name = name;
   }
   public String getName() {
       return name;
   }
  public int getTotal_photos() {
    return total_photos;
  }
  public void setTotal_photos(int total_photos) {
    this.total_photos = total_photos;
  }
  public void setPhotos(List<Photos> photos) {
       this.photos = photos;
   }
   public List<Photos> getPhotos() {
       return photos;
   }
  public int getNext_cursor() {
    return next_cursor;
  }
  public void setNext_cursor(int next_cursor) {
    this.next_cursor = next_cursor;
  }
}
