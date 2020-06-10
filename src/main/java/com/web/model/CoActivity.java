package com.web.model;

import java.util.Date;

/**
 * 活动
 *
 * @author
 * @create 2020-03-13
 **/
public class CoActivity {
    //活动id
    private Long activityId;
    private Long parentId;
    //活动编码
    private String activityCd;
    //活动名称
    private String activityTitle;
    //活动描述
    private String activityDesc;
    //活动规则
    private String activityRule;
    //活动状态101 可用 102 不可用
    private String statusCd;
    //展示时间
    private Date showTime;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //活动方式1 转盘 2 秒杀 3 抽奖
    private String activityMode;
    private String ruleMode;
    private String showRuleMode;
    //点击量
    private Long clickCount;
    //抽奖使用的翼豆
    private Integer useAccount;
    //参与人数
    private Integer inUserCount;
    //开始时间-格式化过的字符串
    private String startTimeStr;
    //结束时间-格式化过的字符串
    private String endTimeStr;
    private String showTimeStr;
    //秒杀类型，1时会场，2日会场
    private String seckillType;

    private Integer buyCount;
    //员工工号
    private Long staffId;
    //最后编辑工号
    private Long editStaffId;
    //中奖概率
    private String probability;

    private String ssoUrl="";

    public String getSsoUrl() {
        return ssoUrl;
    }

    public void setSsoUrl(String ssoUrl) {
        this.ssoUrl = ssoUrl;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
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

    public String getShowTimeStr() {
        return showTimeStr;
    }

    public void setShowTimeStr(String showTimeStr) {
        this.showTimeStr = showTimeStr;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Integer buyCount) {
        this.buyCount = buyCount;
    }

    public String getSeckillType() {
        return seckillType;
    }

    public void setSeckillType(String seckillType) {
        this.seckillType = seckillType;
    }

    public Integer getUseAccount() {
        return useAccount;
    }

    public void setUseAccount(Integer useAccount) {
        this.useAccount = useAccount;
    }

    public Integer getInUserCount() {
        return inUserCount;
    }

    public void setInUserCount(Integer inUserCount) {
        this.inUserCount = inUserCount;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getRuleMode() {
        return ruleMode;
    }

    public void setRuleMode(String ruleMode) {
        this.ruleMode = ruleMode;
    }

    public String getShowRuleMode() {
        return showRuleMode;
    }

    public void setShowRuleMode(String showRuleMode) {
        this.showRuleMode = showRuleMode;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityCd() {
        return activityCd;
    }

    public void setActivityCd(String activityCd) {
        this.activityCd = activityCd;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public String getActivityRule() {
        return activityRule;
    }

    public void setActivityRule(String activityRule) {
        this.activityRule = activityRule;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getActivityMode() {
        return activityMode;
    }

    public void setActivityMode(String activityMode) {
        this.activityMode = activityMode;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }
}
