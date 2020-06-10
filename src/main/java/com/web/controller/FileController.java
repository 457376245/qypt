package com.web.controller;

import com.web.bmo.CommonBmo;
import com.web.bmo.UploadFileService;
import com.web.common.Constants;
import com.web.util.SpringContextUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 文件操作
 * </p>
 *
 * @package: com.web.controller
 * @description: 文件操作
 * @author: wangzx8
 * @date: 2020/4/2 10:34
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Controller("com.web.controller.FileController")
@RequestMapping("/file")
public class FileController {

    @Resource
    private UploadFileService uploadFileService;

    @Resource
    private CommonBmo commonBmo;

//    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String, Object> uploadFile(HttpServletRequest request) {
//        Map<String, Object> turnMap = new HashMap<String, Object>();
//        turnMap.put("resultCode", 1);
//        turnMap.put("resultMsg", "上传失败");
//        MultipartFile requestBody = null;
//        Map<String, MultipartFile> multipartFiles = ((MultipartHttpServletRequest) request).getFileMap();
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        String[] linkType = parameterMap.get("linkType");
//        //  1 商品缩略图 2 商品图
//        String[] fileType = parameterMap.get("fileType");
//
//        String linkTypeStr = "1";
//        if (linkType != null && !StringUtils.isEmpty(linkType[0])) {
//            linkTypeStr = linkType[0];
//        }
//
//        if (fileType == null || StringUtils.isEmpty(fileType[0])) {
//            turnMap.put("resultMsg", "文件类型不能为空");
//            return turnMap;
//        }
//        turnMap = uploadFileService.uploadFile(multipartFiles,  linkTypeStr, fileType[0], "/qy-portal","inside");
//        return turnMap;
//    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public  Map<String, Object> uploadFile(@RequestParam(value = "file")MultipartFile file, HttpServletRequest request) throws IOException {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", 1);
        turnMap.put("resultMsg", "上传失败");
        String fileType = request.getParameter("fileType");
        String linkType = request.getParameter("linkType");
        System.out.println(fileType);
        //  1 商品缩略图 2 商品图

        String linkTypeStr = "1";
        if (linkType != null && !StringUtils.isEmpty(linkType)) {
            linkTypeStr = linkType;
        }

        if (fileType == null || StringUtils.isEmpty(fileType)) {
            turnMap.put("resultMsg", "文件类型不能为空");
            return turnMap;
        }
        Map<String, MultipartFile> fileMap = new HashMap<>();
        fileMap.put("file",file);
        turnMap = uploadFileService.uploadFile(fileMap,  linkTypeStr, fileType, "/qy-portal","inside");
        return turnMap;
    }

    @RequestMapping(value = "/uploadFileQy", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> uploadFileQy(HttpServletRequest request) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", 1);
        turnMap.put("resultMsg", "上传失败");
        MultipartFile requestBody = null;
        Map<String, MultipartFile> multipartFiles = ((MultipartHttpServletRequest) request).getFileMap();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] linkId = parameterMap.get("linkId");

        String[] linkType = parameterMap.get("linkType");
        //  1 商品缩略图 2 商品图
        String[] fileType = parameterMap.get("fileType");

        String linkTypeStr = "1";
        if (linkType != null && !StringUtils.isEmpty(linkType[0])) {
            linkTypeStr = linkType[0];
        }
        if (linkId == null || StringUtils.isEmpty(linkId[0])) {
            turnMap.put("resultMsg", "关联ID不能为空");
            return turnMap;
        }
        if (fileType == null || StringUtils.isEmpty(fileType[0])) {
            turnMap.put("resultMsg", "文件类型不能为空");
            return turnMap;
        }
        turnMap = uploadFileService.uploadFileQy(multipartFiles, linkId[0], linkTypeStr, fileType[0], request.getContextPath(),"qy");
        return turnMap;
    }



    @RequestMapping(value = "/uploadFileProduct", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> uploadFileProduct(@RequestParam(value = "file")MultipartFile file, HttpServletRequest request) throws IOException {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("code", 1);
        turnMap.put("msg", "上传失败");
        //  1 商品缩略图 2 商品图
        String linkTypeStr = "0";
        Map<String, MultipartFile> fileMap = new HashMap<>();
        fileMap.put("file", file);
        Map<String, Object> resultMap = uploadFileService.uploadFile(fileMap, linkTypeStr, "0", "/qy-portal", "prouct");
        if (!Constants.RESULT_SUCC.equals(MapUtils.getString(resultMap, Constants.RESULT_CODE_STR))) {
            turnMap.put("msg", MapUtils.getString(resultMap, Constants.RESULT_MSG_STR));
            return turnMap;
        }
        turnMap.put("code", 0);
        turnMap.put("msg", "上传成功");
        Map<String,Object> result = (Map<String, Object>) MapUtils.getMap(resultMap,"result");
        Map<String,Object> dataMap = new HashMap<>();
        String imageUrl = commonBmo.getImageServerUrl();
        dataMap.put("src",imageUrl + result.get("fileDir"));//图片url
        dataMap.put("title",file.getOriginalFilename());//图片名称，这个会显示在输入框里
        dataMap.put("imgId",result.get("imgId"));
        turnMap.put("data",dataMap);
        return turnMap;
    }

    @RequestMapping(value = "/synFile", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Map<String, Object> synFile(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", "1");
        turnMap.put("resultMsg", "同步失败");
//        String filePath = MapUtils.getString(params, "filePath");
//        String fileName = MapUtils.getString(params, "fileName");
//
//        turnMap = uploadFileService.downloadHttpUrl(filePath,fileName);
        String fileName = MapUtils.getString(params, "fileName");
        String fileCode = MapUtils.getString(params, "fileCode");
        String dirName = MapUtils.getString(params, "dirName");
        turnMap = uploadFileService.downloadRedis(fileCode, fileName,dirName);
        return turnMap;
    }
}
