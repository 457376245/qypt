package com.web.filter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.JavaType;
import com.web.util.JsonUtil;

/**
 * 
 * <b>项目名称：</b>ocPortal<br>
 * <b>类名称：</b>com.ai.ec.mall.filter.XssMappingJacksonHttpMessageConverter<br>
 * <b>类描述：</b>(用于过滤json请求的@RequestBody对象的xss)<br>
 * <b>创建人：</b>oc<br>
 * <b>创建时间：</b>2016年12月30日-下午5:12:35<br>
 * <b>@Copyright:</b>2016-亚信
 *
 */
public class XssMappingJacksonHttpMessageConverter extends
		MappingJackson2HttpMessageConverter {
	/**
	 * 不需要xss过滤的路径
	 */
	private String exclude = null; // 不需要过滤的路径集合
	@SuppressWarnings("unused")
	private Pattern pattern = null; // 匹配不需要过滤路径的正则表达式

	// XSS处理Map
	private static Map<String, String> xssMap = new LinkedHashMap<String, String>();

	static {
		// 含有脚本： script
		xssMap.put("[s|S][c|C][r|R][i|C][p|P][t|T]", "");
		// 含有脚本 javascript
		xssMap.put( "[\\\"\\\'][\\s]*[j|J][a|A][v|V][a|A][s|S][c|C][r|R][i|I][p|P][t|T]:(.*)[\\\"\\\']", "\"\"");
		// 含有函数： eval
		xssMap.put("[e|E][v|V][a|A][l|L]\\((.*)\\)", "");
		// 含有符号 <
		xssMap.put("<", "&lt;");
		// 含有符号 > 
		xssMap.put(">", "&gt;");
		// 含有符号 (
		xssMap.put("\\(", "(");
		// 含有符号 )
		xssMap.put("\\)", ")");
		// 含有符号 '
		xssMap.put("'", "'");
		//
		xssMap.put("eval\\((.*)\\)", "");
	}


	public void setExclude(String exclude) {
		this.exclude = exclude;
		pattern = Pattern.compile(getRegStr(this.exclude));
	}

	// 重写该方法，我们只需要加上Object tempObj = this.process( obj, type, inputMessage );
	// 并返回tempObj,process方法里面我们过滤白名单和进行xss处理
	public Object read(Type type, Class<?> contextClass,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {

		JavaType javaType = getJavaType(type, contextClass);

		Object obj = readJavaType(javaType, inputMessage);

		Object tempObj = this.process(obj, type, inputMessage);

		return tempObj;
	}

	// 这个就是父类的readJavaType方法，由于父类该方法是private的，所以我们copy一个用
	private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
		try {
			return super.getObjectMapper().readValue(inputMessage.getBody(),
					javaType);
		} catch (IOException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: "
					+ ex.getMessage(), ex);
		}
	}

	protected Object process(Object obj, Type type,
			HttpInputMessage inputMessage) {
		if (this.isNeedProcess(inputMessage)) {
			return this.readAfter(obj, type);
		} else {
			return obj;
		}
	}

	/**
	 * 将传递进来的不需要过滤得路径集合的字符串格式化成一系列的正则规则
	 * 
	 * @param str
	 *            不需要过滤的路径集合
	 * @return 正则表达式规则
	 * */
	private String getRegStr(String str) {
		if (StringUtils.isNotBlank(str)) {
			String[] excludes = str.split(";"); // 以分号进行分割
			int length = excludes.length;
			for (int i = 0; i < length; i++) {
				String tmpExclude = excludes[i];
				// 对点、反斜杠和星号进行转义
				tmpExclude = tmpExclude.replace("\\", "\\\\")
						.replace(".", "\\.").replace("*", ".*");

				tmpExclude = "^" + tmpExclude + "$";
				excludes[i] = tmpExclude;
			}
			return StringUtils.join(excludes, "|");
		}
		return str;
	}

	// 根据白名单，判断当前请求路径是否需要xss过滤
	protected boolean isNeedProcess(HttpInputMessage inputMessage) {

		String url = "";

		try {
			/*
			 * //
			 * 经过debug发现inputMessage类型为ServletServerHttpRequest，所以进行下类型转换，这个会转换出错
			 * ，暂时不设置过滤 ServletServerHttpRequest request =
			 * (ServletServerHttpRequest) inputMessage;
			 * 
			 * url = request.getURI().getPath();
			 * 
			 * // 根据白名单做下匹配 if (pattern.matcher(url).matches()) { return false;
			 * } else { return true; }
			 */
			return true;
		} catch (Exception e) {
			logger.error("BACK_ERROR," + this.getClass().getCanonicalName()
					+ ",XSS处理-url处理失败,url=" + url + ",ERROR=", e);
			return true;
		}

	}

	// 最重要的一步，进行xss过滤
	@SuppressWarnings("unchecked")
	public Object readAfter(Object obj, Type type) {
		try {
			// type实际上就是我们需要convert的model，我们通过反射来完成根据NeedXss注解对String
			String objJson = JsonUtil.toString(obj);
			objJson = cleanXSS(objJson);
			obj = JsonUtil.toObject(objJson, obj.getClass());
			return obj;
			/*
			 * if(obj instanceof HashMap){ Map<String, Object> temp =
			 * JsonUtil.toObject(JsonUtil.toString(obj), Map.class);
			 * Iterator<String> iterator=temp.keySet().iterator(); while
			 * (iterator.hasNext()) { String key=iterator.next(); Object
			 * value=temp.get(key); temp.put(key,
			 * cleanXSS(String.valueOf(value))); } return temp; }
			 */
		} catch (Exception e) {
			logger.error("BACK_ERROR," + this.getClass().getCanonicalName()
					+ ",XSS处理失败,obj=" + JsonUtil.toString(obj) + ",javaType="
					+ JsonUtil.toString(type) + ",ERROR=", e);
			return obj;
		}

		// return obj;
	}

	private String cleanXSS(String value) {

		if (value == null || value.isEmpty()) {
			return value;
		}
		Set<String> keySet = xssMap.keySet();
		for (String key : keySet) {
			String v = xssMap.get(key);
			value = value.replaceAll(key, v);
		}
		return value;
		// You'll need to remove the spaces from the html entities below
		/*value = value.replaceAll("<", " ").replaceAll(">", " ");
		value = value.replaceAll("\\(", " ").replaceAll("\\)", " ");
		value = value.replaceAll("'", " ");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']",
				"\"\"");
		value = value.replaceAll("script", "");
		return value;*/
	}

	@Override
	protected Object readInternal(Class<? extends Object> arg0,
			HttpInputMessage arg1) throws IOException,
			HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExclude() {
		return exclude;
	}

}
