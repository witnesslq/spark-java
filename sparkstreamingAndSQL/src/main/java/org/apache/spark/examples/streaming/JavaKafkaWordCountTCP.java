package org.apache.spark.examples.streaming;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

public final class JavaKafkaWordCountTCP {

  private JavaKafkaWordCountTCP() {
  }

  public static void main(String[] args) {
    socket("");
//    hdfs(args[0]);
//    textFile();
  }
  
  @SuppressWarnings("resource")
  public static void socket(String str){
    // Create a local StreamingContext with two working thread and batch interval of 1 second
    SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("JavaKafkaWordCount");
    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,new Duration(1000));
    

    // Create a DStream that will connect to hostname:port, like localhost:9999
    JavaReceiverInputDStream<String> lines = jssc.socketTextStream("192.168.10.163", 9999);
    
    // Split each line into words
    JavaDStream<String> words = lines.flatMap(
      new FlatMapFunction<String, String>() {
        private static final long serialVersionUID = 1L;

        @Override public Iterable<String> call(String x) {
          return Arrays.asList(x.split(" "));
        }
      });
    
    // Count each word in each batch
    JavaPairDStream<String, Integer> pairs = words.mapToPair(
      new PairFunction<String, String, Integer>() {
        private static final long serialVersionUID = 1L;

        @Override public Tuple2<String, Integer> call(String s) {
          return new Tuple2<String, Integer>(s, 1);
        }
      });
    JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
      new Function2<Integer, Integer, Integer>() {
        private static final long serialVersionUID = 1L;

        @Override public Integer call(Integer i1, Integer i2) {
          System.out.println("i1:"+i1+"   i2:"+i2);
          return i1 + i2;
        }
      });

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print();
    jssc.start();
    jssc.awaitTermination();
  }
  @SuppressWarnings("resource")
  public static void hdfs(String args){
    // Create a local StreamingContext with two working thread and batch interval of 1 second
    SparkConf sparkConf = new SparkConf().setMaster(args).setAppName("JavaKafkaWordCount");
    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,new Duration(1000));
    
    JavaPairInputDStream<Object, Object> lines =  jssc.fileStream("D:\test.txt", null, null, null);
    
    
    JavaDStream<Object> words = lines.flatMap(
      new FlatMapFunction<Tuple2<Object,Object>, Object>() {
      private static final long serialVersionUID = 1L;
      @Override
      public Iterable<Object> call(Tuple2<Object, Object> arg0) throws Exception {
        System.out.println(arg0._1+":::::::::"+arg0._2);
        return Arrays.asList(arg0._2);
      }
    });
    
    JavaPairDStream<String, Integer> pairs = words.mapToPair(
      new PairFunction<Object, String, Integer>() {
      private static final long serialVersionUID = 1L;
      @Override
      public Tuple2<String, Integer> call(Object arg0) throws Exception {
        System.out.println(arg0+"=============");
        return new Tuple2<String, Integer>((String) arg0, 1);
      }});

    JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
      new Function2<Integer, Integer, Integer>() {
        private static final long serialVersionUID = 1L;

        @Override public Integer call(Integer i1, Integer i2) {
          System.out.println("i1:"+i1+"   i2:"+i2);
          return i1 + i2;
        }
      });

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print();
    jssc.start();
    jssc.awaitTermination();
  }
  @SuppressWarnings("resource")
  public static void textFile(){
    // Create a local StreamingContext with two working thread and batch interval of 1 second
    SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("JavaKafkaWordCount");
    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,new Duration(1000));
    
    JavaDStream<String> lines =  jssc.textFileStream("D:\\test.txt");
    
    
    // Split each line into words
    JavaDStream<String> words = lines.flatMap(
      new FlatMapFunction<String, String>() {
        private static final long serialVersionUID = 1L;

        @Override public Iterable<String> call(String x) {
          return Arrays.asList(x.split(" "));
        }
      });
    
    // Count each word in each batch
    JavaPairDStream<String, Integer> pairs = words.mapToPair(
      new PairFunction<String, String, Integer>() {
        private static final long serialVersionUID = 1L;

        @Override public Tuple2<String, Integer> call(String s) {
          return new Tuple2<String, Integer>(s, 1);
        }
      });
    JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
      new Function2<Integer, Integer, Integer>() {
        private static final long serialVersionUID = 1L;

        @Override public Integer call(Integer i1, Integer i2) {
          System.out.println("i1:"+i1+"   i2:"+i2);
          return i1 + i2;
        }
      });

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print();
    jssc.start();
    jssc.awaitTermination();
  }
}
