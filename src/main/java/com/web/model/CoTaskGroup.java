package com.web.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoTaskGroup.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务分组<br>
 * <b>创建时间：</b>2020年3月19日-下午8:37:12<br>
 */

public class CoTaskGroup implements Serializable{

	private static final long serialVersionUID = 6913117026960592528L;
	
	/**组ID*/	
	private Long groupId;
	
	/**父ID*/	
	private Long parentId;
	
	/**组名称*/	
	private String groupTitle;
	
	/**组编码*/	
	private String groupCode;
	
	/**组类型*/
	private String groupType;
	
	/**显示类型*/
	private String showType;
	
	/**组描述*/	
	private String groupDesc;
	
	/**添加时间*/	
	private Date addTime;
	
	/**状态时间*/	
	private Date statusDate;
	
	/**修改时间*/	
	private Date updateDate;
	
	/**状态*/	
	private String statusCd;
	
	/**排序*/	
	private Long sortSeq;
	
	/**开始时间*/	
	private Date startTime;
	
	private String startDate;
	
	/**结束时间*/
	private String endTime;
	
	private String endDate;
	
	/**新建工号*/	
	private Long staffId;
	
	/**最后编辑工号*/	
	private Long editStaffId;
	
	/**任务组下属任务*/
	private List<CoTask> taskList;

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

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public List<CoTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<CoTask> taskList) {
		this.taskList = taskList;
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
	
}
