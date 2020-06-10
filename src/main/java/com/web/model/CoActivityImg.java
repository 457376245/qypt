package com.web.model;

import java.util.Date;

/**
 * 
 * 创建人：yibo
 * 类描述：活动图片
 * 创建时间：2020年3月23日下午3:48:38
 */
public class CoActivityImg {
	//图片ID
	private long actImgId;
	
	//活动ID
	private long activityId;

	private long imgId;
	
	//图片分类编码
	private String classCode;
	
	//添加时间
	private Date addTime;
	
	//状态时间
	private Date statusTime;
	
	//修改时间
	private Date updateTime;
	
	//状态
	private String statusCd;
	
	//图片编码
	private String imgCode;
	
	//排序
	private long sortSeq;

	public long getImgId() {
		return imgId;
	}

	public void setImgId(long imgId) {
		this.imgId = imgId;
	}

	public long getActImgId() {
		return actImgId;
	}

	public void setActImgId(long actImgId) {
		this.actImgId = actImgId;
	}

	public long getActivityId() {
		return activityId;
	}

	public void setActivityId(long activityId) {
		this.activityId = activityId;
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

	public Date getStatusTime() {
		return statusTime;
	}

	public void setStatusTime(Date statusTime) {
		this.statusTime = statusTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public String getImgCode() {
		return imgCode;
	}

	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}

	public long getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(long sortSeq) {
		this.sortSeq = sortSeq;
	}
}
