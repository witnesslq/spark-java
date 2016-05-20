package com.kz.face.recognition.mq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;

import com.kz.face.pojo.Event;
import com.kz.face.pojo.SearchParams;
import com.kz.face.recognition.FaceRecognitionContext;
import com.kz.face.recognition.pojo.Face;
import com.kz.face.recognition.service.AlgorithmService;
import com.kz.face.recognition.service.impl.AlgorithmServiceImpl;

/**
 * 人脸检索处理实现类
 * 
 * @author huanghaiyang 2016年1月21日
 */
public class SearchEventProcessor implements EventProcessor ,Serializable{
  private static final long serialVersionUID = 1L;
  private final Log logger = LogFactory.getLog(getClass());

  @Override
  public void handEvent(Event e) {
    if(e == null  || e.getParams() == null){
      return ; 
    }
    SearchParams params = null;
    try {
      params = (SearchParams) e.getParams();
    } catch (Exception e2) {
      logger.error("invalid searchParams.");
      return ;
    }
    //设置返回记录数
//    if (params.getLimit() == 0 ) {
//      int defaultLimit = Integer.parseInt(PropertyUtils.getSystemProperties(PropertyConstants.RECOGNITION_LIMIT_DEFAULT));
//      params.setLimit(defaultLimit);
//    }
    
    //1、提取特征值
   /***
    AlgorithmService algorithmService  = new AlgorithmServiceImpl();
    List<SearchParams> searchParamsList = new ArrayList<SearchParams>();
    searchParamsList.add(params);
    JavaSparkContext sparkContext =  FaceRecognitionContext.getSparkContext();
    JavaRDD<SearchParams> algorithmRDD = sparkContext.parallelize(searchParamsList, 1);
    JavaRDD<Face> featureRDD =  algorithmRDD.map(new Function<SearchParams, Face>() {
      private static final long serialVersionUID = 1L;
      @Override
      public Face call(SearchParams v1) throws Exception {
        //调用底层算法
        return null;
      }
    });
    List<Face> feature =featureRDD.collect();
    */
    
    //2、特征值比对
    Map<String, JavaRDD<Face>>  cacheFeaturesMap =  FaceRecognitionContext.getCacheFeatures();
    JavaRDD<Face> cacheFeaureRDD =  cacheFeaturesMap.get("group_test_1000W");
    
    // 获取增加和删除的广播变量
    Map<String, Broadcast<List<Face>>> addBroadcast =  FaceRecognitionContext.getAddFaces();
    Map<String, Broadcast<List<String>>> deleteBroadcast =  FaceRecognitionContext.getDeleteFaces();

    JavaRDD<String> faceTopRDD = cacheFeaureRDD.mapPartitions(new FlatMapFunction<Iterator<Face>, String>() {
      private static final long serialVersionUID = 1L;
//      FixSizedPriorityQueue<String>  priorityQueue =new FixSizedPriorityQueue<String>(100);
      
      List<String> priorityQueue = new ArrayList<String>();
      @Override
      public Iterable<String> call(Iterator<Face> t) throws Exception {
        double d =0;
//        Face faceSearch = feature.get(0);
//        List<Face> addList = addBroadcast.get("").getValue();
        List<String>  deleteList = deleteBroadcast.get("group_test_1000W").getValue();

        //去cacheFeaureRDD中对比找出最相近的多少个人脸(并且每次都需要在deleteList和addList中获取删除的人脸和新增的人脸信息)
        //然后将结果返回给hbase即可
        while(t.hasNext()){
          Face face = t.next();
          String faceId = face.getFaceId();
          for(String delete :deleteList){
            if(faceId.equals(delete)){
//              priorityQueue.add(faceId);
              System.out.println("faceID::::::::::::"+faceId);
              priorityQueue.add(faceId);
            }
          }
        }
        
//        priorityQueue.sortedList();
//        return priorityQueue.getQueue();
        
        return priorityQueue;
      }});
    //3、结果合并
//    faceTopRDD.reduce(null);
    List<String> faceIds = faceTopRDD.collect();
    
    for(String faceId:faceIds){
      System.out.println("xxxx::"+faceId);
    }
  }
}
