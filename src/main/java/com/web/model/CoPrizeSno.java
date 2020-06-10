package com.web.model;

import java.util.Date;

/**
 *奖品串码库
 */
public class CoPrizeSno {

  private String snoCode;
  private long batId;
  private long prizeId;
  private String productCode;
  private String oProductCode;
  private Date addTime;
  private String statusCd;


  public String getSnoCode() {
    return snoCode;
  }

  public void setSnoCode(String snoCode) {
    this.snoCode = snoCode;
  }


  public long getBatId() {
    return batId;
  }

  public void setBatId(long batId) {
    this.batId = batId;
  }


  public long getPrizeId() {
    return prizeId;
  }

  public void setPrizeId(long prizeId) {
    this.prizeId = prizeId;
  }


  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }


  public String getOProductCode() {
    return oProductCode;
  }

  public void setOProductCode(String oProductCode) {
    this.oProductCode = oProductCode;
  }

  public Date getAddTime() {
    return addTime;
  }

  public void setAddTime(Date addTime) {
    this.addTime = addTime;
  }

  public String getStatusCd() {
    return statusCd;
  }

  public void setStatusCd(String statusCd) {
    this.statusCd = statusCd;
  }

}
