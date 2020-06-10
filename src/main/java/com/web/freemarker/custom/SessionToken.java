package com.web.freemarker.custom;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * TODO 类 概述 .
 * <BR>
 *  TODO 要点概述.
 * <P>
 * @author tang zheng yu
 * @version V1.0 2012-3-21
 * @createDate 2012-3-21 下午10:27:15
 * @modifyDate	 tang 2012-3-21 <BR>
 * @copyRight 亚信联创电信CRM研发部
 */
public class SessionToken  implements TemplateDirectiveModel{
	/** block name */
	public static final String TOKEN_NAME = "token";
	public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {
		HttpRequestHashModel requestHashModel=(HttpRequestHashModel)env.getDataModel().get(FreemarkerServlet.KEY_REQUEST);
		String token="";
		if(requestHashModel!=null){
			System.out.println(requestHashModel.getRequest().getRequestURL());
		} else {
			System.out.println("requestHashModel");
		}

		env.getOut().write(String.valueOf(token));
		

	}
		
}

