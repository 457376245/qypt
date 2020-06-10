package com.web.bmo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.web.common.*;
import com.web.dao.CommonDao;
import com.web.dao.DicGroupAndItemDao;
import com.web.model.CoDicItem;
import com.web.model.CoRegion;
import com.web.util.*;
import com.web.util.common.CommonParams;
import com.web.util.common.Log;
import com.web.util.inter.HttpRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;

/**
 * <b>项目名称：</b>web-portal<br>
 * <b>类名称：</b>com.web.bmo.CommonBmoImpl.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>公共方法调用<br>
 * <b>创建时间：</b>2016年5月24日-上午11:19:08<br>
 */

@Service("com.web.bmo.CommonBmoImpl")
public class CommonBmoImpl implements CommonBmo {

    protected final Log log = Log.getLog(getClass());

    @Autowired
    @Qualifier("com.web.common.ServiceCallSys")
    private ServiceCallSys callSys;

//	@Autowired
//	@Qualifier("redisTemplate")
//	private RedisTemplate<String, Object> jedisTemplate;

    @Autowired
    @Qualifier("com.web.common.PropertyToRedis")
    protected PropertyToRedis propertyToRedis;

    @Resource(name = "com.web.bmo.CommonBmoImpl")
    private CommonBmo commonBmo;
    
    @Resource(name = "com.web.dao.CommonDao")
    private CommonDao commonDao;

    @Autowired
    @Qualifier("comm.dicGroupAndItemDao")
    private DicGroupAndItemDao dicGroupAndItemDao;

    @Resource(name = "RedisUtil")
    private RedisUtil redisUtil;

    //private static final Log log = Log.getLog(CommonBmoImpl.class);

    @Override
    public Map<String, Object> qryInfoForSys(String serviceType, String serviceCode, Map<String, Object> eMap) throws Exception {
        return callSys.httpSysServiceCall(serviceType, serviceCode, eMap);
    }


