package com.kz.face.recognition.pojo;

/**
 * 人脸底库信息
 * 
 * @author huanghaiyang 2016年1月19日
 */
public class GroupInfo {
  public static String TABLE_NAME = "face_group_meta_info";
  private String groupName;
  private String createTime;
  private String tableName;
  private String description;

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
