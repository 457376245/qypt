package com.web.thirdinterface;

import com.web.bmo.CommonBmo;
import com.web.common.Constants;
import com.web.common.PropertyToRedis;
import com.web.common.ServiceParam;
import com.web.model.CoDicItem;
import com.web.util.*;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * BSS3.0接口
 * </p>
 *
 * @package: com.web.thirdinterface
 * @description: BSS3.0接口
 * @author: wangzx8
 * @date: 2020/3/18 15:55
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Component
public class ThirdBSSInterface {

    private static Logger log = LoggerFactory.getLogger(ThirdQYInterface.class);

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    @Qualifier("com.web.common.PropertyToRedis")
    protected PropertyToRedis propertyToRedis;


    @Resource(name = "com.web.bmo.CommonBmoImpl")
    private CommonBmo commonBmo;

    @Resource
    private RedisUtil redisUtil;

    /**
     * CRM3.0-客户中心-查询已订购的附属销售品
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> oOpOfferInst(Map<String, Object> paramsMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "[LOCAL]查询已订购的附属销售品失败");

        try {
            //必传参数accNum或者account二选一，prodType必传，10-固话，11-宽带，12-手机，16-ITV

            //调用远程接口
            Map<String, Object> checkResultMap = this.getHttp("oOpOfferInst", paramsMap, null);

            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put("resultMsg", "[INTERFACE]" + checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcStr = "[{\"prodUseType\":\"2000\",\"offerSysType\":\"1400\",\"offerName\":\"家庭云-0元/月（限天翼畅享套餐用）\",\"isIndependent\":\"0\",\"rstrFlag\":\"OPTIONAL\",\"statusCd\":\"1000\",\"lanId\":8130500,\"prodId\":385698,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"offerType\":\"12\",\"createOrgId\":-1,\"effDate\":\"2019-01-20 11:10:47\",\"offerDesc\":\"月基本费0元，包含家庭云存储空间2TB。适用于畅享全融合、乐享99/129档全融合及城区79元全融合用户订购\",\"regionId\":8130533,\"statusName\":\"有效\",\"offerId\":1419872,\"ownerCustName\":\"张文娜\",\"prodInstId\":2365520626,\"offerInstId\":101486622434,\"offerInstAttr\":[]}]\n";
                    List<Map<String, Object>> existingOffers = new ArrayList<Map<String, Object>>();
                    existingOffers = JsonUtil.toObject(svcStr, ArrayList.class);
                    turnMap.put("existingOffers", existingOffers);//返回的已订购的附属销售品
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                }
                return turnMap;
            }

//			String fakeSvcStr = "[{\"busiModDate\":null,\"createOrgId\":12345678,\"effDate\":\"2017-05-17 10:10:42\",\"expDate\":\"2019-05-17 10:10:42\",\"expProcMethod\":null,\"extOfferInstId\":null,\"isIndependent\":\"N\",\"lanId\":1,\"lastOrderItemId\":1000000000031083,\"offerAgreeId\":null,\"offerId\":300130000040,\"offerInstAttrs\":[],\"offerInstId\":1000000000003204,\"offerName\":\"DID电话\",\"offerType\":\"11\",\"ownerCustId\":10001,\"pageInfo\":null,\"prodFunType\":\"101\",\"prodInstAttrs\":null,\"regionId\":2,\"subOffertype\":null}]";
//			String svcStr = fakeSvcStr;

            String svcStr = MapUtils.getString(checkResultMap, "svcCont");

            List<Map<String, Object>> existingOffers = new ArrayList<Map<String, Object>>();

            if (StringUtil.isEmptyStr(svcStr)) {
                turnMap.put("existingOffers", existingOffers);//如果未返回数据，默认就空
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                return turnMap;
            }

            existingOffers = JsonUtil.toObject(svcStr, ArrayList.class);

            turnMap.put("existingOffers", existingOffers);//返回的已订购的附属销售品
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
        } catch (Exception e) {
            log.error("[LOCAL]查询已订购的附属销售品获得异常:", e);

            turnMap.put(Constants.RESULT_MSG_STR, "[LOCAL]查询已订购的附属销售品异常," + e);
        }

        return turnMap;
    }


    /**
     * CRM3.0-客户中心-查询已订购的附属销售品 返回只有权益的销售品列表
     *
     * @param paramsMap
     * @return
     */
    public Map<String, Object> oOpOfferInstByRights(Map<String, Object> paramsMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "[LOCAL]查询已订购的附属销售品失败");

        try {
            //必传参数accNum或者account二选一，prodType必传，10-固话，11-宽带，12-手机，16-ITV

            //调用远程接口
            Map<String, Object> checkResultMap = this.getHttp("oOpOfferInst", paramsMap, null);

            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put("resultMsg", "[INTERFACE]" + checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcStr = "[{\"prodUseType\":\"2000\",\"offerSysType\":\"1400\",\"offerName\":\"全民K歌VIP会员月卡\",\"isIndependent\":\"0\",\"rstrFlag\":\"OPTIONAL\",\"statusCd\":\"1000\",\"lanId\":8130500,\"prodId\":385698,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"offerType\":\"12\",\"createOrgId\":-1,\"effDate\":\"2019-01-20 11:10:47\",\"offerDesc\":\"月基本费0元，包含家庭云存储空间2TB。适用于畅享全融合、乐享99/129档全融合及城区79元全融合用户订购\",\"regionId\":8130533,\"statusName\":\"有效\",\"offerId\":1419872,\"ownerCustName\":\"张文娜\",\"prodInstId\":2365520626,\"offerInstId\":7100003011,\"offerInstAttr\":[]}]\n";
                    List<Map<String, Object>> existingOffers = new ArrayList<Map<String, Object>>();
                    existingOffers = JsonUtil.toObject(svcStr, ArrayList.class);
                    turnMap.put("existingOffers", existingOffers);//返回的已订购的附属销售品
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                }
                return turnMap;
            }

//			String fakeSvcStr = "[{\"busiModDate\":null,\"createOrgId\":12345678,\"effDate\":\"2017-05-17 10:10:42\",\"expDate\":\"2019-05-17 10:10:42\",\"expProcMethod\":null,\"extOfferInstId\":null,\"isIndependent\":\"N\",\"lanId\":1,\"lastOrderItemId\":1000000000031083,\"offerAgreeId\":null,\"offerId\":300130000040,\"offerInstAttrs\":[],\"offerInstId\":1000000000003204,\"offerName\":\"DID电话\",\"offerType\":\"11\",\"ownerCustId\":10001,\"pageInfo\":null,\"prodFunType\":\"101\",\"prodInstAttrs\":null,\"regionId\":2,\"subOffertype\":null}]";
//			String svcStr = fakeSvcStr;

            String svcStr = MapUtils.getString(checkResultMap, "svcCont");

            List<Map<String, Object>> existingOffers = new ArrayList<Map<String, Object>>();

            if (StringUtil.isEmptyStr(svcStr)) {
                turnMap.put("existingOffers", existingOffers);//如果未返回数据，默认就空
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                return turnMap;
            }

