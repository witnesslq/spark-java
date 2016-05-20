package com.kz.face.recognition.function;

import java.util.Iterator;

import org.apache.spark.api.java.function.FlatMapFunction;

import com.kz.face.recognition.pojo.Face;
import com.kz.face.recognition.utils.FixSizedPriorityQueue;

public class FeatureCompareFunction implements FlatMapFunction<Iterator<Face>, Double> {

  private int size; // 返回记录数
  private FixSizedPriorityQueue<Double> top;// 按从大到小，取topN

  public FeatureCompareFunction(int size) {
    this.size = size;
    top = new FixSizedPriorityQueue<Double>(size);
  }

  private static final long serialVersionUID = 1L;

  @Override
  public Iterable<Double> call(Iterator<Face> t) throws Exception {
    


    return null;
  }



}
