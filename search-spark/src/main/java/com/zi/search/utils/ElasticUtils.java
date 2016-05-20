package com.zi.search.utils;

import java.io.Serializable;
import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
/**
 * 操作es的接口具体实现
 * @author liuxing
 *
 */
public class ElasticUtils implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Client client = null;
	private final static Log logger = LogFactory.getLog(ElasticUtils.class);
	private final static String ip1 = PropertyUtils.getSystemProperties("elasticsearchip1");
	private final static String ip2 = PropertyUtils.getSystemProperties("elasticsearchip2");
	private final static int port = Integer.parseInt(PropertyUtils.getSystemProperties("elasticsearchport"));
	private final static Settings settings = Settings
			.settingsBuilder()
			.put("cluster.name", PropertyUtils.getSystemProperties("cluster.name"))
			.put("client.transport.sniff", PropertyUtils.getSystemProperties("client.transport.sniff"))
			.build();
	/**
	 * 初始化连接
	 */
	static {
		try {
			client = TransportClient
					.builder()
					.settings(settings)
					.build()  
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip1), port))
	                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip2), port));  
		} catch (Exception e) {
			logger.error("client init failed!");
			e.printStackTrace();
		}
	}
	/**
	 * 获取连接
	 * @return
	 */
	public static Client getClient(){
		return client;
	}
	/**
	 * 构造
	 * @param index 索引名称
	 * @param type 类型
	 */
	public static void createIndex(String index,String type){
		if(index == null || "".equals(index) || type == null || "".equals(type)){
			logger.error("索引或者类型不能为空！");
		}else{
			if(createIndex(index)){
				createMapping(index,type);
			}
		}
	}
	private static boolean createIndex(String index) {
		try{
			client
			.admin()
			.indices()
			.create(new CreateIndexRequest(index))
			.actionGet();
			return true;
		}catch(Exception e){
			logger.error(index+" 索引已经存在！");
			return false;
		}
	}

	private static void createMapping(String index, String type) {
		try {
			XContentBuilder builder = XContentFactory.jsonBuilder()
					.startObject()
						.startObject(type)
							.startObject("_all")
								.field("analyzer", "ik_max_word")
								.field("search_analyzer", "ik_max_word")
								.field("term_vector", "no")
								.field("store", "false")
							.endObject()
							.startObject("properties")
								.startObject("content")
									.field("type", "string")
									.field("store", "no")
									.field("term_vector", "with_positions_offsets")
									.field("analyzer", "ik_max_word")
									.field("search_analyzer", "ik_max_word")
									.field("include_in_all", "true")
									.field("boost", 8)
								.endObject()
							.endObject()
						.endObject()
					.endObject();
			PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);
			client.admin().indices().putMapping(mapping).actionGet();
		} catch (Exception e) {
			logger.error("create Mapping failed!");
			e.printStackTrace();
		}
	}
}
