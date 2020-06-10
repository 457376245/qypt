package com.web.thirdinterface;

import com.alibaba.fastjson.JSON;
import com.web.common.Constants;
import com.web.common.PropertyToRedis;
import com.web.common.ServiceParam;
import com.web.dao.CoMemberDao;
import com.web.model.CoLogBean;
import com.web.model.CoMember;
import com.web.util.JsonUtil;
import com.web.util.RedisUtil;
import com.web.util.RestTemplateUtil;
import com.web.util.Toolkit;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 第三方接口实现
 * </p>
 *
 * @package: com.web.thirdinterface
 * @description: 第三方接口实现
 * @author: wangzx8
 * @date: 2020/3/13 10:23
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Component
public class ThirdQYInterface {

    private static Logger log = LoggerFactory.getLogger(ThirdQYInterface.class);

    @Resource
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private CoMemberDao coMemberDao;
    @Resource
    private RedisUtil redisUtil;

    @Autowired
    @Qualifier("com.web.common.PropertyToRedis")
    protected PropertyToRedis propertyToRedis;


    /**
     * 3.1.会员信息查询接口
     *
     * @param map bindingNbr	绑定号码	String  可为空，与custId必须二选一
     *            custId	客户ID	String  可为空，与bindingNbr必须二选一
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * memberId	会员Id	Integer
     * memberName	会员名称	String
     * memberNickname	会员昵称	String
     * areaId	所在地区	Integer	电信集团公共管理区域编码
     * memberAddress	会员地址	String
     * memberBirthday	会员生日	String	8位日期
     * memberSex	会员性别	Integer 1．男，2.女，0.未知
     * memberEmail	会员邮箱	String
     * memberLevelCd	会员级别	Integer  3100 1星,3200 2星,3300 3星,3400 4星,3500 5星,3600 6星,3700 7星,3800 0星
     * memberLevelName	会员级别名称	String
     * effDate	会员生效日期	String  8位日期
     * expDate	会员失效日期	String	8位日期
     * stateCd	会员状态	Integer	1.生效
     * stateName	会员状态名称	String
     * firstLoginDate	首次登陆时间	String	14位时间
     * custId	省内客户ID	String
     * bindingNbr	绑定的手机号码	String
     * score	星级成长值	String
     * rightInfos	星级权益	List	星级会员需要填写
     * [{
     * rightInfo	权益信息	object	n	M
     * {
     * memberRightId	会员权益ID	String
     * rightSpecId	俱乐部会员服务ID	String
     * rightCode	权益编码	String
     * rightName	权益名称	String
     * effDate	权益生效时间	String	格式：yyyy-mm-dd 24HH:MM:SS
     * expDate	权益失效时间	String	格式：yyyy-mm-dd 24HH:MM:SS
     * }
     * }]
     * }
     */
    public Map<String, Object> memberQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        String url = "/member/memberQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"areaId\":8130300,\"bindingNbr\":\"15383995095\",\"custId\":\"423252113\",\"effDate\":\"20200326000000\",\"expDate\":\"20200726000000\",\"firstLoginDate\":\"20200326212432\",\"memberAddress\":null,\"memberBirthday\":null,\"memberEmail\":null,\"memberId\":2,\"memberLevelCd\":3200,\"memberLevelName\":\"二星\",\"memberName\":\"张某\",\"memberNickname\":\"张某\",\"memberSex\":null,\"stateCd\":1,\"stateName\":null}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }
        if (Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, Constants.RESULT_CODE_STR))) {
            Map<String, Object> resultMap = (Map<String, Object>) MapUtils.getMap(turnMap, "result");
            List<Map<String, Object>> list = (List<Map<String, Object>>) resultMap.get("starMemberRightsDtos");
            if (list != null && list.size() > 0) {
                resultMap.put("rightInfos", list);
                resultMap.remove("starMemberRightsDtos");
                turnMap.put("result", resultMap);
            } else {

            }
            String isDebugStr1 = (String) propertyToRedis.getPropertyValue("is_debug");
            boolean isDebug1 = Boolean.valueOf(isDebugStr1);
            if (isDebug || isDebug1) {
                //如果是字符串对象，可以强转成Map<String, Object>
                String rightInfos = "[{\"memberRightId\":11704711,\"rightSpecId\":11,\"rightCode\":100100,\"rightName\":\"积分倍增\",\"rightStatus\":0,\"effDate\":\"2020-03-01 00:00:00\",\"expDate\":\"2020-03-31 23:59:59\"},{\"memberRightId\":11704715,\"rightSpecId\":12,\"rightCode\":100200,\"rightName\":\"10000号优先接入\",\"rightStatus\":0,\"effDate\":\"2019-12-01 00:00:00\",\"expDate\":\"2020-11-30 23:59:59\"},{\"memberRightId\":11704720,\"rightSpecId\":13,\"rightCode\":100300,\"rightName\":\"免费换卡\",\"rightStatus\":0,\"effDate\":\"2019-12-01 00:00:00\",\"expDate\":\"2020-11-30 23:59:59\"},{\"memberRightId\":11704746,\"rightSpecId\":14,\"rightCode\":100400,\"rightName\":\"宽带紧急复机\",\"rightStatus\":0,\"effDate\":\"2020-03-01 00:00:00\",\"expDate\":\"2020-03-31 23:59:59\"},{\"memberRightId\":11704750,\"rightSpecId\":21,\"rightCode\":100800,\"rightName\":\"国漫免预存/押金开通\",\"rightStatus\":0,\"effDate\":\"2019-12-01 00:00:00\",\"expDate\":\"2020-11-30 23:59:59\"},{\"memberRightId\":11704678,\"rightSpecId\":23,\"rightCode\":101000,\"rightName\":\"积分礼包/网龄红包\",\"rightStatus\":0,\"effDate\":\"2019-12-01 00:00:00\",\"expDate\":\"2020-11-30 23:59:59\"},{\"memberRightId\":11704703,\"rightSpecId\":24,\"rightCode\":101100,\"rightName\":\"流量赠送\",\"rightStatus\":0,\"effDate\":\"2019-12-01 00:00:00\",\"expDate\":\"2020-11-30 23:59:59\"},{\"memberRightId\":11704705,\"rightSpecId\":25,\"rightCode\":101200,\"rightName\":\"优惠/优先购机\",\"rightStatus\":0,\"effDate\":\"2019-12-01 00:00:00\",\"expDate\":\"2020-11-30 23:59:59\"}]";
                List<Map<String, Object>> vendors = (List<Map<String, Object>>) JSON.parse(rightInfos);
                resultMap.put("rightInfos", vendors);
            }


            String score = MapUtils.getString(resultMap, "score");
            if (StringUtils.isEmpty(score)) {
                Integer memberLevelCd = MapUtils.getInteger(resultMap, "memberLevelCd");
//                3100 1星,3200 2星,3300 3星,3400 4星,3500 5星,3600 6星,3700 7星,3800 0星
                switch (memberLevelCd) {
                    case 3100:
                        score = "650";
                        break;
                    case 3200:
                        score = "1100";
                        break;
                    case 3300:
                        score = "1300";
                        break;
                    case 3400:
                        score = "1500";
                        break;
                    case 3500:
                        score = "4500";
                        break;
                    case 3600:
                        score = "6600";
                        break;
                    case 3700:
                        score = "8500";
                        break;
                    case 3800:
                        score = "500";
                        break;
                    default:
                        score = "500";
                        break;
                }
                resultMap.put("score", score);
            }

        }

        log.info("最红返回的数据  ============= " + JsonUtil.toStringNonNull(turnMap));
        return turnMap;
    }

    /**
     * 3.2.会员信息更新接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            memberNickname	会员昵称	String
     *            areaId	所在地区	Integer	电信集团公共管理区域编码
     *            memberAddress	会员地址	String
     *            memberBirthday	会员生日	String	8位日期
     *            memberSex	会员性别	Integer	1．男，2.女
     *            memberEmail	会员邮箱	String
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> memberExtEdit(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        String url = "/member/memberExtEdit";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }

    /**
     * 3.3.会员账户查询接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * memberId	会员ID	Integer
     * custId	客户ID	Integer
     * bindingNbr	绑定号码	String
     * acctAmount	账户金额	Integer
     * stateCd	状态编码	Integer		1.正常
     * }
     */
    public Map<String, Object> memberAcctQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        String url = "/member/acctQry";
        Map<String, Object> headerMap = new HashMap<>();

        Map<String, Object> interfaceMap = this.postHttp(url, map, headerMap);

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"acctAmount\":0,\"bindingNbr\":\"18140219867\",\"custId\":3213232,\"memberId\":1,\"stateCd\":1}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return interfaceMap;
    }

    /**
     * 3.4.会员账户变动接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            chgType	变动类型	Integer	1、增加，2、扣减
     *            chgAmount	变动金额	Integer	均为正整数
     *            chgDesc	变动描述	String	如：签到、连续签到奖励、参与抽奖活动扣减等
     *            eventObjType	变动事件对象类型	Integer	1、参与活动，2、参与任务 3、待补充
     *            eventObjId	变动事件对象标识	String	如活动编码、任务编码
     *            eventObjName	变动事件对象名称	String	如每日签到任务、连续签到7天任务、xx节日抽奖活动
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * acctAmount	账户金额	Integer	更新后的账户金额
     * }
     */
    public Map<String, Object> memberAcctChg(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        Integer chgType = MapUtils.getInteger(map, "chgType");
        Integer chgAmount = MapUtils.getInteger(map, "chgAmount");
        String chgDesc = MapUtils.getString(map, "chgDesc");
        Integer eventObjType = MapUtils.getInteger(map, "eventObjType");
        String eventObjId = MapUtils.getString(map, "eventObjId");
        String eventObjName = MapUtils.getString(map, "eventObjName");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        if (chgType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "变动类型为空");
            return turnMap;
        }
        if (chgAmount == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "变动金额为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(chgDesc)) {
            turnMap.put(Constants.RESULT_MSG_STR, "变动描述为空");
            return turnMap;
        }
        if (eventObjType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "变动事件对象类型为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(eventObjId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "变动事件对象标识为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(eventObjName)) {
            turnMap.put(Constants.RESULT_MSG_STR, "变动事件对象名称为空");
            return turnMap;
        }
        String url = "/member/acctChg";
        Map<String, Object> headerMap = new HashMap<>();

        turnMap = this.postHttp(url, map, headerMap);
        try {
            CoMember coMemberParam = new CoMember();
            coMemberParam.setMemberPhone(bindingNbr);
            Map coMember = coMemberDao.getCoMember(coMemberParam);
            if (coMember != null && coMember.size() > 0) {
                CoLogBean logBean = new CoLogBean();
                logBean.setMemberId(Long.parseLong(coMember.get("MEMBER_ID").toString()));
                logBean.setOptType(String.valueOf(chgType));
                logBean.setOptVal(chgAmount);
                logBean.setOptDesc(chgDesc);
                if (Constants.CODE_SUCC.equals(turnMap.get("resultCode"))) {
                    logBean.setOptResult("1");
                } else {
                    logBean.setOptResult("2");
                }
                coMemberDao.insertCoLogBean(logBean);
            }
        } catch (Exception e) {
            log.error("账户变更落日志异常：" + e.getMessage());
        }

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"acctAmount\":100}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }

    /**
     * 3.5.会员账户变动明细查询接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            qryMonth	查询月份	String	yyyymm，只能查询近6个月数据
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页行数（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * total	总条数	Integer
     * acctChgDetailList	列表数据	[]
     * [{
     * chgDate	变动日期	String	14	M	yyyymmddhh24miss
     * chgAmount	变动金额	Integer	10	M
     * chgType	变动类型	Integer	1	M	1、增加，2、扣减
     * chgDesc	变动描述	String	100	M
     * chgBeforeAmount	变动前金额	Integer	10	C1
     * chgAfterAmount	变动后金额	Integer	10	C1
     * }]
     * }
     */
    public Map<String, Object> memberQryAcctChgDetail(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        String qryMonth = MapUtils.getString(map, "qryMonth");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        if (pageNum == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "页码为空");
            return turnMap;
        }
        if (pageSize == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(qryMonth)) {
            LocalDate now = LocalDate.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Toolkit.FMT_DATE);
            String nowDateStr = dateTimeFormatter.format(now);
            qryMonth = nowDateStr.substring(0, 6);
            map.put("qryMonth", qryMonth);

        }
        String url = "/member/qryAcctChgDetail";
        Map<String, Object> headerMap = new HashMap<>();

        turnMap = this.postHttp(url, map, headerMap);


        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"total\":3,\"acctChgDetailList\":[{\"chgAfterAmount\":109,\"chgAmount\":1,\"chgBeforeAmount\":110,\"chgDate\":\"20200413073331\",\"chgDesc\":\"秒杀活动[鏃ヤ細鍦�-2020-04-13]扣减\",\"chgType\":2},{\"chgAfterAmount\":110,\"chgAmount\":10,\"chgBeforeAmount\":100,\"chgDate\":\"20200410064204\",\"chgDesc\":\"参与任务[语音任务]奖励\",\"chgType\":1},{\"chgAfterAmount\":100,\"chgAmount\":1,\"chgBeforeAmount\":99,\"chgDate\":\"20200407074442\",\"chgDesc\":\"参与任务[日常签到]奖励\",\"chgType\":1}]}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }


        return turnMap;
    }

    /**
     * 3.6.会员权益兑换关系查询接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页行数（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * total	总条数	Integer
     * exchObjBusiTypeList	列表数据	[]
     * [{
     * exchBusiTypeCode	兑换业务分类编码
     * exchBusiTypeName	兑换业务分类名称	 如：电信权益
     * exchBusiTypeDesc	兑换业务分类描述
     * stateCd	状态	Integer· 1 生效的
     * stateName	状态名称
     * }]
     * }
     */
    public Map<String, Object> memberExchObjBusiTypeListQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (pageNum == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "页码为空");
            return turnMap;
        }
        if (pageSize == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量为空");
            return turnMap;
        }
        String url = "/member/exchObjBusiTypeListQry";
        Map<String, Object> headerMap = new HashMap<>();

        turnMap = this.postHttp(url, map, headerMap);

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"total\":5,\"exchObjBusiTypeList\":[{\"exchBusiTypeCode\":\"1000\",\"exchBusiTypeDesc\":\"视频音乐\",\"exchBusiTypeName\":\"视频音乐\",\"stateCd\":1,\"stateName\":\"已生效\"},{\"exchBusiTypeCode\":\"2000\",\"exchBusiTypeDesc\":\"生活服务\",\"exchBusiTypeName\":\"生活服务\",\"stateCd\":1,\"stateName\":\"已生效\"},{\"exchBusiTypeCode\":\"3000\",\"exchBusiTypeDesc\":\"旅游出行\",\"exchBusiTypeName\":\"旅游出行\",\"stateCd\":1,\"stateName\":\"已生效\"},{\"exchBusiTypeCode\":\"1005\",\"exchBusiTypeDesc\":\"美食餐饮\",\"exchBusiTypeName\":\"美食餐饮\",\"stateCd\":1,\"stateName\":\"已生效\"}]}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }


        return turnMap;
    }

    /**
     * 3.7.会员权益兑换关系查询接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            exchBusiTypeCode	兑换业务分类编码	String	只查当前分类下的兑换关系
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页行数（分页查询条件）、必填
     *            exchObjCode 兑换对象编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * total	总条数	Integer
     * exchObjList	列表数据	[]
     * [{
     * exchObjType	兑换对象类型	Integer	类型编码
     * exchObjTypeName	兑换对象类型名称	String	如：权益
     * exchObjCode	兑换对象编码	String	 如：S2232211
     * exchObjName	兑换对象名称	String	如：泛智能终端抵用券
     * rightsCode	权益编码	String	60	M
     * rightsName	权益名称	String	100	M
     * limit	剩余数量	Integer	限量权益会有数量限制
     * payType	支付方式	Integer	1、金豆（翼豆）支付，2、金豆（翼豆）+现金混合支付 本期项目仅需payType=1（2暂不做支撑）
     * payGoldAmount	会员账户支付金额	Integer	金豆（翼豆）支付金额，payType为1时有值
     * }]
     * }
     */
    public Map<String, Object> memberExchObjListQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        if (pageNum == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "页码为空");
            return turnMap;
        }
        if (pageSize == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量为空");
            return turnMap;
        }
        String url = "/member/exchObjListQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);


        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"total\":2,\"exchObjList\":[{\"exchObjCode\":\"DX001\",\"exchObjName\":\"200M流量券\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000003\",\"rightsName\":\"200M流量券\"},{\"exchObjCode\":\"AQYDH001\",\"exchObjName\":\"爱奇艺月卡会员\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000005\",\"rightsName\":\"爱奇艺月卡会员\"}]}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }

    private void refreshStockRedis(String rightsCode, Integer count) {
        Map<String, Object> stockParam = new HashMap<>();
        stockParam.put("rightsCode", rightsCode);
        Integer usedNum = (Integer) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + Constants.REDIS_RIGHTS_CODE + rightsCode);
        if (usedNum == null) {
            Map<String, Object> stockQryResultMap = this.rightsStockQry(stockParam);
            if (Constants.CODE_SUCC.equals(MapUtils.getString(stockQryResultMap, Constants.RESULT_CODE_STR))) {
                Map<String, Object> stockQryMap = (Map<String, Object>) MapUtils.getMap(stockQryResultMap, "result");
                if (stockQryMap.get("usedNum") != null) {
                    Integer usedNumStock = MapUtils.getInteger(stockQryMap, "usedNum");
                    usedNum = usedNumStock;
                } else {
                    usedNum = count;
                }
            }

        } else {
            usedNum = usedNum + count;
        }
        redisUtil.addRedisInfo(Constants.REDIS_DEL_KEY + Constants.REDIS_RIGHTS_CODE + rightsCode, usedNum, 1440);
