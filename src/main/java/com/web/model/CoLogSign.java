package com.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoLogSign.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>签到记录<br>
 * <b>创建时间：</b>2020年3月25日-下午3:03:58<br>
 */

public class CoLogSign implements Serializable{

	private static final long serialVersionUID = -2015875778870801603L;
	
	/**签到ID*/	
	private  Long signId;
	
	/**登录ID*/	
	private Long loginId;
	
	/**会员ID*/	
	private Long memberId;
	
	/**签到日期*/	
	private String signDate;
	
	/**累计签到天数*/	
	private Long signCount;
	
	/**添加时间*/	
	private Date addTime;
	
	/**状态*/	
	private String statusCd;
	
	/**状态时间*/	
	private Date statusDate;

	public Long getSignId() {
		return signId;
	}

	public void setSignId(Long signId) {
		this.signId = signId;
	}

	public Long getLoginId() {
		return loginId;
	}

	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getSignDate() {
		return signDate;
	}

	public void setSignDate(String signDate) {
		this.signDate = signDate;
	}

	public Long getSignCount() {
		return signCount;
	}

	public void setSignCount(Long signCount) {
		this.signCount = signCount;
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

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
	
}
