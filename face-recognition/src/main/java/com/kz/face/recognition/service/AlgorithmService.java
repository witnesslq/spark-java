package com.kz.face.recognition.service;

/**
 * 人脸识别算法服务相关接口
 * 
 * @author huanghaiyang 2016年1月18日
 */
public class AlgorithmService {
  
  public AlgorithmService() {
  }
  /**
   * 加载算法模型
   * @param model_path
   * @param type
   * @param Device_type
   * @return
   */
  public native int Init_Model(String model_path, int type, int Device_type);
  /**
   * 人脸信息的提取
   * @param model
   * @param image
   * @param crop
   * @return
   */
  public native String Detect_face_info(int model, byte[] image, int crop);
  /**
   * 人脸信息和提取特征值
   * 检测是否有人脸信息
   * 如果有返回人脸个数、位置、图片裁剪
   * @param image 原图片
   * @param crop  0：不裁剪 1：裁剪
   * @return 
   */
  public native String Get_Feature_Info(byte[] image, int crop);
  /**
   * 检测是否有人脸信息
   * 如果有返回人脸个数、位置、图片裁剪、特征提取
   * @param Face_feature1 人脸1
   * @param Face_feature2 人脸2
   * @return
   */
  public native float Compare_Feature(String Face_feature1, String Face_feature2);
  /**
   * 释放算法模型
   * @return
   */
  public native int Release_Model();
}
