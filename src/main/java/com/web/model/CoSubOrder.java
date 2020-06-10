package com.web.model;

import java.util.Date;

/**
 * 子单表
 */
public class CoSubOrder {
  //子单id
  private long subOrderId;
  //订单id
  private long orderId;
  //父订单id
  private long parentId;
  //商品id
  private long productId;
  //商品标题
  private String productTitle;
  //商品副标题
  private String productTitleSub;
  //商品编码
  private String productCode;
  //商品使用平台
  private String prodSupplier;
  //属性标签
  private String attrTab;
  //商品类型
  private String productType;
  //组合类型
  private String groupType;
  //支付类型
  private String payType;
  //支付值
  private Integer payVal;
  //金额
  private Integer payMoney;
  //添加时间
  private Date addTime;
  //状态时间
  private Date statusDate;
  //修改时间
  private Date updateDate;
  //状态101 可用 102 不可用
  private String statusCd;
  //串码
  private String snoCode;
  //第三方串码
  private String oSnoCode;  
  //购物车ID
  private String olId;
  //购物车流水
  private String olNbr;
  
  public long getSubOrderId() {
    return subOrderId;
  }

  public void setSubOrderId(long subOrderId) {
    this.subOrderId = subOrderId;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public long getParentId() {
    return parentId;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }

  public long getProductId() {
    return productId;
  }

  public void setProductId(long productId) {
    this.productId = productId;
  }

  public String getProductTitle() {
    return productTitle;
  }

  public void setProductTitle(String productTitle) {
    this.productTitle = productTitle;
  }

  public String getProductTitleSub() {
    return productTitleSub;
  }

  public void setProductTitleSub(String productTitleSub) {
    this.productTitleSub = productTitleSub;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getProdSupplier() {
    return prodSupplier;
  }

  public void setProdSupplier(String prodSupplier) {
    this.prodSupplier = prodSupplier;
  }

  public String getAttrTab() {
    return attrTab;
  }

  public void setAttrTab(String attrTab) {
    this.attrTab = attrTab;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public String getGroupType() {
    return groupType;
  }

  public void setGroupType(String groupType) {
    this.groupType = groupType;
  }

  public String getPayType() {
    return payType;
  }

  public void setPayType(String payType) {
    this.payType = payType;
  }

  public Integer getPayVal() {
    return payVal;
  }

  public void setPayVal(Integer payVal) {
    this.payVal = payVal;
  }

  public Integer getPayMoney() {
    return payMoney;
  }

  public void setPayMoney(Integer payMoney) {
    this.payMoney = payMoney;
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

  public String getSnoCode() {
    return snoCode;
  }

  public void setSnoCode(String snoCode) {
    this.snoCode = snoCode;
  }

  public String getoSnoCode() {
    return oSnoCode;
  }

  public void setoSnoCode(String oSnoCode) {
    this.oSnoCode = oSnoCode;
  }

  public String getOlId() {
	return olId;
  }
	
  public void setOlId(String olId) {
	this.olId = olId;
  }
	
  public String getOlNbr() {
	 return olNbr;
  }
	
  public void setOlNbr(String olNbr) {
		this.olNbr = olNbr;
  }
}
