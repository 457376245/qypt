package com.web.controller;

import com.web.bmo.CommonBmo;
import com.web.bmo.ImgBmo;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoImg;
import com.web.util.RedisUtil;
import com.web.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 读取图片操作
 * </p>
 *
 * @package: com.web.controller
 * @description: 读取图片操作
 * @author: wangzx8
 * @date: 2020/3/20 17:09
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

@Controller("com.web.controller.ImageController")
@RequestMapping(value = "/img")
public class ImageController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ActLuckController.class);

    @Resource(name = "com.web.bmo.ImgBmoImpl")
    private ImgBmo imgBmo;

    @Resource
    private RedisUtil redisUtil;

    private static final String DEFAULT_CHARSET = "UTF-8";

    @Resource(name = "com.web.bmo.CommonBmoImpl")
    private CommonBmo commonBmo;

//    /**
//     * 获取图片url
//     *
//     * @param request
//     * @return String
//     */
//    @RequestMapping(value = "/getImgUrl")
//    public void getImgUrl(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> param, Model model) throws Exception {
//        String linkId = String.valueOf(param.get("linkId"));
//        String fileType = String.valueOf(param.get("fileType"));
//        if (StringUtils.isEmpty(linkId) || StringUtils.isEmpty(fileType)) {
//            return;
//        }
//        //调用接口获取图片数据
//        try {
//
//            CoImg imgParam = new CoImg();
//            imgParam.setLinkId(linkId);
//            imgParam.setFileType(fileType);
//            imgParam.setStatusCd("101");
//            String filePath = "";
//            filePath = (String) redisUtil.getRedisForKey(Constants.REDIS_IMAGE_URL + linkId + fileType);
//            if (StringUtils.isEmpty(filePath)) {
//                List<CoImg> coImgList = imgBmo.selectSelective(imgParam);
//
//                log.info("linkId == " + linkId + "   =========    coImgList.size() == " + coImgList.size());
//                if (coImgList != null && coImgList.size() > 0) {
//                    CoImg coImg = coImgList.get(0);
//                    if (coImg != null) {
//                        log.info("url:" + "/images/" + coImg.getFileDir());
//                        filePath = "/images/" + coImg.getFileDir() + coImg.getNewName();
//                    }
//                }
//                redisUtil.addRedisForKey(Constants.REDIS_IMAGE_URL + linkId + fileType, filePath);
//            }
//            if (!StringUtil.isEmptyStr(filePath)) {
//                RequestDispatcher rd = request.getRequestDispatcher(filePath);
//                rd.forward(request, response);
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return;
//    }


    /**
     * 获取图片url
     *
     * @param request
     * @return String
     */
    @RequestMapping(value = "/getImgUrl")
    public String getImgUrl(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> param, Model model) throws Exception {

        String linkId = String.valueOf(param.get("linkId"));
        String fileType = String.valueOf(param.get("fileType"));
        if (StringUtils.isEmpty(linkId) || StringUtils.isEmpty(fileType)) {
            return "";
        }
        //调用接口获取图片数据
        try {

            CoImg imgParam = new CoImg();
            imgParam.setLinkId(linkId);
            imgParam.setFileType(fileType);
            imgParam.setStatusCd("101");
            String filePath = "";
            Map<String, String> map = (Map<String, String>) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + Constants.REDIS_IMAGE_URL);
            if (map == null) {
                map = new HashMap<>();
            }
            String key = linkId + fileType;
            filePath = map.get(key);
//            boolean flag = true;
            if (StringUtils.isEmpty(filePath)) {
                List<CoImg> coImgList = imgBmo.selectSelective(imgParam);
                log.info("linkId == " + linkId + "   =========    coImgList.size() == " + coImgList.size());
                if (coImgList != null && coImgList.size() > 0) {
                    CoImg coImg = coImgList.get(0);
                    if (coImg != null) {
                        log.info("url:" + "/images/" + coImg.getFileDir());
                        filePath = coImg.getFileDir() + coImg.getNewName();
                        map.put(key, filePath);
                        redisUtil.addRedisForKey(Constants.REDIS_DEL_KEY + Constants.REDIS_IMAGE_URL, map);

                    }
                }
            }
            String imageUrl = commonBmo.getImageServerUrl();
            return "redirect:" + imageUrl + filePath;
//            if (!StringUtil.isEmptyStr(filePath)) {
//                byte[] data = null;
//                data = (byte[]) redisUtil.getRedisForKey(Constants.REDIS_IMAGE_BYTE + filePath );
//
////                RequestDispatcher rd = request.getRequestDispatcher(filePath);
////                rd.forward(request, response);
//                if(data == null) {
//                    data = this.doGetRequestForFile(filePath);
//                    if(data == null){
//                        return ;
//                    }
//                    //60分钟不适用过时
//                    redisUtil.addRedisInfo(Constants.REDIS_IMAGE_BYTE + filePath, data, 60);
//                }
//                if(data == null){
//                    return ;
//                }
//
//                OutputStream toClient=response.getOutputStream();         //输出数据
//                toClient.write(data);
//                toClient.flush();
//                toClient.close();
//                return;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 发起Get请求
     *
     * @param urlStr
     * @return
     */
    public byte[] doGetRequestForFile(String urlStr) {

        InputStream is = null;
        ByteArrayOutputStream os = null;
        byte[] buff = new byte[1024];
        int len = 0;
        try {
            URL url = new URL(UriUtils.encodePath(urlStr, DEFAULT_CHARSET));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "plain/text;charset=" + DEFAULT_CHARSET);
            conn.setRequestProperty("charset", DEFAULT_CHARSET);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(60000);
            conn.connect();
            is = conn.getInputStream();
            os = new ByteArrayOutputStream();
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            log.error("发起请求出现异常:", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("【关闭流异常】");
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("【关闭流异常】");
                }
            }
        }
    }
}
