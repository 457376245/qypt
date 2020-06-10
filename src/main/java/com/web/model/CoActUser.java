package com.web.model;

import java.util.Date;

/**
 * 参与用户
 */
public class CoActUser {

  private long privilegeId;
  private long memberId;
  private long activityId;
  //特权次数
  private int privilegeCount;
  //参与次数
  private int inCount;
  private Date statusDate;
  private Date updateDate;
  private String statusCd;


  public long getPrivilegeId() {
    return privilegeId;
  }

  public void setPrivilegeId(long privilegeId) {
    this.privilegeId = privilegeId;
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


  public int getPrivilegeCount() {
    return privilegeCount;
  }

  public void setPrivilegeCount(int privilegeCount) {
    this.privilegeCount = privilegeCount;
  }


  public int getInCount() {
    return inCount;
  }

  public void setInCount(int inCount) {
    this.inCount = inCount;
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

}
