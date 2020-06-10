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
 * @date: 2020/5/20 16:59
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

/**
 * 商品组
 */
public class CoProdGroup {
    /**
     * 组ID
     */
    private Long groupId;

    /**
     * 组名称
     */
    private String brandTitle;

    /**
     * 组介绍
     */
    private String brandDesc;

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

    /**
     * 新建工号
     */
    private Long staffId;

    /**
     * 最后编辑工号
     */
    private Long editStaffId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getBrandTitle() {
        return brandTitle;
    }

    public void setBrandTitle(String brandTitle) {
        this.brandTitle = brandTitle;
    }

    public String getBrandDesc() {
        return brandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        this.brandDesc = brandDesc;
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
}