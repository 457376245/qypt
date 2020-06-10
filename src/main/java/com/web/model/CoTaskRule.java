package com.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoTaskRule.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务规则<br>
 * <b>创建时间：</b>2020年3月16日-上午11:03:10<br>
 */

public class CoTaskRule implements Serializable{

	private static final long serialVersionUID = -4409599444998704458L;
	
	/**规则ID*/
	private Long ruleId;
	
	/**任务ID*/
	private Long taskId;
	
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
	
	private String ruleDataSymbol;
	
	private String ruleDataSub;

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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

	public String getRuleDataSymbol() {
		return ruleDataSymbol;
	}

	public void setRuleDataSymbol(String ruleDataSymbol) {
		this.ruleDataSymbol = ruleDataSymbol;
	}

	public String getRuleDataSub() {
		return ruleDataSub;
	}

	public void setRuleDataSub(String ruleDataSub) {
		this.ruleDataSub = ruleDataSub;
	}
	
}
