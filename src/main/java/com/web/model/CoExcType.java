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
 * @date: 2020/5/25 10:13
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

/**
 * 权益商城商品分类：音乐类、旅行类、通信类等
 */
public class CoExcType {
    /**
     * 分类ID
     */
    private Long typeId;

    /**
     * 分类编码
     */
    private String typeCode;

    /**
     * 分类描述
     */
    private String typeDesc;

    /**
     * 排序
     */
    private Integer sortSeq;

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
     * 101 可用
     * 102 不可用
     */
    private String statusCd;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public Integer getSortSeq() {
        return sortSeq;
    }

    public void setSortSeq(Integer sortSeq) {
        this.sortSeq = sortSeq;
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