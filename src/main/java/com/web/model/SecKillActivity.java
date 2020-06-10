package com.web.model;

import java.util.Date;
import java.util.List;

public class SecKillActivity {

    //活动id
    private Long activityId;
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

    private String timeStr;
    
    //活动是否开始标致  0 未开始  1 进行中  2 已结束
    private String opened;
    
    //0 可见  1 不可见
    private String isShow;
    
    private String startTimeLong;
    
    private String endTimeLong;
    
    
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

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}
 

	public String getOpened() {
		return opened;
	}

	public void setOpened(String opened) {
		this.opened = opened;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getStartTimeLong() {
		return startTimeLong;
	}

	public void setStartTimeLong(String startTimeLong) {
		this.startTimeLong = startTimeLong;
	}

	public String getEndTimeLong() {
		return endTimeLong;
	}

	public void setEndTimeLong(String endTimeLong) {
		this.endTimeLong = endTimeLong;
	}
    

}
