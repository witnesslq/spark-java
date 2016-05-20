package com.kz.face.recognition.function;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.function.Function;

import com.kz.face.recognition.pojo.Face;

import scala.Tuple2;

/**
 * 人脸特征值初始化加载Function
 * 
 * @author huanghaiyang 2016年1月18日
 */
public class FaceFeatureLoadFunction
    implements Function<Tuple2<ImmutableBytesWritable, Result>, Face> {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public Face call(Tuple2<ImmutableBytesWritable, Result> t) throws Exception {
    Face face = new Face();
    face.setFaceId(Bytes.toString(t._2.getRow()));
    face.setAttr1(Bytes.toString(t._2.getValue(Bytes.toBytes("attr"), Bytes.toBytes("attr1"))));
    face.setAttr2(Bytes.toString(t._2.getValue(Bytes.toBytes("attr"), Bytes.toBytes("attr2"))));
    face.setFeature(t._2.getValue(Bytes.toBytes("feature"), Bytes.toBytes("feature")));
    return face;
  }

}
