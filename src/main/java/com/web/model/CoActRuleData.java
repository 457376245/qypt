package com.web.model;


import java.util.Date;

public class CoActRuleData {

  private long expId;
  private long ruleId;
  private String ruleType;
  private String ruleData;
  private Date addTime;
  private Date statusDate;
  private Date updateDate;
  private String statusCd;
  private Integer sortSeq;


  public long getExpId() {
    return expId;
  }

  public void setExpId(long expId) {
    this.expId = expId;
  }


  public long getRuleId() {
    return ruleId;
  }

  public void setRuleId(long ruleId) {
    this.ruleId = ruleId;
  }


  public String getRuleType() {
    return ruleType;
  }

  public void setRuleType(String ruleType) {
    this.ruleType = ruleType;
  }


  public String getRuleData() {
    return ruleData;
  }

  public void setRuleData(String ruleData) {
    this.ruleData = ruleData;
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


  public Integer getSortSeq() {
    return sortSeq;
  }

  public void setSortSeq(Integer sortSeq) {
    this.sortSeq = sortSeq;
  }

}
