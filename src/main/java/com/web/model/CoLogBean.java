package com.web.model;
import java.util.Date;

/**
 * 翼豆记录
 */
public class CoLogBean {

  private long logId;
  private long memberId;
  //操作类型1 增加 2 扣除
  private String optType;
  private Integer optVal;
  //操作结果1  成功 2  失败
  private String optResult;
  private String optDesc;
  private Date addTime;
  private Date statusDate;
  private Date updateDate;
  private String statusCd;
  private long staffId;
  private long editStaffId;


  public long getLogId() {
    return logId;
  }

  public void setLogId(long logId) {
    this.logId = logId;
  }


  public long getMemberId() {
    return memberId;
  }

  public void setMemberId(long memberId) {
    this.memberId = memberId;
  }


  public String getOptType() {
    return optType;
  }

  public void setOptType(String optType) {
    this.optType = optType;
  }


  public Integer getOptVal() {
    return optVal;
  }

  public void setOptVal(Integer optVal) {
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

}
