package com.web.model;

/**
 * <p>
 * 微信token数据
 * </p>
 *
 * @package: com.web.model
 * @description: 微信token数据
 * @author: wangzx8
 * @date: 2020/3/23 17:00
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public class AccessToken {

    private String accessToken;

    private Integer expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
