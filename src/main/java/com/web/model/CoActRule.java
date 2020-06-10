package com.web.model;

import java.util.List;

/**
 * 活动规则
 *
 * @author
 * @create 2020-03-13
 **/
public class CoActRule {
    //规则id
    private Long ruleId;
    //活动id
    private Long activityId;
    //规则编码
    private String ruleCode;
    //规则标题
    private String ruleTitle;
    //规则描述
    private String ruleDesc;
    //规则状态101 可用 102 不可用
    private String statusCd;
    //规则类型
    private String ruleType;
    private String ruleData;
    //规则方式 OR
    private String ruleMode;
    //是否显示规则
    private String isShow;
    private Integer sortSeq;
    //规则表达式
    private List<CoActRuleData> ruleDatas;

    public Integer getSortSeq() {
        return sortSeq;
    }

    public void setSortSeq(Integer sortSeq) {
        this.sortSeq = sortSeq;
    }

    public String getRuleData() {
        return ruleData;
    }

    public void setRuleData(String ruleData) {
        this.ruleData = ruleData;
    }

    public List<CoActRuleData> getRuleDatas() {
        return ruleDatas;
    }

    public void setRuleDatas(List<CoActRuleData> ruleDatas) {
        this.ruleDatas = ruleDatas;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleTitle() {
        return ruleTitle;
    }

    public void setRuleTitle(String ruleTitle) {
        this.ruleTitle = ruleTitle;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleMode() {
        return ruleMode;
    }

    public void setRuleMode(String ruleMode) {
        this.ruleMode = ruleMode;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
