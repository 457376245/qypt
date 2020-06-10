package com.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoTaskReward.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务奖励关联<br>
 * <b>创建时间：</b>2020年3月16日-下午4:07:18<br>
 */

public class CoTaskReward implements Serializable{

	private static final long serialVersionUID = -3343148457993623753L;
	
	/**奖励ID*/	
	private Long rewardId;
	
	/**任务ID*/	
	private Long taskId;
	
	/**奖品ID*/	
	private Long prizeId;
	
	/**奖品数量*/	
	private Integer userPrizeCount;
	
	/**添加时间*/
	private Date addTime;
	
	/**状态时间*/	
	private Date statusDate;
	
	/**修改时间*/	
	private Date updateDate;
	
	/**状态*/	
	private String statusCd;
	
	/**新建工号*/
	private Long staffId;	
	
	/**最后编辑工号*/	
	private Long editStaffId;
	
	/**奖品名称*/
	private String prizeTitle;
	
	/**奖品类型*/
	private String prizeType;
	
	/**奖品简介*/
	private String prizeDesc;
	
	/**剩余奖品总量*/
	private Long prizeCount;
	
	private String ruleMode;
	
	private Long prizeStock;
	
	private String productCode;

	public Long getRewardId() {
		return rewardId;
	}

	public void setRewardId(Long rewardId) {
		this.rewardId = rewardId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(Long prizeId) {
		this.prizeId = prizeId;
	}

	public Integer getUserPrizeCount() {
		return userPrizeCount;
	}

	public void setUserPrizeCount(Integer userPrizeCount) {
		this.userPrizeCount = userPrizeCount;
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

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public Long getEditStaffId() {
		return editStaffId;
	}

	public void setEditStaffId(Long editStaffId) {
		this.editStaffId = editStaffId;
	}

	public String getPrizeTitle() {
		return prizeTitle;
	}

	public void setPrizeTitle(String prizeTitle) {
		this.prizeTitle = prizeTitle;
	}

	public String getPrizeType() {
		return prizeType;
	}

	public void setPrizeType(String prizeType) {
		this.prizeType = prizeType;
	}

	public String getPrizeDesc() {
		return prizeDesc;
	}

	public void setPrizeDesc(String prizeDesc) {
		this.prizeDesc = prizeDesc;
	}

	public Long getPrizeCount() {
		return prizeCount;
	}

	public void setPrizeCount(Long prizeCount) {
		this.prizeCount = prizeCount;
	}

	public String getRuleMode() {
		return ruleMode;
	}

	public void setRuleMode(String ruleMode) {
		this.ruleMode = ruleMode;
	}

	public Long getPrizeStock() {
		return prizeStock;
	}

	public void setPrizeStock(Long prizeStock) {
		this.prizeStock = prizeStock;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
}
