package com.web.model;

import java.io.Serializable;
import java.util.Date;

public class SecKill implements Serializable {
	
    private static final long serialVersionUID = -5161466177783266963L;
    //秒杀id
    private Long seckillId;
    //活动id
    private Long activityId;
    //活动编码
    private String activityCD;
    //活动标题
    private String activityTitle;
    private String seckillType;
    //开始展现时间
    private Date showTime;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //展现规则编码json
    private String showRuleMode;
    //详细规则编码json
    private String ruleMode;
    //活动规则文字描述
    private String activityRule;
	//商品使用平台
	private String prodSupplier;
    //商品ID
    private String productId;
    //商品编码	
	private String productCode;
    //商品标题
    private String productTitle;
    //商品副标题
    private String productTitleSub;
    
    //商品描述 支持富文本
    private String productDesc;
    private int productTotal;
    
    //原价
    private String oldPrice;
    //秒杀价
    private String newPrice;
    //限购数量
    private int buyCount;
    //库存
    private int productStock;
    private String statusCd;
    //版本
    private long version;
    private int sortSeq;
    //0 可见  1 不可见
    private String isShow;
   
    //活动是否开始标致  0 未开始  1 进行中  2 已结束
    private String opened;
	//员工工号
	private Long staffId;
	//最后编辑工号
	private Long editStaffId;

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

	public int getProductTotal() {
		return productTotal;
	}

	public void setProductTotal(int productTotal) {
		this.productTotal = productTotal;
	}

	//为每个秒杀商品生成一个防伪标识
    private String md5;

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

	public String getProdSupplier() {
		return prodSupplier;
	}

	public void setProdSupplier(String prodSupplier) {
		this.prodSupplier = prodSupplier;
	}

	public String getSeckillType() {
		return seckillType;
	}

	public void setSeckillType(String seckillType) {
		this.seckillType = seckillType;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getShowRuleMode() {
		return showRuleMode;
	}

	public void setShowRuleMode(String showRuleMode) {
		this.showRuleMode = showRuleMode;
	}

	public String getRuleMode() {
		return ruleMode;
	}

	public void setRuleMode(String ruleMode) {
		this.ruleMode = ruleMode;
	}

	public String getActivityRule() {
		return activityRule;
	}

	public void setActivityRule(String activityRule) {
		this.activityRule = activityRule;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getProductTitleSub() {
		return productTitleSub;
	}

	public void setProductTitleSub(String productTitleSub) {
		this.productTitleSub = productTitleSub;
	}

	public String getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(String newPrice) {
		this.newPrice = newPrice;
	}

	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public int getProductStock() {
		return productStock;
	}

	public void setProductStock(int productStock) {
		this.productStock = productStock;
	}



    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
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


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Date getShowTime() {
		return showTime;
	}

	public void setShowTime(Date showTime) {
		this.showTime = showTime;
	}

 
 
	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getOpened() {
		return opened;
	}

	public void setOpened(String opened) {
		this.opened = opened;
	}
    
	
	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getActivityCD() {
		return activityCD;
	}

	public void setActivityCD(String activityCD) {
		this.activityCD = activityCD;
	}

	public String getActivityTitle() {
		return activityTitle;
	}

	public void setActivityTitle(String activityTitle) {
		this.activityTitle = activityTitle;
	}

	@Override
    public String toString() {
        return "Seckill{" +
                "seckillId=" + seckillId +
                ", name='" + productTitle + '\'' +
                ", inventory=" + productStock +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", version=" + version +
                '}';
    }


}
