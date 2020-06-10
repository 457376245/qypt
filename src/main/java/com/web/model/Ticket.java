package com.web.model;

/**
 * <p>
 * 微信ticket
 * </p>
 *
 * @package: com.web.model
 * @description: 微信ticket
 * @author: wangzx8
 * @date: 2020/3/23 17:26
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public class Ticket {
    private String ticket;

    private Integer expiresIn;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
