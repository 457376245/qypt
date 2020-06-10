package com.web.model;
import java.util.Date;

/**
 * 订单表
 */
public class CoOrder {
  //订单id
  private long orderId;
  //父订单id
  private long parentId;
  //会员id
  private long memberId;
  //订单流水
  private String orderNo;
  //第三方会员id
  private String oMemberAcct;
  //支付方式
  private String payType;
  //支付值
  private Integer payVal;
  //支付金额
  private Integer payMoney;
  //添加时间
  private Date addTime;
  //状态时间
  private Date statusDate;
  //更新时间
  private Date updateDate;
  //状态
  private String statusCd;
  //订单状态
  private String orderStatus;
  //支付状态
  private String payStatus;
  private long staffId;
  private long editStaffId;
  private String orderDesc;
  //购买类型：1  正常订单2  限时购订单 3  抽奖类订单
  private String buyType;

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

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }


  public long getParentId() {
    return parentId;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }


  public long getMemberId() {
    return memberId;
  }

  public void setMemberId(long memberId) {
    this.memberId = memberId;
  }


  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }


  public String getOMemberAcct() {
    return oMemberAcct;
  }

  public void setOMemberAcct(String oMemberAcct) {
    this.oMemberAcct = oMemberAcct;
  }


  public String getPayType() {
    return payType;
  }

  public void setPayType(String payType) {
    this.payType = payType;
  }


  public Integer getPayVal() {
    return payVal;
  }

  public void setPayVal(Integer payVal) {
    this.payVal = payVal;
  }


  public Integer getPayMoney() {
    return payMoney;
  }

  public void setPayMoney(Integer payMoney) {
    this.payMoney = payMoney;
  }

  public String getStatusCd() {
    return statusCd;
  }

  public void setStatusCd(String statusCd) {
    this.statusCd = statusCd;
  }


  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }


  public String getPayStatus() {
    return payStatus;
  }

  public void setPayStatus(String payStatus) {
    this.payStatus = payStatus;
  }


  public long getStaffId() {
    return staffId;
  }

  public void setStaffId(long staffId) {
    this.staffId = staffId;
  }


  public long getEditStaffId() {
    return editStaffId;
  }

  public void setEditStaffId(long editStaffId) {
    this.editStaffId = editStaffId;
  }


  public String getOrderDesc() {
    return orderDesc;
  }

  public void setOrderDesc(String orderDesc) {
    this.orderDesc = orderDesc;
  }


  public String getBuyType() {
    return buyType;
  }

  public void setBuyType(String buyType) {
    this.buyType = buyType;
  }

}
