package com.kz.face.recognition.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JNIUtils {
  public void init_Model(String model_path, int type, int Device_type) {
    System.out.println(Init_Model(model_path,type, Device_type));
  }

  public void detect_face_info(int model, byte[] image, int crop) {
    System.out.println(Detect_face_info(model,image,crop));
  }

  @SuppressWarnings("resource")
  public String get_Feature_Info(String imagePath){
    try {
      File image = new File(imagePath);
      InputStream is = new FileInputStream(image);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer, 0, size);
      System.out.println(size);
      String feature = Get_Feature_Info(buffer,1);
      System.out.println(feature);
      return feature;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  public void compare_Feature(String facePath) {
    String Face_feature1 = get_Feature_Info(facePath);
    
//    JSONObject jsonArray =  JSONObject.fromBean(Face_feature1);
//    Object face = jsonArray.get("face");
//    String faceStr = face+"";
//    faceStr = faceStr.substring(1,faceStr.length()-1); 
//    JSONObject jsonface =  JSONObject.fromBean(faceStr);
//    String feature1 = jsonface.get("feature")+"";
    
    System.out.println(Compare_Feature(Face_feature1,Face_feature1));
  }

  private native int Init_Model(String model_path, int type, int Device_type);
  
  private native String Detect_face_info(int model, byte[] image, int crop);
  
  private native String Get_Feature_Info(byte[] image, int crop);
  
  private native float Compare_Feature(String Face_feature1, String Face_feature2);
  
  private native int Release_Model();
  
  static {
    System.loadLibrary("JNIUtils");
  }
  
 /* public static void jsonTest(){
    String test = "{\"id\":   0,\"face\": [{ \"rect\": {\"left\": 269,\"top\":  359,\"width\":    259,\"height\":   259},\"attrs\":    {\"yaw\":  0, \"pitch\":    0, \"motion\":   0,\"gaussian\": 0 },\"quality\":  90, \"crop\": {\"rect\": {\"left\": 0, \"top\":  0, \"width\":    259,\"height\":   259}, \"image\":\"\"},\"feature\":  \"zbbKF.\\\\�\\n.M���Xl�F��hV*\\u001f(=�.��捂�X\\u0001�\\u001f�\\u000f\\u001e�\\u001f4W?��ow\" }],\"image_rect\":   {\"left\": 0,\"top\":  0,\"width\":    960,\"height\": 1280}}";
    JSONObject jsonArray =  JSONObject.fromBean(test);
    Object face = jsonArray.get("face");
    String faceStr = face+"";
    faceStr = faceStr.substring(1,faceStr.length()-1); 
    JSONObject jsonface =  JSONObject.fromBean(faceStr);
    String feature = (String) jsonface.get("feature");
    System.out.println(feature);
  
  }*/
  public static void main(String[] args) {
//    jsonTest();
    
    JNIUtils jniUtils = new JNIUtils();
    jniUtils.init_Model("/home/hadoop/model", 2, 1);
    //jniUtils.get_Feature_Info("1.jpg");
    jniUtils.compare_Feature("1.jpg");
  }
}
