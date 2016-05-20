package com.kz.face.recognition.mq;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.broadcast.Broadcast;

import com.kz.face.pojo.DelFaceParams;
import com.kz.face.pojo.Event;
import com.kz.face.recognition.FaceRecognitionContext;
/**
 * 人脸删除处理实现类
 * 
 * @author liuxing
 *
 */
public class DeleteEventProcessor implements EventProcessor{
  private final Log logger = LogFactory.getLog(getClass());

  @Override
  public void handEvent(Event e) {
    logger.info("this is delete handEvent!!!");
    if(e == null  || e.getParams() == null){
      return ; 
    }
    DelFaceParams params = null;
    try {
      params = (DelFaceParams) e.getParams();
    } catch (Exception e2) {
      logger.error("invalid searchParams.");
      return ;
    }
    
    //图片的Id
    //"00000061d08-85e1-414a-81e9-6e5a14b2f626"
    String photoId = params.getPhoto();
    
    
    logger.info("this is delete handEvent!!!"+e.getSession_id());
    Map<String, Broadcast<List<String>>> deleteMap =  FaceRecognitionContext.getDeleteFaces();
    Broadcast<List<String>> deleteBroadcast  = deleteMap.get("group_test_1000W");
    List<String> deleteBroadcastList =  deleteBroadcast.getValue();
    deleteBroadcastList.add(photoId);
  }

}
