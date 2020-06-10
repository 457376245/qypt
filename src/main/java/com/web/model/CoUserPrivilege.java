package com.web.model;

import java.util.Date;

/**
 * 中奖记录
 */
public class CoUserPrivilege {

  private long privilegeId;
  private long memberId;
  private long activityId;
  private long logId;
  private long orderId;
  private Date statusDate;
  private Date updateDate;
  private String statusCd;
  private String prizeTitle;
  private String prizeType;
  private String prizeUse;
  private String productCode;
  private String prodSupplier;
  private String prizeVal;
  private String oPrizeVal;
  private String statusDateStr;

  public String getStatusDateStr() {
    return statusDateStr;
  }

  public void setStatusDateStr(String statusDateStr) {
    this.statusDateStr = statusDateStr;
  }

  public long getPrivilegeId() {
    return privilegeId;
  }

  public void setPrivilegeId(long privilegeId) {
    this.privilegeId = privilegeId;
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

  public String getoPrizeVal() {
    return oPrizeVal;
  }

  public void setoPrizeVal(String oPrizeVal) {
    this.oPrizeVal = oPrizeVal;
  }

  public long getMemberId() {
    return memberId;
  }

  public void setMemberId(long memberId) {
    this.memberId = memberId;
  }


  public long getActivityId() {
    return activityId;
  }

  public void setActivityId(long activityId) {
    this.activityId = activityId;
  }


  public long getLogId() {
    return logId;
  }

  public void setLogId(long logId) {
    this.logId = logId;
  }


  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }


  public String getStatusCd() {
    return statusCd;
  }

  public void setStatusCd(String statusCd) {
    this.statusCd = statusCd;
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


  public String getPrizeUse() {
    return prizeUse;
  }

  public void setPrizeUse(String prizeUse) {
    this.prizeUse = prizeUse;
  }


  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }


  public String getProdSupplier() {
    return prodSupplier;
  }

  public void setProdSupplier(String prodSupplier) {
    this.prodSupplier = prodSupplier;
  }


  public String getPrizeVal() {
    return prizeVal;
  }

  public void setPrizeVal(String prizeVal) {
    this.prizeVal = prizeVal;
  }


  public String getOPrizeVal() {
    return oPrizeVal;
  }

  public void setOPrizeVal(String oPrizeVal) {
    this.oPrizeVal = oPrizeVal;
  }

}
