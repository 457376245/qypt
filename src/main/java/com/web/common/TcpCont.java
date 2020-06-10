package com.web.common;

import java.util.Map;

import com.web.freemarker.FreemarkerHandler;
import com.web.util.StringUtil;

public class TcpCont {
	
	public static String buildInParam(Map<String, Object> inMap, String fileName) {
		FreemarkerHandler fh = FreemarkerHandler.getInstance();
		fh.setTemplateLoaderPaths("/com/web/parameter");
		String strVal = fh.processTemplate(TcpCont.class,fileName+".html", inMap);
		return strVal;
	}
	
	/**
	 * 根据入参模板生成入参字符串(json)
	 * @param inMap 入参数据集
	 * @param ftlName 入参模板名称
	 * @return 根据模板生成的xml串
	 */
	public static String buildInParamForHtml(String htmlPath,Map<String, Object> inMap, String fileName) {
		FreemarkerHandler fh = FreemarkerHandler.getInstance();
		
		if(StringUtil.isEmptyStr(htmlPath)){
			htmlPath="/com/web/paramete";
		}
		
		fh.setTemplateLoaderPaths(htmlPath);
		String xml = fh.processTemplate(TcpCont.class,fileName+".html", inMap);
		return xml;
	}

}
