package com.web.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.RuleBean.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>规则<br>
 * <b>创建时间：</b>2020年3月23日-下午2:31:46<br>
 */

public class RuleCommonBean implements Serializable{

	private static final long serialVersionUID = -901249142540497388L;
	
	/**规则ID*/
	private Long ruleId;
	
	/**任务ID*/
	private Long ruleGroupId;
	
	/**规则编码*/
	private String ruleCode;
	
	/**规则名称*/
	private String ruleTitle;
	
	/**规则描述*/
	private String ruleDesc;
	
	/**添加时间*/
	private Date addTime;
	
	/**状态时间*/
	private Date statusDate;
	
	/**修改时间*/
	private Date updateDate;
	
	/**状态*/
	private String statusCd;
	
	/**规则类型*/
	private String ruleType;
	
	/**是否是控制显示规则*/
	private String isShow;
	
	/**规则数据*/	
	private String ruleData;
	
	/**规则方式*/
	private String ruleMode;
	
	/**规则序列*/
	private Long sortSeq;
	
	/**规则数据集合*/
	private List<CoRuleData> ruleDatalist;

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public Long getRuleGroupId() {
		return ruleGroupId;
	}

	public void setRuleGroupId(Long ruleGroupId) {
		this.ruleGroupId = ruleGroupId;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleTitle() {
		return ruleTitle;
	}

	public void setRuleTitle(String ruleTitle) {
		this.ruleTitle = ruleTitle;
	}

	public String getRuleDesc() {
		return ruleDesc;
	}

	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getRuleData() {
		return ruleData;
	}

	public void setRuleData(String ruleData) {
		this.ruleData = ruleData;
	}

	public String getRuleMode() {
		return ruleMode;
	}

	public void setRuleMode(String ruleMode) {
		this.ruleMode = ruleMode;
	}

	public Long getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(Long sortSeq) {
		this.sortSeq = sortSeq;
	}

	public List<CoRuleData> getRuleDatalist() {
		return ruleDatalist;
	}

	public void setRuleDatalist(List<CoRuleData> ruleDatalist) {
		this.ruleDatalist = ruleDatalist;
	}
	
}
