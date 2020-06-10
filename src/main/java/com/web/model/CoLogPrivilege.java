package com.web.model;


import java.util.Date;

public class CoLogPrivilege {

  private long logId;
  private long privilegeId;
  private long activityId;
  private String optType;
  private double optVal;
  private String optResult;
  private String optDesc;
  private Date addTime;
  private Date statusDate;
  private Date updateDate;
  private String statusCd;
  private long loginId;
  private Long staffId;
  private Long editStaffId;

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

  public long getLogId() {
    return logId;
  }

  public void setLogId(long logId) {
    this.logId = logId;
  }


  public long getPrivilegeId() {
    return privilegeId;
  }

  public void setPrivilegeId(long privilegeId) {
    this.privilegeId = privilegeId;
  }


  public long getActivityId() {
    return activityId;
  }

  public void setActivityId(long activityId) {
    this.activityId = activityId;
  }


  public String getOptType() {
    return optType;
  }

  public void setOptType(String optType) {
    this.optType = optType;
  }


  public double getOptVal() {
    return optVal;
  }

  public void setOptVal(double optVal) {
    this.optVal = optVal;
  }


  public String getOptResult() {
    return optResult;
  }

  public void setOptResult(String optResult) {
    this.optResult = optResult;
  }


  public String getOptDesc() {
    return optDesc;
  }

  public void setOptDesc(String optDesc) {
    this.optDesc = optDesc;
  }


  public String getStatusCd() {
    return statusCd;
  }

  public void setStatusCd(String statusCd) {
    this.statusCd = statusCd;
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

  public long getLoginId() {
    return loginId;
  }

  public void setLoginId(long loginId) {
    this.loginId = loginId;
  }

}