//        Map<String, Object> stockQryResultMap = this.rightsStockQry(stockParam);
//        if (Constants.CODE_SUCC.equals(MapUtils.getString(stockQryResultMap, Constants.RESULT_CODE_STR))) {
//            Map<String, Object> stockQryMap = (Map<String, Object>) MapUtils.getMap(stockQryResultMap, "result");
//            redisUtil.addRedisInfo(Constants.REDIS_RIGHTS_CODE + rightsCode, MapUtils.getInteger(stockQryMap, "usedNum"));
//        }
    }


    private void refreshS1tockRedis(String rightsCode) {
        Map<String, Object> stockParam = new HashMap<>();
        stockParam.put("rightsCode", rightsCode);
        Map<String, Object> stockQryResultMap = this.rightsStockQry(stockParam);
        if (Constants.CODE_SUCC.equals(MapUtils.getString(stockQryResultMap, Constants.RESULT_CODE_STR))) {
            Map<String, Object> stockQryMap = (Map<String, Object>) MapUtils.getMap(stockQryResultMap, "result");
            if (stockQryMap.get("usedNum") != null) {
                redisUtil.addRedisInfo(Constants.REDIS_DEL_KEY + Constants.REDIS_RIGHTS_CODE + rightsCode, MapUtils.getInteger(stockQryMap, "usedNum"), 1440);
            }

        }
    }

