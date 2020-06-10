package com.web.model;

import java.util.Date;

/**
 * <p>
 * 文件保存对象
 * </p>
 *
 * @package: com.web.model
 * @description: 文件保存对象
 * @author: wangzx8
 * @date: 2020/3/17 20:13
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public class CoImg {
    /**
     * 图片ID
     */
    private Long imgId;

    /**
     * 关联ID
     */
    private String linkId;

    /**
     * 关联类型
     * 1  权益ID
     */
    private String linkType;

    /**
     * 文件编码
     */
    private String fileName;

    /**
     * 原文件名
     */
    private String oldName;

    /**
     * 新文件名
     */
    private String newName;

    /**
     * 文件路径
     */
    private String fileDir;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 存储方式
     */
    private String storageType;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 记录每次销售品信息变更的时间，保持时间的连续性。
     */
    private Date statusDate;

    /**
     * 记录修改的时间，可用于人工维护。
     */
    private Date updateDate;

    /**
     * 记录状态。
     * 101 可用
     * 102 下架
     * 103 删除
     */
    private String statusCd;

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
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