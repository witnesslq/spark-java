package com.kz.face.api.pojo.result.common;

public class Face {
  private Rect rect;
  private Attrs attrs;
  private double quality;
  public void setRect(Rect rect) {
       this.rect = rect;
   }
   public Rect getRect() {
       return rect;
   }

  public void setAttrs(Attrs attrs) {
       this.attrs = attrs;
   }
   public Attrs getAttrs() {
       return attrs;
   }

  public void setQuality(double quality) {
       this.quality = quality;
   }
   public double getQuality() {
       return quality;
   }
}
