package com.web.model;
/**
 * 会员信息
 *
 * @author zhengyx
 * @create 2020-03-16
 **/

import java.io.Serializable;
import java.util.Date;

public class CoMember implements Serializable {
    private static final long serialVersionUID = -4478130034365954256L;

    //会员主键ID
    private Long memberId;
    //头像ID
    private Long photoId;
    //会员编码
    private String memberNo;
    //会员号码
    private String memberPhone;
    //号码类型 1 默认手机号码
    private String phoneType;
    //会员等级
    private String memberLevel;
    //第三方会员
    private String oMemberAcct;
    //会员名称
    private String memberName;
    //星级
    private String starLevel;
    //昵称
    private String nickName;
    //添加时间
    private Date addTime;
    private Date statusDate;
    private Date updateDate;
    //记录状态。101 可用102 下架103 删除
    private String statusCd;
    //会员生日
    private String memberBirth;
    //翼豆
    private Long memberBean;
    //会员密码
    private String memberPwd;
    //
    private Long custId;

    private int levelNum;

    private Long loginId;
    //成长值
    private Long score;

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getoMemberAcct() {
        return oMemberAcct;
    }

    public void setoMemberAcct(String oMemberAcct) {
        this.oMemberAcct = oMemberAcct;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(String starLevel) {
        this.starLevel = starLevel;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getMemberBirth() {
        return memberBirth;
    }

    public void setMemberBirth(String memberBirth) {
        this.memberBirth = memberBirth;
    }

    public Long getMemberBean() {
        return memberBean;
    }

    public void setMemberBean(Long memberBean) {
        this.memberBean = memberBean;
    }

    public String getMemberPwd() {
        return memberPwd;
    }

    public void setMemberPwd(String memberPwd) {
        this.memberPwd = memberPwd;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }
}