    public Map<String, Object> qryInterfaceByJson(String serviceType, String serviceCode, String jsonInfo) throws Exception {
        return callSys.httpSysServiceCallJson(serviceType, serviceCode, jsonInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setDelRedisKey(String key) throws Exception {
        Map<String, String> map = (Map<String, String>) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY);
        if (map == null) {
            map = new HashMap<String, String>();
            map.put(key, key);
        } else {
            map.put(key, key);
        }
//		jedisTemplate.opsForValue().set(Constants.REDIS_DEL_KEY, map);//存放数据
        redisUtil.addRedisForKey(Constants.REDIS_DEL_KEY, map);
    }

    @SuppressWarnings("unchecked")
    public void delRedisKey() throws Exception {
//		Map<String, String> map=(Map<String, String>) jedisTemplate.opsForValue().get(Constants.REDIS_DEL_KEY);
        Map<String, String> map = (Map<String, String>) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY);
        if (map != null && map.size() > 0) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
//				jedisTemplate.delete(key);//删除数据
                redisUtil.delRedisForKey(key);
            }
//			jedisTemplate.delete(Constants.REDIS_DEL_KEY);
            redisUtil.delRedisForKey(Constants.REDIS_DEL_KEY);
        }
    }

    public void delCertByNbr(String requestNbr) {
        if (!StringUtil.isEmptyStr(requestNbr)) {
//			jedisTemplate.delete("CERT_"+requestNbr);
            redisUtil.delRedisForKey("CERT_" + requestNbr);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean validationCert(String staffCode, String partyName, String certNumber, String requestNbr) {
        //后期考虑是否要加上日志记录客户定位信息

        String validationMode = (String) propertyToRedis.getPropertyValue("VALIDATION_MODE");

        if (StringUtil.isEmptyStr(validationMode)) {
            validationMode = "2";
        }

        //如果是1，代表不需要验证，返回正确
        if (validationMode.equals("1")) {
            return true;
        }

        if (StringUtil.isEmptyStr(requestNbr) || StringUtil.isEmptyStr(partyName) || StringUtil.isEmptyStr(certNumber)) {
            return false;
        }

//		if(jedisTemplate.opsForValue().get("CERT_"+requestNbr)==null){
        if (redisUtil.getRedisForKey("CERT_" + requestNbr) == null) {
            return false;
        }

//		Map<String, String> map=(Map<String,String>) jedisTemplate.opsForValue().get("CERT_"+requestNbr);
        Map<String, String> map = (Map<String, String>) redisUtil.getRedisForKey("CERT_" + requestNbr);

        if (map == null || map.size() <= 0) {
            return false;
        }

        String appPartyName = map.get("partyName");
        String appCertNumber = map.get("certNumber");

        if (StringUtil.isEmptyStr(appPartyName) || StringUtil.isEmptyStr(appCertNumber)) {
            return false;
        }

        //信息一致通过验证
        if (appPartyName.equals(partyName) && appCertNumber.equals(certNumber)) {
            return true;
        }

        return false;
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean validationCert(String staffCode, String certNumber, String requestNbr) {
        //后期考虑是否要加上日志记录客户定位信息

        String validationMode = (String) propertyToRedis.getPropertyValue("VALIDATION_MODE");

        if (StringUtil.isEmptyStr(validationMode)) {
            validationMode = "2";
        }

        //如果是1，代表不需要验证，返回正确
        if (validationMode.equals("1")) {
            return true;
        }

        if (StringUtil.isEmptyStr(requestNbr) || StringUtil.isEmptyStr(certNumber)) {
            return false;
        }

//		if(jedisTemplate.opsForValue().get("CERT_"+requestNbr)==null){
        if (redisUtil.getRedisForKey("CERT_" + requestNbr) == null) {
            return false;
        }

//		Map<String, String> map=(Map<String,String>) jedisTemplate.opsForValue().get("CERT_"+requestNbr);
        Map<String, String> map = (Map<String, String>) redisUtil.getRedisForKey("CERT_" + requestNbr);
        if (map == null || map.size() <= 0) {
            return false;
        }

        String appCertNumber = map.get("certNumber");
        if (StringUtil.isEmptyStr(appCertNumber)) {
            return false;
        }

        //信息一致通过验证
        if (appCertNumber.equals(certNumber)) {
            return true;
        }

        return false;
    }

    /**
     * 调用接口，保存单点日志等
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> addInterfaceLog(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put("resultCode", "1");
        turnMap.put("resultMsg", "保存日志失败");

        Map<String, Object> queryResult = this.commonBmo.qryInfoForSys("soService", "saveInterFaceLog", paramsMap);

        if (!ServiceParam.SYS_SUCCESS.equals(MapUtils.getString(queryResult, "resultCode"))) {
            turnMap.put("resultMsg", MapUtils.getString(queryResult, "resultMsg"));
            return turnMap;
        }

        //解析数据
        Map<String, Object> reusltInfo = (Map<String, Object>) MapUtils.getMap(queryResult, "result");

        if (!"0".equals(MapUtils.getString(reusltInfo, "resultCode"))) {
            turnMap.put("resultMsg", MapUtils.getString(reusltInfo, "resultMsg"));
            return turnMap;
        }

        //最终返回
        turnMap.put("resultCode", "0");
        turnMap.put("resultMsg", "保存成功");

        return turnMap;
    }

    /**
     * 获取统一登录accessToken
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> queryOauthToken(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        turnMap.put(CommonParams.RESULT_MSG_STR, "获取accessToken失败");

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("client_id", MapUtils.getString(paramsMap, "clientId")));
        formparams.add(new BasicNameValuePair("redirect_uri", MapUtils.getString(paramsMap, "redirectUri")));
        formparams.add(new BasicNameValuePair("grant_type", MapUtils.getString(paramsMap, "grantType")));
        formparams.add(new BasicNameValuePair("code", MapUtils.getString(paramsMap, "tokenCode")));

        log.info("查询accessToken入参：" + paramsMap);

        try {
            String crmAppAccessTokenUrl = (String) propertyToRedis.getPropertyValue("CRM_APP_ACCESSTOKEN_URL");

            //10.40.250.30:8002
            Map<String, Object> httpResult = HttpUtil.sendUrlEncodedPost(crmAppAccessTokenUrl, formparams);

            log.info("查询accessToken出参:" + httpResult);

            if (httpResult == null || MapUtils.getIntValue(httpResult, "httpStatus", 400) != 200) {
                return turnMap;
            }

            log.info("=================2");

            String retSrc = MapUtils.getString(httpResult, "retSrc");

            Map<String, Object> retSrcMap = JsonUtil.toObject(retSrc, Map.class);

            log.info("retSrc:" + retSrcMap);

            String access_token = MapUtils.getString(retSrcMap, "access_token", "");

            log.info("access_token:" + access_token);

            String json = RSAUtils.decryptByPrivateKeyStr(access_token, CommonParams.CRM_APP_RSA_KEY);

            log.info("json:" + json);

            if (json == null || StringUtils.isEmpty(json)) {
                turnMap.put(CommonParams.RESULT_MSG_STR, "解密json为空");
                return turnMap;
            }

            log.info("解密统一认证数据为：" + json);

            Map<String, Object> tokenMap = JsonUtil.toObject(json, Map.class);

            if (tokenMap == null || !tokenMap.containsKey("user") || tokenMap.get("user") == null) {
                turnMap.put(CommonParams.RESULT_MSG_STR, "USER为空");
                return turnMap;
            }

            Map<String, Object> user = (Map<String, Object>) MapUtils.getMap(tokenMap, "user");

            String staffAccount = MapUtils.getString(user, "staffAccount", "");
            String mobilePhone = MapUtils.getString(user, "mobilePhone", "");
            //String certNumber =MapUtils.getString(user, "certNumber","");
            //String staffId =MapUtils.getString(user, "staffId","");
            String orgIds = MapUtils.getString(user, "orgIds", "");

            if (StringUtil.isEmptyStr(staffAccount) || StringUtil.isEmptyStr(mobilePhone) || StringUtil.isEmptyStr(orgIds)) {
                turnMap.put(CommonParams.RESULT_MSG_STR, "返回用户数据为空");
                return turnMap;
            }

            turnMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            turnMap.put(CommonParams.RESULT_MSG_STR, "查询完成");
            turnMap.putAll(user);
        } catch (Exception e) {
            log.error("在调用统一认证接口时获得异常:", e);
        }

        return turnMap;
    }

    //===============CRM3.0接口调用======

    /**
     * 调用CRM3.0接口公共方法
     *
     * @param sysType     调用中心
     * @param interCode   接口编码
     * @param serviceCode 方法编码
     * @param jsonStr     参数（Json）
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> httpServiceForJson(String sysType, String interCode, String serviceCode, String jsonStr, Map<String, Object> otherParam) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("resultCode", "1");
        resultMap.put("resultMsg", "调用远程接口失败");
//		long strNo=(new Date()).getTime();
        log.info("调用中心:" + sysType + ";调用接口:" + interCode + ";调用方法:" + serviceCode);
        log.info("调用[" + serviceCode + "]入参:" + jsonStr);

        if (StringUtil.isEmptyStr(sysType) || StringUtil.isEmptyStr(serviceCode)) {
            return resultMap;
        }

        try {
            String localSysCode = (String) propertyToRedis.getPropertyValue("LOCAL_SYS_CODE");

            //获取调用中心接口地址
            String httpUrl = (String) propertyToRedis.getPropertyValue(sysType + "_" + localSysCode);

            if (!StringUtil.isEmptyStr(interCode)) {
                httpUrl += "/" + interCode;
            }

            if (!StringUtil.isEmptyStr(serviceCode)) {
                httpUrl += "/" + serviceCode;
            }

            log.info("调用[" + serviceCode + "]地址:" + httpUrl);
            String resultJson = HttpUtil.doPost(httpUrl, jsonStr, otherParam);

            log.info("调用[" + serviceCode + "]回参:" + resultJson);

            if (resultJson != null && !"".equals(resultJson)) {
                resultMap = JsonUtil.toObject(resultJson, Map.class);
            }
        } catch (Exception e) {
            log.error("在调用CRM3.0接口时获得异常:" + e);
        }

        return resultMap;
    }

    /**
     * 调用CRM3.0接口公共方法（捕获异常）
     *
     * @param sysType     调用中心
     * @param interCode   接口编码
     * @param serviceCode 方法编码
     * @param jsonStr     参数（Json）
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> httpServiceForCatchJson(String sysType, String interCode, String serviceCode, String jsonStr, Map<String, Object> otherParam) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("resultCode", "1");
        resultMap.put("resultMsg", "调用远程接口失败");
//		long strNo=(new Date()).getTime();
        log.info("调用中心:" + sysType + ";调用接口:" + interCode + ";调用方法:" + serviceCode);
        log.info("调用[" + serviceCode + "]入参:" + jsonStr);

        if (StringUtil.isEmptyStr(sysType) || StringUtil.isEmptyStr(serviceCode)) {
            return resultMap;
        }

        try {
            String localSysCode = (String) propertyToRedis.getPropertyValue("LOCAL_SYS_CODE");

            //获取调用中心接口地址
            String httpUrl = (String) propertyToRedis.getPropertyValue(sysType + "_" + localSysCode);

            if (!StringUtil.isEmptyStr(interCode)) {
                httpUrl += "/" + interCode;
            }

            if (!StringUtil.isEmptyStr(serviceCode)) {
                httpUrl += "/" + serviceCode;
            }

            log.info("调用[" + serviceCode + "]地址:" + httpUrl);
            String resultJson = HttpUtil.doCatchPost(httpUrl, jsonStr, otherParam);

            log.info("调用[" + serviceCode + "]回参:" + resultJson);

            if (!StringUtil.isEmptyStr(resultJson) && resultJson.indexOf("异常") == -1) {
                resultMap = JsonUtil.toObject(resultJson, Map.class);
            } else {//接口异常
                resultMap.put("resultCode", "-1");
            }
        } catch (Exception e) {
            log.error("在调用CRM3.0接口时获得异常:" + e);
            resultMap.put("resultCode", "-1");
        }
        return resultMap;
    }

    /**
     * 进行客户定位
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qryCustomerList(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "客户定位失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cust_cust_qryCustomerList"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_CUST, "service", "cust_cust_qryCustomerList", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");
			
	/*		if(resultObject.get("customers")==null){
				resultMap.put(CommonParams.RESULT_MSG_STR, "客户信息不存在");
				return resultMap;
			}*/

            //对客户信息进行脱敏
            List<Map<String, Object>> customers = (List<Map<String, Object>>) resultObject.get("customers");

            resultMap.put("customers", customers);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询客户信息时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 新建客户
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> addCustomerList(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "新建客户失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cust_cust_createCustomerDetailForProvChnl"), Map.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_CUST, "service", "cust_cust_createCustomerDetailForProvChnl", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            resultMap.put("resultObject", resultObject);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在新建客户信息时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询客户套餐产品数据
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qryOfferInstListView(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询客户套餐产品信息失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cust_inst_qryOfferInstListView"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_ZC, "service", "cust_inst_qryOfferInstListView", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("offerInsts") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无套餐产品信息");
                return resultMap;
            }

            resultMap.put("offerInsts", resultObject.get("offerInsts"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询可订购附属(可选包)
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qryOptionalOffer(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询可订购附属失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_qryOptionalOffer"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_qryOptionalOffer", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("qryOptionalOffers") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无可订购附属信息");
                return resultMap;
            }

            resultMap.put("qryOptionalOffers", resultObject.get("qryOptionalOffers"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询可订购附属(功能产品)
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qryOptionalProd(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询可订购功能产品失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_qryOptionalProd"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_qryOptionalProd", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("qryOptionalProds") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无可订购附属信息");
                return resultMap;
            }

            resultMap.put("qryOptionalProds", resultObject.get("qryOptionalProds"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询可订购附属(包括可选包和功能产品)
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    public Map<String, Object> qryOptionalOfferAndProd(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询可订购附属失败");

        try {
            //获取可选包
            Map<String, Object> offerResult = this.qryOptionalOffer(paramsMap);

            if (!CommonParams.RESULT_SUCC.equals(MapUtils.getString(offerResult, CommonParams.RESULT_CODE_STR))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, offerResult.get(CommonParams.RESULT_MSG_STR));
                return resultMap;
            }

            //获取功能产品
            Map<String, Object> prodResult = this.qryOptionalProd(paramsMap);

            if (!CommonParams.RESULT_SUCC.equals(MapUtils.getString(prodResult, CommonParams.RESULT_CODE_STR))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, prodResult.get(CommonParams.RESULT_MSG_STR));
                return resultMap;
            }

            resultMap.put("qryOptionalProds", prodResult.get("qryOptionalProds"));
            resultMap.put("qryOptionalOffers", offerResult.get("qryOptionalOffers"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 串码查询预占
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> queryMaterial(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "串码校验失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "so_sr_queryMaterial"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_RES, "service", "so_sr_queryMaterial", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回预占结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            String handleResultCode = MapUtils.getString(resultObject, "handleResultCode", "1");
            String handleResultMsg = MapUtils.getString(resultObject, "handleResultMsg", "校验串码失败");

            if (!"0".equals(handleResultCode)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, handleResultMsg);
                return resultMap;
            }

            resultMap.put("materialInfo", queryResultMap.get("resultObject"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在校验串码时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 串码释放
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> releaseMaterial(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "串码释放失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "so_sr_releaseMaterial"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_RES, "service", "so_sr_releaseMaterial", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            String handleResultCode = MapUtils.getString(resultObject, "handleResultCode", "1");
            String handleResultMsg = MapUtils.getString(resultObject, "handleResultMsg", "释放串码失败");

            if (!"0".equals(handleResultCode)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, handleResultMsg);
                return resultMap;
            }

            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在释放串码时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * UIM卡查询预占
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> queryUIMMaterial(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "UIM卡校验失败");

        try {
            // 先查询UIM卡信息 再预占
            Map<String, Object> UIMMaterialInfoMap = queryUIMMaterialInfo(paramsMap);
            String UIMInfoResultCode = MapUtils.getString(UIMMaterialInfoMap, CommonParams.RESULT_CODE_STR, "");
            String UIMInfoResultMsg = MapUtils.getString(UIMMaterialInfoMap, CommonParams.RESULT_MSG_STR, "");
            if (!CommonParams.RESULT_SUCC.equals(UIMInfoResultCode)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, UIMInfoResultMsg);
                return resultMap;
            }
            Map<String, Object> UIMInfoResultObject = (Map<String, Object>) MapUtils.getMap(UIMMaterialInfoMap, "resultObject");

            //进行UIM卡预占
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "so_sr_occupyMktResCore"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_RES, "service", "so_sr_occupyReleaseMktResCore", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回预占结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            String handleResultCode = MapUtils.getString(resultObject, "handleResultCode", "1");
            String handleResultMsg = MapUtils.getString(resultObject, "handleResultMsg", "UIM卡校验失败");

            if (!"0".equals(handleResultCode)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, handleResultMsg);
                return resultMap;
            }

//			resultMap.put("materialInfo", queryResultMap.get("resultObject"));
            resultMap.put("materialInfo", UIMInfoResultObject);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在校验UIM卡时获得异常:", e);
        }

        return resultMap;
    }

    private Map<String, Object> queryUIMMaterialInfo(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "UIM卡查询失败");
        try {
            params.put("pageIndex", 1);
            params.put("pageSize", 20);
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(params, "so_sr_queryMktResListCore"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_RES, "service", "so_sr_queryMktResListCore", jsonStr, params);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));
                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回预占结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            String handleResultCode = MapUtils.getString(resultObject, "handleResultCode", "1");
            String handleResultMsg = MapUtils.getString(resultObject, "handleResultMsg", "UIM卡信息查询失败");

            if (!"0".equals(handleResultCode)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, handleResultMsg);
                return resultMap;
            }
            resultMap.put("resultObject", resultObject);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "UIM卡信息查询成功");
        } catch (Exception e) {
            log.error("在查询UIM卡时获得异常:", e);
        }
        return resultMap;
    }

    /**
     * UIM卡释放
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> releaseUIMMaterial(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "UIM卡释放失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "so_sr_ReleaseMktResCore"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_RES, "service", "so_sr_occupyReleaseMktResCore", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            String handleResultCode = MapUtils.getString(resultObject, "handleResultCode", "1");
            String handleResultMsg = MapUtils.getString(resultObject, "handleResultMsg", "释放UIM卡失败");

            if (!"0".equals(handleResultCode)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, handleResultMsg);
                return resultMap;
            }

            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "UIM卡释放成功");
        } catch (Exception e) {
            log.error("在释放串码时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 可订购附属提交
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> submitOptionalOffer(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "订单提交失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "so_so_saveSceneData"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_ORDER, "service", "so_so_saveSceneData", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "订单提交失败");

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回提交结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            Map<String, Object> ruleChainRespVo = (Map<String, Object>) MapUtils.getMap(resultObject, "ruleChainRespVo", null);//规则数据
            Map<String, Object> customerOrderRspVo = (Map<String, Object>) MapUtils.getMap(resultObject, "customerOrderRspVo", null);//订单数据

            String result = MapUtils.getString(ruleChainRespVo, "result", "1");

            if (!"0".equals(result)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "规则校验不通过");
                return resultMap;
            }

            if (customerOrderRspVo == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回订单信息");
                return resultMap;
            }

            String custOrderNbr = MapUtils.getString(customerOrderRspVo, "custOrderNbr", "");
            String custOrderId = MapUtils.getString(customerOrderRspVo, "custOrderId", "");

            if ("".equals(custOrderNbr) && "".equals(custOrderId)) {
                resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
                resultMap.put(CommonParams.RESULT_MSG_STR, "返回提交结果信息不完全");
            }

            resultMap.put("custOrderNbr", custOrderNbr);
            resultMap.put("custOrderId", custOrderId);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在提交订购附属数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询校园宽带套餐
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qrySchoolOffer(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询校园宽带套餐失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_qryOfferList"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_qryOfferList", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("offers") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无校园宽带套餐信息");
                return resultMap;
            }

            resultMap.put("offers", resultObject.get("offers"));
            resultMap.put("pageInfo", resultObject.get("pageInfo"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询校园宽带礼包
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> qrySchoolGiftsOffer(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询校园宽带礼包失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_qryOfferPackageList"), Map.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_qryOfferPackageList", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("offerPackages") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无校园宽带礼包信息");
                return resultMap;
            }

            resultMap.put("offerPackages", resultObject.get("offerPackages"));
            resultMap.put("pageInfo", resultObject.get("pageInfo"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询校园宽带礼包构成
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> qrySchoolGiMberOffer(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询校园宽带礼包构成失败");
        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_getOfferPkgDetail"), Map.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_getOfferPkgDetail", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("packageObjRels") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无校园宽带礼包构成信息");
                return resultMap;
            }

            resultMap.put("packageObjRels", resultObject.get("packageObjRels"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询是否为AB类套餐, 即是否需要传4G标识
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> isABPackage(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询是否为AB类套餐失败");
        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_judgeOfferCatalogItemRel"), Map.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_judgeOfferCatalogItemRel", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));
                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            String result = MapUtils.getString(resultObject, "result", "");
            if ("EXIST".equalsIgnoreCase(result)) {
                resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
                resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
            } else {
                resultMap.put(CommonParams.RESULT_CODE_STR, "该套餐非AB类套餐");
            }
        } catch (Exception e) {
            log.error("在查询是否为AB类套餐时获得异常:", e);
        }
        return resultMap;
    }

    /**
     * 查询套餐构成
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qryNetConsist(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询校园宽带套餐构成失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_qryOfferStructure"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_qryOfferStructure", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("offerProdRels") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无套餐构成信息");
                return resultMap;
            }

            /**屏蔽部分宽带成员*/
            List<Map<String, Object>> offerProdRels = (List<Map<String, Object>>) resultObject.get("offerProdRels");
            for (int i = 0; i < offerProdRels.size(); i++) {
                Integer offerProdId = MapUtils.getInteger(offerProdRels.get(i), "prodId");
                if (offerProdId == 9 || offerProdId == 10 || offerProdId == 280000224) {
                    offerProdRels.remove(i);
                    i--;
                }
            }
            resultObject.put("offerProdRels", offerProdRels);
            /**屏蔽部分宽带成员*/

            resultMap.put("resultObject", resultObject);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询套餐产品数据时获得异常:", e);
        }

        return resultMap;
    }


    /**
     * 校园宽带提交
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> submitSchoolNetOffer(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "订单提交失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "so_so_saveSceneData"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_ORDER, "service", "so_so_saveSceneData", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty()) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "订单提交失败，未返回订单提交结果");

                return resultMap;
            }

            if (!"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                String msg = MapUtils.getString(queryResultMap, "resultMsg");
                if (msg == null || msg.trim().equals("")) {
                    msg = "订单提交失败，未返回失败信息";
                }
                resultMap.put(CommonParams.RESULT_MSG_STR, msg);
                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回提交结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            Map<String, Object> ruleChainRespVo = (Map<String, Object>) MapUtils.getMap(resultObject, "ruleChainRespVo", null);//规则数据
            Map<String, Object> customerOrderRspVo = (Map<String, Object>) MapUtils.getMap(resultObject, "customerOrderRspVo", null);//订单数据

            String result = MapUtils.getString(ruleChainRespVo, "result", "1");

            if (!"0".equals(result)) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "规则校验不通过");
                return resultMap;
            }

            if (customerOrderRspVo == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回订单信息");
                return resultMap;
            }

            String custOrderNbr = MapUtils.getString(customerOrderRspVo, "custOrderNbr", "");
            String custOrderId = MapUtils.getString(customerOrderRspVo, "custOrderId", "");

            if ("".equals(custOrderNbr) && "".equals(custOrderId)) {
                resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
                resultMap.put(CommonParams.RESULT_MSG_STR, "返回提交结果信息不完全");
            }

            resultMap.put("custOrderNbr", custOrderNbr);
            resultMap.put("custOrderId", custOrderId);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在提交订购附属数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 一证五号查询
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    public Map<String, Object> qryOneToFiveNum(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "一证五号查询失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cust_cust_certFiveNumPreOccupy"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_CUST, "service", "cust_cust_certFiveNumPreOccupy", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "一证五号查询失败");

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            resultMap.put("resultObject", queryResultMap.get("resultObject"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");

        } catch (Exception e) {
            log.error("在查询一证五号数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 基础套餐查询
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    public Map<String, Object> qryBaseDinner(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "基础套餐查询失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_qryBaseOffer"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_qryBaseOffer", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "基础套餐查询失败");

                return resultMap;
            }

            resultMap.put("resultObject", queryResultMap.get("resultObject"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");

        } catch (Exception e) {
            log.error("在查询基础套餐数据时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 我的订单查询
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qryMyOrderList(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询我的订单失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "so_so_queryOrderListByStaff"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_ORDER, "service", "so_so_queryOrderListByStaff", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("customerOrders") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无订单信息");
                return resultMap;
            }

            resultMap.put("pageInfo", resultObject.get("pageInfo"));
            resultMap.put("orderFeeAmount", resultObject.get("orderFeeAmount"));
            resultMap.put("customerOrders", resultObject.get("customerOrders"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询订单时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 附属销售品详情查询
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> qryDetails(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询附属销售品详情失败");
        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cpc_ppm_qryOfferDetail"), Map.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_PPM, "service", "cpc_ppm_qryOfferDetail", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));

                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            if (resultObject.get("offerDetail") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "暂无附属销售品详情信息");
                return resultMap;
            }

            resultMap.put("offerDetail", resultObject.get("offerDetail"));
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询附属销售品详情时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询用户已有产品列表(简略信息)
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> qryCustOwnerTotal(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询用户已有产品列表(简略信息)失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cust_cust_qryCustOwnerTotal"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_CUST, "service", "cust_cust_qryCustOwnerTotal", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));
                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            //号码信息列表
            List<Map<String, Object>> accNumDetailList = (List<Map<String, Object>>) resultObject.get("accNumDetailList");

            resultMap.put("accNumDetailList", accNumDetailList);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询用户已有产品列表(简略信息)时获得异常:", e);
        }

        return resultMap;
    }

    /**
     * 查询号码余额,欠费信息
     *
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> qryCustCostInfo(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_FAIL);
        resultMap.put(CommonParams.RESULT_MSG_STR, "查询号码余额,欠费信息失败");

        try {
            String jsonStr = JsonUtil.toString(JsonUtil.toObject(TcpCont.buildInParam(paramsMap, "cust_cust_qryCustCostInfo"), ArrayList.class));

            if (jsonStr == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "组装入参报文失败");
                return resultMap;
            }

            Map<String, Object> queryResultMap = this.commonBmo.httpServiceForJson(CommonParams.HTTP_URL_CUST, "service", "cust_cust_qryCustCostInfo", jsonStr, paramsMap);

            if (queryResultMap == null || queryResultMap.isEmpty() || !"0".equals(MapUtils.getString(queryResultMap, "resultCode"))) {
                resultMap.put(CommonParams.RESULT_MSG_STR, queryResultMap.get("resultMsg"));
                return resultMap;
            }

            if (queryResultMap.get("resultObject") == null) {
                resultMap.put(CommonParams.RESULT_MSG_STR, "未正确返回查询结果信息");
                return resultMap;
            }

            Map<String, Object> resultObject = (Map<String, Object>) MapUtils.getMap(queryResultMap, "resultObject");

            resultMap.put("custCostInfo", resultObject);
            resultMap.put(CommonParams.RESULT_CODE_STR, CommonParams.RESULT_SUCC);
            resultMap.put(CommonParams.RESULT_MSG_STR, "完成");
        } catch (Exception e) {
            log.error("在查询号码余额,欠费信息时获得异常:", e);
        }

        return resultMap;
    }

    //===============CRM3.0接口调用======

    /**
     * 调用CRM3.0接口公共方法
     *
     * @param sysType     调用中心
     * @param interCode   接口编码
     * @param serviceCode 方法编码
     * @param jsonStr     参数（Json）
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> httpEopServiceForJson(String sysType, String interCode, String serviceCode, String jsonStr, Map<String, Object> otherParam, String eopType) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("resultCode", "1");
        resultMap.put("resultMsg", "调用远程接口失败");
//		long strNo=(new Date()).getTime();
        log.info("调用中心:" + sysType + ";调用接口:" + interCode + ";调用方法:" + serviceCode);
        log.info("调用[" + serviceCode + "]入参:" + jsonStr);

        if (StringUtil.isEmptyStr(sysType) || StringUtil.isEmptyStr(serviceCode)) {
            return resultMap;
        }

        try {
            String localSysCode = (String) propertyToRedis.getPropertyValue("LOCAL_SYS_CODE");

            //获取调用中心接口地址
            String httpUrl = (String) propertyToRedis.getPropertyValue(sysType + "_" + localSysCode);

            if (!StringUtil.isEmptyStr(interCode)) {
                httpUrl += "/" + interCode;
            }

            if (!StringUtil.isEmptyStr(serviceCode)) {
                httpUrl += "/" + serviceCode;
            }

            log.info("调用[" + serviceCode + "]地址:" + httpUrl);
            String appId = (String) propertyToRedis.getPropertyValue(eopType + "_APP_ID_" + localSysCode);
            String appKey = (String) propertyToRedis.getPropertyValue(eopType + "_APP_KEY_" + localSysCode);

            String resultJson = HttpRequest.sendEopPost(httpUrl, jsonStr, appId, appKey);

            log.info("调用[" + serviceCode + "]回参:" + resultJson);

            if (resultJson != null && !"".equals(resultJson)) {
                resultMap = JsonUtil.toObject(resultJson, Map.class);
            }
        } catch (Exception e) {
            log.error("在调用CRM3.0接口时获得异常:" + e);
        }

        return resultMap;
    }

    /**
     * 获取SAOP的header数据
     */
    @SuppressWarnings("unchecked")
    public List<CoDicItem> getSaopRouteCodes() {
        String dictKey = "SAOP_ROUTE_CODES";

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("groupCode", dictKey);

        List<CoDicItem> coDictItems = null;

        if (redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + dictKey) != null) {
            String coDictItemStr = (String) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + dictKey);
            coDictItems = JSONObject.parseArray(coDictItemStr, CoDicItem.class);
        } else {
            coDictItems = dicGroupAndItemDao.queryDicItemByCode(paramsMap);

            if (coDictItems != null && coDictItems.size() > 0) {
                //将Students转换为json格式
                redisUtil.addRedisInfo(Constants.REDIS_DEL_KEY + dictKey, JSON.toJSONString(coDictItems), 10);
            }
        }

        return coDictItems;
    }

	/**查询公共地市数据*/
	public List<CoRegion> getRegionList(Map<String, Object> paramsMap) throws Exception{
		String provinceCode = (String) propertyToRedis.getPropertyValue("PROVINCE_CODE");
		
		if(StringUtil.isEmptyMap(paramsMap)){
			paramsMap=new HashMap<String, Object>();
		}
		
		paramsMap.put("provinceCode", provinceCode);
		
		return this.commonDao.getRegionList(paramsMap);
	}


    /**
     * 获取SAOP的header数据
     */
    @SuppressWarnings("unchecked")
    public List<CoDicItem> getImageServerUrls() {
        String dictKey = "IMAGE_SERVER_URL";

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("groupCode", dictKey);

        List<CoDicItem> coDictItems = null;

        if (redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + dictKey) != null) {
            String coDictItemStr = (String) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + dictKey);
            coDictItems = JSONObject.parseArray(coDictItemStr, CoDicItem.class);
        } else {
            coDictItems = dicGroupAndItemDao.queryDicItemByCode(paramsMap);

            if (coDictItems != null && coDictItems.size() > 0) {
                //将Students转换为json格式
                redisUtil.addRedisInfo(Constants.REDIS_DEL_KEY + dictKey, JSON.toJSONString(coDictItems), 10);
            }
        }

        return coDictItems;
    }


    /**
     * 读取图片服务器地址
     */
    @SuppressWarnings("unchecked")
    public String getImageServerUrl() {
        List<CoDicItem> coDicItemList = this.getImageServerUrls();
        int coDicItemListSize = coDicItemList.size();
        for (int i = 0; i < coDicItemListSize; i++) {
            CoDicItem coDicItem = coDicItemList.get(i);
            if("IMAGE_SERVER_URL".equals(coDicItem.getItemCode())){
                return coDicItem.getItemVal();
            }
        }

        return "";
    }

    @Override
    public List<CoDicItem> getDicItems(Map<String, Object> paramsMap) throws Exception {
        return  dicGroupAndItemDao.queryDicItemByCode(paramsMap);
    }

}
