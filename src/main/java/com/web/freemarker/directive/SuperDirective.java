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
 * 继成父模板的块内容，类似nestd.
 * <P>
 * @author tang zheng yu
 * @version V1.0 2012-1-4
 * @createDate 2012-1-4 下午3:21:24
 * @modifyDate	 tang 2012-1-4 <BR>
 * @copyRight 亚信联创电信CRM研发部
 */
public class SuperDirective implements TemplateDirectiveModel{
	/** super name */
	public static final String DIRECTIVE_NAME = "super";

	public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {
		
		TemplateDirectiveBodyOverrideWraper current = (TemplateDirectiveBodyOverrideWraper)env.getVariable(
				DirectiveUtils.OVERRIDE_CURRENT_NODE);
		if(current == null) {
			throw new TemplateException("<@super/> direction must be child of override", env);
		}
		TemplateDirectiveBody parent = current.getParentBody();
		if(parent == null) {
			throw new TemplateException("not found parent for <@super/>", env);
		}
		parent.render(env.getOut());
		
	}
	
}
