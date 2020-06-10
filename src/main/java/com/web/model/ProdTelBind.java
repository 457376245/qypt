package com.web.model;

import java.util.Date;


/**
 * 创建人：yibo
 * 类描述：销售品
 * 创建时间：2020年4月24日下午4:14:21
 */
public class ProdTelBind {
	  //权益绑定ID
	  private long telBindId;
	  //商品ID
	  private String productId;
	  //商品编码
	  private String productCode;
	  //销售品ID
	  private String telProdId;
	  //销售品名称
	  private String telProdTitle;
	  //销售品类型
	  private String telProdType;
	  //是否允许互斥
	  private String canExcluse;
	  //添加时间
	  private Date addTime;
	  //状态时间
	  private Date statusDate;
	  //修改时间
	  private Date updateDate;
	  //状态
	  private String statusCd;
	public long getTelBindId() {
		return telBindId;
	}
	public void setTelBindId(long telBindId) {
		this.telBindId = telBindId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getTelProdId() {
		return telProdId;
	}
	public void setTelProdId(String telProdId) {
		this.telProdId = telProdId;
	}
	public String getTelProdTitle() {
		return telProdTitle;
	}
	public void setTelProdTitle(String telProdTitle) {
		this.telProdTitle = telProdTitle;
	}	
	public String getTelProdType() {
		return telProdType;
	}
	public void setTelProdType(String telProdType) {
		this.telProdType = telProdType;
	}	
	public String getCanExcluse() {
		return canExcluse;
	}
	public void setCanExcluse(String canExcluse) {
		this.canExcluse = canExcluse;
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
}
