package com.kz.face.pojo;

public enum EventType {
  detect(1), // 检测
  search(2), // 检索
  add(3), // 入库
  delete(4), // 删除人脸
  compare(5), // 人脸比对
  verify(6); // 人脸校验

  private EventType(int value) {
    this.value = value;
  }

  private int value;

  public int getValue() {
    return value;
  }

}
