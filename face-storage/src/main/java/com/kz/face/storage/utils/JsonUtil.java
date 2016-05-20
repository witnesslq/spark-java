package com.kz.face.storage.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class JsonUtil {
	/**
	 * 创建json
	 * @param method 调用的方法
	 * @param status 返回状态码
	 * @param description 状态吗描述
	 * @param param 参数
	 * @return
	 */
	public static String getJson(String method,String status,String description) {
		JSONObject jsonObj = new JSONObject();

		Map <String, String> returnvalue = new HashMap <String, String>();
		returnvalue.put("code", status+"");
		returnvalue.put("description", description);
		
		jsonObj.put("returnvalue",returnvalue);
		jsonObj.put("methodname",method);
		
		return jsonObj.toString();
	}
}