//    private void refreshStockRedis(Map<String, Object> map) {
//        Map<String, Object> turnMap = new HashMap<String, Object>();
//        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
//        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
//        String bindingNbr = MapUtils.getString(map, "bindingNbr");
//        String custId = MapUtils.getString(map, "custId");
//        Integer pageNum = MapUtils.getInteger(map, "pageNum");
//        Integer pageSize = MapUtils.getInteger(map, "pageSize");
//        String url = "/member/exchObjListQry";
//        Map<String, Object> headerMap = new HashMap<>();
//        turnMap = this.postHttp(url, map, headerMap);
//        if (Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, Constants.RESULT_CODE_STR))) {
//            Map<String, Object> resultMap = (Map<String, Object>) MapUtils.getMap(turnMap, "result");
//            List<Map<String, Object>> exchObjList = (List<Map<String, Object>>) resultMap.get("exchObjList");
//            if (exchObjList != null && exchObjList.size() > 0) {
//                for (int i = 0; i < exchObjList.size(); i++) {
//                    Map<String, Object> exchObj = exchObjList.get(i);
//                    String rightsCode = MapUtils.getString(exchObj, "rightsCode");
//                    if (!StringUtils.isEmpty(rightsCode)) {
//                        this.refreshStockRedis(rightsCode, 1);
//                    }
//                }
//
//            }
//        }
//    }


    /**
     * 3.7.会员权益兑换关系查询接口 扩展
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            exchBusiTypeCode	兑换业务分类编码	String	只查当前分类下的兑换关系
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页行数（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * total	总条数	Integer
     * exchObjList	列表数据	[]
     * [{
     * exchObjType	兑换对象类型	Integer	类型编码
     * exchObjTypeName	兑换对象类型名称	String	如：权益
     * exchObjCode	兑换对象编码	String	 如：S2232211
     * exchObjName	兑换对象名称	String	如：泛智能终端抵用券
     * rightsCode	权益编码	String	60	M
     * rightsName	权益名称	String	100	M
     * limit	剩余数量	Integer	限量权益会有数量限制
     * payType	支付方式	Integer	1、金豆（翼豆）支付，2、金豆（翼豆）+现金混合支付 本期项目仅需payType=1（2暂不做支撑）
     * payGoldAmount	会员账户支付金额	Integer	金豆（翼豆）支付金额，payType为1时有值
     * usedNum 使用量
     * }]
     * }
     */
    public Map<String, Object> memberExchObjListQryExt(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        if (pageNum == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "页码为空");
            return turnMap;
        }
        if (pageSize == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量为空");
            return turnMap;
        }
        String url = "/member/exchObjListQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
