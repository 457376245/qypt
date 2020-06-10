package com.web.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.util.common.Log;

/**
 * 
 * @author chenhr
 * @since 2013-4-10
 * @version 1.0.0
 */

public final class JacksonUtil {

	private final static Log log = Log.getLog(JacksonUtil.class);

	private static ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();
	}

	public final static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * 将对象转换为json字符串.
	 * @param obj 对象
	 * @return String json字符串
	 */
	public final static String objectToJson(Object object) {
		if (null == object) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(object);
		} catch (IOException e) {
			log.warn("parse object to json string error:", e);
			return null;
		}
	}
	
	/**
	 * 将json字符串转换为对象
	 * @param jsonstr
	 * @param clazz
	 * @return
	 * @throws Exception 
	 */
	public static Object jsonToObject(String jsonstr,Class<?> clazz) throws Exception{
		
		if (StringUtil.isEmptyStr(jsonstr)) {
			return null;
		}
		
		return objectMapper.readValue(jsonstr, clazz);
		
	}

}
