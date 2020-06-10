package com.web.model;

import java.util.Date;

/**
 * 创建人：yibo
 * 类描述：活动奖品
 * 创建时间：2020年4月7日下午8:03:08
 */
public class CoActPrize {
	//奖励id
	private Long rewardId;
	//奖品id
	private Long prizeId;
	//抽奖id
	private Long luckId;
	//奖品数量
	private int userPrizeCount;
	//添加时间
	private Date addTime;
	//状态时间
	private Date statusDate;
	//修改时间
	private Date updateDate;
	//状态
	private String statusCd;
	//新建工号
	private Long staffId;
	//最后编辑工号
	private Long editStaffId;
	public Long getRewardId() {
		return rewardId;
	}
	public void setRewardId(Long rewardId) {
		this.rewardId = rewardId;
	}
	public Long getPrizeId() {
		return prizeId;
	}
	public void setPrizeId(Long prizeId) {
		this.prizeId = prizeId;
	}
	public Long getLuckId() {
		return luckId;
	}
	public void setLuckId(Long luckId) {
		this.luckId = luckId;
	}
	public int getUserPrizeCount() {
		return userPrizeCount;
	}
	public void setUserPrizeCount(int userPrizeCount) {
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
}
