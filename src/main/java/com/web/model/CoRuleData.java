package com.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>项目名称：</b>qy-portal<br>
 * <b>类名称：</b>com.web.model.CoRuleData.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>规则数据集合<br>
 * <b>创建时间：</b>2020年4月13日-上午11:37:44<br>
 */

public class CoRuleData implements Serializable{

	private static final long serialVersionUID = 9222411443004232635L;
	
	/**表达式ID*/
	private Long expId;	
	
	/**规则ID*/	
	private Long ruleId;
	
	/**规则数据*/	
	private String ruleData;
	
	/**规则类型*/	
	private String ruleType;
	
	/**添加时间*/	
	private Date addTime;
	
	/**状态时间*/
	private Date statusDate;
	
	/**修改时间*/	
	private Date updateDate;
	
	/**状态*/	
	private String statusCd;
	
	/*规则序列*/	
	private Long sortSeq;
	
	private String regionName;
	
	private String parentRegionName;

	public Long getExpId() {
		return expId;
	}

	public void setExpId(Long expId) {
		this.expId = expId;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleData() {
		return ruleData;
	}

	public void setRuleData(String ruleData) {
		this.ruleData = ruleData;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
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

	public Long getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(Long sortSeq) {
		this.sortSeq = sortSeq;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getParentRegionName() {
		return parentRegionName;
	}

	public void setParentRegionName(String parentRegionName) {
		this.parentRegionName = parentRegionName;
	}
	
}

