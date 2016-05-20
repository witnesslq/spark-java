package com.zi.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author liuxing
 *
 */
@SuppressWarnings("deprecation")
public class IKHttpClient {
	private static final String json = "{\"query\":{\"term\":{\"content\":\"中国\"}},\"from\": 0,\"size\": 2,\"highlight\":{\"pre_tags\":[\"<font color=\'red\'>\"],\"post_tags\": [\"</font>\"],\"fields\":{\"content\":{}}}}";

	@SuppressWarnings("resource")
	public static void main(String[] args) throws ClientProtocolException, IOException {
		// 创建HttpClient实例
		HttpClient httpclient = new DefaultHttpClient();
		// 创建Post方法实例
		HttpPost httpPost = new HttpPost("http://192.168.21.137:9200/index/_search?pretty");
		StringEntity se = new StringEntity(json);
		httpPost.setEntity(se);

		HttpResponse response = httpclient.execute(httpPost);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instreams = entity.getContent();
			String str = convertStreamToString(instreams);
			System.out.println(str);
			httpPost.abort();
		}
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