            existingOffers = JsonUtil.toObject(svcStr, ArrayList.class);
            //权益销售品ID集合
            String rightsSales = (String) propertyToRedis.getPropertyValue("rights_sales");
            int existingOffersSize = existingOffers.size();
            List<Map<String, Object>> newExistingOffers = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < existingOffersSize; i++) {
                Map existingOffer = existingOffers.get(i);
                String offerInstId = MapUtils.getString(existingOffer, "offerInstId");
                if (rightsSales.indexOf("," + offerInstId) != -1) {
                    newExistingOffers.add(existingOffer);
                }
            }
            turnMap.put("existingOffers", newExistingOffers);//返回的已订购的权益附属销售品
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
        } catch (Exception e) {
            log.error("[LOCAL]查询已订购的附属销售品获得异常:", e);

            turnMap.put(Constants.RESULT_MSG_STR, "[LOCAL]查询已订购的附属销售品异常," + e);
        }

        return turnMap;
    }

    /**
     * CRM3.0-客户中心-查询已开通的功能产品
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> openFuncProdInst(Map<String, Object> paramsMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "[LOCAL]查询已订购的附属销售品失败");

        try {
            //必传参数accNum或者account二选一，prodType必传，10-固话，11-宽带，12-手机，16-ITV

            //调用远程接口
            Map<String, Object> checkResultMap = this.getHttp("openFuncProdInst", paramsMap, null);

            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put("resultMsg", "[INTERFACE]" + checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "[{\"prodUseType\":\"2000\",\"statusDate\":\"2019-08-02 21:39:32\",\"updateDate\":\"2019-08-02 21:39:32\",\"accProdInstId\":2365520624,\"addressDesc\":\"河北邢台威县洺州镇电力局家属院及周边交通大街围紫园街及周边8号分纤箱扩备用芯\",\"rstrFlag\":\"OPTIONAL\",\"prodId\":385698,\"lanId\":8130500,\"useCustId\":144453190,\"stopRentDate\":\"2037-01-01 00:00:00\",\"paymentModeCd\":\"1201\",\"addressId\":\"49706037\",\"prodInstAttrs\":[{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":-1,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrId\":-20164,\"prodInstId\":2365520626,\"attrValue\":\"IP15509625914\",\"prodInstAttrId\":102133880589,\"attrName\":\"宽带帐号\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":-1,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrId\":600007,\"prodInstId\":2365520626,\"attrValue\":\"17340917775\",\"prodInstAttrId\":102133880590,\"attrName\":\"创建者电话\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":5488,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"2TB\",\"attrId\":-20165,\"prodInstId\":2365520626,\"attrValue\":\"2\",\"prodInstAttrId\":102133880591,\"attrName\":\"云空间容量（单位TB）\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1}],\"prodName\":\"天翼云盘（家庭版）\",\"statusName\":\"在用\",\"createDate\":\"2019-01-20 11:10:47\",\"createOrgName\":\"\",\"accNum\":\"IP15509625914\",\"busiModDate\":\"2019-08-02 21:39:32\",\"beginRentDate\":\"2019-01-20 11:10:47\",\"firstFinishDate\":\"2019-01-20 11:10:47\",\"statusCd\":\"100000\",\"ownerCustId\":144453190,\"createOrgId\":-1,\"regionId\":8130533,\"prodInstId\":2365520626}]\n";
                    List<Map<String, Object>> existingProds = JsonUtil.toObject(svcCont, List.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("existingProds", existingProds);//返回的已订购的附属销售品
                }
                return turnMap;
            }

//			String fakeSvcStr = "[{\"busiModDate\":null,\"createOrgId\":12345678,\"effDate\":\"2017-05-17 10:10:42\",\"expDate\":\"2019-05-17 10:10:42\",\"expProcMethod\":null,\"extOfferInstId\":null,\"isIndependent\":\"N\",\"lanId\":1,\"lastOrderItemId\":1000000000031083,\"offerAgreeId\":null,\"offerId\":300130000040,\"offerInstAttrs\":[],\"offerInstId\":1000000000003204,\"offerName\":\"DID电话\",\"offerType\":\"11\",\"ownerCustId\":10001,\"pageInfo\":null,\"prodFunType\":\"101\",\"prodInstAttrs\":null,\"regionId\":2,\"subOffertype\":null}]";
//			String svcStr = fakeSvcStr;

            String svcStr = MapUtils.getString(checkResultMap, "svcCont");

            List<Map<String, Object>> existingProds = new ArrayList<Map<String, Object>>();

            if (StringUtil.isEmptyStr(svcStr)) {
                turnMap.put("existingProds", existingProds);//如果未返回数据，默认就空
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                return turnMap;
            }

            existingProds = JsonUtil.toObject(svcStr, ArrayList.class);

            turnMap.put("existingProds", existingProds);//返回的已订购的附属销售品
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
        } catch (Exception e) {
            log.error("[LOCAL]查询已开通的功能产品获得异常:", e);

            turnMap.put(Constants.RESULT_MSG_STR, "查询已开通的功能产品异常," + e);
        }

        return turnMap;
    }


    /**
     * 查询客户信息
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCustInfos(Map<String, Object> paramMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "查询客户信息失败");

        try {
            //组装调用接口数据
            if (paramMap == null || paramMap.size() == 0) {
                log.info("查询客户组装保存信息失败:" + paramMap);
                turnMap.put(Constants.RESULT_MSG_STR, "查询客户信息失败");
                return turnMap;
            }

            //调用远程接口
//            Map<String,Object> checkResultMap=callService.unifiedServiceByJsonGet("customer", jsonInfo);
            //调用远程接口
            Map<String, Object> checkResultMap = this.getHttp("customer", paramMap, null);


            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "[{\"serviceGrade\":[],\"custAreaGrade\":\"1200\",\"industryTypeId\":20040200,\"secrecyLevel\":\"1000\",\"specialList\":[],\"isRealname\":1,\"extCustId\":\"991800190009626788\",\"officePhone\":\"13930100525\",\"contactAddr\":\"河北省石家庄市元氏县人民路228号\",\"custId\":129554985,\"custNumber\":\"20311414578170000\",\"custContactsInfoRel\":[{\"headFlag\":1,\"contactId\":10080438261,\"custId\":129554985,\"custConnectId\":520009382001,\"statusCd\":\"1000\",\"contactsInfos\":[{\"statusDate\":\"2015-12-11 11:15:15\",\"officePhone\":\"13930100525\",\"contactId\":10080438261,\"contactName\":\"杨玉辉\",\"contactAddr\":\"河北省石家庄市元氏县人民路228号\",\"contactType\":\"1\",\"partyId\":726910559999,\"contactGender\":\"4\"}]}],\"partyId\":726910559999,\"custBrand\":[{\"statusDate\":\"2016-08-25 18:12:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075089451,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:18:27\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095108,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 12:55:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057545067,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:04:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075334734,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:47:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077391,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:04:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075079224,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-13 10:18:32\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076616355,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:57:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072078,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:04:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072789,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:27:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075305898,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:00:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084705,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:50:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071286,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:44:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076098555,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:38:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075112032,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-20 10:51:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6077042346,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:36:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076216,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:48:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071113,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:35:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075069850,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:17:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076094839,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-30 14:39:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075441451,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:38:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076432,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:40:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076098019,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:45:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075300805,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:42:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076098313,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:51:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071461,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:49:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077653,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:59:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084622,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:27:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075326,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:19:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095335,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 08:52:53\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075333764,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:36:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075069935,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:24:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075068663,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:57:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072071,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:35:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076166,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:07:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075335099,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:52:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071540,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:54:02\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078110,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:57:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072086,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:10:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073499,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:18:27\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095089,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:30:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075337262,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:52:42\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077972,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:41:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076745,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:04:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084975,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:16:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076094688,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:17:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075312513,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 11:20:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057539751,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:35:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075337767,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:22:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074703,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 11:05:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057584530,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:51:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057583459,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:49:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071215,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:19:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075297667,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:33:37\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075337534,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:19:42\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075336196,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:10:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073477,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:03:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084943,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:35:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076097098,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:41:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075070390,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:29:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075514,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 11:42:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057541378,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 10:35:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057535864,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:49:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075083825,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:42:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076889,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:09:36\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075086630,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:07:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075079592,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:22:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075068507,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:00:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078761,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:25:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075038,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:10:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075303914,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:58:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072152,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:56:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076100165,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 11:27:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057586532,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 08:54:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075333896,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:20:42\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095482,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:20:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074537,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:32:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075069515,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:39:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057582425,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:51:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071474,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 11:23:17\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057539904,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 13:06:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057545562,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:22:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074724,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:21:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095633,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:01:36\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084789,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:25:52\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075313690,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:49:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075308656,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:46:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057582973,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:46:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075083473,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:40:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076714,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:12:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073756,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:00:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078775,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:30:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075069268,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:18:32\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095127,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:35:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057582001,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:13:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076093981,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:51:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071362,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:25:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075019,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:54:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078130,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:30:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075314240,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:56:59\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078446,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:00:17\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075310070,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:42:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076098299,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:17:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075068057,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:39:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075082871,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:53:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076099871,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:50:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077744,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:26:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075103898,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:55:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076100045,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:02:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072572,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 15:29:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076089278,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:10:37\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075311615,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:41:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075070403,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:20:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074471,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:37:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076310,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:16:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074145,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:31:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075069438,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:48:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077544,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:26:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075225,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:45:02\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077184,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:10:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073527,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 08:59:02\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075334329,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:30:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075615,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 11:55:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057588747,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:52:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084012,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:45:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075308241,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 11:17:18\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057539463,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:04:32\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075079249,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:14:30\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076094251,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:42:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076098328,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:58:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075302344,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:48:17\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077497,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:45:17\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076098688,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:21:38\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075336370,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:44:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076098574,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:09:48\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075086848,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 11:43:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057587860,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:40:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076676,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:32:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075848,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:58:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078599,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:38:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075070115,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:15:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075067814,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:40:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075300257,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:19:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074450,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:02:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072590,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:58:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084575,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:40:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076664,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:36:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075314948,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:11:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073604,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 11:48:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057588200,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:50:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076099307,\"brandCode\":\"63\"},{\"statusDate\":\"2019-05-23 11:38:23\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6130863623,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:56:36\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084401,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:24:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074900,\"brandCode\":\"63\"},{\"statusDate\":\"2019-05-23 11:26:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6130862718,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:56:52\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071970,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:47:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075083622,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:04:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075066690,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 11:33:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057540699,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 11:40:42\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057541233,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:15:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076094580,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:19:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074364,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:52:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077955,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:18:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075336054,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:39:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075338073,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:21:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075068340,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 13:20:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057546123,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:43:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077037,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:06:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073062,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:54:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078144,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:47:36\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075113697,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:55:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084306,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:16:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074118,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:40:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076722,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:40:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076689,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:02:52\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084904,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:04:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075079243,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:59:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072241,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:07:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073207,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:27:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075271,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:54:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084214,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:01:36\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072472,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:31:52\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075778,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:18:37\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095139,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:31:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075306397,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:15:13\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076094428,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:00:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075084694,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:43:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057582700,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:58:42\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075072160,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:29:52\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075298888,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:35:02\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075299557,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:37:27\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075299863,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:32:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075069592,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:24:36\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076095886,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 08:57:12\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075334111,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 17:06:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076101325,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:56:59\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075078458,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:08:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075079703,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:33:32\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075314645,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 17:07:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075311081,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 15:41:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076090445,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:51:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075083927,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:37:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076097507,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:48:42\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057583184,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:42:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075070481,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 11:25:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057540056,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:27:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057581456,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:45:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075077282,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:06:06\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075085112,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:00:46\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075066401,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 14:57:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075066104,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:27:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075318,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 15:47:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075301164,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:17:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075067989,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-27 18:02:57\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075242877,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:11:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073611,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:30:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075588,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:09:27\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057579783,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:14:03\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073881,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:20:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074499,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:09:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075086451,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:03:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075079140,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:34:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076026,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:49:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075083799,\"brandCode\":\"63\"},{\"statusDate\":\"2019-05-23 11:34:07\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6130863331,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:19:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057580726,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:55:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057583708,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:51:56\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076099519,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 18:14:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075092713,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:33:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075075937,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:39:02\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057582352,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:18:31\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075074320,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:24:52\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075336686,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:17:17\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075335963,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:52:38\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075309098,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:51:47\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075071441,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:11:51\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075073649,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:46:36\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075070937,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 17:41:16\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075083033,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-29 09:28:22\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075337016,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 13:00:26\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057545315,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:45:37\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057582900,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:02:32\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075302850,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:07:27\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075303465,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-28 16:36:32\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075307029,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-29 13:14:01\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057545883,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:39:27\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076097829,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 16:36:41\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075076284,\"brandCode\":\"63\"},{\"statusDate\":\"2016-09-06 16:37:52\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6076097523,\"brandCode\":\"63\"},{\"statusDate\":\"2016-08-25 15:24:21\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6075068682,\"brandCode\":\"63\"},{\"statusDate\":\"2015-10-30 10:30:11\",\"brandType\":\"6\",\"brandName\":\"商务通\",\"brandStatusCd\":\"1000\",\"brandId\":63,\"custId\":129554985,\"custBrandId\":6057581652,\"brandCode\":\"63\"}],\"taxPayerInfo\":[],\"contactName\":\"杨玉辉\",\"isInstead\":0,\"custAddr\":\"河北省石家庄市元氏县人民路228号\",\"custAttr\":[{\"updateDate\":\"2015-12-11 11:15:15\",\"attrId\":6,\"custAttrId\":101178055,\"custId\":129554985,\"attrValue\":\"1\",\"createDate\":\"2015-12-11 11:15:15\"}],\"custName\":\"石家庄第二实验中学（石家庄市第五职业中专学校）\",\"partyCert\":[{\"isRealnameCert\":\"10\",\"isDefault\":\"1\",\"certType\":\"15\",\"effDate\":\"2015-12-11 11:15:15\",\"certAddr\":\"河北省石家庄市元氏县人民路228号\",\"partyCertId\":10020798879,\"statusCd\":\"1000\",\"partyId\":726910559999,\"certNum\":\"60148124-1\",\"expDate\":\"2019-10-17 15:48:46\"}],\"enterDate\":\"2015-10-29 10:30:31\",\"partyAttr\":[],\"regionId\":8130102,\"custControlLevel\":\"1300\",\"custType\":\"1000\",\"custLabel\":[],\"custOrderId\":-1,\"contactPhone\":\"13930100525\",\"party\":[{\"partyName\":\"石家庄第二实验中学（石家庄市第五职业中专学校）\",\"statusCd\":\"1000\",\"partyId\":726910559999,\"partyType\":\"2\"}]}]";
                    List<Map<String, Object>> custList = JsonUtil.toObject(svcCont, List.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("custList", custList);
                }
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");
            List<Map<String, Object>> custList = JsonUtil.toObject(svcCont, List.class);
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
            turnMap.put("custList", custList);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-getCustInfos接口获得异常:", e);
        }

        return turnMap;
    }

    /**
     * 查询三户信息
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> custAnInsts(Map<String, Object> paramMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "查询三户信息失败");

        try {
            //组装调用接口数据
            if (paramMap == null || paramMap.size() == 0) {
                log.info("查询客户组装保存信息失败:" + paramMap);
                turnMap.put(Constants.RESULT_MSG_STR, "查询三户信息失败");
                return turnMap;
            }

            //调用远程接口
//            Map<String,Object> checkResultMap=callService.unifiedServiceByJsonGet("customer", jsonInfo);
            //调用远程接口
            Map<String, Object> checkResultMap = this.getHttp("custAnInst", paramMap, null);


            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "{\"prodInsts\":[{\"prodUseType\":\"1000\",\"statusDate\":\"2019-08-02 21:39:32\",\"updateDate\":\"2019-08-02 21:39:32\",\"accProdInstId\":2365520624,\"addressDesc\":\"河北邢台威县洺州镇电力局家属院及周边交通大街围紫园街及周边8号分纤箱扩备用芯\",\"prodId\":100100,\"lanId\":8130500,\"useCustId\":144453190,\"stopRentDate\":\"2037-01-01 00:00:00\",\"paymentModeCd\":\"1201\",\"addressId\":\"49706037\",\"prodInstAttrs\":[{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":71171,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"FTTH\",\"attrId\":100704,\"prodInstId\":2365520624,\"attrValue\":\"1\",\"prodInstAttrId\":102133880568,\"attrName\":\"接入方式\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":80657,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"200M\",\"attrId\":100081,\"prodInstId\":2365520624,\"attrValue\":\"200M@19\",\"prodInstAttrId\":102133880569,\"attrName\":\"端口速率\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":51864,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"电信自采\",\"attrId\":100324,\"prodInstId\":2365520624,\"attrValue\":\"8\",\"prodInstAttrId\":102133880570,\"attrName\":\"终端提供者\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":80634,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"智能光猫通用模板\",\"attrId\":300004,\"prodInstId\":2365520624,\"attrValue\":\"2613\",\"prodInstAttrId\":102133880571,\"attrName\":\"终端型号\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":5461,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"城市家庭宽带_FTTH\",\"attrId\":300000,\"prodInstId\":2365520624,\"attrValue\":\"12550\",\"prodInstAttrId\":102133880572,\"attrName\":\"用户类型\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":50037,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"宽带准实时计费套餐(日租)\",\"attrId\":100055,\"prodInstId\":2365520624,\"attrValue\":\"430\",\"prodInstAttrId\":102133880573,\"attrName\":\"产品编码\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":-1,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrId\":100829,\"prodInstId\":2365520624,\"attrValue\":\"625914\",\"prodInstAttrId\":102133880574,\"attrName\":\"PPPOE密码\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":50187,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"包月\",\"attrId\":100122,\"prodInstId\":2365520624,\"attrValue\":\"2\",\"prodInstAttrId\":102133880575,\"attrName\":\"计费方法（宽带）\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":51122,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"普通用户\",\"attrId\":100369,\"prodInstId\":2365520624,\"attrValue\":\"10\",\"prodInstAttrId\":102133880576,\"attrName\":\"用户使用性质\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":70007702,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"否\",\"attrId\":700077,\"prodInstId\":2365520624,\"attrValue\":\"0\",\"prodInstAttrId\":102133880577,\"attrName\":\"是否当日装\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":-1,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrId\":700083,\"prodInstId\":2365520624,\"attrValue\":\"公众宽带\",\"prodInstAttrId\":102133880578,\"attrName\":\"宽带用户标识\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":-1,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrId\":700084,\"prodInstId\":2365520624,\"attrValue\":\"-1\",\"prodInstAttrId\":102133880579,\"attrName\":\"政企宽带用户标识\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":50243,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"下周期期初生效\",\"attrId\":100192,\"prodInstId\":2365520624,\"attrValue\":\"1\",\"prodInstAttrId\":102133880592,\"attrName\":\"生效时间\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-01-20 11:10:47\",\"updateDate\":\"2019-01-20 11:10:47\",\"dataVerNum\":0,\"attrValueId\":71176,\"statusCd\":\"1000\",\"parProdInstAttrId\":-1,\"attrValueName\":\"认证\",\"attrId\":100705,\"prodInstId\":2365520624,\"attrValue\":\"0\",\"prodInstAttrId\":102133880593,\"attrName\":\"认证方式\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1},{\"statusDate\":\"2019-08-02 21:39:32\",\"attrId\":813010001,\"dataVerNum\":0,\"attrValueId\":-1,\"statusCd\":\"1000\",\"prodInstId\":2365520624,\"attrValue\":\"8130533\",\"parProdInstAttrId\":-1,\"prodInstAttrId\":122737808297,\"attrName\":\"使用地区\",\"createDate\":\"2019-01-20 11:10:47\",\"lastOrderItemId\":-1}],\"prodName\":\"宽带\",\"statusName\":\"在用\",\"prodInstRels\":[],\"prodInstStates\":[],\"createDate\":\"2019-01-20 11:10:47\",\"prodInstAccRels\":[{\"paymentLimitType\":1,\"statusDate\":\"2019-01-20 11:10:47\",\"acctItemGroupId\":1,\"effDate\":\"2019-01-20 11:10:47\",\"prodInstAcctRelId\":100135380499,\"acctId\":151471307,\"prodInstId\":2365520624,\"statusCd\":\"1\",\"ifDefaultAcctId\":1,\"priority\":999,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\"}],\"accNum\":\"IP15509625914\",\"prodInstContacts\":[{\"firstChoice\":\"1\",\"prodInstId\":2365520624,\"contactPhone\":\"18903290870\",\"prodInstContactId\":1254530737}],\"busiModDate\":\"2019-08-02 21:39:32\",\"beginRentDate\":\"2019-01-20 11:10:47\",\"firstFinishDate\":\"2019-01-20 11:10:47\",\"statusCd\":\"100000\",\"ownerCustId\":144453190,\"prodInstAccNums\":[{\"accNum\":\"IP15509625914\",\"password\":\"123456\",\"platId\":-1,\"applyRegionId\":8130533,\"prodInstId\":2365520624,\"accNumType\":\"1000\",\"prodInstAccNumId\":6075839207},{\"accNum\":\"IP15509625914\",\"password\":\"123456\",\"platId\":-1,\"applyRegionId\":8130533,\"prodInstId\":2365520624,\"accNumType\":\"2000\",\"prodInstAccNumId\":6099054189}],\"devStaffInfos\":[{\"devStaffInfoId\":7863785669,\"devStaffType\":\"1000\",\"objId\":2365520624,\"statusCd\":\"1000\",\"objType\":\"120000\",\"devStaffId\":-1,\"devOrgId\":185100195}],\"createOrgId\":-1,\"regionId\":8130533,\"prodInstId\":2365520624,\"account\":\"IP15509625914\"}],\"accounts\":[{\"statusDate\":\"2018-02-27 15:08:10\",\"extAcctId\":\"991800190042487925\",\"dataVerNum\":0,\"acctAttrs\":[],\"acctId\":151471307,\"statusCd\":\"1\",\"acctName\":\"张文娜\",\"expDate\":\"2037-01-01 00:00:00\",\"paymentPlans\":[{\"statusDate\":\"2018-02-27 15:08:10\",\"updateDate\":\"2018-02-27 15:08:10\",\"dataVerNum\":0,\"acctId\":151471307,\"statusCd\":\"1\",\"priority\":1,\"upperAmount\":-1,\"expDate\":\"2039-12-31 00:00:00\",\"createStaff\":-1,\"paymentLimitType\":1,\"effDate\":\"2018-02-27 15:08:10\",\"payAcctId\":0,\"paymentMethod\":100000,\"paymentPlanId\":6091249792,\"createDate\":\"2018-02-27 15:08:10\",\"updateStaff\":-1,\"payAcctType\":1}],\"effDate\":\"2018-02-27 15:08:10\",\"acctCd\":\"151471307\",\"regionId\":8130533,\"custId\":144453190,\"groupAcctId\":\"-1\",\"prodInstId\":-1,\"acctBillingType\":1,\"createDate\":\"2018-02-27 15:08:10\"}],\"customers\":[{\"serviceGrade\":[],\"custAreaGrade\":\"1200\",\"industryTypeId\":99010100,\"secrecyLevel\":\"1000\",\"specialList\":[],\"isRealname\":1,\"extCustId\":\"991800190026738264\",\"custId\":144453190,\"custNumber\":\"20311562757460000\",\"custContactsInfoRel\":[{\"headFlag\":1,\"contactId\":10095295893,\"custId\":144453190,\"custConnectId\":520015661714,\"statusCd\":\"1000\",\"contactsInfos\":[{\"statusDate\":\"2019-04-12 14:47:24\",\"officePhone\":\"15028882120\",\"contactId\":10095295893,\"contactName\":\"张文娜\",\"contactAddr\":\"河北省邢台市威县洺州镇北关村53号\",\"contactType\":\"1\",\"partyId\":878725630099,\"contactGender\":\"4\"}]}],\"partyId\":878725630099,\"custBrand\":[{\"statusDate\":\"2018-07-11 17:10:11\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6113885114,\"brandCode\":\"0\"},{\"statusDate\":\"2019-04-12 19:15:02\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6128866137,\"brandCode\":\"0\"},{\"statusDate\":\"2018-02-27 15:09:31\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6104929972,\"brandCode\":\"0\"},{\"statusDate\":\"2019-01-20 11:11:07\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6124841206,\"brandCode\":\"0\"},{\"statusDate\":\"2018-02-27 15:09:01\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6104929888,\"brandCode\":\"0\"},{\"statusDate\":\"2018-07-11 18:58:11\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6113893417,\"brandCode\":\"0\"},{\"statusDate\":\"2019-04-12 15:14:18\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6128847754,\"brandCode\":\"0\"},{\"statusDate\":\"2018-02-27 15:09:41\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6104929996,\"brandCode\":\"0\"},{\"statusDate\":\"2019-04-12 19:15:18\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6128866141,\"brandCode\":\"0\"},{\"statusDate\":\"2018-07-11 18:57:56\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6113893404,\"brandCode\":\"0\"},{\"statusDate\":\"2019-01-20 11:10:47\",\"brandType\":\"6\",\"brandName\":\"其它\",\"brandDesc\":\"无\",\"brandStatusCd\":\"1000\",\"brandId\":0,\"custId\":144453190,\"custBrandId\":6124841168,\"brandCode\":\"0\"}],\"taxPayerInfo\":[],\"contactName\":\"张文娜\",\"isInstead\":0,\"custAddr\":\"河北省邢台市威县洺州镇北关村53号\",\"custAttr\":[{\"attrId\":6,\"custAttrId\":140108043,\"custId\":144453190,\"attrValue\":\"2\"},{\"attrId\":813010033,\"custAttrId\":145207887,\"custId\":144453190,\"attrValue\":\"0\"}],\"custName\":\"张文娜\",\"partyCert\":[{\"isRealnameCert\":\"10\",\"isDefault\":\"1\",\"certType\":\"1\",\"effDate\":\"2019-04-12 14:47:24\",\"certAddr\":\"河北省邢台市威县洺州镇北关村53号\",\"partyCertId\":10035852037,\"statusCd\":\"1000\",\"partyId\":878725630099,\"certNum\":\"130531198205020441\",\"expDate\":\"2034-05-05 00:00:00\"}],\"enterDate\":\"2018-02-27 14:44:30\",\"partyAttr\":[],\"regionId\":8130533,\"custControlLevel\":\"1300\",\"custType\":\"1100\",\"custLabel\":[],\"custOrderId\":-1,\"contactPhone\":\"15028882120\",\"party\":[{\"partyName\":\"张文娜\",\"statusCd\":\"1000\",\"partyId\":878725630099,\"partyType\":\"1\"}]}],\"offerInsts\":[{\"offerSysType\":\"1500\",\"offerName\":\"天翼畅享41GB（109元）套餐201711\",\"isIndependent\":\"1\",\"statusCd\":\"1000\",\"lanId\":8130500,\"offerProdInstRel\":[{\"accNum\":\"17320840039\",\"roleId\":50003,\"offerProdRelId\":57384055,\"relType\":\"1000\",\"statusCd\":\"1000\",\"prodId\":100110,\"lanId\":8130500,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"effDate\":\"2018-02-27 15:08:29\",\"regionId\":8130533,\"roleName\":\"副成员\",\"statusName\":\"有效\",\"prodInstId\":1832343678,\"offerProdInstRelId\":2100908902635,\"offerInstId\":101080543412},{\"accNum\":\"17340917775\",\"roleId\":50002,\"offerProdRelId\":57385817,\"relType\":\"1000\",\"statusCd\":\"1000\",\"prodId\":100110,\"lanId\":8130500,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"effDate\":\"2018-02-27 15:08:29\",\"regionId\":8130533,\"roleName\":\"主成员\",\"statusName\":\"有效\",\"prodInstId\":1832343625,\"offerProdInstRelId\":2100908903730,\"offerInstId\":101080543412},{\"accNum\":\"17717724445\",\"roleId\":50003,\"offerProdRelId\":57384055,\"relType\":\"1000\",\"statusCd\":\"1000\",\"prodId\":100110,\"lanId\":8130500,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"effDate\":\"2018-02-27 15:08:29\",\"regionId\":8130533,\"roleName\":\"副成员\",\"statusName\":\"有效\",\"prodInstId\":1832343650,\"offerProdInstRelId\":2100908903999,\"offerInstId\":101080543412},{\"accNum\":\"IP15509625914\",\"roleId\":18,\"offerProdRelId\":57383414,\"relType\":\"1000\",\"statusCd\":\"1000\",\"prodId\":100100,\"lanId\":8130500,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"effDate\":\"2019-01-18 00:00:00\",\"regionId\":8130533,\"roleName\":\"基础有线宽带\",\"statusName\":\"有效\",\"prodInstId\":2365520624,\"offerProdInstRelId\":2101166750780,\"offerInstId\":101080543412,\"account\":\"IP15509625914\"},{\"accNum\":\"IP15509625914@ITV\",\"roleId\":70002,\"offerProdRelId\":57383882,\"relType\":\"1000\",\"statusCd\":\"1000\",\"prodId\":90022,\"lanId\":8130500,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"effDate\":\"2019-01-18 00:00:00\",\"regionId\":8130533,\"roleName\":\"IPTV主成员\",\"statusName\":\"有效\",\"prodInstId\":2365520625,\"offerProdInstRelId\":2101166751307,\"offerInstId\":101080543412,\"account\":\"IP15509625914@ITV\"}],\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"offerType\":\"11\",\"createOrgId\":-1,\"effDate\":\"2018-02-27 15:09:01\",\"offerDesc\":\"1、套餐月基本费及包含使用量\\r\\n109元/月；国内流量（不含港澳台）：41GB达量限速不封顶。国内主叫500分钟，全国接听免费；主副卡本地免费互拨免费；赠送来电显示；可加装4张副卡，副卡功能费10元/张。\\r\\n2、套餐超出资费\\r\\n国内主叫通话0.15元/分钟，不足1分钟按1分钟计。流量最小计费单元为KB,不足1KB部分按1KB计，不足1分钱的按1分钱计收。其他执行标准资费。\\r\\n3、订购当月资费\\r\\n订购当月套餐月基本费按日折算（入网当日到月底），费用四舍五入到分，一次性收取；套餐内容（语音、流量）按照对应流量和分钟数按天折算，向上取整。\\r\\n4、达量降速规则\\r\\n当月手机上网流量达到41GB后，上网速率降至3.1Mbps;当月累计使用达到101GB后，上网速率降至128Kbps，次月恢复（不含港澳台）\\r\\n5、其他说明\\r\\n（1）国内通话和接听免费范围不包括港澳台地区。（2）手机上网流量仅限中国大陆境内使用，只适用于4G、3G、2G，不区分4G、3G、2G，不含WLAN（Wi-Fi）上网。（3）不适用于流量不清零规则。（4）套餐资费的有效期为2年，到期时中国电信可调整相关资费内容，如双方无异议自动续约2年。\",\"regionId\":8130533,\"statusName\":\"有效\",\"offerId\":1422908,\"ownerCustName\":\"张文娜\",\"offerObjInstRel\":[],\"offerInstId\":101080543412,\"offerInstAttr\":[]},{\"offerSysType\":\"1200\",\"offerName\":\"畅享家庭套餐-宽带200M（0元/月）\",\"isIndependent\":\"1\",\"statusCd\":\"1000\",\"lanId\":8130500,\"offerProdInstRel\":[{\"accNum\":\"IP15509625914\",\"roleId\":1,\"offerProdRelId\":57367717,\"relType\":\"1000\",\"statusCd\":\"1000\",\"prodId\":100100,\"lanId\":8130500,\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"effDate\":\"2019-01-20 11:10:47\",\"regionId\":8130533,\"roleName\":\"一般构成成员\",\"statusName\":\"有效\",\"prodInstId\":2365520624,\"offerProdInstRelId\":101546013776,\"offerInstId\":101486622433,\"account\":\"IP15509625914\"}],\"ownerCustId\":144453190,\"expDate\":\"2037-01-01 00:00:00\",\"offerType\":\"12\",\"createOrgId\":-1,\"effDate\":\"2019-01-20 11:10:47\",\"offerDesc\":\"月基本费0元/月，速率200Mbit/s\",\"regionId\":8130533,\"statusName\":\"有效\",\"offerId\":1419870,\"ownerCustName\":\"张文娜\",\"offerObjInstRel\":[],\"offerInstId\":101486622433,\"offerInstAttr\":[{\"effDate\":\"2019-01-18 00:00:00\",\"attrId\":300600,\"offerInstAttrId\":103950502343,\"attrValueId\":-1,\"statusName\":\"有效\",\"statusCd\":\"1000\",\"attrValue\":\"IP15509625914\",\"offerInstId\":101486622433,\"attrName\":\"同组合移动成员号码\",\"expDate\":\"2037-01-01 00:00:00\",\"parOfferInstAttrId\":-1}]}]}";
                    List<Map<String, Object>> custAnInsts = JsonUtil.toObject(svcCont, List.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("custAnInsts", custAnInsts);
                }
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");
            Map<String, Object> custAnInsts = JsonUtil.toObject(svcCont, Map.class);
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
            turnMap.put("custAnInsts", custAnInsts);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-custAnInsts接口获得异常:", e);
        }

        return turnMap;
    }


    /**
     * 查询产品实例信息
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> prodInsts(Map<String, Object> paramMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "查询客户信息失败");
        try {
            //组装调用接口数据
            if (paramMap == null || paramMap.size() == 0) {
                log.info("查询客户组装保存信息失败:" + paramMap);
                turnMap.put(Constants.RESULT_MSG_STR, "查询客户信息失败");
                return turnMap;
            }
            //调用远程接口
            Map<String, Object> checkResultMap = this.getHttp("prodInsts", paramMap, null);

            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "[{\"accNum\":\"123456\",\"accProdInstId\":\"2017010111000000\",\"account\":\"123456\",\"actDate\":\"2017010111000000\",\"addressDesc\":\"地址信息\",\"addressId\":\"5555555555\",\"beginRentDate\":\"2017010111000000\",\"custName\":\"1111\",\"exchId\":\"555\",\"firstFinishDate\":\"2017010111000000\",\"lanId\":\"2\",\"ownerCustId\":\"222222222\",\"paymentModeCd\":\"1201\",\"prodId\":\"8000000000\",\"prodInstAttrs\":[{\"attrId \":\"1003\",\"attrValue \":\"xxxxxx\",\"parProdInstAttrId\":\"20170201111111\",\"prodInstAttrId\":\"20170201111111\"},{\"attrId \":\"1003\",\"attrValue \":\"xxxxxx\",\"parProdInstAttrId\":\"20170201111111\",\"prodInstAttrId\":\"20170201111111\"}],\"prodInstId\":\"2017010111000000\",\"prodUseType\":\"111\",\"regionId\":\"11\",\"statusCd\":\"100000\",\"stopRentDate\":\"\",\"useCustId\":\"222222222\"}]";
                    List<Map<String, Object>> prodInsts = JsonUtil.toObject(svcCont, List.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("prodInsts", prodInsts);
                }
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");
            List<Map<String, Object>> prodInsts = JsonUtil.toObject(svcCont, List.class);
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
            turnMap.put("prodInsts", prodInsts);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-getCustInfos接口获得异常:", e);
        }

        return turnMap;
    }


    /**
     * 累积量查询
     *
     * @param resourceInformation accNbr	1	电话号码
     *                            destinationAttr	1	用户属性（当查询值类型为业务号码时填写） 0：固话 2：移动 3：宽带 99:其他
     *                            prodOfferInstanceId	？	查询销售品实例标识
     *                            queryFlag	1	查询业务类型  3：按销售品查询 4：按套餐内查询 5：按套餐外查询
     *                            productOfferId	？	查询销售品标识（无此AVP时表示查询所有销售品）
     *                            billingCycle	？	查询帐期（无此AVP时表示查当前帐期）
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAccu(Map<String, Object> resourceInformation) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "累积量信息查询失败");
        try {
            //组装调用接口数据
            if (resourceInformation == null || resourceInformation.size() == 0) {
                log.info("查询客户组装保存信息失败:" + resourceInformation);
                turnMap.put(Constants.RESULT_MSG_STR, "累积量信息失败");
                return turnMap;
            }

            Map<String, Object> param = new HashMap<>();
            Map<String, Object> stdCcrUserResourceQuery = new HashMap<>();
            stdCcrUserResourceQuery.put("resourceInformation", resourceInformation);
            param.put("stdCcrUserResourceQuery", stdCcrUserResourceQuery);

            //调用远程接口
            Map<String, Object> checkResultMap = this.postHttp("GetAccu", param, new HashMap<>());
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "{\"responseId\":\"100021202003250949370001\",\"errorCode\":\"0\",\"errorMsg\":\"累积量查询成功\",\"stdCcaUserResourceQuery\":{\"productOffInfo\":[{\"productOffName\":\"天翼畅享10GB（79元）套餐201802\",\"prodOfferInstanceId\":\"101543351114\",\"offerType\":\"11\",\"respondRatableQuery\":[{\"ratableResourceName\":\"套餐内包含天翼畅享流量\",\"ratableAmount\":\"0\",\"ratableResourceID\":\"331101\",\"usageAmount\":\"41346056\",\"balanceAmount\":\"0\",\"ownerType\":\"100000\",\"ownerID\":\"51739628\",\"unitTypeId\":\"3\",\"beginTime\":\"20200201000000\",\"endTime\":\"20200301000000\"},{\"ratableResourceName\":\"国内通话\",\"ratableAmount\":\"100\",\"ratableResourceID\":\"131100\",\"usageAmount\":\"0\",\"balanceAmount\":\"100\",\"ownerType\":\"100000\",\"ownerID\":\"51739628\",\"unitTypeId\":\"1\",\"beginTime\":\"20200201000000\",\"endTime\":\"20200301000000\"}],\"productOfferId\":\"1424212\",\"shareFlag\":\"1\",\"lCustID\":48755971,\"iOrgID\":8130100},{\"productOffName\":\"天翼畅享-79元套餐-200分钟语音两年体验包（201808）\",\"prodOfferInstanceId\":\"101543351116\",\"offerType\":\"12\",\"respondRatableQuery\":[{\"ratableResourceName\":\"国内通话\",\"ratableAmount\":\"200\",\"ratableResourceID\":\"131100\",\"usageAmount\":\"0\",\"balanceAmount\":\"200\",\"ownerType\":\"100000\",\"ownerID\":\"51739628\",\"unitTypeId\":\"1\",\"beginTime\":\"20200201000000\",\"endTime\":\"20200301000000\"}],\"productOfferId\":\"1426786\",\"shareFlag\":\"0\",\"lCustID\":48755971,\"iOrgID\":8130100},{\"productOffName\":\"天翼畅享-79元套餐-400分钟语音两年体验包（201808）\",\"prodOfferInstanceId\":\"101569962716\",\"offerType\":\"12\",\"respondRatableQuery\":[{\"ratableResourceName\":\"国内通话\",\"ratableAmount\":\"400\",\"ratableResourceID\":\"131100\",\"usageAmount\":\"19\",\"balanceAmount\":\"381\",\"ownerType\":\"100000\",\"ownerID\":\"51739628\",\"unitTypeId\":\"1\",\"beginTime\":\"20200201000000\",\"endTime\":\"20200301000000\"}],\"productOfferId\":\"1426783\",\"shareFlag\":\"0\",\"lCustID\":48755971,\"iOrgID\":8130100},{\"productOffName\":\"客户回馈-赠送200分钟语音两年体验包（201805）\",\"prodOfferInstanceId\":\"101569962717\",\"offerType\":\"12\",\"respondRatableQuery\":[{\"ratableResourceName\":\"国内通话\",\"ratableAmount\":\"200\",\"ratableResourceID\":\"131100\",\"usageAmount\":\"200\",\"balanceAmount\":\"0\",\"ownerType\":\"100000\",\"ownerID\":\"51739628\",\"unitTypeId\":\"1\",\"beginTime\":\"20200201000000\",\"endTime\":\"20200301000000\"}],\"productOfferId\":\"1425617\",\"shareFlag\":\"0\",\"lCustID\":48755971,\"iOrgID\":8130100}]}}\n";
                    Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
                    Map<String, Object> stdCcaUserResourceQuery = (Map<String, Object>) resultMap.get("stdCcaUserResourceQuery");
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("stdCcaUserResourceQuery", stdCcaUserResourceQuery);
                }
                return turnMap;

            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");
            Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
            Map<String, Object> stdCcaUserResourceQuery = (Map<String, Object>) resultMap.get("stdCcaUserResourceQuery");
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
            turnMap.put("stdCcaUserResourceQuery", stdCcaUserResourceQuery);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-GetAccu接口获得异常:", e);
        }
        return turnMap;
    }


    /**
     * 缴费充值记录
     *
     * @param stdCcrPaymentRecord accNbr	1	接入号码(如果用户属性为固话、宽带时，此AVP取值要求带区号,含0)
     *                            queryType	1	查询类型 0按账期 1按开始结束时间
     *                            billingCycle	？	 查询帐期（无此AVP时表示查当前帐期）
     *                            beginTime	？	开始时间
     *                            endTime	？	结束时间
     *                            destinationAttr	1	用户属性（当查询值类型为业务号码时填写）0-固话; 2-移动； 3-宽带； 99-其他;
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPayment(Map<String, Object> stdCcrPaymentRecord) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "缴费充值记录查询失败");
        try {
            //组装调用接口数据
            if (stdCcrPaymentRecord == null || stdCcrPaymentRecord.size() == 0) {
                log.info("查询客户组装保存信息失败:" + stdCcrPaymentRecord);
                turnMap.put(Constants.RESULT_MSG_STR, "缴费充值记录查询失败");
                return turnMap;
            }

            Map<String, Object> param = new HashMap<>();
            param.put("stdCcrPaymentRecord", stdCcrPaymentRecord);

            //调用远程接口
            Map<String, Object> checkResultMap = this.postHttp("GetPayment", param, new HashMap<>());
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "{\"responseId\":\"100021202003250946290001\",\"errorCode\":\"0\",\"errorMsg\":\"成功\",\"stdCcaPaymentRecord\":{\"paymentRecordinfo\":[{\"reqSerial\":\"009202003202103521776434100\",\"paymentAmount\":\"10000\",\"curAmount\":\"83435\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200320210353\",\"payChannelId\":\"7\",\"staff\":\"9999999999999912\",\"balanceTypeId\":\"1\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100308539270\",\"paymentAmount\":\"1\",\"curAmount\":\"4\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200317194916\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100308524075\",\"paymentAmount\":\"1\",\"curAmount\":\"3\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200317181619\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100308523016\",\"paymentAmount\":\"1\",\"curAmount\":\"2\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200317181015\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100307076307\",\"paymentAmount\":\"-1\",\"curAmount\":\"1\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200310145628\",\"payChannelId\":\"7\",\"staff\":\"9999999999999913\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100306839015\",\"paymentAmount\":\"1\",\"curAmount\":\"1\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200310145628\",\"payChannelId\":\"7\",\"staff\":\"9999999999999913\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100306838239\",\"paymentAmount\":\"1\",\"curAmount\":\"2\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200309144119\",\"payChannelId\":\"7\",\"staff\":\"9999999999999913\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100302076976\",\"paymentAmount\":\"-1\",\"curAmount\":\"0\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200303161348\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100298607031\",\"paymentAmount\":\"1\",\"curAmount\":\"1\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200303161348\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"}]}}";
                    Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
                    Map<String, Object> stdCcaPaymentRecord = (Map<String, Object>) resultMap.get("stdCcaPaymentRecord");
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("stdCcaPaymentRecord", stdCcaPaymentRecord);
                }
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");
            Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
            Map<String, Object> stdCcaPaymentRecord = (Map<String, Object>) resultMap.get("stdCcaPaymentRecord");
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
            turnMap.put("stdCcaPaymentRecord", stdCcaPaymentRecord);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-getPayment接口获得异常:", e);
        }

        return turnMap;
    }


    /**
     * 缴费充值记录
     *
     * @param stdCcrPaymentRecord accNbr	1	接入号码(如果用户属性为固话、宽带时，此AVP取值要求带区号,含0)
     *                            queryType	1	查询类型 0按账期 1按开始结束时间
     *                            billingCycle	？	 查询帐期（无此AVP时表示查当前帐期）
     *                            beginTime	？	开始时间
     *                            endTime	？	结束时间
     *                            destinationAttr	1	用户属性（当查询值类型为业务号码时填写）0-固话; 2-移动； 3-宽带； 99-其他;
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> qryRightTimesInfo(Map<String, Object> stdCcrPaymentRecord) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "查询补换卡次数失败");
        try {
            //组装调用接口数据
            if (stdCcrPaymentRecord == null || stdCcrPaymentRecord.size() == 0) {
                log.info("查询补换卡次数失败:" + stdCcrPaymentRecord);
                turnMap.put(Constants.RESULT_MSG_STR, "查询补换卡次数失败");
                return turnMap;
            }
            Map<String,Object> tcpCont = new HashMap<>();
            tcpCont.put("EventType","SYNC");
            tcpCont.put("BusCode","STAR_MEMBER");
            tcpCont.put("ServiceCode","QRY2007");

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Toolkit.FMT_DATE);
            String timeStr = dateTimeFormatter.format(now);
            tcpCont.put("RequestTime", timeStr);
            String transactionId = timeStr ;
            tcpCont.put("TransactionId", transactionId);

//            tcpCont.put("TransactionId","11126410576");
            tcpCont.put("AreaCode","-10012");
            tcpCont.put("StaffCode","-10012");
            tcpCont.put("ReqTime",timeStr);
            tcpCont.put("EncryptStr","");

            Map<String,Object> requestMess = new HashMap<>();
            requestMess.put("nbrType","4");
            requestMess.put("qryNbr","18133847339");
            requestMess.put("rightCode","100300");


            Map<String, Object> contractRoot = new HashMap<>();
            contractRoot.put("TcpCont", tcpCont);
            contractRoot.put("requestMess", requestMess);

            Map<String, Object> param = new HashMap<>();
            param.put("ContractRoot", contractRoot);

            //调用远程接口
            Map<String, Object> checkResultMap = this.postHttp("ZT001/qryRightTimesInfo", param, new HashMap<>());
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "{\"responseId\":\"100021202003250946290001\",\"errorCode\":\"0\",\"errorMsg\":\"成功\",\"stdCcaPaymentRecord\":{\"paymentRecordinfo\":[{\"reqSerial\":\"009202003202103521776434100\",\"paymentAmount\":\"10000\",\"curAmount\":\"83435\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200320210353\",\"payChannelId\":\"7\",\"staff\":\"9999999999999912\",\"balanceTypeId\":\"1\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100308539270\",\"paymentAmount\":\"1\",\"curAmount\":\"4\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200317194916\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100308524075\",\"paymentAmount\":\"1\",\"curAmount\":\"3\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200317181619\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100308523016\",\"paymentAmount\":\"1\",\"curAmount\":\"2\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200317181015\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100307076307\",\"paymentAmount\":\"-1\",\"curAmount\":\"1\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200310145628\",\"payChannelId\":\"7\",\"staff\":\"9999999999999913\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100306839015\",\"paymentAmount\":\"1\",\"curAmount\":\"1\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200310145628\",\"payChannelId\":\"7\",\"staff\":\"9999999999999913\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100306838239\",\"paymentAmount\":\"1\",\"curAmount\":\"2\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200309144119\",\"payChannelId\":\"7\",\"staff\":\"9999999999999913\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100302076976\",\"paymentAmount\":\"-1\",\"curAmount\":\"0\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200303161348\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"},{\"reqSerial\":\"100298607031\",\"paymentAmount\":\"1\",\"curAmount\":\"1\",\"paymentMethod\":\"11\",\"accNbrDetail\":\"账户级账本,抵扣所有账目项\",\"stateDate\":\"20200303161348\",\"payChannelId\":\"7\",\"staff\":\"5678\",\"balanceTypeId\":\"60\",\"ifPrintInv\":\"0\",\"balanceTypeFlag\":\"0\",\"billingCycle\":\"\",\"due\":\"0\"}]}}";
                    Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
                    Map<String, Object> stdCcaPaymentRecord = (Map<String, Object>) resultMap.get("stdCcaPaymentRecord");
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("stdCcaPaymentRecord", stdCcaPaymentRecord);
                }
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");
            Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
            Map<String, Object> stdCcaPaymentRecord = (Map<String, Object>) resultMap.get("stdCcaPaymentRecord");
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询补换卡次数成功");
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-qryRightTimesInfo接口获得异常:", e);
        }

        return turnMap;
    }


    /**
     * 发送短信
     *
     * @param map telPhone 接收短信的电话号码
     *            msgContent 接收短信内容
     *            sceneId: 场景id    手机短信验证码，实时发送SceneId 95769
     *                              翼支付失败短信，5分钟内发送SceneId 95768
     *                              防诈骗短信，1小时内发送SceneId 95767
     *            regionCode: 区域编码
     *            <p>
     *            {
     *            "ContractRoot": {
     *            "TcpCont": {
     *            "TransactionId": "${transactionID}",
     *            "SystemCode": "${systemCode}",
     *            "Password": "${password}",
     *            "UserAcct": "${userAcct}",
     *            "RequestTime": "${reqTime}"
     *            },
     *            "BizCont": {
     *            "AccNbr": "${accNbr}",
     *            "SceneId": "${sceneId}",
     *            "LanId": "${lanId}",
     *            "OrderContent": "${orderContent}"
     *            }
     *            }
     *            }
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> sendSmsCodeMsg(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "短信发送失败");
        try {
            //组装调用接口数据
            if (map == null || map.size() == 0) {
                log.info("短信发送失败:" + map);
                turnMap.put(Constants.RESULT_MSG_STR, "短信发送失败");
                return turnMap;
            }
            Map<String, Object> tcpCont = new HashMap<>();
            String systemCode = (String) propertyToRedis.getPropertyValue("sms_systemCode");
            tcpCont.put("SystemCode", systemCode);
            tcpCont.put("Password", propertyToRedis.getPropertyValue("sms_password"));
            tcpCont.put("UserAcct", propertyToRedis.getPropertyValue("sms_userAcct"));

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Toolkit.FMT_DATE);
            String timeStr = dateTimeFormatter.format(now);
            tcpCont.put("RequestTime", timeStr);
            String transactionId = MapUtils.getString(tcpCont, "SystemCode", "") + timeStr + redisUtil.incrAtomic(Constants.SMS_REQUEST_ID + timeStr, 1, 10, TimeUnit.SECONDS);
            tcpCont.put("TransactionId", transactionId);

            Map<String, Object> bizCont = new HashMap<>();
            String telPhone = MapUtils.getString(map, "telPhone");
            bizCont.put("AccNbr",telPhone );
            bizCont.put("OrderContent", MapUtils.getString(map, "msgContent"));
            bizCont.put("SceneId", "6729");
//            bizCont.put("SceneId", MapUtils.getString(map, "sceneId"));
//            Map<String, Object> paramMap = new HashMap<String, Object>();
//            paramMap.put("areaLevel", "2");
//            List<CoRegion> coRegionList = commonBmo.queryCoRegionList(paramMap);
//            String regionCode = MapUtils.getString(map, "regionCode");
//            regionCode = regionCode.substring(0, 5) + "00";
//            int coRegionListSize = coRegionList.size();
//            CoRegion coRegion = null;
//            for (int i = 0; i < coRegionListSize; i++) {
//                coRegion = coRegionList.get(i);
//                if (regionCode.equals(coRegion.getRegionCode())) {
//                    Integer zoneNumber = Integer.parseInt(coRegion.getZoneNumber());
//                    bizCont.put("LanId",zoneNumber);
//                }
//            }
            bizCont.put("LanId", "311");
            Map<String, Object> contractRoot = new HashMap<>();
            contractRoot.put("TcpCont", tcpCont);
            contractRoot.put("BizCont", bizCont);
            Map<String, Object> param = new HashMap<>();
            param.put("ContractRoot", contractRoot);

            String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
            boolean isDebug = Boolean.valueOf(isDebugStr);
            if (isDebug) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "短信发送成功");
                return turnMap;
            }

            //调用远程接口
            Map<String, Object> checkResultMap = this.postHttpBySendMsg("UCCPServiceHttpPort", param, new HashMap<>());
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");
            Map<String, Object> resMap = JsonUtil.toObject(svcCont, Map.class);
            Map<String, Object> resultContractRoot = (Map<String, Object>) MapUtils.getMap(resMap, "ContractRoot");
            Map<String, Object> resultTcpCont = (Map<String, Object>) MapUtils.getMap(resultContractRoot, "TcpCont");

            String resultCode = MapUtils.getString(resultTcpCont, "RespCode", "");
            String resultMsg = MapUtils.getString(resultTcpCont, "RespDesc", "短信发送失败");

            if (!"0000".equals(resultCode)) {
                turnMap.put("resultMsg", "短信发送失败,短信发送号码:" + telPhone + "," + resultMsg);
                return turnMap;
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "短信发送成功");
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-sendSmsCodeMsg接口获得异常:", e);
        }

        return turnMap;
    }

    /**
     * 是否为5G用户
     *
     * @param param accNum 电话号码
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> get5GLabel(Map<String, Object> param) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "是否为5G用户信息查询失败");
        try {
            //组装调用接口数据
//            if (param == null || param.size() == 0) {
//                log.info("查询客户组装保存信息失败:" + param);
//                turnMap.put(Constants.RESULT_MSG_STR, "是否为5G用户信息失败");
//                return turnMap;
//            }

            //调用远程接口
//            Map<String,Object> checkResultMap=callService.unifiedServiceByJsonGet("customer", jsonInfo);
            //调用远程接口
            Map<String, Object> checkResultMap = this.getHttpByCPCP("get5GLabel", param, new HashMap<>());

            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "[{\"InjectionLabelName\":\"是否5G套餐用户\",\"valueName\":\"否\",\"valueDesc\":\"\",\"InjectionLabelId \":\"\",\"labelValue\":\"0\",\"prodInstId\":\"\",\"remark\":\"19933136692\",\"InjectionLabelCode\":\"10B02009001001028\"},{\"InjectionLabelName\":\"是否5G功能用户\",\"valueName\":\"否\",\"valueDesc\":\"\",\"InjectionLabelId \":\"\",\"labelValue\":\"0\",\"prodInstId\":\"\",\"remark\":\"19933136692\",\"InjectionLabelCode\":\"10B02001001018\"},{\"InjectionLabelName\":\"是否5G终端用户\",\"valueName\":\"是\",\"valueDesc\":\"\",\"InjectionLabelId \":\"\",\"labelValue\":\"1\",\"prodInstId\":\"\",\"remark\":\"19933136692\",\"InjectionLabelCode\":\"10A02008001015\"},{\"InjectionLabelName\":\"是否5G首话单用户\",\"valueName\":\"是\",\"valueDesc\":\"\",\"InjectionLabelId \":\"\",\"labelValue\":\"1\",\"prodInstId\":\"\",\"remark\":\"19933136692\",\"InjectionLabelCode\":\"10B02001001020\"},{\"InjectionLabelName\":\"是否5G用户\",\"valueName\":\"是\",\"valueDesc\":\"\",\"InjectionLabelId \":\"\",\"labelValue\":\"1\",\"prodInstId\":\"\",\"remark\":\"19933136692\",\"InjectionLabelCode\":\"10B02001001019\"}]";
                    List<Map<String, Object>> resultMap = JsonUtil.toObject(svcCont, List.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("label", resultMap);
                }
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");

            List<Map<String, Object>> resultMap = JsonUtil.toObject(svcCont, List.class);
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
            turnMap.put("label", resultMap);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-get5GLabel接口获得异常:", e);
        }

        return turnMap;
    }


    /**
     * 订购附属品
     *
     * @param param telphone 手机号码
     *              offerId 销售品ID
     *
     *
     * {
     *     "acceptLanId": 8120100,
     *     "acceptRegionId": 8120100,
     *     "createOrgId": 46899,
     *     "custId": 225012035265,  ---变量  客户实例获取
     *     "custName":"test",---变量
     *     "offerDatas": [
     *         {
     *             "accProds": [
     *                 {
     *                     "isDel": "N",
     * 					    "piRegionId": 8120100,
     *                     "prodId": 379,
     *                     "prodInstId": 222012105404,   ---变量  产品实例获取
     *                     "roleId":1
     *                 }
     *             ],
     *             "offerId": 2201010,  --销售品id
     *             "offerInstId": -1,
     *             "operType": "ADD",
     *             "serviceOfferId": "3010100000"
     *         }
     *     ],
     *     "orderSource": "1012",
     *     "sceneCode": "unify",
     *     "staffId": -10050,
     *     "sysSource":"1012113"
     * }
     *
     * @return
     * @throws Exception
     *
     *
     *
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> submitCustomerOrder(Map<String, Object> param) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "保存订单失败");
        String telphone = MapUtils.getString(param,"telphone");
        String offerId = MapUtils.getString(param,"offerId");
        if(StringUtils.isEmpty(telphone)){
            turnMap.put(Constants.RESULT_MSG_STR, "号码为空");
            return turnMap;
        }
        if(StringUtils.isEmpty(offerId) ){
            turnMap.put(Constants.RESULT_MSG_STR, "销售品id为空");
            return turnMap;
        }
        try {
            //组装调用接口数据
//            if (param == null || param.size() == 0) {
//                log.info("查询客户组装保存信息失败:" + param);
//                turnMap.put(Constants.RESULT_MSG_STR, "是否为5G用户信息失败");
//                return turnMap;
//            }

            //调用远程接口
//            Map<String,Object> checkResultMap=callService.unifiedServiceByJsonGet("customer", jsonInfo);
            //调用远程接口
            Map<String,Object> searchInfoInMap = new HashMap<>();
            searchInfoInMap.put("prodType", "12");//12-移动电话
            searchInfoInMap.put("accNum", MapUtils.getString(param,"telphone"));//电话号码
            searchInfoInMap.put("flag", "2");

            Map<String, Object> searchResultMap = this.getCustInfos(searchInfoInMap);

            if(!Constants.CODE_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
                turnMap.put(Constants.RESULT_MSG_STR, "查询客户信息失败,请稍后再试");
                return turnMap;
            }
            List<Map<String, Object>> custList=(List<Map<String, Object>>) searchResultMap.get("custList");
            Map<String,Object> custMap = custList.get(0);
            param.put("custMap",custMap);
            Map<String, Object> searchProdResultMap = this.prodInsts(searchInfoInMap);
            if(!Constants.CODE_SUCC.equals(MapUtils.getString(searchProdResultMap, Constants.RESULT_CODE_STR))){
                turnMap.put(Constants.RESULT_MSG_STR, "查询销售品失败,请稍后再试");
                return turnMap;
            }
            List<Map<String, Object>> prodInstList = (List<Map<String, Object>>) searchProdResultMap.get("prodInsts");
            param.put("prodInsts",prodInstList);
            Map<String,Object> paramMap = this.flow(param);
            log.info("=================" + JsonUtil.toString(paramMap));
            Map<String, Object> checkResultMap = new HashMap<>();
            checkResultMap = this.postHttpSubmit("submitCustomerOrder", paramMap, new HashMap<>());
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "{\"csNbr\":\"52202001170028961782\",\"ruleChainRespVo\":{\"result\":\"0\",\"grpsMid\":[],\"oiRelas\":[],\"display\":false,\"oiStatuses\":[],\"oIs\":[{\"prodUseType\":\"1000\",\"specId\":100110,\"offerSysType\":\"1000\",\"isIndependent\":\"\",\"csNbr\":\"52202001170028961782\",\"orderItemId\":520277403231,\"serviceOfferName\":\"政企实名返档\",\"operType\":\"1000\",\"isChainCreate\":false,\"serviceOfferId\":4040804101,\"flowInstId\":900028257044,\"instId\":520039575593,\"instAccessNumber\":\"17734041151\",\"specName\":\"手机\",\"regionId\":8130108,\"root\":true,\"orderItemCd\":\"1300\",\"seq\":1001,\"ruleStatusCd\":\"S\"}],\"automActionsStatusCdMid\":[],\"rules\":[],\"modifyOisMid\":[],\"cancelUpdateStatusCoSeqs\":[],\"modifySeqRelas\":[]},\"customerOrderRspVo\":{\"custOrderNbr\":\"52202001170028961782\",\"resultCode\":\"0\",\"custOrderId\":520029455385}}";
                    Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "保存订单成功");
                    turnMap.put("result", resultMap);
                }
                return turnMap;
            }
            String svcCont = (String) MapUtils.getObject(checkResultMap, "svcCont");

            Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "保存订单成功");
            turnMap.put("result", resultMap);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-submitCustomerOrder接口获得异常:", e);
        }

        return turnMap;
    }

    /**
     * 充值流量数据组装
     * @param param telphone 手机号码
     *              offerId 销售品ID
     * @return
     */
    private Map<String,Object> flow(Map<String,Object> param){
        String telphone = MapUtils.getString(param,"telphone");
        String offerId = MapUtils.getString(param,"offerId");
        Map<String,Object> custMap = (Map<String, Object>) MapUtils.getMap(param,"custMap");

//         accProds.put("isDel", "N");//传输协议
//        accProds.put("piRegionId", -1);//中心名
//        accProds.put("prodId", -1);//请求时间  年月日 时分秒 毫秒 17位毫秒数
//        accProds.put("prodInstId", -1);//暂不启用 写死
//        accProds.put("roleId", -1); //暂不启动 写死
//        accProdsList.add(accProds);
//        offerDatas.put("accProds", accProdsList);
//        offerDatas.put("offerId", Long.parseLong(offerId));
//        offerDatas.put("offerInstId", -1);
//        offerDatas.put("operType", "ADD");
//        offerDatas.put("serviceOfferId", "3010100000");
//        offerDatas.put("effTime", "");
//        offerDatas.put("expTime", "");
//
//
//        Map requestMess = new HashMap();
//        offerDatasList.add(offerDatas);
//        requestMess.put("accNum", prodInstId);
//        requestMess.put("orderSource", "1012");
//        requestMess.put("sceneCode", "unify");
//        requestMess.put("", "SF_CLYXHDPT");
//        requestMess.put("sysSource", "1012170");
//        requestMess.put("offerDatas",offerDatasList );

        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("acceptRegionId",MapUtils.getLong(custMap,"regionId"));
        paramMap.put("createOrgId",188585412);
//        Map<String,Object> commonData = new HashMap<>();
//        commonData.put("contactPhone","18133847339");
//        commonData.put("devStaffId",10356026);
//        commonData.put("handler",MapUtils.getString(param,"custName"));
//        commonData.put("handlerCertNum","");
//        commonData.put("handlerCertType","1");
//        commonData.put("handlerId",144914076);
//        commonData.put("remark","test");
//        paramMap.put("commonData",commonData);

        paramMap.put("custId",MapUtils.getLong(custMap,"custId"));
        paramMap.put("custName",MapUtils.getString(custMap,"custName"));
        List<Map<String,Object>> offerDataList = new ArrayList<>();
        Map<String,Object> offerDataMap = new HashMap<>();
        List<Map<String,Object>> accProdList = new ArrayList<>();
        Map<String,Object> accProdMap = null;
        List<Map<String,Object>> prodInstList = (List<Map<String, Object>>) param.get("prodInsts");
        int size = prodInstList.size();
        String lanId = "";
        for (int i = 0; i < size; i++) {

            Map<String,Object> prodInst = prodInstList.get(0);
            if(i == 0) {
                 lanId = MapUtils.getString(prodInst, "lanId");
            }
            accProdMap = new HashMap<>();
            accProdMap.put("isDel","N");
            accProdMap.put("piRegionId",MapUtils.getLong(prodInst,"regionId"));
            accProdMap.put("prodId",MapUtils.getLong(prodInst,"prodId"));
            accProdMap.put("prodInstId",MapUtils.getLong(prodInst,"prodInstId"));
            accProdMap.put("roleId",1);
            accProdList.add(accProdMap);
        }
        paramMap.put("acceptLanId",lanId);
        offerDataMap.put("accProds",accProdList);
        offerDataMap.put("offerId",offerId);
        offerDataMap.put("offerInstId",-1);
        offerDataMap.put("operType","ADD");
        offerDataMap.put("serviceOfferId","3010100000");
        offerDataList.add(offerDataMap);
        paramMap.put("offerDatas",offerDataList);
        paramMap.put("orderSource","1012");
        paramMap.put("sceneCode","unify");
//        paramMap.put("staffId",);
        paramMap.put("staffId",10331307);
        paramMap.put("staffCode","SF_CLYXHDPT");
        paramMap.put("sysSource",1012170);
        return paramMap;
    }


    /**
     * 被动触点接口
     *
     * @param telphone 电话号码
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> execOrderViewServlet(String telphone) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "被动触点接口记录查询失败");
        try {
            //组装调用接口数据
            if (StringUtils.isEmpty(telphone)) {
                log.info("被动触点接口记录信息失败:" + telphone);
                turnMap.put(Constants.RESULT_MSG_STR, "被动触点接口记录查询失败");
                return turnMap;
            }
            Map<String,Object> param = new HashMap<>();
            param.put("objectType","0");
            param.put("objectId",telphone);
            param.put("busiCode","OPR_POS_REQ");
            param.put("latnId","OPR_POS_REQ");
            String contactChlId = (String) propertyToRedis.getPropertyValue("contactChlId");
            param.put("contactChlId",contactChlId);

            Map<String,Object> requestMap = new HashMap<>();
            requestMap.put("Request",param);

            //调用远程接口
            Map<String, Object> checkResultMap = this.postHttpByCpcp("ExecOrderUCServlet", requestMap, new HashMap<>());
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "[{\"busiInfo\":{\"recomCount\":\"1\",\"contactChlId\":\"10020\",\"recomInfo\":[{\"activityId\":\"15767\",\"userInfo\":[{\"labelInfo\":[],\"offerInfo\":[{\"offerType\":\"12\",\"offerName\":\"5G升级会员20G包29元201910\",\"offerUrl\":\"\",\"offerId\":\"7100002020\",\"remark\":\"\",\"briefIntroduction\":\"\",\"oprPosDiction\":\"推荐办理29元5G升级包\",\"smsDiction\":\"\"}],\"contactOrderId\":\"262037523\",\"userId\":\"13315958751\"}],\"activityStartDate\":\"2020-02-26 00:00:00\",\"activityEndDate\":\"2020-12-31 00:00:00\",\"activityName\":\"活动：推荐办理29元5G升级包_2020\",\"strategyId\":\"3681\",\"operationItem\":\"3000,5000\",\"priority\":0,\"activityDesc\":\"\"}]},\"resultCode\":1,\"resultDesc\":\"处理成功\",\"objectId\":\"13315958751\"}]";
                    List<Map<String, Object>> resultMap = JsonUtil.toObject(svcCont, List.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("result", resultMap);
                }
                return turnMap;
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
            turnMap.put("result", MapUtils.getObject(checkResultMap, "svcCont"));
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-execOrderViewServlet接口获得异常:", e);
        }

        return turnMap;
    }


    /**
     * 被动触点接口 是否有满足的活动
     *
     * @param telphone 电话号码
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public boolean execOrderViewServletExt(String telphone) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "被动触点接口记录查询失败");

        turnMap = this.execOrderViewServlet(telphone);
        if(Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))){
            String activityIds = (String) propertyToRedis.getPropertyValue("activityIds");
            List<Map<String,Object>> responseList = (List<Map<String, Object>>) turnMap.get("result");
            int responseListSize = responseList.size();
            for (int i = 0; i < responseListSize; i++) {
                Map<String,Object> response = responseList.get(i);
                Map<String,Object>  busiInfo = (Map<String, Object>) response.get("busiInfo");
                List<Map<String,Object>> recomInfoList = (List<Map<String, Object>>) busiInfo.get("recomInfo");
                int recomInfoListSize = recomInfoList.size();
                for (int j = 0; j < recomInfoListSize; j++) {
                    Map<String,Object> recomInfo = recomInfoList.get(i);
                    String activity = (String) recomInfo.get("activityId");
                    if(activityIds.indexOf("," +activity ) != -1){
                        return true;
                    }
                }
            }
        }else{
            return false;
        }
        return false;
    }

    /**
     * 无卡充值接口
     *
     * @param telphone 电话号码
     * @param rechargeAmount 充值金额 单位（分）
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> noCardRecharge(String telphone ,Integer rechargeAmount) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "充值失败");
        Map<String, Object> toJsonMap = new HashMap<String, Object>();
        if(StringUtils.isEmpty(telphone)){
            turnMap.put(Constants.RESULT_MSG_STR, "电话号码不能为空");
            return turnMap;
        }
        if(rechargeAmount == null || rechargeAmount <= 0){
            turnMap.put(Constants.RESULT_MSG_STR, "金额错误");
            return turnMap;
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String requestTime = sDateFormat.format(new Date());
        String incr = redisUtil.incrAtomic(Constants.BILL_REQUEST_ID + requestTime, 1, 8, TimeUnit.SECONDS);
        String requestId = requestTime + incr + Constants.SELFUSE + Constants.VCSYSTEMID;
//        String requestId = (String) billCharge.get("requestId");
        toJsonMap.put("requestTime", requestTime);
        // 请求流水（格式YYYYMMDDHHMMSS+8位循环数字+2位各省自用+2位省VC平台编码(固定值，如河北31)（详见六、附录 流水号规则）
        toJsonMap.put("requestId", requestId);
        // 充值来源 河北为18
        toJsonMap.put("rechargeSource", Constants.RechargeSource);
        // 被充值号码
        toJsonMap.put("destinationId", telphone);

        toJsonMap.put("destinationaAttr", 2);
        // 被充值用户号码类型： 0 – 客户ID 1 - 用户ID 2 - 用户号码 3 – 合同号
        toJsonMap.put("destinationIdType", "2");
        // 账本类型 0- 默认 1- 本地话费 2- 长途话费 3 - 上网费 7-全国促销流量 8-全国流量 9-积分转赠送
        toJsonMap.put("balanceItemTypeId", "0");
        // 充值量单位类型 4– 分（金额） 5– 分钟（时长） 6– 次数 7 – 流量（KB
        toJsonMap.put("rechargeUnit", "4");
        // 充值量
        toJsonMap.put("rechargeAmount", rechargeAmount);
        // 对账日期
        toJsonMap.put("auditTime", requestTime.subSequence(0, 8));
        // 工号
//        toJsonMap.put("staffId", billCharge.get("staffId"));
        //组织机构编码
//        toJsonMap.put("orgId", billCharge.get("orgId"));

        toJsonMap.put("bankId", "992");
        try {


            //调用远程接口
            Map<String, Object> checkResultMap = this.postHttpRecharge("NoCardRecharge", toJsonMap, new HashMap<>());
//            Map<String, Object> checkResultMap = this.postHttp("RechargeBalance", toJsonMap, new HashMap<>());

            if (!Constants.CODE_SUCC.equals(MapUtils.getString(checkResultMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_MSG_STR, checkResultMap.get("resultMsg"));
                String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
                boolean isDebug = Boolean.valueOf(isDebugStr);
                if (isDebug) {
                    String svcCont = "{\"requestId\":\"" + requestId + "\",\"effDate\":\"\",\"unitTypeId\":0,\"bonusAmount\":0,\"retBalance\":12647,\"resultCode\":\"0\",\"acctId\":153166113,\"startTime\":\"\",\"endTime\":\"\",\"balanceTypeId\":1,\"expDate\":\"\",\"acctBalanceId\":327856306,\"resultMsg\":\"充值成功\"}\n";
                    Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                    turnMap.put("result", resultMap);
                }
                return turnMap;
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "充值成功");
            String svcCont = MapUtils.getString(checkResultMap, "svcCont");
            Map<String, Object> resultMap = JsonUtil.toObject(svcCont, Map.class);
            resultMap.put("requestId",requestId);
            turnMap.put("result",resultMap);
            return turnMap;
        } catch (Exception e) {
            log.error("调用CRM3.0-资源中心-execOrderViewServlet接口获得异常:", e);
        }

        return turnMap;
    }


    private Map<String, Object> postHttpSubmit(String
                                                 url, Map<String, Object> paramMap, Map<String, String> headersMap) {

        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String saopUrl = (String) propertyToRedis.getPropertyValue("SERVICE_SAOP_URL_3");
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        List<CoDicItem> list = commonBmo.getSaopRouteCodes();
        for (CoDicItem coDicItem : list) {
            headersMap.put(coDicItem.getItemCode(), coDicItem.getItemVal());
        }
//        if("submitCustomerOrder".equals(url)){
//            saopUrl = "http://136.142.46.162:8470/serviceAgent/http";
//            headersMap.put("X-APP-ID","a532ed55384524a17d40b1c309ab53dc" );
//            headersMap.put("X-APP-KEY", "70d2a1b4b88b2d15b795030a48e6df5d");
//        }
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");
        paramMap.put("systemId", ServiceParam.SERVICE_JF_SYSTEM_ID);
        String requestTime = DateUtil.sdfSs.format(new Date());
        String incr = redisUtil.incrAtomic(Constants.REDIS_JF_SERIAL_NBR + requestTime, 1, 4, TimeUnit.SECONDS);
        String reqServiceId = ServiceParam.SERVICE_JF_SYSTEM_ID + requestTime + incr;
        paramMap.put("requestId", reqServiceId);
        ResponseEntity<String> resEntity = restTemplateUtil.postHttpForResponseEntity(saopUrl + "/" + url, paramMap, headersMap);

        String result = "";
        HttpHeaders headers = null;
        if (resEntity != null) {
            if (resEntity.getStatusCode() == HttpStatus.OK) {
                result = resEntity.getBody();
                headers = resEntity.getHeaders();

                if (!JsonUtil.isValidJson(result)) {
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
                    turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
                    log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
                } else {

//                    Map<String, Object> resMap = JsonUtil.toObject(result, Map.class);
//                  String headerCode = MapUtils.getString(resMap, "Code");
                    String headerCode = headers.getFirst("Code");

                    if (StringUtil.isEmptyStr(headerCode)) {
                        turnMap.put("resultMsg", !StringUtil.isEmptyStr(result) ? result : "调用接口失败");
                        return turnMap;
                    }
                    Map map = null;
                    if (result != null && result.trim().indexOf("{") == 0) {
                        map = JsonUtil.toObject(result, Map.class);
                    }

                    if (!"0000".equals(headerCode)) {
                        boolean flag = true;
                        if (map != null){
                            String message = MapUtils.getString(map, "message");
                            if(!StringUtils.isEmpty(message)){
                                flag = false;
                                turnMap.put("resultMsg", message);
                            }
                        }
                        if(flag) {
                            turnMap.put("resultMsg", !StringUtil.isEmptyStr(result) ? result : "调用接口失败");
                        }
                        return turnMap;
                    }
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
                    turnMap.put("svcCont", result);
                }
                return turnMap;
            } else {
                turnMap.put("resultMsg", !StringUtil.isEmptyStr(result) ? result : "调用接口失败");
                return turnMap;
            }
        }
        return turnMap;
    }


    private Map<String, Object> postHttp(String
                                                       url, Map<String, Object> paramMap, Map<String, String> headersMap) {

        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String saopUrl = (String) propertyToRedis.getPropertyValue("SERVICE_SAOP_URL_3");
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        List<CoDicItem> list = commonBmo.getSaopRouteCodes();
        for (CoDicItem coDicItem : list) {
            headersMap.put(coDicItem.getItemCode(), coDicItem.getItemVal());
        }
//        if("submitCustomerOrder".equals(url)){
//            saopUrl = "http://136.142.46.162:8470/serviceAgent/http";
//            headersMap.put("X-APP-ID","a532ed55384524a17d40b1c309ab53dc" );
//            headersMap.put("X-APP-KEY", "70d2a1b4b88b2d15b795030a48e6df5d");
//        }
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");
        paramMap.put("systemId", ServiceParam.SERVICE_JF_SYSTEM_ID);
        String requestTime = DateUtil.sdfSs.format(new Date());
        String incr = redisUtil.incrAtomic(Constants.REDIS_JF_SERIAL_NBR + requestTime, 1, 4, TimeUnit.SECONDS);
        String reqServiceId = ServiceParam.SERVICE_JF_SYSTEM_ID + requestTime + incr;
        paramMap.put("requestId", reqServiceId);
        String result = restTemplateUtil.postHttp(saopUrl + "/" + url, paramMap, headersMap);



        if (!JsonUtil.isValidJson(result)) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
            turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
            log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
        } else {
            Map<String, Object> resMap = JsonUtil.toObject(result, Map.class);
            String errorCode = MapUtils.getString(resMap, "errorCode");
            String errorMsg = MapUtils.getString(resMap, "errorMsg");

//            String stdCcaUserResourceQuery = MapUtils.getString(resMap, "stdCcaUserResourceQuery");

            if (StringUtil.isEmptyStr(errorCode)) {
                if(StringUtils.isEmpty(errorMsg)){
                    String code = MapUtils.getString(resMap, "code");
                    String message = MapUtils.getString(resMap, "message");
                    if("0".equals(code)){
                        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                        turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
                        turnMap.put("svcCont", result);
                        return turnMap;
                    }else {
                        turnMap.put("resultMsg", !StringUtil.isEmptyStr(message) ? message : "调用接口失败");
                        return turnMap;
                    }
                }
                turnMap.put("resultMsg", !StringUtil.isEmptyStr(errorMsg) ? errorMsg : "调用接口失败");
                return turnMap;
            }
            Map map = null;
            if (result != null && result.trim().indexOf("{") == 0) {
                map = JsonUtil.toObject(result, Map.class);
            }
            if (map != null && map.get("errorCode") != null && !"".equals(map.get("errorCode")) && !"0".equals(map.get("errorCode"))) {
                turnMap.put("resultMsg", !StringUtil.isEmptyStr((String) map.get("message")) ? map.get("message") : "调用接口失败");
                return turnMap;
            }
            if (!"0".equals(errorCode)) {
                turnMap.put("resultMsg", !StringUtil.isEmptyStr(errorMsg) ? errorMsg : "调用接口失败");
                return turnMap;
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
            turnMap.put("svcCont", result);
        }
        return turnMap;
    }


    private Map<String, Object> postHttpRecharge(String
                                                 url, Map<String, Object> paramMap, Map<String, String> headersMap) {

        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String saopUrl = (String) propertyToRedis.getPropertyValue("SERVICE_SAOP_URL_3");
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        List<CoDicItem> list = commonBmo.getSaopRouteCodes();
        for (CoDicItem coDicItem : list) {
            headersMap.put(coDicItem.getItemCode(), coDicItem.getItemVal());
        }
            saopUrl = "http://136.142.46.162:8470/serviceAgent/http";
            headersMap.put("X-APP-ID","a532ed55384524a17d40b1c309ab53dc" );
            headersMap.put("X-APP-KEY", "70d2a1b4b88b2d15b795030a48e6df5d");
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");
//        paramMap.put("systemId", ServiceParam.SERVICE_JF_SYSTEM_ID);
//        String requestTime = DateUtil.sdfSs.format(new Date());
//        String incr = redisUtil.incrAtomic(Constants.REDIS_JF_SERIAL_NBR + requestTime, 1, 4, TimeUnit.SECONDS);
//        String reqServiceId = ServiceParam.SERVICE_JF_SYSTEM_ID + requestTime + incr;
//        paramMap.put("requestId", reqServiceId);
        String result = restTemplateUtil.postHttp(saopUrl + "/" + url, paramMap, headersMap);
        if (!JsonUtil.isValidJson(result)) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
            turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
            log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
        } else {
            Map<String, Object> resMap = JsonUtil.toObject(result, Map.class);
            String resultCode = MapUtils.getString(resMap, "resultCode");
            String resultMsg = MapUtils.getString(resMap, "resultMsg");

//            String stdCcaUserResourceQuery = MapUtils.getString(resMap, "stdCcaUserResourceQuery");

            if (StringUtil.isEmptyStr(resultCode)) {
                turnMap.put("resultMsg", !StringUtil.isEmptyStr(resultMsg) ? resultMsg : "调用接口失败");
                return turnMap;
            }
            Map map = null;
            if (result != null && result.trim().indexOf("{") == 0) {
                map = JsonUtil.toObject(result, Map.class);
            }
            if (map != null && map.get("errorCode") != null && !"".equals(map.get("errorCode")) && !"0".equals(map.get("errorCode"))) {
                turnMap.put("resultMsg", !StringUtil.isEmptyStr((String) map.get("message")) ? map.get("message") : "调用接口失败");
                return turnMap;
            }
            if (!"0".equals(resultCode)) {
                turnMap.put("resultMsg", !StringUtil.isEmptyStr(resultMsg) ? resultMsg : "调用接口失败");
                return turnMap;
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
            turnMap.put("svcCont", result);
        }
        return turnMap;
    }

    /**
     * http 请求 GET  读取3.0
     *
     * @param url    地址
     * @param params 参数
     * @return String 类型
     */
    private Map<String, Object> getHttp(String
                                                url, Map<String, Object> params, Map<String, String> headersMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String saopUrl = (String) propertyToRedis.getPropertyValue("SERVICE_SAOP_URL_3");
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        List<CoDicItem> list = commonBmo.getSaopRouteCodes();
        for (CoDicItem coDicItem : list) {
            headersMap.put(coDicItem.getItemCode(), coDicItem.getItemVal());
        }
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");

        ResponseEntity<String> resEntity = restTemplateUtil.getHttp(saopUrl + "/" + url, params, headersMap);
        String result = "";
        HttpHeaders headers = null;
        if (resEntity != null) {
            if (resEntity.getStatusCode() == HttpStatus.OK) {
                result = resEntity.getBody();
                headers = resEntity.getHeaders();

                if (!JsonUtil.isValidJson(result)) {
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
                    turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
                    log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
                } else {

//                    Map<String, Object> resMap = JsonUtil.toObject(result, Map.class);
//                  String headerCode = MapUtils.getString(resMap, "Code");
                    String headerCode = headers.getFirst("Code");

                    if (StringUtil.isEmptyStr(headerCode)) {
                        turnMap.put("resultMsg", !StringUtil.isEmptyStr(result) ? result : "调用接口失败");
                        return turnMap;
                    }
                    Map map = null;
                    if (result != null && result.trim().indexOf("{") == 0) {
                        map = JsonUtil.toObject(result, Map.class);
                    }

                    if (!"0000".equals(headerCode)) {
                        turnMap.put("resultMsg", !StringUtil.isEmptyStr(result) ? result : "调用接口失败");
                        return turnMap;
                    }
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
                    turnMap.put("svcCont", result);
                }
                return turnMap;
            } else {
                turnMap.put("resultMsg", !StringUtil.isEmptyStr(result) ? result : "调用接口失败");
                return turnMap;
            }
        }
        return turnMap;
    }


    /**
     * http 请求 GET  读取3.0
     *
     * @param url    地址
     * @param params 参数
     * @return String 类型
     */
    private Map<String, Object> getHttpByCPCP(String
                                                      url, Map<String, Object> params, Map<String, String> headersMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String saopUrl = (String) propertyToRedis.getPropertyValue("SERVICE_SAOP_URL_3");
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        List<CoDicItem> list = commonBmo.getSaopRouteCodes();
        for (CoDicItem coDicItem : list) {
            headersMap.put(coDicItem.getItemCode(), coDicItem.getItemVal());
        }
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");
//        if ("get5GLabel".equals(url)) {
        saopUrl = "http://136.142.41.15:9700/serviceAgent/rest/he/cpcp/get5GLabel";
//        headersMap.put("X-APP-ID", "c3d8e3ad79007a6130bd5f04f64652fe");
//        headersMap.put("X-APP-KEY", "bb27e68f298ee17ed9428e549fed60de");
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");
//        }ZT001/qryRightTimesInfo

        ResponseEntity<String> resEntity = restTemplateUtil.getHttp(saopUrl + "/" + url, params, headersMap);
        String result = "";
        HttpHeaders headers = null;
        if (resEntity != null) {
            if (resEntity.getStatusCode() == HttpStatus.OK) {
                result = resEntity.getBody();
                headers = resEntity.getHeaders();
                if (!JsonUtil.isValidJson(result)) {
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
                    turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
                    log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
                } else {
                    Map map = null;
                    if (result != null && result.trim().indexOf("{") == 0) {
                        map = JsonUtil.toObject(result, Map.class);
                    }
                    if (map != null && map.get("code") != null && !"".equals(map.get("code")) && !"0".equals(map.get("code"))) {
                        turnMap.put("resultMsg", !StringUtil.isEmptyStr((String) map.get("message")) ? map.get("message") : "调用接口失败");
                        return turnMap;
                    }

                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
                    turnMap.put("svcCont", result);
                }
                return turnMap;
            } else {
                headers = resEntity.getHeaders();
                if (headers != null && headers.size() > 0) {
                    for (String key : headers.keySet()) {
                        turnMap.put(key, headers.getFirst(key));
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("code", resEntity.getStatusCodeValue());
                turnMap.put("resultData", map);
                return turnMap;
            }
        }
        return turnMap;
    }

    private Map<String, Object> postHttpBySendMsg(String
                                                          url, Map<String, Object> paramMap, Map<String, String> headersMap) {

        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String saopUrl = (String) propertyToRedis.getPropertyValue("SERVICE_SAOP_URL_3");
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        List<CoDicItem> list = commonBmo.getSaopRouteCodes();
        for (CoDicItem coDicItem : list) {
            headersMap.put(coDicItem.getItemCode(), coDicItem.getItemVal());
        }
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");
        String result = restTemplateUtil.postHttp(saopUrl + "/" + url, paramMap, headersMap);
        if (!JsonUtil.isValidJson(result)) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
            turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
            log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
        } else {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
            turnMap.put("svcCont", result);
        }
        return turnMap;
    }

    private Map<String, Object> postHttpByCpcp(String
                                                 url, Map<String, Object> paramMap, Map<String, String> headersMap) {

        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String saopUrl = (String) propertyToRedis.getPropertyValue("SERVICE_SAOP_URL_3");
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        List<CoDicItem> list = commonBmo.getSaopRouteCodes();
        for (CoDicItem coDicItem : list) {
            headersMap.put(coDicItem.getItemCode(), coDicItem.getItemVal());
        }
//        headersMap.put("X-APP-ID","30920be1d320cb74c413cfcb5709d33f" );
//        headersMap.put("X-APP-KEY", "8bb28664abe3e858e2ff7d20f4cf3abb");
        paramMap.put("systemId", ServiceParam.SERVICE_JF_SYSTEM_ID);
        String requestTime = DateUtil.sdfSs.format(new Date());
        String incr = redisUtil.incrAtomic(Constants.REDIS_JF_SERIAL_NBR + requestTime, 1, 4, TimeUnit.SECONDS);
        String reqServiceId = ServiceParam.SERVICE_JF_SYSTEM_ID + requestTime + incr;
        paramMap.put("requestId", reqServiceId);
        String result = restTemplateUtil.postHttp(saopUrl + "/" + url, paramMap, headersMap);
        if (!JsonUtil.isValidJson(result)) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
            turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
            log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
        } else {
            Map<String, Object> resMap = JsonUtil.toObject(result, Map.class);
            List<Map<String,Object>> responseList = (List<Map<String, Object>>) resMap.get("Response");
            if(responseList != null && responseList.size() > 0){
                Map<String,Object> responseMap = responseList.get(0);
                String resultCode = MapUtils.getString(responseMap,"resultCode");
                if(!"1".equals(resultCode)){
                    turnMap.put("resultMsg", !StringUtil.isEmptyStr((String) responseMap.get("resultDesc")) ? responseMap.get("resultDesc") : "调用接口失败");
                    return turnMap;
                }else{
                    turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                    turnMap.put(Constants.RESULT_MSG_STR, "调用完成");
                    turnMap.put("svcCont", responseList);
                    return  turnMap;
                }
            }
        }
        return turnMap;
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Toolkit.FMT_DATE);
        String createTime = dateTimeFormatter.format(now);
        System.out.println(createTime);


        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Toolkit.FMT_DATE);
        String timeStr = dateFormat.format(date);
        System.out.println(timeStr);
    }
}
