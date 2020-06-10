package com.web.model;

import java.util.Date;

/**
 * @author zhengyx
 * @create 2020/3/16
 **/
public class CoMemberExt {
    /*
    CREATE TABLE CO_MEMBER_EXT
(
   EXT_ID               BIGINT(18) NOT NULL AUTO_INCREMENT  COMMENT '扩展ID',
   MEMBER_ID            BIGINT(18) NOT NULL  COMMENT '会员主键ID',
   ATTR_CODE            VARCHAR(32)  COMMENT '属性编码',
   ATTR_VAL             VARCHAR(250) NOT NULL  COMMENT '属性值',
   ATTR_TYPE            VARCHAR(4)  COMMENT '属性类型',
   SORT_SEQ             NUMERIC(5,0) NOT NULL  COMMENT '排序',
   ADD_TIME             DATETIME DEFAULT CURRENT_TIMESTAMP  COMMENT '添加时间',
   STATUS_DATE          DATETIME  COMMENT '记录每次销售品信息变更的时间，保持时间的连续性。',
   UPDATE_DATE          DATETIME  COMMENT '记录修改的时间，可用于人工维护。',
   STATUS_CD            VARCHAR(4) NOT NULL  COMMENT '记录状态。
             101 可用
             102 下架
             103 删除',
   PRIMARY KEY (EXT_ID)
);
     */
    //扩展ID
    private Long extId;
    //会员主键ID
    private Long memberId;
    //属性编码
    private String attrCode;
    //属性值
    private String attrVal;
    //属性类型
    private String attrType;
    //排序
    private Long sortSeq;
    //添加时间
    private Date addTime;
    private Date statusDate;
    private Date updateDate;
    //记录状态。101 可用102 下架103 删除'
    private String statusCd;

    public Long getExtId() {
        return extId;
    }

    public void setExtId(Long extId) {
        this.extId = extId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getAttrCode() {
        return attrCode;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public String getAttrVal() {
        return attrVal;
    }

    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public Long getSortSeq() {
        return sortSeq;
    }

    public void setSortSeq(Long sortSeq) {
        this.sortSeq = sortSeq;
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
