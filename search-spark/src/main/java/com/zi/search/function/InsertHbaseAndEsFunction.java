package com.zi.search.function;

import java.util.Iterator;
import java.util.Map;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.zi.search.constants.PropertyConstants;
import com.zi.search.job.SparkStart;
import com.zi.search.service.ElasticSearchService;
import com.zi.search.service.HbaseServer;
import com.zi.search.service.impl.ElasticSearchServiceImpl;
import com.zi.search.service.impl.HbaseServerImpl;

import scala.Tuple2;

/**
 * 将数据插入到es和hbase中
 * 
 * @author liuxing
 *
 */
public class InsertHbaseAndEsFunction implements Function<JavaPairRDD<String, Integer>, Void> {
	private static final long serialVersionUID = 1L;

	@Override
	public Void call(JavaPairRDD<String, Integer> rdd) throws Exception {
		if(!rdd.isEmpty()){
//			JavaPairRDD<String, Integer> cachedRdd = rdd.persist(StorageLevel.MEMORY_ONLY());
			rdd.foreachPartition(new SendDataFunction());
//			cachedRdd.unpersist();
		}
		return null;
	}

}

class SendDataFunction implements VoidFunction<Iterator<Tuple2<String, Integer>>> {
	private static final long serialVersionUID = 42l;
	@Override
	public void call(Iterator<Tuple2<String, Integer>> iter) throws Exception {
		// get data from broadcast
		//TODO broadcast数据没能获取到
		Map<String,Broadcast<String>> broadcastMap = SparkStart.broadcastMap;
		String tableName = broadcastMap.get(PropertyConstants.TABLENAME).getValue();
		String cf = broadcastMap.get(PropertyConstants.CF).getValue();
		String index = broadcastMap.get(PropertyConstants.INDEX).getValue();
		String type = broadcastMap.get(PropertyConstants.TYPE).getValue();
		
		// 1.获取连接
		ElasticSearchService elasticSearchService = new ElasticSearchServiceImpl();
		HbaseServer hbaseServer = new HbaseServerImpl();
		hbaseServer.getTable(tableName);
		System.err.println("init...............................");
		
		while (iter.hasNext()) {
			// 2.插入数据
			Tuple2<String, Integer> tuple2 = iter.next();
			String t1 = tuple2._1;
			Integer t2 = tuple2._2;
			// 2.1.数据放入到hbase中
			hbaseServer.insertData(cf, t1, "test", t2+"");
			
			// 2.2数据放入到es中
			XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
            jsonBuilder.startObject().field("content", t2+"").endObject();
            jsonBuilder.close();
            elasticSearchService.indexRecord(index, type , t1, jsonBuilder.string());
		}
		
		// 3.资源回收
		hbaseServer.closeTable();
	}
}
