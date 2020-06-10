package com.web.freemarker.directive; 

import java.io.IOException;
import java.util.Map;

import com.web.freemarker.directive.OverrideDirective.TemplateDirectiveBodyOverrideWraper;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 父模板分块，让子模板继成.
 * <BR>
 *定义块，可以被子模板用@override指令覆盖显示,没有被override则显示自身的内容.
 * <P>
 * @author tang zheng yu
 * @version V1.0 2012-1-4
 * @createDate 2012-1-4 下午3:21:24
 * @modifyDate	 tang 2012-1-4 <BR>
 * @copyRight 亚信联创电信CRM研发部
 */
public class BlockDirective implements TemplateDirectiveModel{
	/** block name */
	public static final String DIRECTIVE_NAME = "block";

	public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {
		String name = DirectiveUtils.getRequiredParam(params, "name");
		TemplateDirectiveBodyOverrideWraper overrideBody = DirectiveUtils.getOverrideBody(env, name);
		if(overrideBody == null) {
			if(body != null) {
				body.render(env.getOut());
			}
		}else {
			DirectiveUtils.setTopBodyForParentBody(env, new TemplateDirectiveBodyOverrideWraper(
					body,env), overrideBody);
			overrideBody.render(env.getOut());
		}
	}
		
}
