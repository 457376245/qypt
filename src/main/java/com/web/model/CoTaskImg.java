package com.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoTaskImg.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务图片<br>
 * <b>创建时间：</b>2020年4月10日-上午9:30:24<br>
 */

public class CoTaskImg implements Serializable{

	private static final long serialVersionUID = -5143454297625094858L;
	
	/**绑定图片ID*/	
	private Long taskImgId;	
	
	/**图片ID*/	
	private Long imgId;
	
	/**任务ID*/	
	private Long taskId;
	
	/**图片分类编码*/	
	private String classCode;
	
	/**添加时间*/	
	private Date addTime;
	
	/**添加时间*/
	private String addDate;
	
	/**状态时间*/	
	private Date statusDate;
	
	/**修改时间*/	
	private Date updateDate;
	
	/**图片访问路径*/	
	private String imgCode;
	
	/**排序*/	
	private Long sortSeq;

	/*原文件名*/	
	private String oldName;
	
	/*新文件名*/	
	private String newName;	
	
	private Long linkId;
	
	private String fileType;
	
	public Long getTaskImgId() {
		return taskImgId;
	}

	public void setTaskImgId(Long taskImgId) {
		this.taskImgId = taskImgId;
	}

	public Long getImgId() {
		return imgId;
	}

	public void setImgId(Long imgId) {
		this.imgId = imgId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
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

	public String getImgCode() {
		return imgCode;
	}

	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}

	public Long getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(Long sortSeq) {
		this.sortSeq = sortSeq;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}

