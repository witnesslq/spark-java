package com.kz.face.recognition;



import com.kz.face.recognition.leader.FaceLeaderManager;

/**
 * 主函数入口
 * 
 * @author huanghaiyang 2016年1月19日
 */
public class FaceRecognitionMain {
  public static void main(String[] args) {
    FaceLeaderManager manager = null;
    try {
      manager = FaceLeaderManager.Instance();
      manager.StartManager();
    } catch (Exception e) {
    } finally {
    }
  }
}
