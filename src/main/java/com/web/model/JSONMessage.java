package com.web.model;

import com.alibaba.fastjson.JSON;

public class JSONMessage {
    public static interface Flag {
        String FAIL = "0";
        String SUCCESS = "1";
        String TIMEOUT = "2";
    }

    /** 1-成功 其它-失败 */
    private String flag = Flag.FAIL;
    private String msg = "系统异常";
    private Object data;

    public JSONMessage() {
    }

    public JSONMessage(String flag, String msg, Object data) {
        super();
        this.flag = flag;
        this.msg = msg;
        this.data = data;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSON(this).toString();
    }
}
