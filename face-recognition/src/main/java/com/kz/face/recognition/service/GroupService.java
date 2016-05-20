package com.kz.face.recognition.service;

import java.util.List;

import com.kz.face.recognition.pojo.GroupInfo;

/**
 * 人脸底库信息服务接口
 * 
 * @author huanghaiyang 2016年1月19日
 */
public interface GroupService {
  /**
   * 获取所有的人脸底库列表
   * 
   * @return
   */
  List<GroupInfo> getAllGroupInfo();
  /**
   * 根据groupName查找GroupInfo
   * @param groupName
   * @return
   */
  GroupInfo getGroupInfo(String groupName);
}
