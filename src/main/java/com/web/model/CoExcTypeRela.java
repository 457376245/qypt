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
 * @date: 2020/6/2 16:07
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

/**
 * 商品分类关系
 */
public class CoExcTypeRela {
    /**
     * 关系id
     */
    private Long objTypeId;

    /**
     * 商品id
     */
    private Long objId;

    /**
     * 分类ID
     */
    private Long typeId;

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

    //商品信息
    private CoExcObj coExcObj;

    /**
     * 记录状态。
     * 101 可用
     * 102 不可用
     */
    private String statusCd;

    public Long getObjTypeId() {
        return objTypeId;
    }

    public void setObjTypeId(Long objTypeId) {
        this.objTypeId = objTypeId;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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

    public CoExcObj getCoExcObj() {
        return coExcObj;
    }

    public void setCoExcObj(CoExcObj coExcObj) {
        this.coExcObj = coExcObj;
    }
}