package com.zi.search.service.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.zi.search.service.SearchService;
import com.zi.search.utils.PropertyUtils;
/**
 * 操作es的接口具体实现
 * @author liuxing
 *
 */
public class SearchServiceImpl implements SearchService{
	private static Client client = null;
	private final static Log logger = LogFactory.getLog(SearchServiceImpl.class);
	private final static String ip1 = PropertyUtils.getSystemProperties("elasticsearchip1");
	private final static String ip2 = PropertyUtils.getSystemProperties("elasticsearchip2");
	private final static int port = Integer.parseInt(PropertyUtils.getSystemProperties("elasticsearchport"));
	private final static Settings settings = Settings
			.settingsBuilder()
			.put("cluster.name", PropertyUtils.getSystemProperties("cluster.name"))
			.put("client.transport.sniff", PropertyUtils.getSystemProperties("client.transport.sniff"))
			.build();
	

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

	public SearchServiceImpl(){}
	/**
	 * 构造
	 * @param index 索引名称
	 * @param type 类型
	 */
	public SearchServiceImpl(String index,String type){
		if(index == null || "".equals(index) || type == null || "".equals(type)){
			logger.error("索引或者类型不能为空！");
		}else{
			if(createIndex(index)){
				createMapping(index,type);
			}
		}
	}
	
	@Override
	public void init() {
		try {
			if(client == null){
				for(int i = 0 ; i < 3 ; i++){
					logger.info("rebuilt client "+i+" times");
					client = TransportClient
							.builder()
							.settings(settings)
							.build()  
							.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip1), port))
							.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip2), port));
					if(client!=null){
						logger.info("rebuilt client success");
						break;
					}else{
						Thread.sleep(1000);
					}
				}
			}
		} catch (Exception e) {
			logger.error("client rebuilt failed!");
			e.printStackTrace();
		}
	}

	@Override
	public boolean createIndex(String index) {
		try{
			init();
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

	@Override
	public void createMapping(String index, String type) {
		init();
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

	@Override
	public void createData(String index, String type,List<String> dataList) {
		init();
		for (int i = 0; i < dataList.size(); i++) {
					client
					.prepareIndex()
					.setIndex(index)
					.setType(type)
					.setSource(dataList.get(i))
					.execute().actionGet();
		}
	}
	
	@Override
	public void createDataBatch(String index, String type, List<String> dataList) {
		try{
			init();
			List<IndexRequest> requests = new ArrayList<IndexRequest>();
			for (int i = 0; i < dataList.size(); i++) {
				IndexRequest request = client
						.prepareIndex(index, type)
						.setSource(dataList.get(i))
						.request();
				requests.add(request);
				//每10000条数据刷新一次
				if((i+1) % 5000 == 0){
					BulkRequestBuilder bulkRequest = client.prepareBulk();
					for (IndexRequest req : requests) {
						bulkRequest.add(req);
					}
					BulkResponse bulkResponse = bulkRequest.execute().actionGet();
					if (bulkResponse.hasFailures()) {
						logger.error("createDataBatch failed!");
					}
					//清空集合，重新添加数据
					requests.clear();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void createDataBatch(String index, String type, Map<String, String> dataMap) {

        try {
            init();
            List<IndexRequest> requests = new ArrayList<IndexRequest>();
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                IndexRequest request = client.prepareIndex(index, type, entry.getKey()).setSource(entry.getValue()).request();
                bulkRequest.add(request);
               /*一条一条插入数据
               count++;
                IndexRequestBuilder requestBuilder = client.prepareIndex(index,
                        type, entry.getKey()).setRefresh(true);
                try {
                    requestBuilder.setSource(entry.getValue())
                            .execute().actionGet();
                } catch (Exception ex0) {
                    System.out.println(entry.getKey() + "  =======  " + entry.getValue());
                } finally {
                    System.out.println("==========> " + count);
                }*/
            }
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                Iterator<BulkItemResponse> iter = bulkResponse.iterator();
                while (iter.hasNext()) {
                    BulkItemResponse itemResponse = iter.next();
                    if (itemResponse.isFailed()) {
                        logger.error(itemResponse.getFailureMessage());
                    }
                }
            }
            //清空集合，重新添加数据
            requests.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Override
	public String queryData(String index, String type,String field,int start,int size) {
		init();
		QueryBuilder queryBuilder = QueryBuilders
				.matchQuery("content", new String[]{field})
				.operator(MatchQueryBuilder.Operator.AND);
		SearchResponse searchResponse = client
				.prepareSearch(index)
				.setTypes(type)
				.setQuery(queryBuilder)
				.setFrom(start)
				.setSize(size)
				.addHighlightedField("content")
				.setHighlighterPreTags("<span style=\"color:red;\">")
				.setHighlighterPostTags("</span>")
				.execute()
				.actionGet();
		System.out.println(searchResponse.toString());
		return searchResponse.toString();
	}
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		SearchService ikClient = new SearchServiceImpl("","");
//		long one = System.currentTimeMillis();
//		ikClient.queryData("question", "bank","数学 语文",0,10);
//		long two = System.currentTimeMillis();
//		System.err.println(two - one+" ms==============================================>");
//		ikClient.queryData("question", "bank","珊瑚",0,10);
//		long three = System.currentTimeMillis();
//		System.err.println(three- two+" ms==============================================>");
//		ikClient.queryData("question", "bank","珊瑚",0,10);
//		System.err.println(System.currentTimeMillis()- three+" ms==============================================>");
	
	}
}
