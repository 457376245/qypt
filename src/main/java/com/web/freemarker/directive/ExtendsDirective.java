package com.web.freemarker.directive; 

import java.io.IOException;
import java.util.Map;

import freemarker.cache.TemplateCache;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 *继成指定模板.
 *<BR>
 *@extends : 继承其它模板，必须放在模板的最后面<BR>
 *（注：该指令完全等价于#include指令，只是为了提供统一的语义，即extends比include更好理解）
 * <P>
 * @author tang zheng yu
 * @version V1.0 2012-1-4
 * @createDate 2012-1-4 下午3:21:24
 * @modifyDate	 tang 2012-1-4 <BR>
 * @copyRight 亚信联创电信CRM研发部
 */
public class ExtendsDirective implements TemplateDirectiveModel {

	/** extends name */
	public static final String DIRECTIVE_NAME = "extends";

	public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {
		
		String name = DirectiveUtils.getRequiredParam(params, "name");
		String encoding = DirectiveUtils.getParam(params, "encoding",null);
		String includeTemplateName = TemplateCache.getFullTemplatePath(env, getTemplatePath(env), name);
		env.include(includeTemplateName, encoding, true);
	}

	/**
	 * 获取模板路径
	 * @param env Environment
	 * @return String 模板路径
	 */
	private String getTemplatePath(Environment env) {
		String templateName = env.getTemplate().getName();
        return templateName.lastIndexOf('/') == -1 ? "" : templateName.substring(0, templateName.lastIndexOf('/') + 1);
	}

}
