package com.web.model;

import java.util.Date;

public class LogWarning {	   
	//警告ID
    private Long warningId;
    //会员主键ID
    private Long memberId;
    //奖品ID
    private Long prizeId;
    //商品ID
    private Long productId;
    //订单ID
    private Long orderId;
    //警告类型
    private String warningType;
    //警告内容
    private String warningContent;
    //添加时间
    private Date addTime;
    //状态变更时间
    private Date statusDate;
    //更新时间
    private Date updateDate;
    //记录状态
    private String statusCd;
    //处理结果
    private String optResult;
    //处理结果
    private String optRemark;
    //处理工号
    private String optStaff;
    //处理时间
    private Date optDate;
    //业务类型
    private String busiType;
    //业务ID
    private Long busiId;
	public Long getWarningId() {
		return warningId;
	}
	public void setWarningId(Long warningId) {
		this.warningId = warningId;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Long getPrizeId() {
		return prizeId;
	}
	public void setPrizeId(Long prizeId) {
		this.prizeId = prizeId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getWarningType() {
		return warningType;
	}
	public void setWarningType(String warningType) {
		this.warningType = warningType;
	}
	public String getWarningContent() {
		return warningContent;
	}
	public void setWarningContent(String warningContent) {
		this.warningContent = warningContent;
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
	public Date getUdateDate() {
		return updateDate;
	}
	public void setUdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getStatusCd() {
		return statusCd;
	}
	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}
	public String getOptResult() {
		return optResult;
	}
	public void setOptResult(String optResult) {
		this.optResult = optResult;
	}
	public String getOptRemark() {
		return optRemark;
	}
	public void setOptRemark(String optRemark) {
		this.optRemark = optRemark;
	}
	public String getOptStaff() {
		return optStaff;
	}
	public void setOptStaff(String optStaff) {
		this.optStaff = optStaff;
	}
	public Date getOptDate() {
		return optDate;
	}
	public void setOptDate(Date optDate) {
		this.optDate = optDate;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public Long getBusiId() {
		return busiId;
	}
	public void setBusiId(Long busiId) {
		this.busiId = busiId;
	}
}
