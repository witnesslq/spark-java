package com.zi.search;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Unit test for simple App.
 */
@SuppressWarnings("deprecation")
public class HttpClientTest{
	@SuppressWarnings({ "resource", "unused" })
	public static void main(String[] args) {
		try {
			// 创建HttpClient实例     
			HttpClient httpclient = new DefaultHttpClient();  
			// 创建Get方法实例     
			HttpGet httpgets = new HttpGet("http://192.168.21.57:9200/app/article/_search?  -d'{\"query\" : { \"term\" : { \"article.title\" : \"es\" }}}'");    
			HttpResponse response = httpclient.execute(httpgets);
			HttpEntity entity = response.getEntity();    
			if (entity != null) {    
				InputStream instreams = entity.getContent();    
				System.out.println("Do something");   
				// Do not need the rest    
				httpgets.abort();    
			}  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}
}
