package com.web.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.web.bean.CustomLocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * <p>
 *      ${description} 
 * </p>
 *
 * @package: com.web.model
 * @description: ${description}  
 * @author: wangzx8
 * @date: 2020/6/1 11:16
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
/**
    * 权益产品封装成可兑换的权益商品,一个产品可封装成个商品,可针对不同用户有不同的价格优惠
    */
public class CoExcObj {
    /**
    * 商品id
    */
    private Long objId;

    /**
    * 商品编码
    */
    private String objCode;

    /**
    * 商品ID
    */
    private Long productId;

    /**
    * 支付类型
             1 翼豆
             2 积分
             3  翼豆+现金
             4 积分+现金
    */
    private String payType;

    /**
    * 支付值
    */
    private Integer payVal;

    /**
    * 金额
    */
    private Integer payMoney;

    /**
    * 添加时间
    */
    private LocalDateTime addTime;

    /**
    * 生效时间
    */
    private LocalDateTime effDate;

    /**
    * 失效时间
    */
    private LocalDateTime expDate;

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

    /**
    * 新建工号
    */
    private Long staffId;

    /**
    * 最后编辑工号
    */
    private Long editStaffId;

    /**
    * 排序
    */
    private Integer sortSeq;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getObjCode() {
        return objCode;
    }

    public void setObjCode(String objCode) {
        this.objCode = objCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class )
    public LocalDateTime getEffDate() {
        return effDate;
    }

    public void setEffDate(LocalDateTime effDate) {
        this.effDate = effDate;
    }

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class )
    public LocalDateTime getExpDate() {
        return expDate;
    }


    public void setExpDate(LocalDateTime expDate) {
        this.expDate = expDate;
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

    public Integer getSortSeq() {
        return sortSeq;
    }

    public void setSortSeq(Integer sortSeq) {
        this.sortSeq = sortSeq;
    }
}