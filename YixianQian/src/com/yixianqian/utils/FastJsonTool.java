package com.yixianqian.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class FastJsonTool {

	public FastJsonTool() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 把对象生成Json字符串
	 * 
	 * @param object
	 * @return
	 */
	public static String createJsonString(Object object) {
		String jsonString = JSON.toJSONString(object);
		return jsonString;
	}

	/**
	 * 完成对单个javaBean的解析
	 * @param jsonString要解析的字符串
	 * @param cls
	 * @return
	 */
	public static <T> T getObject(String jsonString, Class<T> cls) {
		T t = null;
		try {
			t = JSON.parseObject(jsonString, cls);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return t;
	}

	/**
	 * 完成对List列表的解析
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getObjectList(String jsonString, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		try {
			list = JSON.parseArray(jsonString, cls);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return list;
	}

	/**
	 * 完成对List列表套Map数据的解析
	 * @param jsonString
	 * @return
	 */
	public static List<Map<String, Object>> getObjectMap(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			// TODO: handle exception
		}

		return list;

	}

}
