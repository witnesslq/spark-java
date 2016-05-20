package com.kz.face.api.pojo.result.common;

public class Attrs {
  private int size;
  private double brightness;
  private double gaussian_blurness;
  private double motion_blurness;
  private double yaw;
  private double pitch;
  public void setSize(int size) {
       this.size = size;
   }
   public int getSize() {
       return size;
   }

  public void setBrightness(double brightness) {
       this.brightness = brightness;
   }
   public double getBrightness() {
       return brightness;
   }
  public double getGaussian_blurness() {
    return gaussian_blurness;
  }
  public void setGaussian_blurness(double gaussian_blurness) {
    this.gaussian_blurness = gaussian_blurness;
  }
  public double getMotion_blurness() {
    return motion_blurness;
  }
  public void setMotion_blurness(double motion_blurness) {
    this.motion_blurness = motion_blurness;
  }
  public void setYaw(double yaw) {
       this.yaw = yaw;
   }
   public double getYaw() {
       return yaw;
   }

  public void setPitch(double pitch) {
       this.pitch = pitch;
   }
   public double getPitch() {
       return pitch;
   }
}
