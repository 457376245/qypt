package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.bean.StaffInfo;
import com.web.common.Constants;
import com.web.dao.ActivityDao;
import com.web.model.CoActivity;
import com.web.model.CoActivityImg;
import com.web.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 其它活动配置
 *
 * @author
 * @create 2020-05-15
 **/
@Service("com.web.bmo.OtherActBmoImpl")
public class OtherActBmoImpl implements OtherActBmo{
    @Autowired
    private ActivityDao activityDao;
    @Override
    public Map<String, Object> qryOtherActList(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum= MapUtil.asInt(param,"page");
        int pageSize=MapUtil.asInt(param,"limit");
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
                case "4" ://已结束
                    param.put("afterEndTime","1");
                    param.put("statusCd", Constants.STATUS_EFFECT);
                    break;
                default :
            }
        }
        param.put("activityMode","4");
        PageHelper.startPage(pageNum, pageSize);
        List<CoActivity> list=activityDao.queryActivityList(param);
        PageInfo<CoActivity> info = new PageInfo<>(list);
        for(CoActivity coActivity:info.getList()){
            Date startTime=coActivity.getStartTime();
            Date endTime=coActivity.getEndTime();
            Date nowTime=new Date();
            if(nowTime.compareTo(startTime)<0){
                coActivity.setStatusCd("2");
            }else if(nowTime.compareTo(endTime)>0){
                coActivity.setStatusCd("4");
            }else{
                coActivity.setStatusCd("1");
            }
            coActivity.setStartTimeStr(DateUtil.format(startTime,"yyyy/MM/dd"));
            coActivity.setEndTimeStr(DateUtil.format(endTime,"yyyy/MM/dd"));
        }
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> saveActivities(Map<String, Object> paramsMap) throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"新增活动失败");
        String[] fields = {"activityTitle","showTime","startTime","endTime"};
        Map<String, Object> validationResult = FieldValidation.validation(paramsMap, fields);
        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
            return resultMap;
        }
        if(paramsMap.get("staffInfo")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少登录数据");
            return resultMap;
        }
        StaffInfo info=(StaffInfo)paramsMap.get("staffInfo");
        SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
        String activityTitle=MapUtil.asStr(paramsMap,"activityTitle");
        String showTime=MapUtil.asStr(paramsMap,"showTime");
        String startTime=MapUtil.asStr(paramsMap,"startTime");
        String endTime=MapUtil.asStr(paramsMap,"endTime");
        String activityDesc=MapUtil.asStr(paramsMap,"activityDesc");
        String activityRule=MapUtil.asStr(paramsMap,"activityRule");
        String ssoUrl=MapUtil.asStr(paramsMap,"ssoUrl");
        //落父级活动表
        CoActivity parCoActivity=new CoActivity();
        parCoActivity.setActivityTitle(activityTitle);
        parCoActivity.setSsoUrl(ssoUrl);
        parCoActivity.setShowTime(format.parse(showTime));
        parCoActivity.setStartTime(format.parse(startTime));
        parCoActivity.setEndTime(format.parse(endTime));
        parCoActivity.setProbability("0");
        parCoActivity.setActivityCd(Toolkit.getBusiSerialNumber("HD"));
        parCoActivity.setStatusCd("101");
        parCoActivity.setParentId(0L);
        parCoActivity.setShowRuleMode("");
        parCoActivity.setRuleMode("");
        parCoActivity.setActivityRule("");
        parCoActivity.setActivityMode("4");
        parCoActivity.setSeckillType("");
        parCoActivity.setStaffId(info.getStaffId());
        parCoActivity.setEditStaffId(info.getStaffId());
        parCoActivity.setActivityDesc(activityDesc);
        parCoActivity.setActivityRule(activityRule);
        if(parCoActivity.getStartTime().compareTo(parCoActivity.getEndTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "结束时间不得小于活动开始时间");
            return resultMap;
        }
        if(new Date().compareTo(parCoActivity.getStartTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "活动开始时间不得小于当前时间");
            return resultMap;
        }
        activityDao.insertActivity(parCoActivity);
        long activityId=parCoActivity.getActivityId();
        //落图片表
        if(paramsMap.get("banners")!=null){
            List<Map> bannerList=(List<Map>)paramsMap.get("banners");
            if(bannerList!=null&&bannerList.size()>0) {
                long index=0;
                for (Map map : bannerList) {
                    index++;
                    CoActivityImg coActivityImg=new CoActivityImg();
                    coActivityImg.setActivityId(parCoActivity.getActivityId());
                    coActivityImg.setImgCode(MapUtil.asStr(map,"imgCode"));
                    coActivityImg.setImgId(Long.parseLong(MapUtil.asStr(map,"imgId")));
                    coActivityImg.setSortSeq(index);
                    coActivityImg.setStatusCd("101");
                    coActivityImg.setClassCode(MapUtil.asStr(map,"classCode"));
                    activityDao.insertActImg(coActivityImg);
                }
            }
        }
        resultMap.put("activityId",activityId);
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        return resultMap;
    }

    @Override
    public Map<String, Object> editActivities(Map<String, Object> paramsMap) throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"编辑活动失败");
        String[] fields = {"activityTitle","showTime","startTime","endTime"};
        Map<String, Object> validationResult = FieldValidation.validation(paramsMap, fields);
        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
            return resultMap;
        }
        if(paramsMap.get("staffInfo")==null){
            resultMap.put(Constants.RESULT_MSG_STR, "缺少登录的参数");
            return resultMap;
        }
        StaffInfo staffInfo=(StaffInfo)paramsMap.get("staffInfo");
        SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
        long activityId=Long.parseLong(MapUtil.asStr(paramsMap,"activityId"));
        String activityTitle=MapUtil.asStr(paramsMap,"activityTitle");
        String showTime=MapUtil.asStr(paramsMap,"showTime");
        String startTime=MapUtil.asStr(paramsMap,"startTime");
        String endTime=MapUtil.asStr(paramsMap,"endTime");
        String activityDesc=MapUtil.asStr(paramsMap,"activityDesc");
        String activityRule=MapUtil.asStr(paramsMap,"activityRule");
        String ssoUrl=MapUtil.asStr(paramsMap,"ssoUrl");
        CoActivity parCoActivity=new CoActivity();
        parCoActivity.setActivityId(activityId);
        parCoActivity.setActivityTitle(activityTitle);
        parCoActivity.setSsoUrl(ssoUrl);
        parCoActivity.setShowTime(format.parse(showTime));
        parCoActivity.setStartTime(format.parse(startTime));
        parCoActivity.setEndTime(format.parse(endTime));
        parCoActivity.setStatusCd("101");
        parCoActivity.setShowRuleMode("");
        parCoActivity.setRuleMode("");
        parCoActivity.setActivityRule("");
        parCoActivity.setActivityMode("4");
        parCoActivity.setSeckillType("");
        parCoActivity.setActivityDesc(activityDesc);
        parCoActivity.setActivityRule(activityRule);
        parCoActivity.setEditStaffId(staffInfo.getStaffId());
        if(parCoActivity.getStartTime().compareTo(parCoActivity.getEndTime())>0){
            resultMap.put(Constants.RESULT_MSG_STR, "结束时间不得小于活动开始时间");
            return resultMap;
        }
        activityDao.updateActivity(parCoActivity);
        if(paramsMap.get("banners") != null){
            List<Map<String,Object>> bannerList = (List<Map<String,Object>>)paramsMap.get("banners");
            if(bannerList != null && bannerList.size()>0) {
                //删除已删除的图片
                List<CoActivityImg> coActivityImgs = activityDao.getCoActivityImgList(paramsMap);//查询已存在的
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
                        coActivityImg.setStatusCd("101");
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
                        coActivityImg.setStatusCd("101");
                        activityDao.insertActImg(coActivityImg);
                    }
                }
            }
        }
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        resultMap.put("activityId",activityId);
        return resultMap;
    }

    @Override
    public Map queryActivity(Map<String, Object> paramsMap)throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"查询活动成功");
        String[] fields = {"activityId"};
        Map<String, Object> validationResult = FieldValidation.validation(paramsMap, fields);
        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
            return resultMap;
        }
        List<CoActivity> coActivities=activityDao.queryActivityList(paramsMap);
        resultMap.put("coActivity",coActivities.get(0));
        List<CoActivityImg> coActivityImgs = activityDao.getCoActivityImgList(paramsMap);
        resultMap.put("coActivityImgs",coActivityImgs);
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        return resultMap;
    }

    @Override
    public Map deleteAct(Map param) throws Exception {
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"删除活动失败");
        String[] fields = {"activityId"};
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
        String statusCd="102";
        Long activityId=Long.parseLong(MapUtil.asStr(param,"activityId"));
        Map<String,Object> actParam=new HashMap();
        actParam.put("activityId",activityId);
        CoActivity upCoAct = new CoActivity();
        upCoAct.setActivityId(activityId);
        upCoAct.setEditStaffId(staffInfo.getStaffId());
        upCoAct.setStatusCd(statusCd);
        activityDao.updateActivity(upCoAct);
        CoActivityImg coActivityImg = new CoActivityImg();
        coActivityImg.setActivityId(upCoAct.getActivityId());
        coActivityImg.setStatusCd(statusCd);
        activityDao.updateActImg(coActivityImg);
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        return resultMap;
    }
}
