package com.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoTaskRd.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务完成记录<br>
 * <b>创建时间：</b>2020年3月16日-下午2:17:11<br>
 */

public class CoTaskRd implements Serializable{

	private static final long serialVersionUID = 4348135290395095007L;
	
	/**记录ID*/
	private Long rdId;
	
	/**任务ID*/
	private Long taskId;
	
	/**会员ID*/
	private Long memberId;
	
	/**第三方会员*/
	private String oMemberAcct;
	
	/**添加时间*/
	private Date addTime;
	
	private String addDate; 
	
	/**状态时间*/
	private Date statusDate;
	
	/**修改时间*/
	private Date updateDate;
	
	/**状态*/
	private String statusCd;
	
	/**登录ID*/
	private Long loginId;

	public Long getRdId() {
		return rdId;
	}

	public void setRdId(Long rdId) {
		this.rdId = rdId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getoMemberAcct() {
		return oMemberAcct;
	}

	public void setoMemberAcct(String oMemberAcct) {
		this.oMemberAcct = oMemberAcct;
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

	public Long getLoginId() {
		return loginId;
	}

	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}
	
}
