package com.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.web.bean.CustomLocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDateTime;

/**
 * <p>
 *      ${description} 
 * </p>
 *
 * @package: com.web.model
 * @description: ${description}  
 * @author: wangzx8
 * @date: 2020/5/28 16:50
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

/**
 * 权益产品
 */
public class CoProduct {
    /**
     * 权益产品ID
     */
    private Long productId;

    /**
     * 权益产品编码
     */
    private String productCode;

    /**
     * 权益产品名称
     */
    private String productTitle;

    /**
     * 分类编码
     */
    private String classCode;

    /**
     * 品牌编码
     */
    private String brandCode;

    /**
     * 组ID
     */
    private Long groupId;

    /**
     * 第三方编码
     */
    private String oProductCode;

    /**
     * 提供平台
     * 1  自有
     * 2  第三方
     */
    private String prodSupplier;

    /**
     * 副标题
     */
    private String productTitleSub;

    /**
     * 属性标签
     */
    private String attrTab;

    /**
     * 添加时间
     */
    //备用yyyy-MM-dd HH:mm:ss
    private LocalDateTime addTime;

    /**
     * 记录每次销售品信息变更的时间，保持时间的连续性。
     */
    private LocalDateTime statusDate;

    /**
     * 记录修改的时间，可用于人工维护。
     */
    private LocalDateTime updateDate;

    /**
     * 记录状态。
     * 101 可用
     * 102 不可用
     */
    private String statusCd;

    /**
     * 展示时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime showTime;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 库存管理方式
     * 1 自有
     * 2 第三方
     */
    private String stockType;

    /**
     * 权益类型
     */
    private String productType;

    /**
     * 组合类型
     * 1 单商品
     * 2 组合商品
     */
    private String groupType;

    /**
     * 权益原价展示
     */
    private String oldPrice;

    /**
     * 新建工号
     */
    private Long staffId;

    /**
     * 最后编辑工号
     */
    private Long editStaffId;

    /**
     * 链接方式
     * 1  本地详情
     * 2  第三方详情
     */
    private String linkType;

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 列表图片编码
     */
    private String listImgCode;

    /**
     * 图片编码
     */
    private String imgCode;

    /**
     * 版本号
     */
    private Long versionNo;

    /**
     * 产品描述
     */
    private String productDes;

    private CoProductStock coProductStock;

    /**
     * 是否实例化库存
     */
    private String stockInst;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getoProductCode() {
        return oProductCode;
    }

    public void setoProductCode(String oProductCode) {
        this.oProductCode = oProductCode;
    }

    public String getProdSupplier() {
        return prodSupplier;
    }

    public void setProdSupplier(String prodSupplier) {
        this.prodSupplier = prodSupplier;
    }

    public String getProductTitleSub() {
        return productTitleSub;
    }

    public void setProductTitleSub(String productTitleSub) {
        this.productTitleSub = productTitleSub;
    }

    public String getAttrTab() {
        return attrTab;
    }

    public void setAttrTab(String attrTab) {
        this.attrTab = attrTab;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public LocalDateTime getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDateTime statusDate) {
        this.statusDate = statusDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class )
    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class )
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class )
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
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

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

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

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getListImgCode() {
        return listImgCode;
    }

    public void setListImgCode(String listImgCode) {
        this.listImgCode = listImgCode;
    }

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }

    public Long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    public String getProductDes() {
        return productDes;
    }

    public void setProductDes(String productDes) {
        this.productDes = productDes;
    }

    public String getStockInst() {
        return stockInst;
    }

    public void setStockInst(String stockInst) {
        this.stockInst = stockInst;
    }

    public CoProductStock getCoProductStock() {
        return coProductStock;
    }

    public void setCoProductStock(CoProductStock coProductStock) {
        this.coProductStock = coProductStock;
    }
}