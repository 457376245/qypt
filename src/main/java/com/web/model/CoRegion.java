package com.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.model.CoRegion.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>地市<br>
 * <b>创建时间：</b>2020年4月13日-下午3:44:28<br>
 */

public class CoRegion implements Serializable{

	private static final long serialVersionUID = -2354841088959164145L;
	
	/**地市ID*/	
	private Long regionId;
	
	/**父ID*/	
	private Long parentId;
	
	/**父级地市名称*/
	private String parentRegionName;
	
	/**区域名称*/	
	private String regionName;
	
	/**区域编码*/	
	private String regionCode;
	
	/**区域类型*/	
	private String regionType;
	
	/**区域描述*/	
	private String regionDesc;
	
	/**编码前缀*/	
	private Integer idPrefix;
	
	/**地区级别*/	
	private Integer areaLevel;
	
	/**邮编*/	
	private String zipCode;
	
	/**区号*/	
	private String zoneNumber;
	
	/**添加时间*/	
	private Date addTime;
	
	/**状态时间*/	
	private Date statusDate;
	
	/**修改时间*/	
	private Date updateDate;
	
	/**状态*/	
	private Date statusCd;

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getParentRegionName() {
		return parentRegionName;
	}

	public void setParentRegionName(String parentRegionName) {
		this.parentRegionName = parentRegionName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getRegionType() {
		return regionType;
	}

	public void setRegionType(String regionType) {
		this.regionType = regionType;
	}

	public String getRegionDesc() {
		return regionDesc;
	}

	public void setRegionDesc(String regionDesc) {
		this.regionDesc = regionDesc;
	}

	public Integer getIdPrefix() {
		return idPrefix;
	}

	public void setIdPrefix(Integer idPrefix) {
		this.idPrefix = idPrefix;
	}

	public Integer getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(Integer areaLevel) {
		this.areaLevel = areaLevel;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(String zoneNumber) {
		this.zoneNumber = zoneNumber;
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

	public Date getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(Date statusCd) {
		this.statusCd = statusCd;
	}
	
}

