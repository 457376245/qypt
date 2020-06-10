package com.web.bean;

import java.io.Serializable;

/**员工信息*/
public class StaffInfo implements Serializable {

	/**序列号*/
	private static final long serialVersionUID = -3141714859575699816L;

	/* 归属组织ID */
	private Long orgId;

	/* 归属组织名称 */
	private String orgName;

	/* 员工ID */
	private Long staffId;

	/* 员工工号 */
	private String staffCode;

	/* 员工名称 */
	private String staffName;

	/**身份证*/
	private String primaryIdentifyNum;
	
	/* 账号ID */
	private Long acctId;

	/* 账号  */
	private String acctNbr;

	/* 地区ID */
	private Long commonRegionId;

	/* 区号 */
	private Long areaId;
	
	/* 区号 */
	private String areaCode;
	
	/* 联系电话 */
	private String contactTele;

	/* 归属渠道ID */
	private Long channelId;

	/* 归属渠道名称 */
	private String channelName;
	
	/* 登陆提示编码 */
	private Integer hintCode;

	/* 是否超级管理员角色 */
	private boolean isSuperManager;
	
	/**
	 * 对应的省份编码
	 */
	private String provinceAreaCode;
	
	/* 转售商ID */
	private String partnerId;
	
	/* 转售商名称 */
	private String partnerName;
	
	/* 转售商编码*/
	private String partnerCode;
	
	//当前用户数据路由名称
	private String routingName;
	
	//当前所属省市县地区名称
	private String upProvinName;
	
	private String cityName;
	
	private String countryName;

	public String getUpProvinName() {
		return upProvinName;
	}

	public void setUpProvinName(String upProvinName) {
		this.upProvinName = upProvinName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getProvinceAreaCode() {
		return provinceAreaCode;
	}

	public void setProvinceAreaCode(String provinceAreaCode) {
		this.provinceAreaCode = provinceAreaCode;
	}

	public StaffInfo() {
		
	}

	/**
	 * @return the orgId
	 */
	public Long getOrgId() {
		return orgId;
	}

	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 * @return the staffId
	 */
	public Long getStaffId() {
		return staffId;
	}

	/**
	 * @param staffId the staffId to set
	 */
	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	/**
	 * @return the staffCode
	 */
	public String getStaffCode() {
		return staffCode;
	}

	/**
	 * @param staffCode the staffCode to set
	 */
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	/**
	 * @return the staffName
	 */
	public String getStaffName() {
		return staffName;
	}

	/**
	 * @param staffName the staffName to set
	 */
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	/**
	 * @return the acctId
	 */
	public Long getAcctId() {
		return acctId;
	}

	/**
	 * @param acctId the acctId to set
	 */
	public void setAcctId(Long acctId) {
		this.acctId = acctId;
	}

	/**
	 * @return the acctNbr
	 */
	public String getAcctNbr() {
		return acctNbr;
	}

	/**
	 * @param acctNbr the acctNbr to set
	 */
	public void setAcctNbr(String acctNbr) {
		this.acctNbr = acctNbr;
	}

	/**
	 * @return the commonRegionId
	 */
	public Long getCommonRegionId() {
		return commonRegionId;
	}

	/**
	 * @param commonRegionId the commonRegionId to set
	 */
	public void setCommonRegionId(Long commonRegionId) {
		this.commonRegionId = commonRegionId;
	}

	/**
	 * @return the areaCode
	 */
	public String getAreaCode() {
		return areaCode;
	}

	/**
	 * @param areaCode the areaCode to set
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 * @return the contactTele
	 */
	public String getContactTele() {
		return contactTele;
	}

	/**
	 * @param contactTele the contactTele to set
	 */
	public void setContactTele(String contactTele) {
		this.contactTele = contactTele;
	}

	/**
	 * @return the channelId
	 */
	public Long getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the hintCode
	 */
	public Integer getHintCode() {
		return hintCode;
	}

	/**
	 * @param hintCode the hintCode to set
	 */
	public void setHintCode(Integer hintCode) {
		this.hintCode = hintCode;
	}

	/**
	 * @return the isSuperManager
	 */
	public boolean isSuperManager() {
		return isSuperManager;
	}

	/**
	 * @param isSuperManager the isSuperManager to set
	 */
	public void setSuperManager(boolean isSuperManager) {
		this.isSuperManager = isSuperManager;
	}

	/**
	 * @return the routingName
	 */
	public String getRoutingName() {
		return routingName;
	}

	/**
	 * @param routingName the routingName to set
	 */
	public void setRoutingName(String routingName) {
		this.routingName = routingName;
	}

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public String getPrimaryIdentifyNum() {
		return primaryIdentifyNum;
	}

	public void setPrimaryIdentifyNum(String primaryIdentifyNum) {
		this.primaryIdentifyNum = primaryIdentifyNum;
	}
	
}
