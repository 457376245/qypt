package com.web.model;

import java.util.Date;

/**
 * 奖品
 *
 * @author
 * @create 2020-03-13
 **/
public class CoPrize {
    //奖品id
    private Long prizeId;
    //对应的权益商品id
    private Long productId;
    //奖品名称
    private String prizeTitle;
    //奖品类型
    private String prizeType;
    //奖品描述
    private String prizeDesc;
    //奖品数量
    private int prizeCount;
    //奖品使用方式
    private String prizeUse;
    //商品编码
    private String productCode;
    //商品使用平台
    private String prodSupplier;
    //奖品价值
    private String prizeVal;
    //奖品状态：101 可用,102 不可用
    private String statusCd;
    //图片编码
    private String imgCode;
    //奖品库存
    private int prizeStock;
    //兑换量
    private int sellCount;
    //第三方串码，最终中奖时封装
    //员工工号
    private Long staffId;
    //最后编辑工号
  	private Long editStaffId;
  	//开始时间
  	private Date startTime;
  	//结束时间
  	private Date endTime;
    public String getSnoCode() {
        return snoCode;
    }

    public void setSnoCode(String snoCode) {
        this.snoCode = snoCode;
    }

    private String snoCode;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
    }

    public String getPrizeType() {
        return prizeType;
    }

    public void setPrizeType(String prizeType) {
        this.prizeType = prizeType;
    }

    public String getPrizeDesc() {
        return prizeDesc;
    }

    public void setPrizeDesc(String prizeDesc) {
        this.prizeDesc = prizeDesc;
    }

    public int getPrizeCount() {
        return prizeCount;
    }

    public void setPrizeCount(int prizeCount) {
        this.prizeCount = prizeCount;
    }

    public String getPrizeUse() {
        return prizeUse;
    }

    public void setPrizeUse(String prizeUse) {
        this.prizeUse = prizeUse;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProdSupplier() {
        return prodSupplier;
    }

    public void setProdSupplier(String prodSupplier) {
        this.prodSupplier = prodSupplier;
    }

    public String getPrizeVal() {
        return prizeVal;
    }

    public void setPrizeVal(String prizeVal) {
        this.prizeVal = prizeVal;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }

    public int getPrizeStock() {
        return prizeStock;
    }

    public void setPrizeStock(int prizeStock) {
        this.prizeStock = prizeStock;
    }

    public int getSellCount() {
        return sellCount;
    }

    public void setSellCount(int sellCount) {
        this.sellCount = sellCount;
    }

    public Long getPrizeId() {

        return prizeId;
    }

    public void setPrizeId(Long prizeId) {
        this.prizeId = prizeId;
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
}
