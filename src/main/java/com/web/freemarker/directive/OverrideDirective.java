package com.web.freemarker.directive; 

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 覆盖指定区域模板.
 * <BR>
 * 重定义@block指令显示的内容.
 * <P>
 * @author tang zheng yu
 * @version V1.0 2012-1-4
 * @createDate 2012-1-4 下午3:21:24
 * @modifyDate	 tang 2012-1-4 <BR>
 * @copyRight 亚信联创电信CRM研发部
 */
public class OverrideDirective implements TemplateDirectiveModel {

	/** 变更名. */
	public  static final String DIRECTIVE_NAME = "override";

	public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {
		String name = DirectiveUtils.getRequiredParam(params, "name");
		String overrideVariableName = DirectiveUtils.getOverrideVariableName(name);
		
		TemplateDirectiveBodyOverrideWraper override = DirectiveUtils.getOverrideBody(env, name);
		TemplateDirectiveBodyOverrideWraper current = new TemplateDirectiveBodyOverrideWraper(body,env);
		if(override == null) {
			env.setVariable(overrideVariableName, current);
		}else {
			DirectiveUtils.setTopBodyForParentBody(env, current, override);
		}
	}
	/**
	 * body override.
	 * @author tang
	 *
	 */
	static class TemplateDirectiveBodyOverrideWraper
	 implements TemplateDirectiveBody,TemplateModel{
		/** parent Body */
		private TemplateDirectiveBodyOverrideWraper parentBody;
		/** Environment  */
		private Environment env;
		/** TemplateDirectiveBody  */
		private TemplateDirectiveBody body;
		/**
		 * 重包装
		 * @param body TemplateDirectiveBody
		 * @param env Environment
		 */
		public TemplateDirectiveBodyOverrideWraper(TemplateDirectiveBody body,
				Environment env) {
			super();
			this.body = body;
			this.env = env;
		}

		public void render(Writer out) throws TemplateException, IOException {
			if(body == null){ return;}
			TemplateDirectiveBodyOverrideWraper preOverridy = 
					(TemplateDirectiveBodyOverrideWraper)env.getVariable(DirectiveUtils.OVERRIDE_CURRENT_NODE);
			try {
				env.setVariable(DirectiveUtils.OVERRIDE_CURRENT_NODE, this);
				body.render(out);
			}finally {
				env.setVariable(DirectiveUtils.OVERRIDE_CURRENT_NODE, preOverridy);
			}
		}
		public TemplateDirectiveBodyOverrideWraper getParentBody() {
			return parentBody;
		}

		public void setParentBody(TemplateDirectiveBodyOverrideWraper parentBody) {
			this.parentBody = parentBody;
		}

		public TemplateDirectiveBody getBody() {
			return body;
		}

		public void setBody(TemplateDirectiveBody body) {
			this.body = body;
		}
	}
	
}
