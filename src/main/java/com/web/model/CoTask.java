package com.web.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoTask.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务列表<br>
 * <b>创建时间：</b>2020年3月16日-上午10:11:06<br>
 */

public class CoTask implements Serializable{

	private static final long serialVersionUID = 1407487257003141680L;
	
	/**任务ID*/
	private Long taskId;

	/**组ID*/
	private Long groupId;
	
	/**父ID*/
	private Long parentId;
	
	/**任务编码*/
	private String taskCode;
	
	/**任务名称*/
	private String taskName;
	
	/**任务描述*/
	private String taskDesc;
	
	/**任务类型*/
	private String taskType;
	
	/**奖励描述*/
	private String prizeDesc;
	
	/**添加时间*/
	private Date addTime;
	
	private String addDate;
	
	/**状态时间*/	
	private Date statusDate;
	
	/**修改时间*/	
	private Date updateDate;
	
	/**状态*/
	private String statusCd;
	
	/**展示时间*/
	private Date showTime;
	
	/**开始时间*/
	private Date startTime;
	
	/**结束时间*/	
	private Date endTime;
	
	/**展示时间*/
	private String showDate;
	
	/**开始时间*/
	private String startDate;
	
	/**结束时间*/	
	private String endDate;
	
	/**周期类型*/	
	private String cycleType;
	
	/**周期值*/	
	private Integer cycleVal;
	
	/**参与类型*/	
	private String inType;	
	
	/**规则数量*/	
	private Integer ruleCount;
	
	/**排序*/	
	private Long sortSeq;
	
	/**整体规则方式*/	
	private String ruleMode;
	
	/**参与规则方式*/	
	private String showRuleMode;
	
	/**新建工号*/	
	private Long staffId;
	
	/**最后编辑工号*/
	private Long editStaffId;
	
	/**图片编码*/
	private String imgCode;
	
	/**任务跳转地址*/
	private String taskUrl;
	
	/**动作标签*/
	private String actTabTitle;
	
	/**完成标签*/	
	private String comTabTitle;
	
	/**促销标签*/
	private String sellTabTitle;
	
	/**是否已经完成过任务*/
	private String isReceiveReward;
	
	/**任务是否开始*/
	private String isTaskStart;
	
	/**归属的任务分组名称*/
	private String ownerGroupTitle;
	
	/**任务关联的规则*/
	private List<CoTaskRule> taskRuleList;
	
	/**任务关联的完成记录*/
	private List<CoTaskRd> taskRdList;
	
	/**任务关联的奖品*/
	private List<CoTaskReward> taskRewardList;
	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getPrizeDesc() {
		return prizeDesc;
	}

	public void setPrizeDesc(String prizeDesc) {
		this.prizeDesc = prizeDesc;
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

	public Date getShowTime() {
		return showTime;
	}

	public void setShowTime(Date showTime) {
		this.showTime = showTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCycleType() {
		return cycleType;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}


	public String getInType() {
		return inType;
	}

	public void setInType(String inType) {
		this.inType = inType;
	}

	public Integer getRuleCount() {
		return ruleCount;
	}

	public void setRuleCount(Integer ruleCount) {
		this.ruleCount = ruleCount;
	}

	public Long getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(Long sortSeq) {
		this.sortSeq = sortSeq;
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

	public String getImgCode() {
		return imgCode;
	}

	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}

	public List<CoTaskRule> getTaskRuleList() {
		return taskRuleList;
	}

	public void setTaskRuleList(List<CoTaskRule> taskRuleList) {
		this.taskRuleList = taskRuleList;
	}

	public List<CoTaskRd> getTaskRdList() {
		return taskRdList;
	}

	public void setTaskRdList(List<CoTaskRd> taskRdList) {
		this.taskRdList = taskRdList;
	}

	public List<CoTaskReward> getTaskRewardList() {
		return taskRewardList;
	}

	public void setTaskRewardList(List<CoTaskReward> taskRewardList) {
		this.taskRewardList = taskRewardList;
	}

	public Integer getCycleVal() {
		return cycleVal;
	}

	public void setCycleVal(Integer cycleVal) {
		this.cycleVal = cycleVal;
	}

	public String getIsReceiveReward() {
		return isReceiveReward;
	}

	public void setIsReceiveReward(String isReceiveReward) {
		this.isReceiveReward = isReceiveReward;
	}

	public String getActTabTitle() {
		return actTabTitle;
	}

	public void setActTabTitle(String actTabTitle) {
		this.actTabTitle = actTabTitle;
	}

	public String getComTabTitle() {
		return comTabTitle;
	}

	public void setComTabTitle(String comTabTitle) {
		this.comTabTitle = comTabTitle;
	}

	public String getSellTabTitle() {
		return sellTabTitle;
	}

	public void setSellTabTitle(String sellTabTitle) {
		this.sellTabTitle = sellTabTitle;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public String getIsTaskStart() {
		return isTaskStart;
	}

	public void setIsTaskStart(String isTaskStart) {
		this.isTaskStart = isTaskStart;
	}

	public String getShowDate() {
		return showDate;
	}

	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getOwnerGroupTitle() {
		return ownerGroupTitle;
	}

	public void setOwnerGroupTitle(String ownerGroupTitle) {
		this.ownerGroupTitle = ownerGroupTitle;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}
	
}
