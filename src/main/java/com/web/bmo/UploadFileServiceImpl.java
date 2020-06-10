package com.web.bmo;

import com.web.common.Constants;
import com.web.common.PropertyToRedis;
import com.web.dao.DicGroupAndItemDao;
import com.web.fastdfs.ImageUtil;
import com.web.model.CoDicItem;
import com.web.model.CoImg;
import com.web.util.DateUtil;
import com.web.util.JsonUtil;
import com.web.util.RedisUtil;
import com.web.util.RestTemplateUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 图片保存
 * </p>
 *
 * @package: com.web.service
 * @description: 图片保存
 * @author: wangzx8
 * @date: 2020/3/17 19:48
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.service.UploadFileServiceImpl")
public class UploadFileServiceImpl implements UploadFileService {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);

    @Resource
    private ImgBmo imgBmo;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PropertyToRedis propertyToRedis;

    @Autowired
    private DicGroupAndItemDao dicGroupAndItemDao;

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Resource
    private ImageUtil imageUtil;


    @Resource
    private CommonBmo commonBmo;

    @Override
    public Map<String, Object> uploadFile(Map<String, MultipartFile> multipartFiles, String linkType, String fileType, String projectName, String dirName) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", 1);
        turnMap.put("resultMsg", "上传失败");

//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
//        ServletContext servletContext = webApplicationContext.getServletContext();
//        String realPath = servletContext.getRealPath(File.separator);
        String imgPath = (String) propertyToRedis.getPropertyValue("img.path");
        String requestTime = DateUtil.sdfSs.format(new Date());
//        String basePath = imgPath + File.separator + dirName + File.separator + requestTime.substring(0, 6) + File.separator;
        boolean falg = true;
        Iterator fileIte = multipartFiles.keySet().iterator();
        Map<String, Object> resultMap = new HashMap<>();

        while (fileIte.hasNext()) {
            String key = (String) fileIte.next();
            if (!"file".equals(key)) {
                continue;
            }
            falg = false;
            MultipartFile file = multipartFiles.get(key);

            String filePath = null;
            try {
                filePath = imageUtil.saveFile(file);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                turnMap.put("msg", "处理失败");
                return turnMap;
            }
            String incr = redisUtil.incrAtomic(Constants.REDIS_FILE_NBR + requestTime, 1, 4, TimeUnit.SECONDS);
            String fileType1 = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
            String newFileName = requestTime + incr;

//            String filePath = basePath + newFileName + fileType1;
//            File desFile = new File(filePath);
//            if (!desFile.getParentFile().exists()) {
//                desFile.getParentFile().mkdirs();
//            }
//            try {
//                byte[] bytes = file.getBytes();
//                file.transferTo(desFile);
//                //集群同步服务器
//                redisUtil.addRedisForKey(newFileName, bytes);
//                Map<String, Object> map = synFile(newFileName, newFileName + fileType1, projectName, dirName);
//                redisUtil.delRedisForKey(newFileName);
//                if (!Constants.CODE_SUCC.equals(map.get(Constants.RESULT_CODE_STR))) {
//                    turnMap.put("resultMsg", map.get("resultMsg"));
//                    return turnMap;
//                }
//
//            } catch (IllegalStateException | IOException e) {
//                e.printStackTrace();
//                turnMap.put("msg", "处理失败");
//                return turnMap;
//            }
            String newFileName1 = filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));
//            String fileType1 = filePath.substring(filePath.indexOf("."));
            String dirPath = filePath.substring(0,filePath.lastIndexOf("/")+1);
            CoImg img = new CoImg();
            img.setLinkType(linkType);
            img.setFileName(newFileName);
            img.setOldName(file.getOriginalFilename());
//            img.setNewName(newFileName + fileType1);
            img.setNewName(newFileName1 + fileType1);
//            img.setFileDir(dirName + "/" + requestTime.substring(0, 6) + "/");
            img.setFileDir(dirPath);
            img.setStatusCd("101");
            img.setStorageType("1");
            img.setAddTime(new Date());
            img.setFileType(fileType);
            Integer count = imgBmo.insertAndUpdateSelective(img);
            resultMap.put("imgId", img.getImgId());
