package com.web.bmo;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * <p>
 * 上传文件接口
 * </p>
 *
 * @package: com.web.service
 * @description: 上传文件接口
 * @author: wangzx8
 * @date: 2020/3/17 20:16
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

public interface UploadFileService {

    public Map<String, Object> uploadFile(Map<String, MultipartFile> multipartFiles,  String linkType, String fileType, String projectName, String dirName);


    public Map<String, Object> downloadRedis(String fileCode, String fileName, String dirName);

    public Map<String, Object> uploadFileQy(Map<String, MultipartFile> multipartFiles,String linkId,String linkType,String fileType ,String projectName,String dirName);
}