//        if (Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, Constants.RESULT_CODE_STR))) {
//            Map<String, Object> resultMap = (Map<String, Object>) MapUtils.getMap(turnMap, "result");
//            List<Map<String, Object>> exchObjList = (List<Map<String, Object>>) resultMap.get("exchObjList");
//            if (exchObjList != null && exchObjList.size() > 0) {
//                for (int i = 0; i < exchObjList.size(); i++) {
//                    Map<String, Object> exchObj = exchObjList.get(i);
//                    String rightsCode = MapUtils.getString(exchObj, "rightsCode");
//                    if (!StringUtils.isEmpty(rightsCode)) {
//                        Integer usedNum = (Integer) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + Constants.REDIS_RIGHTS_CODE + rightsCode);
//                        if (usedNum == null) {
//                            Map<String, Object> stockParam = new HashMap<>();
//                            stockParam.put("rightsCode", rightsCode);
//                            Map<String, Object> stockQryResultMap = this.rightsStockQry(stockParam);
//                            if (Constants.CODE_SUCC.equals(MapUtils.getString(stockQryResultMap, Constants.RESULT_CODE_STR))) {
//                                Map<String, Object> stockQryMap = (Map<String, Object>) MapUtils.getMap(stockQryResultMap, "result");
//                                if (stockQryMap.get("usedNum") == null) {
//                                    exchObj.put("usedNum", 0);
//                                } else {
//                                    exchObj.put("usedNum", MapUtils.getInteger(stockQryMap, "usedNum"));
//                                    redisUtil.addRedisInfo(Constants.REDIS_DEL_KEY + Constants.REDIS_RIGHTS_CODE + rightsCode, MapUtils.getInteger(stockQryMap, "usedNum"), 1440);
//                                }
//                            }
//                        } else {
//                            exchObj.put("usedNum", usedNum );
//                        }
//                    }
//                }
//
//            }
//        }

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"total\":12,\"exchObjList\":[{\"exchObjCode\":\"DX001\",\"exchObjName\":\"200M流量券\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000003\",\"rightsName\":\"200M流量券\",\"usedNum\":2},{\"exchObjCode\":\"AQYDH001\",\"exchObjName\":\"爱奇艺月卡会员\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000005\",\"rightsName\":\"爱奇艺月卡会员\",\"usedNum\":2},{\"exchObjCode\":\"YYHDX001\",\"exchObjName\":\"音乐会入场券\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":100,\"payType\":1,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"usedNum\":11},{\"exchObjCode\":\"LVDX001\",\"exchObjName\":\"北京2日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"usedNum\":20},{\"exchObjCode\":\"CYDX001\",\"exchObjName\":\"复古咖啡体验券\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":5,\"payType\":1,\"rightsCode\":\"S202003280000000007\",\"rightsName\":\"复古咖啡体验券\",\"usedNum\":0},{\"exchObjCode\":\"10021\",\"exchObjName\":\"兑换北京2日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":15,\"payType\":1,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"usedNum\":20},{\"exchObjCode\":\"2002\",\"exchObjName\":\"爱奇艺\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":20,\"payType\":1,\"rightsCode\":\"S202003280000000005\",\"rightsName\":\"爱奇艺月卡会员\",\"usedNum\":2},{\"exchObjCode\":\"3003\",\"exchObjName\":\"100M\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":40,\"payType\":1,\"rightsCode\":\"S202003280000000056\",\"rightsName\":\"流量100M\",\"usedNum\":0},{\"exchObjCode\":\"S202003280000000056001\",\"exchObjName\":\"流量100M\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":12,\"payType\":1,\"rightsCode\":\"S202003280000000056\",\"rightsName\":\"流量100M\",\"usedNum\":0},{\"exchObjCode\":\"S202003280000000006003\",\"exchObjName\":\"北京2日游再来一次\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":66,\"payType\":1,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"usedNum\":20},{\"exchObjCode\":\"SJZYRY001\",\"exchObjName\":\"石家庄一日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":100,\"payType\":1,\"rightsCode\":\"S202003290000000009\",\"rightsName\":\"石家庄一日游\",\"usedNum\":0},{\"exchObjCode\":\"DHSJZERY001\",\"exchObjName\":\"石家庄二日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":100,\"payType\":1,\"rightsCode\":\"S202004060000000037\",\"rightsName\":\"石家庄二日游\",\"usedNum\":0}]}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }

    /**
     * 判断当前日期是否是当月的第一天、第二天和最后一天
     *
     * @return
     */
    private boolean validationDate() {
        LocalDate today = LocalDate.now();
        //本月的最后一天
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        if (today.getDayOfMonth() == 1 || today.getDayOfMonth() == 2 || today.getDayOfMonth() == lastDay.getDayOfMonth()) {
            return true;
        }
        return false;
    }

    /**
     * 3.8.会员权益兑换接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            exchBusiTypeCode	兑换业务分类编码	String	只查当前分类下的兑换关系1
     *            exchObjTypeName	兑换对象类型名称	String	如：第三方商户权益券
     *            exchObjNane	兑换对象名称	String	如：泛智能终端抵用券
     *            exchObjCode	兑换对象编码	String	必须传3.7兑换关系查询接口返回的exchObjCode，不可传权益编码
     *            payType	支付方式	Integer	1、金豆（翼豆）支付,本期项目仅需payType=1
     *            payGoldAmount	会员账户支付金额	Integer	选填
     *            rightsCode 权益编码 用户刷新缓存
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result
     * {
     * rightsInstNbr 当前第三方券码导入的权益 为兑换码
     * }
     */
    public Map<String, Object> memberRightsExch(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        //判断当前日期是否是当月的第一天、第二天和最后一天
        boolean validationDate = this.validationDate();
        if(validationDate) {
            turnMap.put(Constants.RESULT_MSG_STR, "目前平台处于休市期，权益产品无法兑换");
            return turnMap;
        }

        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        Integer exchObjType = MapUtils.getInteger(map, "exchObjType");
        String exchObjTypeName = MapUtils.getString(map, "exchObjTypeName");
        String exchObjCode = MapUtils.getString(map, "exchObjCode");
        String exchObjName = MapUtils.getString(map, "exchObjName");
        Integer payType = MapUtils.getInteger(map, "payType");
        String exchBusiTypeCode = MapUtils.getString(map, "exchBusiTypeCode");
        String rightsCode = MapUtils.getString(map, "rightsCode"); //用户刷新缓存的使用量
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        if (exchObjType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "兑换对象类型为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(exchObjTypeName)) {
            turnMap.put(Constants.RESULT_MSG_STR, "兑换对象类型名称为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(exchObjCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "兑换对象编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(exchObjName)) {
            turnMap.put(Constants.RESULT_MSG_STR, "兑换对象名称为空");
            return turnMap;
        }
        if (payType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "支付方式为空");
            return turnMap;
        }

        String url = "/member/rightsExch";
        Map<String, Object> headerMap = new HashMap<>();

        turnMap = this.postHttp(url, map, headerMap);
        if (Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, Constants.RESULT_CODE_STR))) {
            this.refreshStockRedis(rightsCode, 1);
        }

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"total\":12,\"exchObjList\":[{\"exchObjCode\":\"DX001\",\"exchObjName\":\"200M流量券\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000003\",\"rightsName\":\"200M流量券\",\"usedNum\":2},{\"exchObjCode\":\"AQYDH001\",\"exchObjName\":\"爱奇艺月卡会员\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000005\",\"rightsName\":\"爱奇艺月卡会员\",\"usedNum\":2},{\"exchObjCode\":\"YYHDX001\",\"exchObjName\":\"音乐会入场券\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":100,\"payType\":1,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"usedNum\":11},{\"exchObjCode\":\"LVDX001\",\"exchObjName\":\"北京2日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":10,\"payType\":1,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"usedNum\":20},{\"exchObjCode\":\"CYDX001\",\"exchObjName\":\"复古咖啡体验券\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":5,\"payType\":1,\"rightsCode\":\"S202003280000000007\",\"rightsName\":\"复古咖啡体验券\",\"usedNum\":0},{\"exchObjCode\":\"10021\",\"exchObjName\":\"兑换北京2日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":15,\"payType\":1,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"usedNum\":20},{\"exchObjCode\":\"2002\",\"exchObjName\":\"爱奇艺\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":20,\"payType\":1,\"rightsCode\":\"S202003280000000005\",\"rightsName\":\"爱奇艺月卡会员\",\"usedNum\":2},{\"exchObjCode\":\"3003\",\"exchObjName\":\"100M\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":40,\"payType\":1,\"rightsCode\":\"S202003280000000056\",\"rightsName\":\"流量100M\",\"usedNum\":0},{\"exchObjCode\":\"S202003280000000056001\",\"exchObjName\":\"流量100M\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":12,\"payType\":1,\"rightsCode\":\"S202003280000000056\",\"rightsName\":\"流量100M\",\"usedNum\":0},{\"exchObjCode\":\"S202003280000000006003\",\"exchObjName\":\"北京2日游再来一次\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":66,\"payType\":1,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"usedNum\":20},{\"exchObjCode\":\"SJZYRY001\",\"exchObjName\":\"石家庄一日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":100,\"payType\":1,\"rightsCode\":\"S202003290000000009\",\"rightsName\":\"石家庄一日游\",\"usedNum\":0},{\"exchObjCode\":\"DHSJZERY001\",\"exchObjName\":\"石家庄二日游\",\"exchObjType\":1,\"exchObjTypeName\":\"权益\",\"limit\":999,\"payGoldAmount\":100,\"payType\":1,\"rightsCode\":\"S202004060000000037\",\"rightsName\":\"石家庄二日游\",\"usedNum\":0}]}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }


    /**
     * 3.9.会员兑换记录查询接口
     *
     * @param map bindingNbr	绑定号码	String	可为空，与custId必须二选一
     *            custId	客户ID	String	可为空，与bindingNbr必须二选一
     *            qryMonth	查询月份	String	yyyymm，只能查询近6个月数据
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页行数（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * total	总条数	Integer
     * exchRecList	列表数据	[]
     * [{
     * exchObjType	兑换对象类型	Integer	1.权益
     * exchObjTypeName	兑换对象类型名称	String	如：第三方商户权益券
     * exchObjCode	兑换对象编码	String
     * exchObjName	兑换对象名称	String	如：泛智能终端抵用券
     * payGoldAmount	会员账户支付金额	Integer
     * payCashAmount	现金支付金额	Integer	单位：分
     * exchDate	兑换日期	String	yyyymmddhh24miss
     * stateCd	状态编码	Integer	1、成功，3、失败
     * stateName	状态名称	String	如：成功
     * }]
     * }
     */
    public Map<String, Object> memberExchHisQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String bindingNbr = MapUtils.getString(map, "bindingNbr");
        String custId = MapUtils.getString(map, "custId");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        String qryMonth = MapUtils.getString(map, "qryMonth");
        if (StringUtils.isEmpty(bindingNbr) && StringUtils.isEmpty(custId)) {
            turnMap.put(Constants.RESULT_MSG_STR, "绑定号码和客户ID均为空");
            return turnMap;
        }
        if (pageNum == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "页码为空");
            return turnMap;
        }
        if (pageSize == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(qryMonth)) {
            turnMap.put(Constants.RESULT_MSG_STR, "查询月份为空");
            return turnMap;
        }
        String url = "/member/exchHisQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);


        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"total\":20,\"exchRecList\":[{\"exchDate\":\"2020-03-25 21:37:03.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:58.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:55.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:52.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:50.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:45.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:34.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:30.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 21:36:27.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-27 17:54:34.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-27 17:54:37.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-27 17:54:41.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-25 20:31:16.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-26 11:20:47.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-27 04:03:21.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-27 04:09:24.0\",\"exchObjCode\":\"20200300000001\",\"exchObjName\":\"电信自有权益033\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":100,\"rightsCode\":\"S202003240000000037\",\"rightsName\":\"音乐会入场券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-29 02:15:32.0\",\"exchObjCode\":\"LVDX001\",\"exchObjName\":\"北京2日游\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":10,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-29 02:17:51.0\",\"exchObjCode\":\"LVDX001\",\"exchObjName\":\"北京2日游\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":10,\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-29 11:14:44.0\",\"exchObjCode\":\"DX001\",\"exchObjName\":\"200M流量券\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":10,\"rightsCode\":\"S202003280000000003\",\"rightsName\":\"200M流量券\",\"stateCd\":3,\"stateName\":\"3\"},{\"exchDate\":\"2020-03-29 12:39:38.0\",\"exchObjCode\":\"AQYDH001\",\"exchObjName\":\"爱奇艺月卡会员\",\"exchObjType\":null,\"exchObjTypeName\":\"权益\",\"payCashAmount\":null,\"payGoldAmount\":10,\"rightsCode\":\"S202003280000000005\",\"rightsName\":\"爱奇艺月卡会员\",\"stateCd\":3,\"stateName\":\"3\"}]}\n";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }

    /**
     * 3.10.活动信息同步接口
     *
     * @param map activityCode	活动编码	String	每个活动需要唯一
     *            acivityName	活动名称	String
     *            activityDesc	活动描述	String
     *            startDate	活动开始时间	String	yyyymmddhh24miss
     *            endDate	活动结束时间	String	yyyymmddhh24miss
     *            areaRel	活动关联地区	Integer[]	公共管理区域编码,如[510000,520000]
     *            rightsRel	活动关联权益	[]
     *            [{
     *            rightsCode	权益编码	String
     *            useNum	使用数量	Integer
     *            }]
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> activitySync(Map<String, Object> map) {

        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String activityCode = MapUtils.getString(map, "activityCode");
        String acivityName = MapUtils.getString(map, "acivityName");
        String activityDesc = MapUtils.getString(map, "activityDesc");
        String createdate = MapUtils.getString(map, "createdate");
        String startDate = MapUtils.getString(map, "startDate");
        String endDate = MapUtils.getString(map, "endDate");
        List<Integer> areaRel = (List<Integer>) map.get("areaRel");

        if (StringUtils.isEmpty(activityCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(acivityName)) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动名称为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(activityDesc)) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动描述为空");
            return turnMap;
        }

        if (StringUtils.isEmpty(createdate)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Toolkit.FMT_DATE);
            String requestTime = dateTimeFormatter.format(now);
            map.put("createdate", requestTime);
        }
        if (StringUtils.isEmpty(startDate)) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动开始时间为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(endDate)) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动结束时间为空");
            return turnMap;
        }
        if (areaRel == null || areaRel.size() == 0) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动关联地区为空");
            return turnMap;
        }
        String url = "/activity/activitySync";
        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Object> interfaceMap = this.postHttp(url, map, headerMap);
        return interfaceMap;
    }

    /**
     * 3.11.权益分类查询接口
     *
     * @param map rightsSpecCd	权益分类标识	String	可为空
     *            rightsSpecName	权益分类名称	String	可为空
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页航速（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * totalNum	总数	Integer	符合条件的数据总量
     * rightsSpecList	权益分类列表	[]
     * [{
     * rightsSpecCd	权益分类编码	Integer	20	M	权益分类标识
     * rightsSpecName	权益分类名称	String	20	M	权益分类名称
     * parRightsSpecCd	上级权益分类编码	Integer	20	C	上级权益分类标识（可空）
     * }]
     * }
     */
    public Map<String, Object> rightsSpecQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (pageNum == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "页码为空");
            return turnMap;
        }
        if (pageSize == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量为空");
            return turnMap;
        }
        if (pageNum < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "页码为空");
            return turnMap;
        }
        if (pageSize < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量为空");
            return turnMap;
        }
        String url = "/rights/rightsSpecQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);


        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"rightsSpecList\":[{\"parRightsSpecCd\":null,\"rightsSpecCd\":\"11\",\"rightsSpecName\":\"电信自有权益\"},{\"parRightsSpecCd\":null,\"rightsSpecCd\":\"12\",\"rightsSpecName\":\"合作商户权益\"}],\"totalNum\":2}\n";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }

    /**
     * 3.12.权益查询接口
     *
     * @param map rightsReqParam	权益请求参数	String	非必填，可为权益编码或权益名称或权益类型标识
     *            rightsReqParamType	请求参数类型	Integer	非必填，rightsReqParam不为空时该字段必填
     *            1、按权益编码查询
     *            2、按权益名称查询（模糊查询）
     *            3、混合查询（采用OR）
     *            4、按权益分类标识查询
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页航速（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * totalNum	总数	Integer	符合条件的数据总量
     * rightsList	权益列表	[]
     * [{
     * rightsCode	权益编码	String	权益编码
     * rightsName	权益名称	String
     * rightsDesc	权益描述	String	权益描述
     * rightsConsType	权益构成类型	Integer	1、单权益，2、权益包
     * faceValue	权益面额	Float	权益的展示额度信息
     * faceValueUnit	权益面额单位	String	权益展示额度的价值单位。如优惠金额的元、角、分；赠送流量的M、G；赠送时长的分、时等
     * rightsSpecCd	权益分类编码	Integer
     * rightsSpecName	权益分类名称	String
     * effDate	权益生效时间	yyyyMMDD
     * expDate	权益失效时间	yyyyMMDD
     * rightsRegion	权益适用地区		Intger[]	地区ID列表,只到第二级或第三级地区，使用集团公共管理权益编码（一般仅有一个地区编码）
     * totalNum	权益总量	Intger	权益总量
     * remainNum	权益剩余量	Intger	权益未分配数量
     * rightsRel	权益关系	[]	表达权益包父子关系，
     * [{
     * rightsCode	权益编码	String	权益编码
     * rightsName	权益名称	String
     * rightsConsType	权益构成类型	Integer	1、单权益，2、权益包
     * faceValue	权益面额	Float	权益的展示额度信息
     * faceValueUnit	权益面额单位	String	权益展示额度的价值单位。如优惠金额的元、角、分；赠送流量的M、G；赠送时长的分、时等
     * rightsSpecCd	权益分类编码	Integer
     * rightsSpecName	权益分类名称	String
     * effDate	权益生效时间	yyyyMMDD
     * expDate	权益失效时间	yyyyMMDD
     * rightsRegion	权益适用地区	Intger[]	地区ID列表,只到第二级或第三级地区，使用集团公共管理权益编码（一般仅有一个地区编码）
     * totalNum	权益总量	Intger	权益总量
     * remainNum	权益剩余量   Intger	权益未分配数量
     * }]
     * }]
     * }
     */
    public Map<String, Object> rightsQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (pageNum == null && pageNum < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "页面错误");
            return turnMap;
        }
        if (pageSize == null && pageSize < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量错误");
            return turnMap;
        }
        String url = "/rights/rightsQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"rightsList\":[{\"effDate\":\"20200323\",\"expDate\":\"20200530\",\"faceValue\":100,\"faceValueUnit\":\"元\",\"remainNum\":2,\"rightsCode\":\"S202003240000000037\",\"rightsConsType\":1,\"rightsDesc\":\"1212\",\"rightsName\":\"音乐会入场券\",\"rightsRegion\":[8130000],\"rightsRel\":[],\"rightsSpecCd\":11,\"rightsSpecName\":\"电信自有权益\",\"totalNum\":13}],\"totalNum\":1}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }


    /**
     * 3.12.权益查询接口
     *
     * @param rightsCode 权益编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * rightsDetail{
     * rightsCode	权益编码	String	权益编码
     * rightsName	权益名称	String
     * rightsDesc	权益描述	String	权益描述
     * rightsConsType	权益构成类型	Integer	1、单权益，2、权益包
     * faceValue	权益面额	Float	权益的展示额度信息
     * faceValueUnit	权益面额单位	String	权益展示额度的价值单位。如优惠金额的元、角、分；赠送流量的M、G；赠送时长的分、时等
     * rightsSpecCd	权益分类编码	Integer
     * rightsSpecName	权益分类名称	String
     * effDate	权益生效时间	yyyyMMDD
     * expDate	权益失效时间	yyyyMMDD
     * rightsRegion	权益适用地区		Intger[]	地区ID列表,只到第二级或第三级地区，使用集团公共管理权益编码（一般仅有一个地区编码）
     * totalNum	权益总量	Intger	权益总量
     * remainNum	权益剩余量	Intger	权益未分配数量
     * rightsRel	权益关系	[]	表达权益包父子关系，
     * usedNum: 已使用量	Integer
     * [{
     * rightsCode	权益编码	String	权益编码
     * rightsName	权益名称	String
     * rightsConsType	权益构成类型	Integer	1、单权益，2、权益包
     * faceValue	权益面额	Float	权益的展示额度信息
     * faceValueUnit	权益面额单位	String	权益展示额度的价值单位。如优惠金额的元、角、分；赠送流量的M、G；赠送时长的分、时等
     * rightsSpecCd	权益分类编码	Integer
     * rightsSpecName	权益分类名称	String
     * effDate	权益生效时间	yyyyMMDD
     * expDate	权益失效时间	yyyyMMDD
     * rightsRegion	权益适用地区	Intger[]	地区ID列表,只到第二级或第三级地区，使用集团公共管理权益编码（一般仅有一个地区编码）
     * totalNum	权益总量	Intger	权益总量
     * remainNum	权益剩余量   Intger	权益未分配数量
     * }]
     * }
     */
    public Map<String, Object> rightsDetail(String rightsCode) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", 1);
        map.put("pageSize", 1);
        map.put("rightsReqParam", rightsCode);
        map.put("rightsReqParamType", 1);
        String url = "/rights/rightsQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        if (Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, Constants.RESULT_CODE_STR))) {
            String rightsReqParam = MapUtils.getString(map, "rightsReqParam");
            Integer rightsReqParamType = MapUtils.getInteger(map, "rightsReqParamType");
            if (rightsReqParamType == 1) {
                Map<String, Object> resultMap = (Map<String, Object>) MapUtils.getMap(turnMap, "result");
                List<Map<String, Object>> rightsList = (List<Map<String, Object>>) resultMap.get("rightsList");
                Map<String, Object> rights = null;
                for (int i = 0; i < rightsList.size(); i++) {
                    rights = rightsList.get(i);
                    String rightsCodeByList = MapUtils.getString(rights, "rightsCode");
                    if (!StringUtils.isEmpty(rightsCodeByList) && rightsCode.equals(rightsReqParam)) {
                        Integer usedNum = (Integer) redisUtil.getRedisForKey(Constants.REDIS_DEL_KEY + Constants.REDIS_RIGHTS_CODE + rightsCode);
                        if (usedNum == null) {
                            Map<String, Object> stockParam = new HashMap<>();
                            stockParam.put("rightsCode", rightsCode);
                            Map<String, Object> stockQryResultMap = this.rightsStockQry(stockParam);
                            if (Constants.CODE_SUCC.equals(MapUtils.getString(stockQryResultMap, Constants.RESULT_CODE_STR))) {
                                Map<String, Object> stockQryMap = (Map<String, Object>) MapUtils.getMap(stockQryResultMap, "result");
                                if (stockQryMap.get("usedNum") == null) {
                                    usedNum = 0;
                                } else {
                                    usedNum = MapUtils.getInteger(stockQryMap, "usedNum");
                                    usedNum = getActualExchange(rightsCode, usedNum);
                                    redisUtil.addRedisInfo(Constants.REDIS_DEL_KEY + Constants.REDIS_RIGHTS_CODE + rightsCode, usedNum, 1440);
                                }
                                rights.put("usedNum", usedNum);
                            }
                        } else {
                            rights.put("usedNum", usedNum);
                        }
                        break;
                    }
                }
                turnMap.remove("result");
                turnMap.put("result", rights);
            }
        }

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"effDate\":\"20200323\",\"expDate\":\"20200530\",\"faceValue\":100,\"faceValueUnit\":\"元\",\"remainNum\":2,\"rightsCode\":\"S202003240000000037\",\"rightsConsType\":1,\"rightsDesc\":\"1212\",\"rightsName\":\"音乐会入场券\",\"rightsRegion\":[8130000],\"rightsRel\":[],\"rightsSpecCd\":11,\"rightsSpecName\":\"电信自有权益\",\"totalNum\":13,\"usedNum\":11}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }

        return turnMap;
    }

    /**
     * 3.13.权益库存查询 权益库存查询接口
     *
     * @param map rightsCode 权益编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * rightsCode: 权益编码	String
     * rightsName: 权益名称	String
     * totalNum: 权益总量	Integer
     * usedNum: 已使用量	Integer
     * }
     */
    public Map<String, Object> rightsStockQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String rightsCode = MapUtils.getString(map, "rightsCode");
        if (StringUtils.isEmpty(rightsCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益编码为空");
            return turnMap;
        }
        String url = "/rights/rightsStockQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);

        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"totalNum\":20,\"usedNum\":20}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }


        return turnMap;
    }

    /**
     * 3.13.权益领取校验接口 用户权益领取校验接口
     *
     * @param map rightsCode 权益编码
     *            objType 领取对象类型 1：客户标识  2：手机号码
     *            objNbr 领取对象号码仅限电信手机号码或客户标识
     *            objArea 领取对象地区 使用公共管理区域编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> rightsGrantPreCheck(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String rightsCode = MapUtils.getString(map, "rightsCode");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        if (StringUtils.isEmpty(rightsCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        String url = "/rights/rightsGrantPreCheck";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            if (!Constants.CODE_SUCC.equals(MapUtils.getString(turnMap, "resultCode"))) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
                turnMap.put(Constants.RESULT_MSG_STR, "查询成功");
                String s = "{\"rightsCode\":\"S202003280000000006\",\"rightsName\":\"北京2日游\",\"totalNum\":20,\"usedNum\":20}";
                Map<String, Object> resultMap = JSON.parseObject(s, HashMap.class);
                turnMap.put("result", resultMap);
            }
        }


        return turnMap;
    }

    /**
     * 3.14.权益领取接口 用户权益领取接口
     *
     * @param map rightsCode 权益编码
     *            activityCode 活动编码 如果是活动领取则必填
     *            objType 领取对象类型 1：客户标识  2：手机号码
     *            objNbr 领取对象号码仅限电信手机号码或客户标识
     *            objArea 领取对象地区 使用公共管理区域编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * success	校验领取成功	boolean	成功：true，不成功：false
     * 下面是文旦会返回的，实际测试接口就返回 success
     * rightsInstList[] 权益实例列表
     * [{
     * rightsCode	权益编码	String
     * rightsName	权益名称	Strings
     * showAmount	显示额度	Strings 含额度单位，如5元、7折、1G等
     * rightsInstNbr	实例编码	String 实例化的权益必填
     * effDate	权益生效日期	String yyyymmdd
     * expDate	权益失效日期	String yyyymmdd
     * }]
     * }
     */
    public Map<String, Object> rightsGrant(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String rightsCode = MapUtils.getString(map, "rightsCode");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        if (StringUtils.isEmpty(rightsCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }

        String url = "/rights/rightsGrant";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }

    /**
     * 3.15.权益领取接口 此接口已经整合了校验，校验通过后直接领取权益
     *
     * @param map rightsCode 权益编码
     *            activityCode 活动编码 如果是活动领取则必填
     *            objType 领取对象类型 1：客户标识  2：手机号码
     *            objNbr 领取对象号码仅限电信手机号码或客户标识
     *            objArea 领取对象地区 使用公共管理区域编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * success	校验领取成功	boolean	成功：true，不成功：false
     * 下面是文旦会返回的，实际测试接口就返回 success
     * rightsInstList[] 权益实例列表
     * [{
     * rightsCode	权益编码	String
     * rightsName	权益名称	Strings
     * showAmount	显示额度	Strings 含额度单位，如5元、7折、1G等
     * rightsInstNbr	实例编码	String 实例化的权益必填
     * effDate	权益生效日期	String yyyymmdd
     * expDate	权益失效日期	String yyyymmdd
     * }]
     * }
     */
    public Map<String, Object> rightsGrantAndGrantPreCheck(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String rightsCode = MapUtils.getString(map, "rightsCode");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        if (StringUtils.isEmpty(rightsCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        Map<String, Object> checkMap = this.rightsGrantPreCheck(map);
        String resultCode = MapUtils.getString(checkMap, Constants.RESULT_CODE_STR);
        if (Constants.CODE_SUCC.equals(resultCode)) {
            String url = "/rights/rightsGrant";
            Map<String, Object> headerMap = new HashMap<>();
            turnMap = this.postHttp(url, map, headerMap);
            return turnMap;
        } else {
            return checkMap;
        }
    }

    /**
     * 3.16.已领权益查询接口  用户已领在用权益查询接口
     *
     * @param map objType 领取对象类型 1：客户标识  2：手机号码
     *            objNbr 领取对象号码仅限电信手机号码或客户标识
     *            objArea 领取对象地区 使用公共管理区域编码
     *            rightsInstNbr	实例编码 fei
     *            pageNum	当前页码	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	每页航速（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * totalNum	总数	Integer	符合条件的数据总量
     * rightsInstList[] 权益实例列表
     * [{
     * rightsCode	权益编码	String
     * rightsName	权益名称	String
     * rightsDesc	权益描述	String
     * showAmount	显示额度	String		含额度单位，如5元、7折、1G等
     * receiveDate	领取时间	String		yyyymmdd
     * rightsInstNbr	实例编码	String		实例化的权益必填
     * instStateCd	实例状态	Integer		0：已预占，1：待使用，2：使用，3：已失效（过期的）
     * effDate	权益生效日期	String		yyyymmdd
     * expDate	权益失效日期	String		yyyymmdd
     * }]
     * }
     */
    public Map<String, Object> rightsInstQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        if (pageNum == null && pageNum < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "页面错误");
            return turnMap;
        }
        if (pageSize == null && pageSize < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量错误");
            return turnMap;
        }
        String url = "/rights/rightsInstQry";
        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Object> interfaceMap = this.postHttp(url, map, headerMap);

        return interfaceMap;
    }

    /**
     * 3.17.权益核销校验接口  用户权益核销同步接口  该接口仅做校验，不做核销操作
     *
     * @param map rightsCode 权益编码
     *            rightsInstNbr	权益实例编码	有实例编码必填
     *            objType 领取对象类型 1：客户标识  2：手机号码
     *            objNbr 领取对象号码仅限电信手机号码或客户标识
     *            objArea 领取对象地区 使用公共管理区域编码
     *            useDate    核销时间		14	M	yyyymmddhh24miss
     *            orderDiscount	订单优惠信息	Object	n	C	订单优惠信息
     *            {
     *            orderNbr	订单流水	String 订单流水号
     *            channelId	渠道ID	String 订单受理的渠道、商户等
     *            channelName	渠道名称	String 订单受理的渠道、商户等
     *            channelArea	渠道地区	Integer 订单受理渠道所属地区
     *            staffId	受理员工ID	String 非自助受理需要填写
     *            staffName	受理员工Name	String 非自助受理需要填写
     *            orderCommitDate	订单受理时间	String yyyymmddhh24miss
     *            totalDiscount	总优惠金额	Integer 该优惠券产生的总优惠金额，单位：分
     *            discountItems	优惠明细项目	Object 优惠明细项目，类型是discountItem
     *            {
     *            itemObjType	项目对象类型	Integer 1、销售品，2、营销资源
     *            itemObjCode	项目对象编码	String 填写项目对象标识
     *            itemDiscount	项目优惠金额	Integer 本明细项优惠金额，单位：分
     *            }
     *            }
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> rightsUseCheck(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String rightsCode = MapUtils.getString(map, "rightsCode");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        String useDate = MapUtils.getString(map, "useDate");
        String rightsInstNbr = MapUtils.getString(map, "rightsInstNbr");
        if (StringUtils.isEmpty(rightsCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        if (StringUtils.isEmpty(useDate)) {
            turnMap.put(Constants.RESULT_MSG_STR, "核销时间为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(rightsInstNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益实例编码为空");
            return turnMap;
        }
        String url = "/rights/rightsUseCheck";
        Map<String, Object> headerMap = new HashMap<>();


//        Map<String,Object> map = new HashMap<String,Object>();

        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }

    /**
     * 3.18.权益核销通知接口 用户权益核销通知接口
     *
     * @param map rightsCode 权益编码
     *            rightsInstNbr	权益实例编码	有实例编码必填
     *            objType 领取对象类型 1：客户标识  2：手机号码
     *            objNbr 领取对象号码仅限电信手机号码或客户标识
     *            objArea 领取对象地区 使用公共管理区域编码
     *            useDate    核销时间		14	M	yyyymmddhh24miss
     *            orderDiscount	订单优惠信息	Object	n	C	订单优惠信息
     *            {
     *            orderNbr	订单流水	String 订单流水号
     *            channelId	渠道ID	String 订单受理的渠道、商户等
     *            channelName	渠道名称	String 订单受理的渠道、商户等
     *            channelArea	渠道地区	Integer 订单受理渠道所属地区
     *            staffId	受理员工ID	String 非自助受理需要填写
     *            staffName	受理员工Name	String 非自助受理需要填写
     *            orderCommitDate	订单受理时间	String yyyymmddhh24miss
     *            totalDiscount	总优惠金额	Integer 该优惠券产生的总优惠金额，单位：分
     *            discountItems	优惠明细项目	Object 优惠明细项目，类型是discountItem
     *            {
     *            itemObjType	项目对象类型	Integer 1、销售品，2、营销资源
     *            itemObjCode	项目对象编码	String 填写项目对象标识
     *            itemDiscount	项目优惠金额	Integer 本明细项优惠金额，单位：分
     *            }
     *            }
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> rightsUseNotice(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String rightsCode = MapUtils.getString(map, "rightsCode");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        String useDate = MapUtils.getString(map, "useDate");
        String rightsInstNbr = MapUtils.getString(map, "rightsInstNbr");
        if (StringUtils.isEmpty(rightsCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        if (StringUtils.isEmpty(useDate)) {
            turnMap.put(Constants.RESULT_MSG_STR, "核销时间为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(rightsInstNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益实例编码为空");
            return turnMap;
        }
        String url = "/rights/rightsUseNotice";
        Map<String, Object> headerMap = new HashMap<>();


        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }

    /**
     * 3.19.权益返销接口 用户权益返销接口  此接口暂未开放，只针对内部使用
     *
     * @param map rightsCode	权益编码	String
     *            rightsInstNbr	权益实例编码	String	有实例编码必填
     *            objType	核销对象类型	Integer	1：客户标识  2：手机号码
     *            objNbr	核销对象号码	String	仅限电信手机号码或客户标识
     *            objArea	核销对象地区	String	使用公共管理区域编码
     *            revokeDate	返销时间	String	yyyymmddhh24miss
     *            orderDiscount	订单优惠信息	Object	订单优惠信息
     *            {
     *            orderNbr	订单流水	String	订单流水号
     *            channelId	渠道ID	String	订单受理的渠道、商户等
     *            channelName	渠道名称	String	订单受理的渠道、商户等
     *            channelArea	渠道地区	Integer	订单受理渠道所属地区
     *            staffId	受理员工ID	String	非自助受理需要填写
     *            staffName	受理员工Name	String	非自助受理需要填写
     *            orderCacelDate	订单撤销时间	String	yyyymmddhh24miss
     *            }
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> rightsUseRevoke(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String rightsCode = MapUtils.getString(map, "rightsCode");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        String revokeDate = MapUtils.getString(map, "revokeDate");
        String rightsInstNbr = MapUtils.getString(map, "rightsInstNbr");
        if (StringUtils.isEmpty(rightsCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益编码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        if (StringUtils.isEmpty(revokeDate)) {
            turnMap.put(Constants.RESULT_MSG_STR, "返销时间为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(rightsInstNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "权益实例编码为空");
            return turnMap;
        }
        String url = "/rights/rightsUseRevoke";
        Map<String, Object> headerMap = new HashMap<>();


        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }

    /**
     * 3.20.权益领取记录查询接口
     *
     * @param map objType	领取对象类型	Integer	1：客户标识  2：手机号码
     *            objNbr	领取对象号码	String	仅限电信手机号码或客户标识
     *            objArea	领取对象地区	String	使用公共管理区域编码
     *            startDate	查询开始日期	String	开始日期yyyymmdd
     *            endDate	查询结束日期	String	结束日期yyyymmdd
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页航速（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * totalNum	总数	Integer	符合条件的数据总量
     * grantRecList[] 权益列表  领取记录列表
     * [{
     * grantSerialNbr	记录流水号	String
     * rightsCode	权益编码	String
     * rightsDesc	权益描述	String
     * rightsName	权益名称	String
     * grantStateCd	领取状态编码	Integer
     * grantStateName	领取状态中文	String
     * grantStateDate	领取状态时间	String	yyyymmddhh24miss
     * grantDate	领取时间	String	yyyymmddhh24miss
     * showAmount	显示额度	String	含额度单位，如5元、7折、1G等
     * grantSystemName	领取发起系统	String
     * grantAbnormalCode	领取异常编码	String
     * grantAbnormalDesc	领取异常描述	Integer
     * remarks	备注描述	String
     * }]
     * }
     */
    public Map<String, Object> rightsGrantHisQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (pageNum == null && pageNum < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "页面错误");
            return turnMap;
        }
        if (pageSize == null && pageSize < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量错误");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        String url = "/rights/rightsGrantHisQry";
        Map<String, Object> headerMap = new HashMap<>();


        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }

    /**
     * 3.21.权益核销记录查询接口
     *
     * @param map objType	领取对象类型	Integer	1：客户标识  2：手机号码
     *            objNbr	领取对象号码	String	仅限电信手机号码或客户标识
     *            objArea	领取对象地区	String	使用公共管理区域编码
     *            startDate	查询开始日期	String	开始日期yyyymmdd
     *            endDate	查询结束日期	String	结束日期yyyymmdd
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页航速（分页查询条件）、必填
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * totalNum	总数	Integer	符合条件的数据总量
     * useRecList[] 权益实例列表
     * [{
     * rightsCode	权益编码	String
     * rightsInstNbr	权益实例编码	String
     * rightsDesc	权益描述	String
     * rightsName	权益名称	String
     * useStateCd	核销状态编码	Integer
     * useStateName	核销状态中文	String
     * useStateDate	核销状态时间	String	yyyymmddhh24miss
     * useDate	核销时间	String	yyyymmddhh24miss
     * showAmount	显示额度	String	含额度单位，如5元、7折、1G等
     * useSystemName	核销发起系统	String
     * useOrderNbr	核销订单流水	String
     * useDiscountAmount	核销优惠总额	Integer	单位：分
     * remarks	备注描述	String
     * }]
     * }
     */
    public Map<String, Object> rightsUseHisQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (pageNum == null && pageNum < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "页面错误");
            return turnMap;
        }
        if (pageSize == null && pageSize < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量错误");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        String url = "/rights/rightsUseHisQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }

    /**
     * 3.22.权益返销记录查询接口
     *
     * @param map objType	领取对象类型	Integer	1：客户标识  2：手机号码
     *            objNbr	领取对象号码	String	仅限电信手机号码或客户标识
     *            objArea	领取对象地区	String	使用公共管理区域编码
     *            startDate	查询开始日期	String	开始日期yyyymmdd
     *            endDate	查询结束日期	String	结束日期yyyymmdd
     *            pageNum	当前页码	Integer	当前页面（分页查询条件）、必填
     *            pageSize	每页行数	Integer	每页航速（分页查询条件）、必填 权益编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     * result{
     * totalNum	总数	Integer	符合条件的数据总量
     * useRecList[] 权益实例列表
     * [{
     * rightsCode	权益编码	String
     * rightsInstNbr	权益实例编码	String
     * rightsDesc	权益描述	String
     * rightsName	权益名称	String
     * revokeStateCd	核销状态编码	Integer
     * revokeStateName	核销状态中文	String
     * revokeStateDate	核销状态时间	String	yyyymmddhh24miss
     * revokeDate	核销时间	String	yyyymmddhh24miss
     * showAmount	显示额度	String	含额度单位，如5元、7折、1G等
     * revokeSystemName	返销发起系统	String
     * useOrderNbr	核销订单流水	String
     * useDiscountAmount	核销优惠总额	Integer	单位：分
     * remarks	备注描述	String
     * }]
     * }
     */
    public Map<String, Object> rightsUseRevokeHisQry(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String objNbr = MapUtils.getString(map, "objNbr");
        Integer objType = MapUtils.getInteger(map, "objType");
        String objArea = MapUtils.getString(map, "objArea");
        Integer pageNum = MapUtils.getInteger(map, "pageNum");
        Integer pageSize = MapUtils.getInteger(map, "pageSize");
        if (pageNum == null && pageNum < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "页面错误");
            return turnMap;
        }
        if (pageSize == null && pageSize < 1) {
            turnMap.put(Constants.RESULT_MSG_STR, "每页数量错误");
            return turnMap;
        }
        if (StringUtils.isEmpty(objNbr)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象号码为空");
            return turnMap;
        }
        if (StringUtils.isEmpty(objArea)) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象地区");
            return turnMap;
        }
        if (objType == null) {
            turnMap.put(Constants.RESULT_MSG_STR, "领取对象类型");
            return turnMap;
        }
        String url = "/rights/rightsUseRevokeHisQry";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }

    /**
     * 3.23.活动结束同步接口
     *
     * @param map activityCode 活动编码
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> activityTerminate(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String activityCode = MapUtils.getString(map, "activityCode");
        if (StringUtils.isEmpty(activityCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动编码不能为空");
            return turnMap;
        }

        String url = "/activity/activityTerminate";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }


    /**
     * 3.24.活动修改同步接口
     *
     * @param map activityCode	活动编码	String  每个活动需要唯一，按照此标识查询并修改活动信息
     *            acivityName	活动名称	String
     *            activityDesc	活动描述	String
     *            startDate	活动开始时间	String  yyyymmddhh24miss，已开始的活动不支持修改开始时间
     *            endDate	活动结束时间	String  yyyymmddhh24miss，已开始的活动结束日期不可小于当天
     *            rightsRel	活动关联权益	[]  类型为rightsRelObj
     *            <p>
     *            rightsRelObj内容描述：
     *            [
     *            rightsCode	权益编码	String  活动开始后，不可新增/减少活动权益
     *            useNum	使用数量	Integer 活动开始后，变更后的使用数量只能比现有数量大
     *            ]
     * @return resultCode POR-0000 表示成功，其它为失败
     * resultMsg 错误信息
     */
    public Map<String, Object> activityModify(Map<String, Object> map) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String activityCode = MapUtils.getString(map, "activityCode");
        if (StringUtils.isEmpty(activityCode)) {
            turnMap.put(Constants.RESULT_MSG_STR, "活动编码不能为空");
            return turnMap;
        }

        String url = "/activity/activityModify";
        Map<String, Object> headerMap = new HashMap<>();
        turnMap = this.postHttp(url, map, headerMap);
        return turnMap;
    }


    /**
     * 访问权益接口 http 请求 post 权益接口使用
     *
     * @param url        地址
     * @param params     参数
     * @param headersMap header
     * @return String 类型
     */
    private Map<String, Object> postHttp(String url, Map<String, Object> params, Map<String, Object> headersMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
        String isDebugStr = (String) propertyToRedis.getPropertyValue("is_debug_qy");
        boolean isDebug = Boolean.valueOf(isDebugStr);
        if (isDebug) {
            return turnMap;
        }
        String serviceUrl = (String) propertyToRedis.getPropertyValue("service.url");
        params.put("systemId", ServiceParam.SERVICE_SYSTEM_ID);
        //流水号
//        String requestTime = DateUtil.sdfSs.format(new Date());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Toolkit.FMT_DATE);
        String requestTime = dateTimeFormatter.format(now);
        String incr = redisUtil.incrAtomic(Constants.REDIS_QY_SERIAL_NBR + requestTime, 1, 4, TimeUnit.SECONDS);
        String reqServiceId = Constants.REDIS_QY_SERIAL_NBR + requestTime + incr;
        params.put("serialNbr", reqServiceId);
        if (headersMap == null) {
            headersMap = new HashMap();
        }
        headersMap.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        String result = restTemplateUtil.postHttp(serviceUrl + url, params, headersMap);
        if (!JsonUtil.isValidJson(result)) {
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_ANALYSIS);
            turnMap.put(Constants.RESULT_MSG_STR, "接口返回信息异常");
            log.error("ERROR:调用接口[" + url + "] 返回JSON格式异常");
        } else {
            Map<String, Object> interfaceMap = JsonUtil.toObject(result, Map.class);
            String resultCode = MapUtils.getString(interfaceMap, Constants.RESULT_CODE_STR, "");
            if (!"0".equals(resultCode)) {
                turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_PREFIX_FAIL + resultCode.trim());
                String message = (String) propertyToRedis.getPropertyValue(Constants.CODE_PREFIX_FAIL + resultCode.trim());
                if (!StringUtils.isEmpty(message)) {
                    turnMap.put(Constants.RESULT_MSG_STR, message);
                } else {
                    turnMap.put(Constants.RESULT_MSG_STR, interfaceMap.get(Constants.RESULT_MSG_STR));
                }
                return turnMap;
            }
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "调用接口成功");
            turnMap.put("result", interfaceMap.get("data"));
        }
        return turnMap;
    }

    public Integer getActualExchange(String rightsCode, Integer usedNum) {
        Integer actualExchange = usedNum;
        if ("7100005467".equals(rightsCode) || "7100005470".equals(rightsCode)) {
            actualExchange = usedNum - 1000000;
        }
        if (actualExchange <= 0) {
            actualExchange = 0;
        }
        return actualExchange;
    }
}
