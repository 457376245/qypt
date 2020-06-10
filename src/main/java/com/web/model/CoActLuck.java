package com.web.model;

/**
 * 抽奖等级
 *
 * @author
 * @create 2020-03-13
 **/
public class CoActLuck {
    //抽奖id
    private Long luckId;
    //活动id
    private Long activityId;
    //幸运等级
    private String luckLevel;
    //幸运名称
    private String luckTitle;
    //幸运描述
    private String luckDesc;
    //中奖权重
    private Integer weightVal;
    //图片编码
    private String imgCode;
    //奖品状态：101 可用,102 不可用
    private String statusCd;
    //排序
    private int sortSeq;
    //中奖奖品
    private CoPrize prize;
    //中奖奖品对应的串码
    private CoPrizeSno prizeSno;

    private Long prizeId;
    
    //创建工号
    private Long staffId;
    //最后编辑工号
    private Long editStaffId;

    public Long getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(Long prizeId) {
        this.prizeId = prizeId;
    }

    public CoPrize getPrize() {
        return prize;
    }

    public void setPrize(CoPrize prize) {
        this.prize = prize;
    }

    public Long getLuckId() {
        return luckId;
    }

    public void setLuckId(Long luckId) {
        this.luckId = luckId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getLuckLevel() {
        return luckLevel;
    }

    public void setLuckLevel(String luckLevel) {
        this.luckLevel = luckLevel;
    }

    public String getLuckTitle() {
        return luckTitle;
    }

    public void setLuckTitle(String luckTitle) {
        this.luckTitle = luckTitle;
    }

    public String getLuckDesc() {
        return luckDesc;
    }

    public void setLuckDesc(String luckDesc) {
        this.luckDesc = luckDesc;
    }

    public Integer getWeightVal() {
        return weightVal;
    }

    public void setWeightVal(Integer weightVal) {
        this.weightVal = weightVal;
    }

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }
    
    public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public int getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(int sortSeq) {
		this.sortSeq = sortSeq;
	}

	public CoPrizeSno getPrizeSno() {
        return prizeSno;
    }

    public void setPrizeSno(CoPrizeSno prizeSno) {
        this.prizeSno = prizeSno;
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