//            resultMap.put("fileDir", "/images/" + img.getFileDir() + img.getNewName());
            resultMap.put("fileDir", img.getFileDir() + img.getNewName());
            if (count != 1) {
                turnMap.put("resultMsg", "保存数据失败");
                return turnMap;
            }
//            redisUtil.addRedisForKey(Constants.REDIS_IMAGE_URL + linkId + fileType,  "/images/" + img.getFileDir() + img.getNewName());
        }
        if (falg) {
            turnMap.put("resultMsg", "附件读取失败");
            return turnMap;
        }
        turnMap.put("resultCode", 0);
        turnMap.put("resultMsg", "上传成功");
        turnMap.put("result", resultMap);
        return turnMap;

    }

    @Override
    public Map<String, Object> uploadFileQy(Map<String, MultipartFile> multipartFiles,String linkId,String linkType,String fileType ,String projectName,String dirName) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", 1);
        turnMap.put("resultMsg", "上传失败");

//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
//        ServletContext servletContext = webApplicationContext.getServletContext();
//        String realPath = servletContext.getRealPath(File.separator);
        String imgPath = (String) propertyToRedis.getPropertyValue("img.path");
        String requestTime = DateUtil.sdfSs.format(new Date());
//        String basePath = imgPath + File.separator + dirName + File.separator + requestTime.substring(0,6) + File.separator;
        boolean falg = true;
        Iterator fileIte = multipartFiles.keySet().iterator();
        while (fileIte.hasNext()) {
            String key = (String) fileIte.next();
            if(!"file".equals(key)){
                continue;
            }
            falg = false;
            MultipartFile file  = multipartFiles.get(key);
            String filePath = null;
            try {
                filePath = imageUtil.saveFile(file);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                turnMap.put("msg", "处理失败");
                return turnMap;
            }
            String incr = redisUtil.incrAtomic(Constants.REDIS_FILE_NBR + requestTime, 1, 4, TimeUnit.SECONDS);
            String fileType1 = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
            String newFileName = requestTime + incr;
//            String filePath =  basePath  + newFileName  + fileType1 ;
//            File desFile = new File(filePath);
//            if (!desFile.getParentFile().exists()) {
//                desFile.getParentFile().mkdirs();
//            }
//            try {
//                byte[] bytes = file.getBytes();
//                file.transferTo(desFile);
//                //集群同步服务器
//                redisUtil.addRedisForKey(newFileName,bytes);
//
//                Map<String,Object> map = synFile(newFileName,newFileName  + fileType1,projectName,dirName);
//
//                redisUtil.delRedisForKey(newFileName);
//                if(!Constants.CODE_SUCC.equals(map.get("resultCode"))){
//                    turnMap.put("resultMsg", map.get("resultMsg"));
//                    return turnMap;
//                }
//
//            } catch (IllegalStateException | IOException e) {
//                e.printStackTrace();
//                turnMap.put("msg", "处理失败");
//                return turnMap;
//            }

            String newFileName1 = filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));
//            String fileType1 = filePath.substring(filePath.indexOf("."));
            String dirPath = filePath.substring(0,filePath.lastIndexOf("/")+1);
            CoImg img = new CoImg();
            img.setLinkId(linkId);
            img.setLinkType(linkType);
            img.setFileName(newFileName);
            img.setOldName(file.getOriginalFilename());
            img.setNewName(newFileName1 + fileType1);
            img.setFileDir(dirPath);
            img.setStatusCd("101");
            img.setStorageType("1");
            img.setAddTime(new Date());
            img.setFileType(fileType);
            Integer count = imgBmo.insertAndUpdateSelective(img);
            if(count != 1) {
                turnMap.put("resultMsg", "保存数据失败");
                return turnMap;
            }
//            String imageUrl = (String) propertyToRedis.getPropertyValue("image-url");
            String imageUrl = commonBmo.getImageServerUrl();
            filePath = imageUrl +  img.getFileDir() + img.getNewName();
            Map<String, String> map = (Map<String, String>) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + Constants.REDIS_IMAGE_URL);
            if (map == null) {
                map = new HashMap<>();
            }
            String redisMapKey = linkId + fileType;
