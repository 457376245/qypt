package com.web.freemarker.directive; 

import java.util.Map;


import org.apache.commons.lang3.StringUtils;

import com.web.freemarker.directive.OverrideDirective.TemplateDirectiveBodyOverrideWraper;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * 函数工具类.
 * <P>
 * @author tang zheng yu
 * @version V1.0 2012-1-4
 * @createDate 2012-1-4 下午3:21:24
 * @modifyDate	 tang 2012-1-4 <BR>
 * @copyRight 亚信联创电信CRM研发部
 */
public class DirectiveUtils {
	/** BLOCK name  __ftl_override__ */
	public static final String BLOCK = "__ftl_override__";
	/**    __ftl_override_current_node */
	public static final String OVERRIDE_CURRENT_NODE = "__ftl_override_current_node";
	/**
	 * 返回 Override  Name
	 * @param name name
	 * @return String __ftl_override__+name
	 */
	public static String getOverrideVariableName(String name) {
		return BLOCK + name;
	}
	/**
	 * 增加函数方法
	 * @param conf Configuration
	 */
	public static void exposeRapidMacros(Configuration conf) {
		conf.setSharedVariable(BlockDirective.DIRECTIVE_NAME, new BlockDirective());
		conf.setSharedVariable(ExtendsDirective.DIRECTIVE_NAME, new ExtendsDirective());
		conf.setSharedVariable(OverrideDirective.DIRECTIVE_NAME, new OverrideDirective());
		conf.setSharedVariable(SuperDirective.DIRECTIVE_NAME, new SuperDirective());
	}
	/**
	 * 获取参数值
	 * @param params 参数
	 * @param key key
	 * @return  String RequiredParam
	 * @throws TemplateException
	 */
	static String getRequiredParam(Map params,String key) throws TemplateException {
		Object value = params.get(key);
		if(value == null || StringUtils.isEmpty(value.toString())) {
			throw new TemplateModelException("not found required parameter:"+key+" for directive");
		}
		return value.toString();
	}
	/**
	 * 获取参数值
	 * @param params 参数
	 * @param key key
	 * @param defaultValue 默认值
	 * @return String 参数值
	 * @throws TemplateException
	 */
	static String getParam(Map params,String key,String defaultValue)
			throws TemplateException {
		Object value = params.get(key);
		return value == null ? defaultValue : value.toString();
	}
	/**
	 *  获取　OverrideBody
	 * @param env Environment
	 * @param name 名称
	 * @return TemplateDirectiveBodyOverrideWraper 获取　OverrideBody
	 * @throws TemplateModelException
	 */
	static TemplateDirectiveBodyOverrideWraper getOverrideBody(Environment env, String name) 
			throws TemplateModelException {
		TemplateDirectiveBodyOverrideWraper value = (TemplateDirectiveBodyOverrideWraper)env.getVariable(
				DirectiveUtils.getOverrideVariableName(name));
		return value;
	}
	/**
	 * 设置parent body
	 * @param env Environment
	 * @param topBody 父body
	 * @param overrideBody overrideBody
	 */
	static void setTopBodyForParentBody(Environment env,
			TemplateDirectiveBodyOverrideWraper topBody,
			TemplateDirectiveBodyOverrideWraper overrideBody) {
		TemplateDirectiveBodyOverrideWraper parent = overrideBody;
		while(parent.getParentBody() != null) {
			parent = parent.getParentBody();
		}
		parent.setParentBody(topBody);
	}
}
