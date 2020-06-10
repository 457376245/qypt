package com.web.model;

import java.util.Date;

/**
 * @author zhengyx
 * @create 2020/3/25
 **/
public class CoLogLogin {
    /*CREATE TABLE CO_LOG_LOGIN
            (
                    LOGIN_ID             BIGINT(18) NOT NULL AUTO_INCREMENT  COMMENT '登录ID',
    SSO_ID               BIGINT(18)  COMMENT '单点ID',
    LOGIN_TYPE           VARCHAR(4)  COMMENT '登录类型
            1  自平台登录
             2  单点登录',
    LOGIN_ACCOUNT        VARCHAR(50)  COMMENT '登录账号',
    ACCOUNT_TYPE         VARCHAR(4)  COMMENT '账号类型
            1 默认手机号码',
    ADD_TIME             DATETIME DEFAULT CURRENT_TIMESTAMP  COMMENT '添加时间',
    SSO_PLATFORM         VARCHAR(4) NOT NULL  COMMENT '登录平台',
    SESSION_ID           VARCHAR(50)  COMMENT '会话id',
    CLIENT_VER           VARCHAR(50)  COMMENT '版本',
    CLIENT_IP            VARCHAR(50)  COMMENT '用户IP',
    CLIENT_MAC           VARCHAR(50)  COMMENT 'mac地址',
    TERMINAL_CODE        VARCHAR(50)  COMMENT '终端型号',
    TERMINAL_OS          VARCHAR(50)  COMMENT '系统',
    OS_VER               VARCHAR(32)  COMMENT '系统版本',
    LOGIN_CD             VARCHAR(4)  COMMENT '登录状态
            101 成功
             102 失败',
    LOGIN_MSG            VARCHAR(250)  COMMENT '状态描述',
    PRIMARY KEY (LOGIN_ID)
);*/
    //登录ID
    private int loginId;
    //单点ID
    private Long ssoId;
    //登录类型 1  自平台登录 2  单点登录',
    private String loginType;
    //登录账号
    private String loginAccount;
    //账号类型1 默认手机号码
    private String accountType;
    //添加时间
    private Date addTime;
    //登录平台
    private String ssoPlatform;
    //会话id
    private String sessionId;
    //版本
    private String clientVer;
    //用户IP
    private String clientIp;
    //mac地址
    private String clientMac;
    //终端型号
    private String terminalCode;
    //系统
    private String terminalOs;
    //系统版本
    private String osVer;
    //登录状态101 成功102 失败'
    private String loginCd;
    //状态描述
    private String loginMsg;

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public Long getSsoId() {
        return ssoId;
    }

    public void setSsoId(Long ssoId) {
        this.ssoId = ssoId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getSsoPlatform() {
        return ssoPlatform;
    }

    public void setSsoPlatform(String ssoPlatform) {
        this.ssoPlatform = ssoPlatform;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getClientVer() {
        return clientVer;
    }

    public void setClientVer(String clientVer) {
        this.clientVer = clientVer;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getClientMac() {
        return clientMac;
    }

    public void setClientMac(String clientMac) {
        this.clientMac = clientMac;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTerminalOs() {
        return terminalOs;
    }

    public void setTerminalOs(String terminalOs) {
        this.terminalOs = terminalOs;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getLoginCd() {
        return loginCd;
    }

    public void setLoginCd(String loginCd) {
        this.loginCd = loginCd;
    }

    public String getLoginMsg() {
        return loginMsg;
    }

    public void setLoginMsg(String loginMsg) {
        this.loginMsg = loginMsg;
    }
}
