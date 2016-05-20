package com.zi.search;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;

import com.zi.search.utils.PropertyUtils;

/**
 * @author liuxing
 *
 */
public class IKClient2 {
	public static void main(String args[]) throws IOException { 
		IKClient2 ikClient = new IKClient2();
//		ikClient.createIndex("index");
//		ikClient.createMapping("index", "type");
//		ikClient.createData("index", "type");
		ikClient.queryData("index", "type");
	}
	/**
	 * 获取链接
	 * @return
	 */
	public static Client getClient() {
		Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").build();
		Client client = null;
		try {
			client = TransportClient
					.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(PropertyUtils.getSystemProperties("elasticsearchip")), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return client;
	}
	/**
	 * 创建索引
	 * @param index 索引名称
	 */
	public void createIndex(String index) {
		getClient()
		.admin()
		.indices()
		.create(new CreateIndexRequest(index))
		.actionGet();
	}
	/**
	 * 创建mapping，和curl中完全对应，同样指定分析器为ik
	 * @param index 索引
	 * @param type	类型
	 * @throws IOException
	 */
	public void createMapping(String index, String type) throws IOException {
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
		getClient().admin().indices().putMapping(mapping).actionGet();
	}
	/**
	 * 索引一些数据，创建成功isCreated()返回true
	 * @param index
	 * @param type
	 */
	public void createData(String index, String type) {
		List<String> jsondata = IKClient2.getInitJsonData();
		for (int i = 0; i < jsondata.size(); i++) {
			IndexResponse indexResp = getClient()
					.prepareIndex()
					.setIndex(index)
					.setType(type)
					.setSource(jsondata.get(i))
					.execute().actionGet();
			boolean isCreated = indexResp.isCreated();
			System.out.println("是否成功创建数据isCreated:" + isCreated);
		}
	}
	/**
	 * 
	 * @return
	 */
	public static List<String> getInitJsonData() {
		List<String> list = new ArrayList<String>();
		list.add("{\"content\":\"{question_body_html:<table><tbody><tr><td><div>珊瑚礁当所处环境恶化时，失去了共生藻类的珊瑚虫会因为死亡而导致珊瑚礁逐渐“白化”，失去其鲜艳的色彩，那里生物多样性也锐减。这体现了生态工程什么原理（　 　 ）<table name=  optionsTable   cellpadding=  0   cellspacing=  0   width=  100%  ><tr><td width=  100%  >A．系统的结构决定功能原理</td></tr><tr><td width=  100%  >B．整体性原理</td></tr><tr><td width=  100%  >C．系统整体性原理</td></tr><tr><td width=  100%  >D．协调与平衡原理</td></tr></table></div></td></tr></tbody></table><script type='text/javascript' defer='defer'>window.addEventListener('load',function(){var imgArr =document.getElementsByTagName('img');if(imgArr.length >= 1){for(var i= 0;i < imgArr.length ;i++){var img = imgArr[i];var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;if(w<=0 || w=='NaN'){w=305;};if(img.width > w){img.setAttribute('width','100%');}img.setAttribute('max-width','100%');}} var tableArr = document.getElementsByTagName('TABLE');if(tableArr.length > 0){var tb = tableArr[0];               var text = tb.style.cssText + ' line-height:150%;-webkit-text-size-adjust:none;';tb.setAttribute('style',text);}    }) </script> ,  answer_analysis  :  <table style=  WORD-BREAK: break-all;   border=  0   cellspacing=  0   width=  100%  ><tr style=  margin-top: 2em  ><td><span style=  font-family: '应用字体 Regular', '应用字体';  >试题难度：</span><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/a83224b4a1a72da821ecd21e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/a83224b4a1a72da821ecd21e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/a83224b4a1a72da821ecd21e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/5d588f0279a556191bf9e96e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/5d588f0279a556191bf9e96e.png   /></td></tr><tr ><td style=  padding-top:1em;padding-bottom:1em;  ><p><span style=  font-family: '应用字体 Regular', '应用字体';  color:grey;  >解答：</span></p></td></tr> </table>试题分析：系统各组分之间要有适当比例关系，这样才能顺利完成能量、物质、信息的转换和流通，并且实现总体大于各部分之和的效果，为生态工程的系统整体性原理，题意体现的就是就是该原理，故C正确。</div><table style=  WORD-BREAK: break-all   border=  0   cellspacing=  0   width=  100%  ><tr><td style=  padding-top:1em  > <p><span style=  font-family: '应用字体 Regular', '应用字体'; color: grey;  >考点：</span></p></td></tr><tr><td style=  padding-top:1em  ><span style=  padding: 0.3em 1em 0.35em;line-height:2em; color:#000000;border-radius:1em     >生态工程的基本原理</span></td></tr><tr><td style=  padding-top:1em  ><span style=  padding: 0.3em 1em 0.35em;line-height:2em; color:#000000;border-radius:1em     >生态工程的实例和发展前景</span></td></tr></table><script type='text/javascript' defer='defer'>window.addEventListener('load',function(){var imgArr =document.getElementsByTagName('img');if(imgArr.length >= 1){for(var i= 0;i < imgArr.length ;i++){var img = imgArr[i];var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;if(w<=0 || w=='NaN'){w=305;};if(img.width > w){img.setAttribute('width','100%');}img.setAttribute('max-width','100%');}} var tableArr = document.getElementsByTagName('TABLE');if(tableArr.length > 0){var tb = tableArr[0];               var text = tb.style.cssText + ' line-height:150%;-webkit-text-size-adjust:none;';tb.setAttribute('style',text);}    }) </script> ,  question_body :   , know_analysis : { 99100477 :  生态工程的基本原理 , 99100478 :  生态工程的实例和发展前景 }} \"}");
		return list;
	}
	/**
	 * 查询数据方法
	 * @param index
	 * @param type
	 */
	public void queryData(String index, String type) {
		//	重启 ES 实例
		//	英文单词必须是小写  才能检索到
		QueryBuilder queryBuilder = QueryBuilders.termQuery("content", "百度");
		SearchResponse searchResponse = getClient()
				.prepareSearch(index)
				.setTypes(type)
				.setQuery(queryBuilder)
				.addHighlightedField("content")
				.setHighlighterPreTags("<span style=\"color:red;\">")
				.setHighlighterPostTags("</span>")
				.execute()
				.actionGet();
		SearchHits hits = searchResponse.getHits();
		System.out.println("查询到记录数:" + hits.getTotalHits());
		SearchHit[] searchHists = hits.getHits();
		for (SearchHit sh : searchHists) {
			String json = sh.getSourceAsString();
			System.out.println(json);
			// 获取对应的高亮域
			Map<String, HighlightField> result = sh.highlightFields();
			HighlightField titleField = result.get("content");
			// 取得定义的高亮标签
			Text[] titleTexts = titleField.fragments();
			// 为title串值增加自定义的高亮标签
			String title = "";
			for (Text text : titleTexts) {
				title += text.string();
			}
			// 将追加了高亮标签的串值重新填充到对应的对象
			// product.setTitle(title);
			// 打印高亮标签追加完成后的实体对象
			System.out.println(title);
			
			
		}
	}
}
