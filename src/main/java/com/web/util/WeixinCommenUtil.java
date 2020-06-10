package com.web.util;

import com.alibaba.fastjson.JSONObject;
import com.web.common.Constants;
import com.web.model.AccessToken;
import com.web.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 微信辅助
 * </p>
 *
 * @package: com.web.util
 * @description: 微信辅助
 * @author: wangzx8
 * @date: 2020/3/23 16:49
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Component
public class WeixinCommenUtil {

    private static Logger log = LoggerFactory.getLogger(WeixinCommenUtil.class);

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    // 获取access_token的接口地址（GET） 限2000（次/天）
    private static String accessTokenUrl = Constants.ACCESS_TOKEN_URL;


    public AccessToken getToken(String appid, String appsecret) {
        AccessToken token = null;
        //访问微信服务器的地址
        String accessTokenUrl = Constants.ACCESS_TOKEN_URL;
        String requestUrl = accessTokenUrl.replace("APPID", appid).replace("APPSECRET", appsecret);
        //HttpRequestUtil httpRequestUtil=new HttpRequestUtil();
        //创建一个json对象
        String result = this.getHttp(requestUrl, new HashMap<>());
        if (JsonUtil.isValidJson(result)) {
            JSONObject json = JSONObject.parseObject(result);
            System.out.println("获取到的json格式的Token为:" + json);
            //判断json是否为空
            if (json != null) {

                try {
                    token = new AccessToken();
                    //将获取的access_token放入accessToken对象中
                    token.setAccessToken(json.getString("access_token"));
                    //将获取的expires_in时间放入accessToken对象中
                    token.setExpiresIn(json.getInteger("expires_in"));
                } catch (Exception e) {
                    token = null;
                    e.printStackTrace();
                    System.out.println("系统出错了！");
                }
            } else {
                token = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}");
            }
        } else {
            token = null;
            // 获取token失败
            log.error("解析json异常 errcode:{} errmsg:{}");
        }
        return token;

    }

    public Ticket getTicket(String accessToken) {
        Ticket ticket = null;
        //访问微信服务器的地址
        String ticketUrl = Constants.TICKET_URL;
        String requestUrl = ticketUrl.replace("ACCESS_TOKEN", accessToken);
        //HttpRequestUtil httpRequestUtil=new HttpRequestUtil();
        //创建一个json对象
        String result = this.getHttp(requestUrl, new HashMap<>());
        if (JsonUtil.isValidJson(result)) {
            JSONObject json = JSONObject.parseObject(result);
            System.out.println("获取到的json格式的Token为:" + json);
            //判断json是否为空
            if (json != null) {

                try {
                    ticket = new Ticket();
                    //将获取的access_token放入accessToken对象中
                    ticket.setTicket(json.getString("ticket"));
                    //将获取的expires_in时间放入accessToken对象中
                    ticket.setExpiresIn(json.getInteger("expires_in"));
                } catch (Exception e) {
                    ticket = null;
                    e.printStackTrace();
                    System.out.println("系统出错了！");
                }
            } else {
                ticket = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}");
            }
        } else {
            ticket = null;
            // 获取token失败
            log.error("解析json异常 errcode:{} errmsg:{}");
        }
        return ticket;

    }

    private String getHttp(String requestUrl, Map<String, Object> params){
        ResponseEntity<String> resEntity = restTemplateUtil.getHttp(requestUrl,params,new HashMap<>());
        String result = null;
        if (resEntity != null) {
            result = resEntity.getBody();
        }
        return result;
    }

}
