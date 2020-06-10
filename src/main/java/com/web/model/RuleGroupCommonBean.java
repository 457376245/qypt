package com.web.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.GroupCommonBean.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>规则组（就是具体的任务、活动等）<br>
 * <b>创建时间：</b>2020年3月23日-下午2:29:21<br>
 */

public class RuleGroupCommonBean implements Serializable{
	
	private static final long serialVersionUID = -3523949951947266222L;

	private Long ruleGroupId;
	
	/**整体规则方式*/	
	private String ruleMode;
	
	/**参与规则方式*/	
	private String showRuleMode;

	/**具体的规则列表*/
	private List<RuleCommonBean> ruleCommonList;
	
	/**将规则List转成键值对的形式*/
	private Map<String, Object> ruleMap;
	
	public Long getRuleGroupId() {
		return ruleGroupId;
	}

	public void setRuleGroupId(Long ruleGroupId) {
		this.ruleGroupId = ruleGroupId;
	}

	public String getRuleMode() {
		return ruleMode;
	}

	public void setRuleMode(String ruleMode) {
		this.ruleMode = ruleMode;
	}

	public String getShowRuleMode() {
		return showRuleMode;
	}

	public void setShowRuleMode(String showRuleMode) {
		this.showRuleMode = showRuleMode;
	}

	public List<RuleCommonBean> getRuleCommonList() {
		return ruleCommonList;
	}

	public void setRuleCommonList(List<RuleCommonBean> ruleCommonList) {
		this.ruleCommonList = ruleCommonList;
	}

	public Map<String, Object> getRuleMap() {
		return ruleMap;
	}

	public void setRuleMap(Map<String, Object> ruleMap) {
		this.ruleMap = ruleMap;
	}
	
}