//            filePath = map.get(redisMapKey);
            filePath = img.getFileDir() + img.getNewName();
            map.put(redisMapKey, filePath);
            redisUtil.addRedisForKey(Constants.REDIS_DEL_KEY + Constants.REDIS_IMAGE_URL,  map);
        }
        if(falg){
            turnMap.put("resultMsg", "附件读取失败");
            return turnMap;
        }
        turnMap.put("resultCode", 0);
        turnMap.put("resultMsg", "上传成功");
        return turnMap;

    }


    private Map<String, Object> synFile(String fileCode, String fileName, String projectName, String dirName) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", Constants.CODE_FAIL);
        turnMap.put("resultMsg", "同步失败");
        Map dictItemParam = new HashMap();
        dictItemParam.put("groupCode", "CLUSTER_CONFIG");
        List<CoDicItem> dicItems = dicGroupAndItemDao.queryDicItemByCode(dictItemParam);
        String clusterConfigStr = "";
        for (int i = 0; i < dicItems.size(); i++) {
            if (dicItems.get(i).getItemCode().equals("CLUSTER_CONFIG")) {
                clusterConfigStr = dicItems.get(i).getItemVal();
            }
        }
        String[] clusterConfig = clusterConfigStr.split(",");
        for (String config : clusterConfig) {
            if (!StringUtils.isEmpty(config)) {
                config = "10.19.83.36:8080";
                Map<String, Object> map = synFileHttp(config, fileCode, fileName, projectName, dirName);
                if (!Constants.CODE_SUCC.equals(map.get("resultCode"))) {
                    return map;
                }
            }
        }
        turnMap.put("resultCode", Constants.CODE_SUCC);
        turnMap.put("resultMsg", "同步成功");
        return turnMap;
    }


    private Map<String, Object> synFileHttp(String ip, String fileCode, String fileName, String projectName, String dirName) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", Constants.CODE_FAIL);
        turnMap.put("resultMsg", "调用接口失败");
        String url = "/service/synFile";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> map = new HashMap<>();
        map.put("fileCode", fileCode);
        map.put("fileName", fileName);
        map.put("dirName", dirName);
        turnMap = this.postHttp("http://" + ip + projectName + url, map, headerMap);
        return turnMap;
    }

    /**
     * 访问权益接口 http 请求 post 内部使用
     *
     * @param url        地址
     * @param params     参数
     * @param headersMap header
     * @return String 类型
     */
    private Map<String, Object> postHttp(String url, Map<String, Object> params, Map headersMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String result = restTemplateUtil.postHttp(url, params, headersMap);
        if (!JsonUtil.isValidJson(result)) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
            turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
            log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
        } else {
            Map<String, Object> interfaceMap = JsonUtil.toObject(result, Map.class);

            if (!"0".equals(MapUtils.getString(interfaceMap, Constants.RESULT_CODE_STR))) {
                turnMap.put(Constants.RESULT_MSG_STR, interfaceMap.get(Constants.RESULT_MSG_STR));
                return turnMap;
            }

            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "调用接口成功");
            turnMap.put("result", interfaceMap.get("data"));
        }
        return turnMap;
    }


    public Map<String, Object> downloadRedis(String fileCode,String fileName,String dirName) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", "1");
        turnMap.put("resultMsg", "同步失败");

        String imgPath = (String) propertyToRedis.getPropertyValue("img.path");
//        String basePath = realPath + File.separator + "resources" + File.separator + imgPath + File.separator + fileName.substring(0,6) + File.separator;
        String basePath = imgPath + File.separator + dirName + File.separator + fileName.substring(0,6) + File.separator;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        byte[] bytes = (byte[]) redisUtil.getRedisForKey(fileCode);
        if(bytes == null){
            log.info("缓存读取失败");
            return turnMap;
        }
        try {
            File desFile = new File(basePath + fileName);
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            if(desFile.exists()){
                turnMap.put("resultCode", "0");
                turnMap.put("resultMsg", "同步成功");
                return turnMap;
            }
            fos = new FileOutputStream(desFile);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        turnMap.put("resultCode", "0");
        turnMap.put("resultMsg", "同步成功");
        return turnMap;

    }

}
