package com.web.model;

import java.time.LocalDateTime;

/**
 * <p>
 *      ${description} 
 * </p>
 *
 * @package: com.web.model
 * @description: ${description}  
 * @author: wangzx8
 * @date: 2020/5/29 9:42
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
/**
    * 库存管理
    */
public class CoProductStock {
    /**
    * 商品ID
    */
    private Long productId;

    /**
    * 商品编码
    */
    private String productCode;

    /**
    * 商品库存
    */
    private Integer productStock;

    /**
    * 已兑换数量
    */
    private Integer productUse;

    /**
    * 预占数量
    */
    private Integer productPreemption;

    /**
    * 预警
    */
    private Integer productWarn;

    /**
    * 添加时间
    */
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
             101 可用
             102 不可用
    */
    private String statusCd;

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

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getProductUse() {
        return productUse;
    }

    public void setProductUse(Integer productUse) {
        this.productUse = productUse;
    }

    public Integer getProductPreemption() {
        return productPreemption;
    }

    public void setProductPreemption(Integer productPreemption) {
        this.productPreemption = productPreemption;
    }

    public Integer getProductWarn() {
        return productWarn;
    }

    public void setProductWarn(Integer productWarn) {
        this.productWarn = productWarn;
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
}