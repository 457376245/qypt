package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.bean.StaffInfo;
import com.web.common.Constants;
import com.web.dao.ActivityDao;
import com.web.dao.SecKillDao;
import com.web.model.*;
import com.web.thirdinterface.ThirdQYInterface;
import com.web.util.*;
import com.web.util.common.CommonParams;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 秒杀活动配置
 *
 * @author
 * @create 2020-04-05
 **/
@Service("com.web.bmo.SeckillBmoImpl")
public class SeckillBmoImpl implements SeckillBmo {
    //商品列表缓存key
    private static final String SECKILL_GOODS = "SECKILL_GOODS";
    //秒杀活动的列表
    public static final String Activity_ID_SET = "ACTIVITY_ID_SET";

    public static final String SECKILL_INVENTORY = "SECKILL_INVENTORY";
    @Resource
    private ThirdQYInterface thirdInterface;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private SecKillDao secKillDao;
    @Resource(name = "RedisUtil")
    private RedisUtil redisUtil;
    @Resource(name = "com.web.bmo.TaskCenterBmoImpl")
    private TaskCenterBmo taskCenterBmo;
    private static Logger log = LoggerFactory.getLogger(SeckillBmoImpl.class);
    @Override
    public Map qrySeckillList(Map param) throws Exception{
        Map resultMap=new HashMap();
        int pageNum= MapUtil.asInt(param,"page");
        int pageSize=MapUtil.asInt(param,"limit");
        param.put("parentId","0");//取父节点
        if(param.get("statusCd")!=null){
            String statusCd=(String)param.remove("statusCd");
            switch(statusCd){
                case "1" ://进行中
                    param.put("nowTime","1");
                    param.put("statusCd", Constants.STATUS_EFFECT);
                    break;
                case "2" ://未开始
                    param.put("beforeStartTime","1");
                    param.put("statusCd", Constants.STATUS_EFFECT);
                    break;
                case "3" ://未发布
                    param.put("statusCd", Constants.STATUS_NOTPUBLISH);
                    break;
                case "4" ://已结束
                    param.put("afterEndTime","1");
                    param.put("statusCd", Constants.STATUS_EFFECT);
                    break;
                case "5" ://已下架
                    param.put("statusCd", Constants.STATUS_DROP);
                    break;
                default :
            }
        }
        param.put("activityMode","2");
        PageHelper.startPage(pageNum, pageSize);
        List<CoActivity> list=activityDao.queryActivityList(param);
        PageInfo<CoActivity> info = new PageInfo<>(list);
        for(CoActivity coActivity:info.getList()){
            //参与用户，需要统计子节点的
            Integer inUserCount=activityDao.querySekillUserCount(coActivity.getActivityId());
            coActivity.setInUserCount(inUserCount);
            String statusCd=coActivity.getStatusCd();
            Date startTime=coActivity.getStartTime();
            Date endTime=coActivity.getEndTime();
            Date nowTime=new Date();
            if(Constants.STATUS_NOTPUBLISH.equals(statusCd)) {
                coActivity.setStatusCd("3");
                coActivity.setBuyCount(0);
            }else if(Constants.STATUS_DROP.equals(statusCd)){
                coActivity.setStatusCd("5");
                Integer buyCount=activityDao.queryProBuyCount(coActivity.getActivityId());
                coActivity.setBuyCount(buyCount);
            }else{
                if(nowTime.compareTo(startTime)<0){
                    coActivity.setStatusCd("2");
                    coActivity.setBuyCount(0);
                }else if(nowTime.compareTo(endTime)>0){
                    Integer buyCount=activityDao.queryProBuyCount(coActivity.getActivityId());
                    coActivity.setBuyCount(buyCount);
                    coActivity.setStatusCd("4");
                }else{
                    coActivity.setStatusCd("1");
                    Integer buyCount=activityDao.queryProBuyCount(coActivity.getActivityId());
                    coActivity.setBuyCount(buyCount);
                }
            }
            coActivity.setStartTimeStr(DateUtil.format(startTime,"yyyy/MM/dd"));
            coActivity.setEndTimeStr(DateUtil.format(endTime,"yyyy/MM/dd"));
        }
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }
    /**
     * 活动详情相关数据
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public Map qrySeckillAct(Map param) throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"查询秒杀活动成功");
        String[] fields = {"activityId"};
        Map<String, Object> validationResult = FieldValidation.validation(param, fields);
        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
            return resultMap;
        }
        List<CoActivity> coActivities=activityDao.queryActivityList(param);
        if(coActivities==null||coActivities.size()==0){
            return resultMap;
        }
        CoActivity coActivity=coActivities.get(0);
        if("1".equals(coActivity.getSeckillType())) {
            coActivity.setStartTimeStr(DateUtil.format(coActivity.getStartTime(), "yyyy年MM月dd日"));
        }
        coActivity.setShowTimeStr(DateUtil.format(coActivity.getShowTime(),DateUtil.DATE_FORMATE_STRING_A));
        resultMap.put("coActivity",coActivity);
        Map sonParam=new HashMap();
        sonParam.put("parentId",param.get("activityId"));
        List<CoActivity> sonActivities=activityDao.queryActivityList(sonParam);
        //封装秒杀节点
        if("1".equals(coActivity.getSeckillType())) {//时会场
            Map continueTime=DateUtil.getDateTimeDifference(sonActivities.get(0).getEndTime(),sonActivities.get(0).getStartTime());
            String day=continueTime.get("day").toString();
            String hour=continueTime.get("hour").toString();
            String min=continueTime.get("min").toString();
            int continueSpace=Integer.parseInt(day)*24*60+Integer.parseInt(hour)*60+Integer.parseInt(min);
            String endTimeStr=String.valueOf(continueSpace);
            coActivity.setEndTimeStr(endTimeStr);//把持续时间暂时设置在父级这个属性中
            for(CoActivity sonAct:sonActivities){
                sonAct.setStartTimeStr(String.valueOf(DateUtil.getHour(sonAct.getStartTime())));
            }
        }else{
            String continueTime=DateUtil.format(sonActivities.get(0).getStartTime(),"HH:mm:ss")+" - "+DateUtil.format(sonActivities.get(0).getEndTime(),"HH:mm:ss");
            String endTimeStr=String.valueOf(continueTime);
            coActivity.setEndTimeStr(endTimeStr);//把持续时间暂时设置在父级这个属性中
            for(CoActivity sonAct:sonActivities){
                sonAct.setStartTimeStr(String.valueOf(DateUtil.format(sonAct.getStartTime(),"yyyy年MM月dd日")));
            }
        }
        resultMap.put("sonActivities", sonActivities);
        if(!StringUtil.isEmptyStr(coActivities.get(0).getShowRuleMode())) {
            Map ruleParam=new HashMap();
            ruleParam.putAll(param);
            ruleParam.remove("statusCd");
            List<CoActRule> coActRules = activityDao.queryActRules(ruleParam);
            resultMap.put("coActRules", coActRules);
        }
        List<CoActivityImg> coActivityImgs=activityDao.getCoActivityImgList(param);
        resultMap.put("coActivityImgs",coActivityImgs);
        param.put("ruleType","8");//区域规则数据
        List<CoActRuleData> coActRuleDatas=activityDao.queryActRuleData(param);
        resultMap.put("coActRuleDatas",coActRuleDatas);
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        return resultMap;
    }

    @Override
    public Map qrySeckillPro(Map param) throws Exception {
        Map resultMap=new HashMap();
        List<SecKill> list=secKillDao.querySecKill(param);
        List<Map> datas=new ArrayList<>();
        List<String>  times=new ArrayList<>();
        String statusCd="101";
        for(SecKill secKill:list){
            Map<String,Object> secKills=new HashMap<>();
            String time="";
            if("1".equals(secKill.getSeckillType())){//时会场
               time=String.valueOf(DateUtil.getHour(secKill.getStartTime()));

            }else{//日会场
                time=String.valueOf(DateUtil.format(secKill.getStartTime(),"yyyy/MM/dd"));
            }
            if(!times.contains(time)){
                times.add(time);
                List<SecKill> slist=new ArrayList<>();
                long actId=secKill.getActivityId();
                for(SecKill secKillSon:list){
                    if(!StringUtil.isEmptyStr(secKillSon.getProductCode())&&secKillSon.getActivityId()==actId){
                        slist.add(secKillSon);
                    }
                }
                secKills.put("type",secKill.getSeckillType());
                secKills.put("activityId",secKill.getActivityId());
                secKills.put("time",time);
                secKills.put("slist",slist);
                statusCd=secKill.getStatusCd();
                datas.add(secKills);
            }
        }
        resultMap.put("data",datas);
        resultMap.put("statusCd",statusCd);
        return resultMap;
    }
    @Override
    public Map saveSeckill(Map param) throws Exception {
        Map resultMap=new HashMap();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"新增秒杀商品失败");
        String statusCd=MapUtil.asStr(param,"statusCd");
        String parentId=MapUtil.asStr(param,"parentId");
        if(StringUtil.isEmptyStr(parentId)){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
            return resultMap;
        }
        if(param.get("staffInfo")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少登录的参数");
            return resultMap;
        }
        StaffInfo staffInfo=(StaffInfo)param.get("staffInfo");
        if(StringUtil.isEmptyStr(statusCd)){
            statusCd="103";
        }
        Map sonParam=new HashMap();
        sonParam.put("parentId",parentId);
        List<CoActivity> sonActivities=activityDao.queryActivityList(sonParam);
        if(param.get("addPro")!=null) {
            List<Map> addPro = (List<Map>) param.get("addPro");
            if (addPro != null && addPro.size() > 0) {
                for (Map mm : addPro) {
                    //mm.put("parentId",parentId);
                    int count = secKillDao.queryIsExist(mm);
                    if (count > 0) {
                        resultMap.put(Constants.RESULT_MSG_STR, "配置的权益商品重复，请重新选择");
                        return resultMap;
                    }
                }
            }
        }
        if("101".equals(statusCd)) {//发布
            CoActivity setAct=new CoActivity();
            for(CoActivity coActivity:sonActivities){
                setAct.setStatusCd(statusCd);
                setAct.setActivityId(coActivity.getActivityId());
                setAct.setEditStaffId(staffInfo.getStaffId());
                activityDao.updateActivity(setAct);
                SecKill kill=new SecKill();
                kill.setActivityId(coActivity.getActivityId());
                kill.setStatusCd(statusCd);
                kill.setEditStaffId(staffInfo.getStaffId());
                secKillDao.updateSeckill(kill);

            }
            //设置状态为发布
            CoActivity upCoAct = new CoActivity();
            upCoAct.setActivityId(Long.parseLong(parentId));
            upCoAct.setStatusCd(statusCd);
            activityDao.updateActivity(upCoAct);
            CoActRule coActRule = new CoActRule();
            coActRule.setActivityId(upCoAct.getActivityId());
            coActRule.setStatusCd(statusCd);
            activityDao.updateActRule(coActRule);
            CoActivityImg coActivityImg = new CoActivityImg();
            coActivityImg.setActivityId(upCoAct.getActivityId());
            coActivityImg.setStatusCd(statusCd);
            activityDao.updateActImg(coActivityImg);
            //设置状态为发布
        }
        if(param.get("addPro")!=null){
            List<Map> addPro=(List<Map>)param.get("addPro");
            if(addPro!=null&&addPro.size()>0){
                for(Map mm:addPro){
                    SecKill secKill=new SecKill();
                    secKill.setActivityId(Long.parseLong(mm.get("activityId").toString()));
                    secKill.setProductId("0");
                    secKill.setProductCode(mm.get("productCode").toString());
                    secKill.setProductTitle(mm.get("productTitle").toString());
                    secKill.setProductTitleSub(mm.get("productTitle").toString());
                    secKill.setBuyCount(Integer.parseInt(mm.get("buyCount").toString()));
                    secKill.setProdSupplier(mm.get("prodSupplier").toString());
                    secKill.setOldPrice(mm.get("oldPrice").toString());
                    secKill.setNewPrice(mm.get("newPrice").toString());
                    secKill.setProductDesc(mm.get("productDesc").toString());
                    secKill.setProductStock(Integer.parseInt(mm.get("productStock").toString()));
                    secKill.setProductTotal(secKill.getProductStock());
                    secKill.setVersion(0l);
                    secKill.setSortSeq(1);
                    secKill.setStatusCd(statusCd);//秒杀商品状态都是在用，活动状态区分发布和未发布
                    secKill.setEditStaffId(staffInfo.getStaffId());
                    secKill.setStaffId(staffInfo.getStaffId());
                    secKillDao.insertSeckill(secKill);
                }
            }
        }
        if(param.get("delPro")!=null){
            List<Map> delPro=(List<Map>)param.get("delPro");
            if(delPro!=null&&delPro.size()>0){
                for(Map mm:delPro){
                    secKillDao.deleteSeckill(mm);
                }
            }
        }
        if(param.get("editPro")!=null){
            List<Map> editPro=(List<Map>)param.get("editPro");
            if(editPro!=null&&editPro.size()>0){
                for(Map mm:editPro) {
                    SecKill secKill = new SecKill();
                    secKill.setActivityId(Long.parseLong(mm.get("activityId").toString()));
                    secKill.setProductCode(mm.get("productCode").toString());
                    secKill.setBuyCount(Integer.parseInt(mm.get("buyCount").toString()));
                    secKill.setOldPrice(mm.get("oldPrice").toString());
                    secKill.setNewPrice(mm.get("newPrice").toString());
                    secKill.setEditStaffId(staffInfo.getStaffId());
                    secKillDao.updateSeckill(secKill);
                }
            }
        }
        if("101".equals(statusCd)){//发布时校验
            List<Map> check=secKillDao.queryIsAllSet(Long.parseLong(parentId));
            if(check==null&&check.size()==0){
                resultMap.put(Constants.RESULT_MSG_STR, "有未配置商品的秒杀节点，不能发布");
                throw new RuntimeException("有未配置商品的秒杀节点，不能发布");
            }
            Map checkMap=new HashMap();
            for(Map mm:check){
                checkMap.put(mm.get("ACTIVITY_ID").toString(),mm.get("COUNT"));
            }
            for(CoActivity coActivity:sonActivities){
                if(checkMap.get(String.valueOf(coActivity.getActivityId()))==null){
                    throw new RuntimeException("有未配置商品的秒杀节点，不能发布");
                }
                if(coActivity.getStartTime().compareTo(new Date())<0){
                    throw new RuntimeException("活动开始时间不得小于当前时间");
                }
            }
            syncActivity(Long.parseLong(parentId));//活动同步，接口失败回滚数据
            reflashRedis();//刷新缓存
        }
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        return resultMap;
    }
    @Override
    public Map saveSeckillStock(Map param) throws Exception {
        Map resultMap=new HashMap();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"修改商品库存失败");
        String parentId=MapUtil.asStr(param,"parentId");
        if(StringUtil.isEmptyStr(parentId)){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
            return resultMap;
        }
        if(param.get("staffInfo")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少登录的参数");
            return resultMap;
        }
        StaffInfo staffInfo=(StaffInfo)param.get("staffInfo");
        if(param.get("editPro")!=null){
            List<Map> editPro=(List<Map>)param.get("editPro");
            if(editPro!=null&&editPro.size()>0){
                List<Map<String,Object>> rightsRel=new ArrayList<>();
                for(Map mm:editPro) {
                    String seckillId=mm.get("seckillId").toString();
                    Map sonParam=new HashMap();
                    sonParam.put("activityId",parentId);
                    sonParam.put("seckillId",Long.parseLong(seckillId));
                    List<SecKill> secKills=secKillDao.querySecKill(sonParam);
                    if(secKills==null||secKills.size()==0){
                        resultMap.put(Constants.RESULT_MSG_STR, "未找到权益商品["+mm.get("seckillId").toString()+"]"+mm.get("productTitle").toString());
                        return resultMap;
                    }
                    if(secKills.get(0).getProductStock()>0){
                        resultMap.put(Constants.RESULT_MSG_STR, "权益商品["+mm.get("seckillId").toString()+"]"+mm.get("productTitle").toString()+"库存为0时才可追加");
                        return resultMap;
                    }
                    boolean isQrySuccess=false;
                    String productCode=mm.get("productCode").toString();
                    int productStock=Integer.parseInt(mm.get("productStock").toString());
                    //封装修改同步数据
                    Map<String,Object> secPro=new HashMap<String,Object>();
                    secPro.put("rightsCode",secKills.get(0).getProductCode());
                    secPro.put("useNum",secKills.get(0).getProductTotal()+productStock);
                    rightsRel.add(secPro);
                    //封装修改同步数据
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("rightsCode",productCode);
                    Map<String, Object> rightsMap = thirdInterface.rightsStockQry(map);
                    if(rightsMap != null && rightsMap.size() > 0){
                        if(Constants.CODE_SUCC.equals(String.valueOf(rightsMap.get(CommonParams.RESULT_CODE_STR)))){
                            Map<String, Object> dataMap = (Map<String, Object>) rightsMap.get("result");
                            if(dataMap != null && dataMap.size() > 0){
                                isQrySuccess=true;
                                int leftStock=MapUtils.getInteger(dataMap,"totalNum")-MapUtils.getInteger(dataMap,"usedNum");
                                if(leftStock<productStock){
                                    resultMap.put(Constants.RESULT_MSG_STR, "权益商品["+mm.get("seckillId").toString()+"]"+mm.get("productTitle").toString()+"剩余库存不足");
                                    return resultMap;
                                }
                            }
                        }
                    }
                    if(!isQrySuccess){
                        resultMap.put(Constants.RESULT_MSG_STR, "权益商品["+mm.get("seckillId").toString()+"]"+mm.get("productTitle").toString()+"库存查询失败");
                        return resultMap;
                    }
                    SecKill secKill = new SecKill();
                    secKill.setActivityId(Long.parseLong(mm.get("activityId").toString()));
                    secKill.setProductCode(mm.get("productCode").toString());
                    secKill.setProductTotal(productStock);
                    secKill.setEditStaffId(staffInfo.getStaffId());
                    int upResult=secKillDao.updateSeckill(secKill);
                    if(upResult>0){
                        activityModify(Long.parseLong(parentId),rightsRel);//修改接口同步
                        reflashRedis();//更新缓存前端重新获取商品和库存
                        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
                    }
                }
            }
        }
        return resultMap;
    }
    /**
     * 活动修改
     * @param activityId
     * @throws Exception
     */
    private void activityModify(long activityId,List<Map<String,Object>> rightsRel)throws Exception{
        Map<String,Object> parentParam=new HashMap<String,Object>();
        parentParam.put("activityId",activityId);
        List<CoActivity> activities=activityDao.queryActivityList(parentParam);
        if(activities!=null&&activities.size()>0){
            CoActivity coActivity=activities.get(0);
            String activityCode=coActivity.getActivityCd();
            String acivityName=coActivity.getActivityTitle();
            String activityTypeCode="2";
            String activityTypeName="秒杀";
            String activityDesc=coActivity.getActivityDesc();
            String createdate=DateUtil.format(new Date(),DateUtil.DATE_FORMATE_STRING_DEFAULT);
            String startDate=DateUtil.format(coActivity.getStartTime(),DateUtil.DATE_FORMATE_STRING_DEFAULT);
            String endDate=DateUtil.format(coActivity.getEndTime(),DateUtil.DATE_FORMATE_STRING_DEFAULT);
            Map<String,Object> params=new HashMap<String,Object>();
            params.put("activityCode",activityCode);
            params.put("acivityName",acivityName);
            params.put("activityTypeCode",activityTypeCode);
            params.put("activityTypeName",activityTypeName);
            if(StringUtil.isEmptyStr(activityDesc)){
                activityDesc=acivityName;
            }
            params.put("activityDesc",activityDesc);
            params.put("createdate",createdate);
            params.put("startDate",startDate);
            params.put("endDate",endDate);
            params.put("rightsRel",rightsRel);
            Map<String,Object> resultMap=thirdInterface.activityModify(params);
            if (!Constants.CODE_SUCC.equals(resultMap.get("resultCode"))) {
                throw new RuntimeException("同步修改活动接口调用失败，修改失败");
            }
        }else{
            throw new RuntimeException("同步修改活动接口调用失败，修改失败");
        }
    }
    /**
     * 活动同步
     * @param activityId
     * @throws Exception
     */
    private void syncActivity(long activityId)throws Exception{
        Map<String,Object> sonParam=new HashMap<String,Object>();
        sonParam.put("activityId",activityId);
        List<CoActivity> activities=activityDao.queryActivityList(sonParam);
        if(activities!=null&&activities.size()>0){
            CoActivity coActivity=activities.get(0);
            String activityCode=coActivity.getActivityCd();
            String acivityName=coActivity.getActivityTitle();
            String activityTypeCode="2";
            String activityTypeName="秒杀";
            String activityDesc=coActivity.getActivityDesc();
            String createdate=DateUtil.format(new Date(),DateUtil.DATE_FORMATE_STRING_DEFAULT);
            String startDate=DateUtil.format(coActivity.getStartTime(),DateUtil.DATE_FORMATE_STRING_DEFAULT);
            String endDate=DateUtil.format(coActivity.getEndTime(),DateUtil.DATE_FORMATE_STRING_DEFAULT);
            Map<String,Object> param=new HashMap<String,Object>();
            param.put("activityId",activityId);
            param.put("ruleType","8");
            List<CoActRuleData> areas=activityDao.queryActRuleData(param);
            List<Integer> areaRel=new ArrayList<>();
            for(CoActRuleData coActRuleData:areas){
                areaRel.add(Integer.parseInt(coActRuleData.getRuleData()));
            }
            List<SecKill> secKills=secKillDao.querySecKill(param);
            List<Map<String,Object>> rightsRel=new ArrayList<>();
            for(SecKill secKill:secKills){
               Map<String,Object> secPro=new HashMap<String,Object>();
                secPro.put("rightsCode",secKill.getProductCode());
                secPro.put("useNum",secKill.getProductTotal());
                rightsRel.add(secPro);
            }
            Map<String,Object> params=new HashMap<String,Object>();
            params.put("activityCode",activityCode);
            params.put("acivityName",acivityName);
            params.put("activityTypeCode",activityTypeCode);
            params.put("activityTypeName",activityTypeName);
            if(StringUtil.isEmptyStr(activityDesc)){
                activityDesc=acivityName;
            }
            params.put("activityDesc",activityDesc);
            params.put("createdate",createdate);
            params.put("startDate",startDate);
            params.put("endDate",endDate);
            params.put("areaRel",areaRel);
            params.put("rightsRel",rightsRel);
            Map<String,Object> resultMap=thirdInterface.activitySync(params);
            if (!Constants.CODE_SUCC.equals(resultMap.get("resultCode"))) {
                throw new RuntimeException("同步活动接口调用失败，不能发布");
            }
        }else{
            throw new RuntimeException("同步活动查询失败，不能发布");
        }
    }
    @Override
    public Map deleteAct(Map param) throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"删除/下架秒杀活动失败");
        String[] fields = {"activityId","statusCd"};
        Map<String, Object> validationResult = FieldValidation.validation(param, fields);
        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
            return resultMap;
        }
        if(param.get("staffInfo")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少登录的参数");
            return resultMap;
        }
        StaffInfo staffInfo=(StaffInfo)param.get("staffInfo");
        String statusCd=MapUtil.asStr(param,"statusCd");
        Long activityId=Long.parseLong(MapUtil.asStr(param,"activityId"));
        Map<String,Object> actParam=new HashMap();
        actParam.put("activityId",activityId);
        List<CoActivity> list=activityDao.queryActivityList(actParam);
        if("103".equals(statusCd)){//前端状态未开始的活动，需要再次校验是否未开始
            if(new Date().compareTo(list.get(0).getStartTime())>0){
                statusCd="104";
            }
        }
        CoActivity upCoAct = new CoActivity();
        upCoAct.setActivityId(activityId);
        upCoAct.setEditStaffId(staffInfo.getStaffId());
        upCoAct.setStatusCd(statusCd);
        activityDao.updateActivity(upCoAct);
        CoActRule coActRule = new CoActRule();
        coActRule.setActivityId(upCoAct.getActivityId());
        coActRule.setStatusCd(statusCd);
        activityDao.updateActRule(coActRule);
        CoActivityImg coActivityImg = new CoActivityImg();
        coActivityImg.setActivityId(upCoAct.getActivityId());
        coActivityImg.setStatusCd(statusCd);
        activityDao.updateActImg(coActivityImg);
        Map sonParam=new HashMap();
        sonParam.put("parentId",activityId);
        List<CoActivity> sonActivities=activityDao.queryActivityList(sonParam);
        if(sonActivities!=null&&sonActivities.size()>0){
            CoActivity setAct=new CoActivity();
            for(CoActivity coActivity:sonActivities){
                setAct.setStatusCd(statusCd);
                setAct.setActivityId(coActivity.getActivityId());
                setAct.setEditStaffId(staffInfo.getStaffId());
                activityDao.updateActivity(setAct);
                SecKill kill=new SecKill();
                kill.setActivityId(coActivity.getActivityId());
                kill.setEditStaffId(coActivity.getEditStaffId());
                kill.setStatusCd(statusCd);
                secKillDao.updateSeckill(kill);
            }
        }
        reflashRedis();//刷新缓存
        try {
            //删除活动接口，如果调用失败，需要等权益平台自动释放库存
            Map<String,Object> terminate =new HashMap<>();
            terminate.put("activityCode",list.get(0).getActivityCd());
            thirdInterface.activityTerminate(terminate);
        }catch (Exception e){
            log.error("活动删除失败："+list.get(0).getActivityCd());
        }
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        return resultMap;
    }

    @Override
    public Map editPro(Map param) throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"编辑秒杀活动失败");
        String[] fields = {"activityId","activityTitle","showTime","seckillType","continueTime","statusCd"};
        Map<String, Object> validationResult = FieldValidation.validation(param, fields);
        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
            return resultMap;
        }
        if(param.get("times")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
            return resultMap;
        }
        List<String> times=(List<String>)MapUtil.path(param,"times");
        if(times.size()==0){
            resultMap.put(Constants.RESULT_MSG_STR, "秒杀节点必须选择");
            return resultMap;
        }
        if(param.get("staffInfo")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少登录的参数");
            return resultMap;
        }
        if(param.get("areaRelRules")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
            return resultMap;
        }
        List<Map> areaRelRules=(List<Map>) param.get("areaRelRules");
        if(areaRelRules.size()==0) {
            resultMap.put(Constants.RESULT_MSG_STR, "请至少选择一个地市");
            return resultMap;
        }
        StaffInfo staffInfo=(StaffInfo)param.get("staffInfo");
        SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
        long activityId=Long.parseLong(MapUtil.asStr(param,"activityId"));
        String activityTitle=MapUtil.asStr(param,"activityTitle");
        String showTime=MapUtil.asStr(param,"showTime");
        String continueTime=MapUtil.asStr(param,"continueTime");
        String seckillType=MapUtil.asStr(param,"seckillType");
        String statusCd=MapUtil.asStr(param,"statusCd");//101正常，103待发布
        String ruleMode=MapUtil.asStr(param,"showRuleMode");
        String activityDesc=MapUtil.asStr(param,"activityDesc");
        String activityRule=MapUtil.asStr(param,"activityRule");
        activityRule=activityRule.replaceAll("&lt;","<");
        activityRule=activityRule.replaceAll("&gt;",">");
        Date showTimeDt=format.parse(showTime);
        CoActivity parCoActivity=new CoActivity();
        parCoActivity.setActivityId(activityId);
        parCoActivity.setActivityTitle(activityTitle);
        parCoActivity.setShowTime(showTimeDt);
        parCoActivity.setActivityDesc(activityDesc);
        parCoActivity.setStatusCd(statusCd);
        parCoActivity.setShowRuleMode(ruleMode);
        parCoActivity.setRuleMode(ruleMode);
        parCoActivity.setActivityRule(activityRule);
        parCoActivity.setActivityMode("2");
        parCoActivity.setSeckillType(seckillType);
        parCoActivity.setEditStaffId(staffInfo.getStaffId());
        if("1".equals(seckillType)){
            String startTime=MapUtil.asStr(param,"startTime");
            if(StringUtil.isEmptyStr(startTime)){
                resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
                return resultMap;
            }
            Date actStartTime=getParentStartTime(seckillType,times,startTime,continueTime);
            parCoActivity.setStartTime(actStartTime);
            parCoActivity.setEndTime(getParentEndTime(seckillType,times,startTime,continueTime));
        }else{
            Date actStartTime=getParentStartTime(seckillType,times,"",continueTime);
            parCoActivity.setStartTime(actStartTime);
            parCoActivity.setEndTime(getParentEndTime(seckillType,times,"",continueTime));
        }
        if(showTimeDt.compareTo(parCoActivity.getStartTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "展示时间不得大于活动开始时间");
            return resultMap;
        }
        if(parCoActivity.getStartTime().compareTo(parCoActivity.getEndTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "结束时间不得小于活动开始时间");
            return resultMap;
        }
        if(new Date().compareTo(parCoActivity.getStartTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "活动开始时间不得小于当前时间");
            return resultMap;
        }
        Map sonParam=new HashMap();
        sonParam.put("parentId",activityId);
        List<CoActivity> sonActivities=activityDao.queryActivityList(sonParam);
        if("101".equals(statusCd)){//发布时校验
            List<Map> check=secKillDao.queryIsAllSet(activityId);
            if(check==null&&check.size()==0){
                resultMap.put(Constants.RESULT_MSG_STR, "有未配置商品的秒杀节点，不能发布");
                return resultMap;
            }
            Map checkMap=new HashMap();
            for(Map mm:check){
                checkMap.put(mm.get("ACTIVITY_ID").toString(),mm.get("COUNT"));
            }
            for(CoActivity coActivity:sonActivities){
                if(checkMap.get(String.valueOf(coActivity.getActivityId()))==null){
                    resultMap.put(Constants.RESULT_MSG_STR, "有未配置商品的秒杀节点，不能发布");
                    return resultMap;
                }
            }
        }
        //比对秒杀节点开始
        List<String> addTime=new ArrayList<>();
        List<CoActivity> editTime=new ArrayList<>();
        List<Long> delTime=new ArrayList<>();
        if(sonActivities!=null&&sonActivities.size()>0){
            for(CoActivity sonAct:sonActivities){
                String time="";
                if("1".equals(seckillType)){
                    time=String.valueOf(DateUtil.getHour(sonAct.getStartTime()));
                }else{
                    time=DateUtil.format(sonAct.getStartTime(),"yyyy-MM-dd");
                }
                boolean isExist=false;
                for(String tt:times){
                    if(tt.equals(time)){
                        isExist=true;
                        CoActivity edit=new CoActivity();
                        edit.setActivityId(sonAct.getActivityId());
                        if("1".equals(seckillType)){
                            if(Integer.parseInt(time)<10){
                                time="0"+time;
                            }
                            String startTime=MapUtil.asStr(param,"startTime");
                            edit.setActivityTitle(activityTitle + "-" + time + "时");
                            edit.setStartTime(getStartTime(startTime+" "+time+":00:00"));
                            edit.setEndTime(getEndTime(edit.getStartTime(),continueTime));
                        }else{
                            String dayStart=continueTime.split("-")[0].trim();
                            String dayEnd=continueTime.split("-")[1].trim();
                            edit.setActivityTitle(activityTitle + "-" + time);
                            edit.setStartTime(getStartTime(time+" "+dayStart));
                            edit.setEndTime(getStartTime(time+" "+dayEnd));
                        }
                        editTime.add(edit);
                        break;
                    }
                }
                if(!isExist){//子节点被编辑删除
                    delTime.add(sonAct.getActivityId());
                }
            }
        }
        for(String tt:times){
            boolean isExist=false;
            if(sonActivities!=null&&sonActivities.size()>0) {
                for (CoActivity sonAct : sonActivities) {
                    String time="";
                    if("1".equals(seckillType)){
                        time=String.valueOf(DateUtil.getHour(sonAct.getStartTime()));
                    }else{
                        time=DateUtil.format(sonAct.getStartTime(),"yyyy-MM-dd");
                    }
                    if(tt.equals(time)) {
                        isExist = true;
                        break;
                    }
                }
            }
            if(!isExist){
                addTime.add(tt);
            }
        }
        //比对秒杀节点结束
        if(addTime!=null&&addTime.size()>0){
            if("101".equals(statusCd)){
                resultMap.put(Constants.RESULT_MSG_STR, "有未配置商品的秒杀节点，不能发布");
                return resultMap;
            }
        }
        if("101".equals(statusCd)) {
            if(sonActivities!=null&&sonActivities.size()>0) {
                for (CoActivity sonAct : sonActivities) {
                    SecKill kill = new SecKill();
                    kill.setActivityId(sonAct.getActivityId());
                    kill.setStatusCd(statusCd);
                    secKillDao.updateSeckill(kill);
                }
            }
        }
        //更新父级
        activityDao.updateActivity(parCoActivity);
        //更新子级
        if(editTime!=null&&editTime.size()>0){
            for(CoActivity son:editTime){
                son.setShowTime(showTimeDt);
                son.setActivityDesc(parCoActivity.getActivityDesc());
                son.setEditStaffId(staffInfo.getStaffId());
                son.setStatusCd(statusCd);
                activityDao.updateActivity(son);
            }
        }
        //增加子级
        //落子级活动表
        for(String date:addTime){
            CoActivity sonCoActivity=new CoActivity();
            if("1".equals(seckillType)) {
                sonCoActivity.setActivityTitle(activityTitle + "-" + date + "时");
            }else{
                sonCoActivity.setActivityTitle(activityTitle + "-" + date);
            }
            sonCoActivity.setShowTime(showTimeDt);
            sonCoActivity.setActivityDesc(activityDesc);
            sonCoActivity.setActivityCd(Toolkit.getBusiSerialNumber("HD"));
            sonCoActivity.setStatusCd(statusCd);
            sonCoActivity.setParentId(activityId);
            sonCoActivity.setShowRuleMode("");
            sonCoActivity.setRuleMode("");
            sonCoActivity.setActivityRule("");
            sonCoActivity.setActivityMode("2");
            sonCoActivity.setSeckillType(seckillType);
            sonCoActivity.setStaffId(staffInfo.getStaffId());
            sonCoActivity.setEditStaffId(staffInfo.getStaffId());
            if("1".equals(seckillType)){
                if(Integer.parseInt(date)<10){
                    date="0"+date;
                }
                String startTime=MapUtil.asStr(param,"startTime");
                sonCoActivity.setStartTime(getStartTime(startTime+" "+date+":00:00"));
                sonCoActivity.setEndTime(getEndTime(sonCoActivity.getStartTime(),continueTime));
            }else{
                String dayStart=continueTime.split("-")[0].trim();
                String dayEnd=continueTime.split("-")[1].trim();
                sonCoActivity.setStartTime(getStartTime(date+" "+dayStart));
                sonCoActivity.setEndTime(getStartTime(date+" "+dayEnd));
            }
            activityDao.insertActivity(sonCoActivity);
        }
        //删除子级
        if(delTime!=null&&delTime.size()>0){
            for(long actId:delTime){
                activityDao.deleteActivity(actId);
            }
        }
        //校验活动时间段重叠
        checkIdRepeat(activityId);
        //处理活动规则
        dealRuleData(activityId,param);
        //处理图片
        dealActImg(activityId,param);
        if("101".equals(statusCd)){//发布活动，同步权益，接口失败回滚数据
            syncActivity(activityId);
            reflashRedis();//刷新缓存
        }
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        resultMap.put("activityId",activityId);
        return resultMap;
    }

    /**
     * 刷新今天和明天的秒杀数据缓存
     */
    @Override
    public Map reflashRedis(){
        Map<String, Object> turnMap=new HashMap<String,Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "重载缓存失败");
        String today = DateUtil.format(new Date(),"yyyyMMdd");
        String goodsKey=SECKILL_GOODS + today;
        String actKey=Activity_ID_SET+today;
        redisUtil.delRedisForKey(goodsKey);
        redisUtil.delRedisForKey(actKey);
        String tomorrow= DateUtil.getNextDayTimeForStr(new Date(),1,"yyyyMMdd");
        goodsKey=SECKILL_GOODS + tomorrow;
        actKey=Activity_ID_SET+tomorrow;
        redisUtil.delRedisForKey(goodsKey);
        redisUtil.delRedisForKey(actKey);
        try {
            List<String> ruleFroms = new ArrayList<String>();
            ruleFroms.add("101");
            turnMap=taskCenterBmo.reloadRuleRedisInfo(ruleFroms);
        }catch (Exception e){
        }
        return turnMap;
    }
    /**
     * 处理活动规则
     * @param activityId
     * @param param
     * @throws Exception
     */
    private void dealRuleData(long activityId,Map param) throws Exception{
        String statusCd=MapUtil.asStr(param,"statusCd");//101正常，103待发布
        List<Map<String,Object>> ruleDataArray = (List<Map<String, Object>>) param.get("ruleDataArray");
        List<Map<String,Object>> areaRelRules= (List<Map<String, Object>>) param.get("areaRelRules");
        //删除已删除的规则
        Map paramMap = new HashMap<String, Object>();
        paramMap.put("activityId", activityId);
        List<CoActRule> coActRules = activityDao.queryActRules(paramMap);//查询已存在的规则
        if(ruleDataArray != null) {
            for (CoActRule coActRule : coActRules) {
                boolean delFlag = true;
                for (Map<String, Object> ruleData : ruleDataArray) {
                    if (!StringUtil.isEmptyStr(MapUtil.asStr(ruleData, "ruleId"))) {
                        Long ruleId = Long.parseLong(MapUtil.asStr(ruleData, "ruleId"));
                        if (coActRule.getRuleId().longValue() == ruleId.longValue()) {//存在，不需要删除
                            delFlag = false;
                            break;
                        }
                    }
                }
                if (delFlag&&!"8".equals(coActRule.getRuleType())) {//删除已删除的规则
                    paramMap = new HashMap<String, Object>();
                    paramMap.put("activityId", activityId);
                    paramMap.put("ruleId", coActRule.getRuleId());
                    activityDao.deleteActRules(paramMap);
                }
                if("8".equals(coActRule.getRuleType())){
                    CoActRule areaActRule = new CoActRule();
                    areaActRule.setRuleId(coActRule.getRuleId());
                    areaActRule.setStatusCd(statusCd);
                    activityDao.updateActRules(areaActRule);
                }
            }
            int index = 0;
            for (Map<String, Object> ruleData : ruleDataArray) {
                index++;
                String ruleDataTemp=MapUtil.asStr(ruleData, "ruleData");
                ruleDataTemp=ruleDataTemp.replaceAll("&lt;","<");
                ruleDataTemp=ruleDataTemp.replaceAll("&gt;",">");
                if (!StringUtil.isEmptyStr(MapUtil.asStr(ruleData, "ruleId"))) {//修改
                    Long ruleId = Long.parseLong(MapUtil.asStr(ruleData, "ruleId"));
                    CoActRule coActRule = new CoActRule();
                    coActRule.setRuleCode(MapUtil.asStr(ruleData, "ruleCode"));
                    coActRule.setRuleTitle(MapUtil.asStr(ruleData, "ruleTitle"));
                    coActRule.setRuleDesc(MapUtil.asStr(ruleData, "ruleTitle"));
                    coActRule.setStatusCd(statusCd);
                    coActRule.setRuleType(MapUtil.asStr(ruleData, "ruleType"));
                    coActRule.setRuleData(ruleDataTemp);
                    coActRule.setSortSeq(index);
                    coActRule.setIsShow(MapUtil.asStr(ruleData, "isShow"));
                    coActRule.setRuleId(ruleId);
                    activityDao.updateActRules(coActRule);
                } else {
                    CoActRule coActRule = new CoActRule();
                    coActRule.setActivityId(activityId);
                    coActRule.setRuleCode(MapUtil.asStr(ruleData, "ruleCode"));
                    coActRule.setRuleTitle(MapUtil.asStr(ruleData, "ruleTitle"));
                    coActRule.setRuleDesc(MapUtil.asStr(ruleData, "ruleTitle"));
                    coActRule.setStatusCd(statusCd);
                    coActRule.setRuleType(MapUtil.asStr(ruleData, "ruleType"));
                    coActRule.setRuleData(ruleDataTemp);
                    coActRule.setSortSeq(index);
                    coActRule.setIsShow(MapUtil.asStr(ruleData, "isShow"));
                    activityDao.insertActRules(coActRule);
                }
            }
        }
        long ruleId=0;
        for (CoActRule coActRule : coActRules) {
            if("8".equals(coActRule.getRuleType())){
                ruleId=coActRule.getRuleId();
                break;
            }
        }
        if(areaRelRules!=null&&areaRelRules.size()>0){
            paramMap.put("ruleType", "8");
            List<CoActRuleData> coActRuleDatas = activityDao.queryActRuleData(paramMap);//查询已存在的区域规则数据
            for (CoActRuleData coActRuleData : coActRuleDatas) {
                boolean delFlag = true;
                for (Map<String, Object> areaRuleData : areaRelRules) {
                    if (!StringUtil.isEmptyStr(MapUtil.asStr(areaRuleData, "expId"))) {
                        Long expId = Long.parseLong(MapUtil.asStr(areaRuleData, "expId"));
                        if (coActRuleData.getExpId() == expId.longValue()) {//存在，不需要删除
                            delFlag = false;
                            break;
                        }
                    }
                }
                if (delFlag) {//删除区域规则
                    activityDao.deleteActRuleData(coActRuleData.getExpId());
                }
            }
            int index = 0;
            for (Map<String, Object> areaRelRule : areaRelRules) {
                index++;
                if (StringUtil.isEmptyStr(MapUtil.asStr(areaRelRule, "expId"))) {//新增
                    CoActRuleData coActRuleData=new CoActRuleData();
                    coActRuleData.setRuleData(MapUtil.asStr(areaRelRule, "ruleData"));
                    coActRuleData.setRuleId(ruleId);
                    coActRuleData.setRuleType(MapUtil.asStr(areaRelRule, "ruleType"));
                    coActRuleData.setSortSeq(index);
                    coActRuleData.setStatusCd("101");
                    activityDao.insertActRuleData(coActRuleData);
                }
            }
        }
    }

    /**
     * 处理活动图片
     * @param activityId
     * @param param
     * @throws Exception
     */
    private void dealActImg(long activityId,Map param) throws Exception{
        String statusCd=MapUtil.asStr(param,"statusCd");//101正常，103待发布
        if(param.get("banners") != null){
            List<Map<String,Object>> bannerList = (List<Map<String,Object>>)param.get("banners");
            if(bannerList != null && bannerList.size()>0) {
                //删除已删除的图片
                List<CoActivityImg> coActivityImgs = activityDao.getCoActivityImgList(param);//查询已存在的
                for(CoActivityImg coActivityImg:coActivityImgs){
                    boolean delFlag = true;
                    for(Map<String,Object> bannerMap : bannerList){
                        if(!StringUtil.isEmptyStr(MapUtil.asStr(bannerMap,"actImgId"))){
                            Long actImgId = Long.parseLong(MapUtil.asStr(bannerMap,"actImgId"));
                            if(coActivityImg.getActImgId() == actImgId){//存在，不需要删除
                                delFlag = false;
                                break;
                            }
                        }
                    }
                    if(delFlag){//删除已删除的图片
                        Map paramMap = new HashMap<String,Object>();
                        paramMap.put("activityId", activityId);
                        paramMap.put("actImgId", coActivityImg.getActImgId());
                        activityDao.deleteActImg(paramMap);
                    }
                }
                int index = 0;
                for (Map<String,Object> bannerMap : bannerList) {
                    index++;
                    if(bannerMap.containsKey("actImgId")){//修改
                        Long actImgId = Long.parseLong(MapUtil.asStr(bannerMap,"actImgId"));
                        CoActivityImg coActivityImg = new CoActivityImg();
                        coActivityImg.setImgCode(MapUtil.asStr(bannerMap,"imgCode").trim());
                        coActivityImg.setImgId(Long.parseLong(MapUtil.asStr(bannerMap,"imgId")));
                        coActivityImg.setClassCode(MapUtil.asStr(bannerMap,"classCode"));
                        coActivityImg.setSortSeq(index);
                        coActivityImg.setStatusCd(statusCd);
                        coActivityImg.setActImgId(actImgId);
                        coActivityImg.setActivityId(activityId);
                        activityDao.updateActImg(coActivityImg);
                    }else{
                        CoActivityImg coActivityImg = new CoActivityImg();
                        coActivityImg.setActivityId(activityId);
                        coActivityImg.setImgCode(MapUtil.asStr(bannerMap,"imgCode").trim());
                        coActivityImg.setImgId(Long.parseLong(MapUtil.asStr(bannerMap,"imgId")));
                        coActivityImg.setClassCode(MapUtil.asStr(bannerMap,"classCode"));
                        coActivityImg.setSortSeq(index);
                        coActivityImg.setStatusCd(statusCd);
                        activityDao.insertActImg(coActivityImg);
                    }
                }
            }
        }
    }

    /**
     * 校验时间段是否重复
     * @param activityId
     * @throws Exception
     */
    private void checkIdRepeat(long activityId) throws Exception{
        Map sonParam=new HashMap();
        sonParam.put("parentId",activityId);
        List<CoActivity> updateSon=activityDao.queryActivityList(sonParam);
        if(updateSon!=null&&updateSon.size()>0) {
            for (CoActivity sonAct : updateSon) {
                Map timeQry = new HashMap();
                timeQry.put("beginDt", sonAct.getStartTime());
                timeQry.put("endDt", sonAct.getEndTime());
                timeQry.put("activityId", sonAct.getActivityId());
                Integer count= secKillDao.queryActivityList(timeQry);
                if (count > 0) {
                    throw new RuntimeException("配置的活动时段与其它秒杀活动时段重叠");
                }
            }
        }
    }
    @Override
    public Map saveActivity(Map param) throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"新增秒杀活动失败");
        String[] fields = {"activityTitle","showTime","seckillType","continueTime","statusCd"};
        Map<String, Object> validationResult = FieldValidation.validation(param, fields);
        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
            return resultMap;
        }
        if(param.get("times")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
            return resultMap;
        }
        if(param.get("staffInfo")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少登录数据");
            return resultMap;
        }
        if(param.get("areaRelRules")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
            return resultMap;
        }
        List<Map<String,Object>> areaRelRules= (List<Map<String, Object>>) param.get("areaRelRules");
        if(areaRelRules.size()==0){
            resultMap.put(Constants.RESULT_MSG_STR, "请至少选择一个地市");
            return resultMap;
        }
        StaffInfo info=(StaffInfo)param.get("staffInfo");
        SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
        String activityTitle=MapUtil.asStr(param,"activityTitle");
        String showTime=MapUtil.asStr(param,"showTime");
        String continueTime=MapUtil.asStr(param,"continueTime");
        List<String> times=(List<String>)MapUtil.path(param,"times");
        String seckillType=MapUtil.asStr(param,"seckillType");
        String statusCd=MapUtil.asStr(param,"statusCd");//101正常，103待发布
        String ruleMode=MapUtil.asStr(param,"showRuleMode");
        String activityDesc=MapUtil.asStr(param,"activityDesc");
        String activityRule=MapUtil.asStr(param,"activityRule");
        activityRule=activityRule.replaceAll("&lt;","<");
        activityRule=activityRule.replaceAll("&gt;",">");
        Date showTimeDt=format.parse(showTime);
        //落父级活动表
        CoActivity parCoActivity=new CoActivity();
        parCoActivity.setActivityTitle(activityTitle);
        parCoActivity.setShowTime(showTimeDt);
        parCoActivity.setActivityDesc(activityDesc);
        parCoActivity.setProbability("0");
        parCoActivity.setActivityCd(Toolkit.getBusiSerialNumber("HD"));
        parCoActivity.setStatusCd(statusCd);
        parCoActivity.setParentId(0L);
        parCoActivity.setShowRuleMode(ruleMode);
        parCoActivity.setRuleMode(ruleMode);
        parCoActivity.setActivityRule(activityRule);
        parCoActivity.setActivityMode("2");
        parCoActivity.setSeckillType(seckillType);
        parCoActivity.setStaffId(info.getStaffId());
        parCoActivity.setEditStaffId(info.getStaffId());
        if("1".equals(seckillType)){
            String startTime=MapUtil.asStr(param,"startTime");
            if(StringUtil.isEmptyStr(startTime)){
                resultMap.put(Constants.RESULT_MSG_STR, "缺少必要的参数");
                return resultMap;
            }
            Date actStartTime=getParentStartTime(seckillType,times,startTime,continueTime);
            parCoActivity.setStartTime(actStartTime);
            parCoActivity.setEndTime(getParentEndTime(seckillType,times,startTime,continueTime));
        }else{
            Date actStartTime=getParentStartTime(seckillType,times,"",continueTime);
            parCoActivity.setStartTime(actStartTime);
            parCoActivity.setEndTime(getParentEndTime(seckillType,times,"",continueTime));
        }
        if(showTimeDt.compareTo(parCoActivity.getStartTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "展示时间不得大于活动开始时间");
            return resultMap;
        }
        if(parCoActivity.getStartTime().compareTo(parCoActivity.getEndTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "结束时间不得小于活动开始时间");
            return resultMap;
        }
        if(new Date().compareTo(parCoActivity.getStartTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "活动开始时间不得小于当前时间");
            return resultMap;
        }
        activityDao.insertActivity(parCoActivity);
        Long activityId=parCoActivity.getActivityId();
        //落子级活动表
        for(String date:times){
            CoActivity sonCoActivity=new CoActivity();
            if("1".equals(seckillType)) {
                sonCoActivity.setActivityTitle(activityTitle + "-" + date + "时");
            }else{
                sonCoActivity.setActivityTitle(activityTitle + "-" + date);
            }
            sonCoActivity.setShowTime(showTimeDt);
            sonCoActivity.setActivityDesc(activityDesc);
            sonCoActivity.setActivityCd(Toolkit.getBusiSerialNumber("HD"));
            sonCoActivity.setProbability("0");
            sonCoActivity.setStatusCd(statusCd);
            sonCoActivity.setParentId(activityId);
            sonCoActivity.setShowRuleMode("");
            sonCoActivity.setRuleMode("");
            sonCoActivity.setActivityRule("");
            sonCoActivity.setActivityMode("2");
            sonCoActivity.setSeckillType(seckillType);
            if("1".equals(seckillType)){
                if(Integer.parseInt(date)<10){
                    date="0"+date;
                }
                String startTime=MapUtil.asStr(param,"startTime");
                sonCoActivity.setStartTime(getStartTime(startTime+" "+date+":00:00"));
                sonCoActivity.setEndTime(getEndTime(sonCoActivity.getStartTime(),continueTime));
            }else{
                String dayStart=continueTime.split("-")[0].trim();
                String dayEnd=continueTime.split("-")[1].trim();
                sonCoActivity.setStartTime(getStartTime(date+" "+dayStart));
                sonCoActivity.setEndTime(getStartTime(date+" "+dayEnd));
            }
            sonCoActivity.setStaffId(info.getStaffId());
            sonCoActivity.setEditStaffId(info.getStaffId());
            activityDao.insertActivity(sonCoActivity);
        }
        //校验活动时间段重叠
        checkIdRepeat(activityId);
        //落规则表
        if(param.get("ruleDataArray")!=null){
            List<Map> ruleList=(List<Map>)param.get("ruleDataArray");
            if(ruleList!=null&&ruleList.size()>0){
                Integer index=0;
                for(Map map:ruleList){
                    index++;
                    CoActRule rule=new CoActRule();
                    String ruleData=MapUtil.asStr(map,"ruleData");
                    ruleData=ruleData.replaceAll("&lt;","<");
                    ruleData=ruleData.replaceAll("&gt;",">");
                    rule.setActivityId(activityId);
                    rule.setIsShow(MapUtil.asStr(map,"isShow"));
                    rule.setRuleCode(MapUtil.asStr(map,"ruleCode"));
                    rule.setRuleData(ruleData);
                    rule.setRuleType(MapUtil.asStr(map,"ruleType"));
                    rule.setRuleTitle(MapUtil.asStr(map,"ruleTitle"));
                    rule.setStatusCd(statusCd);
                    rule.setSortSeq(index);
                    activityDao.insertActRule(rule);
                }
            }
        }
        if(areaRelRules!=null&&areaRelRules.size()>0) {
            Integer index = 0;
            long ruleId=0;
            for (Map areaRelRule : areaRelRules) {
                index++;
                if (index == 1) {//插入地市规则
                    CoActRule rule = new CoActRule();
                    rule.setActivityId(activityId);
                    rule.setIsShow(MapUtil.asStr(areaRelRule, "isShow"));
                    rule.setRuleCode(MapUtil.asStr(areaRelRule, "ruleCode"));
                    rule.setRuleData("");
                    rule.setRuleType(MapUtil.asStr(areaRelRule, "ruleType"));
                    rule.setRuleTitle(MapUtil.asStr(areaRelRule, "ruleTitle"));
                    rule.setStatusCd(statusCd);
                    rule.setSortSeq(0);
                    activityDao.insertActRule(rule);
                    ruleId=rule.getRuleId();
                }
                //插入区域规则数据
                CoActRuleData coActRuleData = new CoActRuleData();
                coActRuleData.setRuleData(MapUtil.asStr(areaRelRule, "ruleData"));
                coActRuleData.setRuleId(ruleId);
                coActRuleData.setRuleType(MapUtil.asStr(areaRelRule, "ruleType"));
                coActRuleData.setSortSeq(index);
                coActRuleData.setStatusCd("101");
                activityDao.insertActRuleData(coActRuleData);
            }
        }
        //落图片表
        if(param.get("banners")!=null){
            List<Map> bannerList=(List<Map>)param.get("banners");
            if(bannerList!=null&&bannerList.size()>0) {
                long index=0;
                for (Map map : bannerList) {
                    index++;
                    CoActivityImg coActivityImg=new CoActivityImg();
                    coActivityImg.setActivityId(activityId);
                    coActivityImg.setImgCode(MapUtil.asStr(map,"imgCode"));
                    coActivityImg.setImgId(Long.parseLong(MapUtil.asStr(map,"imgId")));
                    coActivityImg.setSortSeq(index);
                    coActivityImg.setStatusCd(statusCd);
                    coActivityImg.setClassCode(MapUtil.asStr(map,"classCode"));
                    activityDao.insertActImg(coActivityImg);
                }
            }
        }
        resultMap.put("activityId",activityId);
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        return resultMap;
    }

    /**
     * 获取父级总的开始时间
     */
    private Date getParentStartTime(String seckillType,List<String> times,String startTime,String continueTime) throws Exception{
        Date minDate=null;
        SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
        if("1".equals(seckillType)){//时会场
            for(String date:times){
                if(Integer.parseInt(date)<10){
                    date="0"+date;
                }
                Date currDate=format.parse(startTime+" "+date+":00:00");
                if(minDate==null){
                    minDate=currDate;
                }else{
                    if(currDate.compareTo(minDate)<0){
                        minDate=currDate;
                    }
                }
            }
        }else{
            String dayStart=continueTime.split("-")[0].trim();
            for(String date:times){
                Date currDate=format.parse(date+" "+dayStart);
                if(minDate==null){
                    minDate=currDate;
                }else{
                    if(currDate.compareTo(minDate)<0){
                        minDate=currDate;
                    }
                }
            }
        }
        return minDate;
    }
    /**
     * 获取父级总的结束时间
     */
    private Date getParentEndTime(String seckillType,List<String> times,String startTime,String continueTime) throws Exception{
        Date maxDate=null;
        SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
        if("1".equals(seckillType)){//时会场
            for(String date:times){
                if(Integer.parseInt(date)<10){
                    date="0"+date;
                }
                Date currDate=format.parse(startTime+" "+date+":00:00");
                int conTime=Integer.parseInt(continueTime);
                if(conTime>60){
                    conTime=60;
                }
                currDate=DateUtil.getAftMin(currDate,conTime);
                if(conTime==60){//60分钟时提前一秒
                    currDate=DateUtil.getBefSecond(currDate,1);
                }
                if(maxDate==null){
                    maxDate=currDate;
                }else{
                    if(currDate.compareTo(maxDate)>0){
                        maxDate=currDate;
                    }
                }
            }
        }else{
            String dayEnd=continueTime.split("-")[1].trim();
            for(String date:times){
                Date currDate=format.parse(date+" "+dayEnd);
                if(maxDate==null){
                    maxDate=currDate;
                }else{
                    if(currDate.compareTo(maxDate)>0){
                        maxDate=currDate;
                    }
                }
            }
        }
        return maxDate;
    }

    /**
     * 获取秒杀节点的开始时间
     * @param startTime
     * @return
     */
    private Date getStartTime(String startTime)throws Exception{
        SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
        return format.parse(startTime);
    }

    /**
     *  获取秒杀节点的结束时间
     * @param startTime
     * @param continueTime
     * @return
     * @throws Exception
     */
    private Date getEndTime(Date startTime,String continueTime)throws Exception{
        int conTime=Integer.parseInt(continueTime);
        if(conTime>60){
            conTime=60;
        }
        Date endTime=DateUtil.getAftMin(startTime,conTime);
        if(conTime==60){//60分钟时提前一秒
            endTime=DateUtil.getBefSecond(endTime,1);
        }
        return endTime;
    }
}
