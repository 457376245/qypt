package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.ActivityDao;
import com.web.dao.CoRegionDao;
import com.web.dao.DicGroupAndItemDao;
import com.web.model.*;
import com.web.thirdinterface.ThirdQYInterface;
import com.web.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 活动-抽奖
 *
 * @author
 * @create 2020-03-13
 **/
@Service("com.web.bmo.ActLuckBmoImpl")
public class ActLuckBmoImpl implements ActLuckBmo {
	
    private static final Logger log = LoggerFactory.getLogger(ActLuckBmoImpl.class);

    
    @Autowired
    private DicGroupAndItemDao dicGroupAndItemDao;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private CoRegionDao coRegionDao;
    //翼豆的扣减规则编码
    private final String reduceYdCode="ACT_RULE_CHECK_YD";
	//抽奖频次规则编码
    private final String countRateCode="ACT_RULE_RATECOUNT";
    
    //区域规则类型
    private final String areaRuleType = "8";
    
    @Resource(name = "RedisUtil")
    private RedisUtil redisUtil;
    
    @Resource
    private ThirdQYInterface thirdInterface;
	@Resource(name = "com.web.bmo.TaskCenterBmoImpl")
	private TaskCenterBmo taskCenterBmo;
    @Override
    public Map<String, Object> qryLotteryList(Map<String, Object> param) throws Exception{
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
        param.put("activityMode","1");
        PageHelper.startPage(pageNum, pageSize);

        List<CoActivity> list=activityDao.queryActivityList(param);
        PageInfo<CoActivity> info = new PageInfo<>(list);
        for(CoActivity coActivity:info.getList()){
            Map<String, Object> ruleParam=new HashMap<String, Object>();
            ruleParam.put("ruleCode",reduceYdCode);
            ruleParam.put("activityId",coActivity.getActivityId());
            List<CoActRule> rule=activityDao.queryActRules(ruleParam);
            if(rule != null && rule.size() > 0){
            	String ruleData=rule.get(0).getRuleData();
                ruleData=ruleData.replaceAll(">","");
                ruleData=ruleData.replaceAll("=","");
                ruleData=ruleData.replaceAll("<","");
                coActivity.setUseAccount(Integer.parseInt(ruleData));
            }
            ruleParam = new HashMap<String, Object>();
            ruleParam.put("activityId", coActivity.getActivityId());
            ruleParam.put("statusCd", param.get("statusCd"));
            Integer inUserCount=activityDao.queryActUserCount(ruleParam);
            coActivity.setInUserCount(inUserCount);
            String statusCd=coActivity.getStatusCd();
            Date startTime=coActivity.getStartTime();
            Date endTime=coActivity.getEndTime();
            Date nowTime=new Date();
            if(Constants.STATUS_NOTPUBLISH.equals(statusCd)){
                coActivity.setStatusCd("3");
            }else if(Constants.STATUS_DROP.equals(statusCd)){
                coActivity.setStatusCd("5");
            }else{
                if(nowTime.compareTo(startTime)<0){
                    coActivity.setStatusCd("2");
                }else if(nowTime.compareTo(endTime)>0){
                    coActivity.setStatusCd("4");
                }else{
                    coActivity.setStatusCd("1");
                }
            }
            coActivity.setStartTimeStr(DateUtil.format(startTime,"yyyy/MM/dd"));
            coActivity.setEndTimeStr(DateUtil.format(endTime,"yyyy/MM/dd"));
        }
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> saveActivities(Map<String, Object> paramsMap) throws Exception {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		SimpleDateFormat format=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
		resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
	    resultMap.put(Constants.RESULT_MSG_STR,"新增转盘活动失败");
		String showTime = MapUtil.asStr(paramsMap,"showTime");
		String startTime = MapUtil.asStr(paramsMap,"startTime");
		String endTime = MapUtil.asStr(paramsMap,"endTime");
		Date showTimtDt=DateUtil.getDayTime(showTime, DateUtil.FMT_DATE);
		Date startTimeDt=DateUtil.getDayTime(startTime, DateUtil.FMT_DATE);
		Date endTimeDt=DateUtil.getDayTime(endTime, DateUtil.FMT_DATE);
		String countRate = MapUtil.asStr(paramsMap,"countRate");
		if(showTimtDt.compareTo(startTimeDt)>0){
			resultMap.put(Constants.RESULT_MSG_STR, "展示时间不得大于活动开始时间");
			return resultMap;
		}
		if(startTimeDt.compareTo(endTimeDt)>0){
			resultMap.put(Constants.RESULT_MSG_STR, "结束时间不得小于活动开始时间");
			return resultMap;
		}
		if(new Date().compareTo(startTimeDt)>0){
			resultMap.put(Constants.RESULT_MSG_STR, "活动开始时间不得小于当前时间");
			return resultMap;
		}
        try {
        	if(paramsMap.containsKey("activityId")){//修改活动
        		Long activityId = Long.parseLong(MapUtil.asStr(paramsMap,"activityId"));
        		String statusCd = MapUtil.asStr(paramsMap,"statusCd");//101正常，103待发布
            	//更新活动表
            	CoActivity coActivity = new CoActivity();
            	coActivity.setActivityTitle(MapUtil.asStr(paramsMap,"activityTitle"));
				coActivity.setProbability(MapUtil.asStr(paramsMap,"probability"));
            	coActivity.setActivityDesc(MapUtil.asStr(paramsMap,"activityDesc"));
            	coActivity.setActivityRule(MapUtil.asStr(paramsMap,"activityRule"));
            	coActivity.setStatusCd(statusCd);
            	coActivity.setShowTime(showTimtDt);
            	coActivity.setStartTime(startTimeDt);
            	coActivity.setEndTime(endTimeDt);
            	String showRuleMode = MapUtil.asStr(paramsMap,"showRuleMode");
            	coActivity.setRuleMode(showRuleMode);
            	coActivity.setShowRuleMode(showRuleMode);
            	coActivity.setActivityId(activityId);
            	coActivity.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
            	activityDao.updateActivity(coActivity);
            	
            	Map<String,Object> paramMap = null;
            	//更新活动规则
				Long countRateRuleId=null;
            	List<Map<String,Object>> ruleDataArray = (List<Map<String, Object>>) paramsMap.get("ruleDataArray");
            	if(ruleDataArray != null){
            		//删除已删除的规则
    				paramMap = new HashMap<String,Object>();
            		paramMap.put("activityId", activityId);
            		List<CoActRule> coActRules = activityDao.queryActRules(paramMap);//查询已存在的规则
            		Long ydRuleId = null;
            		for(CoActRule coActRule:coActRules){
            			boolean delFlag = true;
            			for(Map<String,Object> ruleData:ruleDataArray){
            				if(!StringUtil.isEmptyStr(MapUtil.asStr(ruleData,"ruleId"))){
            					Long ruleId = Long.parseLong(MapUtil.asStr(ruleData,"ruleId"));
                    			if(coActRule.getRuleId() == ruleId){//存在，不需要删除
                    				delFlag = false;
                    				break;
                    			}
            				}            				
            			}
            			if(reduceYdCode.equals(coActRule.getRuleCode())){//翼豆扣减规则不删除
            				delFlag = false;
            				ydRuleId = coActRule.getRuleId();
            			}
            			if(countRateCode.equals(coActRule.getRuleCode())){//抽奖频次单独处理
							delFlag = false;
							countRateRuleId = coActRule.getRuleId();
						}
            			if(areaRuleType.equals(coActRule.getRuleType())){//区域规则不删除
            				delFlag = false;
            			}
            			if(delFlag){//删除已删除的规则
            				paramMap = new HashMap<String,Object>();
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
            		for(Map<String,Object> ruleData:ruleDataArray){
            			index++;
            			if(!StringUtil.isEmptyStr(MapUtil.asStr(ruleData,"ruleId"))){//修改
                			Long ruleId = Long.parseLong(MapUtil.asStr(ruleData,"ruleId"));
            				CoActRule coActRule = new CoActRule();
                        	coActRule.setRuleCode(MapUtil.asStr(ruleData,"ruleCode"));                	
                        	coActRule.setRuleTitle(MapUtil.asStr(ruleData,"ruleTitle"));
                        	coActRule.setRuleDesc(MapUtil.asStr(ruleData,"ruleTitle"));
                        	coActRule.setStatusCd(statusCd);
                        	coActRule.setRuleType(MapUtil.asStr(ruleData,"ruleType"));
                        	coActRule.setRuleData(MapUtil.asStr(ruleData,"ruleData"));
                        	coActRule.setRuleMode(MapUtil.asStr(paramsMap,"ruleMode"));
                        	coActRule.setSortSeq(index);
                        	coActRule.setIsShow(MapUtil.asStr(ruleData,"isShow"));
                        	coActRule.setRuleId(ruleId);
                        	activityDao.updateActRules(coActRule);            			
            			}else{            				
                        	CoActRule coActRule = new CoActRule();
                        	coActRule.setActivityId(activityId);
                        	coActRule.setRuleCode(MapUtil.asStr(ruleData,"ruleCode"));                	
                        	coActRule.setRuleTitle(MapUtil.asStr(ruleData,"ruleTitle"));
                        	coActRule.setRuleDesc(MapUtil.asStr(ruleData,"ruleTitle"));
                        	coActRule.setStatusCd(statusCd);
                        	coActRule.setRuleType(MapUtil.asStr(ruleData,"ruleType"));
                        	coActRule.setRuleData(MapUtil.asStr(ruleData,"ruleData"));
                        	coActRule.setRuleMode(MapUtil.asStr(paramsMap,"ruleMode"));
                        	coActRule.setSortSeq(index);
                        	coActRule.setIsShow(MapUtil.asStr(ruleData,"isShow"));
                        	activityDao.insertActRules(coActRule);
            			}
            		}
            		index++;
            		if(ydRuleId != null){//翼豆扣减规则，直接更新
        				String YdNum = MapUtil.asStr(paramsMap,"YdNum");
                    	CoActRule coActRule = new CoActRule();
                    	coActRule.setRuleId(ydRuleId);                   	
                    	coActRule.setRuleData(">=" + YdNum);
                    	coActRule.setSortSeq(index);
                    	coActRule.setStatusCd(statusCd);
                    	activityDao.updateActRules(coActRule);
        			}
            	}
            	//更新抽奖频次
            	if(StringUtil.isEmptyStr(countRate)&&countRateRuleId!=null){//限制改成不限制
					paramMap = new HashMap<String,Object>();
					paramMap.put("activityId", activityId);
					paramMap.put("ruleId",countRateRuleId);
					activityDao.deleteActRules(paramMap);
				}else if(!StringUtil.isEmptyStr(countRate)&&countRateRuleId!=null){//修改限制次数
					CoActRule coActRule = new CoActRule();
					coActRule.setRuleId(countRateRuleId);
					coActRule.setRuleData(">=" + countRate);
					coActRule.setStatusCd(statusCd);
					activityDao.updateActRules(coActRule);
				}else if(!StringUtil.isEmptyStr(countRate)&&countRateRuleId==null){//不限制改成限制
					CoActRule coActRule = new CoActRule();
					coActRule.setActivityId(activityId);
					coActRule.setRuleCode(countRateCode);
					coActRule.setRuleTitle("抽奖频次限制");
					coActRule.setRuleDesc("抽奖频次限制");
					coActRule.setStatusCd(statusCd);
					coActRule.setRuleType("9");
					coActRule.setRuleData(">=" + countRate);
					coActRule.setRuleMode("");
					coActRule.setSortSeq(0);
					coActRule.setIsShow("0");
					activityDao.insertActRules(coActRule);
				}
            	//更新区域规则数据
            	List<Map<String,Object>> areaRelRules = (List<Map<String, Object>>) paramsMap.get("areaRelRules");
            	if(areaRelRules != null && areaRelRules.size() > 0){
            		//删除已删除的规则
    				paramMap = new HashMap<String,Object>();
            		paramMap.put("activityId", activityId);
            		List<CoActRuleData> coActRuleDatas = activityDao.queryActRuleData(paramMap);//查询已存在的区域规则数据
            		for(CoActRuleData coActRuleData:coActRuleDatas){
            			boolean delFlag = true;
            			for(Map<String,Object> areaRelRule:areaRelRules){
            				if(!StringUtil.isEmptyStr(MapUtil.asStr(areaRelRule,"expId"))){
            					Long expId = Long.parseLong(MapUtil.asStr(areaRelRule,"expId"));
                    			if(coActRuleData.getExpId() == expId){//存在，不需要删除
                    				delFlag = false;
                    				break;
                    			}
            				}            				
            			}
            			if(delFlag){//删除已删除的区域规则数据
            				activityDao.deleteActRuleData(coActRuleData.getExpId());
            			}
            		}            		
            		int index = 0;
            		for(Map<String,Object> ruleData:areaRelRules){
            			index++;
            			if(!StringUtil.isEmptyStr(MapUtil.asStr(ruleData,"expId"))){//修改
                			Long expId = Long.parseLong(MapUtil.asStr(ruleData,"expId"));
                			CoActRuleData coActRuleData = new CoActRuleData();
                			coActRuleData.setExpId(expId);
                			coActRuleData.setRuleType(MapUtil.asStr(ruleData,"ruleType"));
                			coActRuleData.setRuleData(MapUtil.asStr(ruleData,"regionCode"));
                			coActRuleData.setStatusCd(statusCd);
                			coActRuleData.setSortSeq(index);
                			activityDao.updateActRuleData(coActRuleData);
            			}else{
            				CoActRuleData coActRuleData = new CoActRuleData();            				
                			coActRuleData.setRuleId(coActRuleDatas.get(0).getRuleId());
                			coActRuleData.setRuleType(MapUtil.asStr(ruleData,"ruleType"));
                			coActRuleData.setRuleData(MapUtil.asStr(ruleData,"regionCode"));
                			coActRuleData.setStatusCd(statusCd);
                			coActRuleData.setSortSeq(index);
                			activityDao.insertActRuleData(coActRuleData);
            			}
            		}
            	}
            	
            	//修改奖品表
            	List<Map<String,Object>> prizes = (List<Map<String, Object>>) paramsMap.get("prizes");
            	if(prizes != null && prizes.size() > 0){
            		//删除已删除的奖品列表
        	        List<Map<String,Object>> prizeList = activityDao.queryPrizeList(activityId);
        	        for(Map<String,Object> prizeMap:prizeList){
            			boolean delFlag = true;
            			for(Map<String,Object> prize:prizes){
            				if(!StringUtil.isEmptyStr(MapUtil.asStr(prize,"prizeId"))){
                    			Long prizeId = Long.parseLong(MapUtil.asStr(prize,"prizeId"));
                    			Long luckId = Long.parseLong(MapUtil.asStr(prize,"luckId"));
                    			if(Long.parseLong(MapUtil.asStr(prizeMap,"PRIZE_ID")) == prizeId 
                    					&& Long.parseLong(MapUtil.asStr(prizeMap,"LUCK_ID")) == luckId){//存在，不需要删除
                    				delFlag = false;
                    				break;
                    			}
                			}
            			}
            			if(delFlag){//删除已删除的奖品列表
            				paramMap = new HashMap<String,Object>();
            				paramMap.put("activityId", activityId);
            				paramMap.put("luckId", Long.parseLong(MapUtil.asStr(prizeMap,"LUCK_ID")));
            				activityDao.deleteCoActPrize(Long.parseLong(MapUtil.asStr(prizeMap,"LUCK_ID")));
            				activityDao.deleteCoPrize(Long.parseLong(MapUtil.asStr(prizeMap,"PRIZE_ID")));
            				activityDao.deleteCoActLuck(paramMap);
            			}
            		}
            		for(Map<String,Object> prize:prizes){
            			if(!StringUtil.isEmptyStr(MapUtil.asStr(prize,"prizeId"))){//修改
            				//奖品表
                			Long prizeId = Long.parseLong(MapUtil.asStr(prize,"prizeId"));
                			CoPrize coPrize = new CoPrize();
                			coPrize.setPrizeTitle(MapUtil.asStr(prize,"prizeTitle"));
                			coPrize.setPrizeType(MapUtil.asStr(prize,"prizeType"));
                			coPrize.setPrizeDesc(MapUtil.asStr(prize,"prizeDesc"));
                			coPrize.setPrizeCount(Integer.parseInt(MapUtil.asStr(prize,"prizeCount")));
                			coPrize.setPrizeUse(MapUtil.asStr(prize,"prizeUse"));
                			coPrize.setProductCode(MapUtil.asStr(prize,"productCode"));
                			coPrize.setProdSupplier(MapUtil.asStr(prize,"prodSupplier"));
                			coPrize.setPrizeVal(MapUtil.asStr(prize,"prizeVal"));
                			coPrize.setStatusCd(statusCd);
                			coPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                			coPrize.setPrizeStock(Integer.parseInt(MapUtil.asStr(prize,"prizeStock")));
                			coPrize.setStartTime(DateUtil.getDayTime(startTime, DateUtil.FMT_DATE));
                			coPrize.setEndTime(DateUtil.getDayTime(endTime, DateUtil.FMT_DATE));
                			coPrize.setPrizeId(prizeId);
                			activityDao.updateCoPrize(coPrize);
                			
                        	Long luckId = Long.parseLong(MapUtil.asStr(prize,"luckId"));
                			//抽奖类活动       
                			CoActLuck coActLuck = new CoActLuck();
                        	coActLuck.setLuckLevel(MapUtil.asStr(prize,"luckLevel"));
                        	coActLuck.setLuckTitle(MapUtil.asStr(prize,"prizeTitle"));
                        	coActLuck.setLuckDesc(MapUtil.asStr(prize,"prizeDesc"));
                        	coActLuck.setWeightVal(Integer.parseInt(MapUtil.asStr(prize,"weightVal")));
                        	coActLuck.setStatusCd(statusCd);
                        	coActLuck.setSortSeq(Integer.parseInt(MapUtil.asStr(prize,"prizeIndex")));
                        	coActLuck.setLuckId(luckId);
                        	coActLuck.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                        	activityDao.updateCoActLuck(coActLuck);
                        	
                        	//活动奖品
                        	CoActPrize coActPrize = new CoActPrize();
                        	coActPrize.setPrizeId(prizeId);
                        	coActPrize.setLuckId(luckId);
                        	coActPrize.setUserPrizeCount(Integer.parseInt(MapUtil.asStr(prize,"usePrizeCount")));
                        	coActPrize.setStatusCd(statusCd);
                        	coActPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                        	activityDao.updateCoActPrize(coActPrize); 
            			}else{
            				//奖品表
                			CoPrize coPrize = new CoPrize();
                			coPrize.setPrizeTitle(MapUtil.asStr(prize,"prizeTitle"));
                			coPrize.setPrizeType(MapUtil.asStr(prize,"prizeType"));
                			coPrize.setPrizeDesc(MapUtil.asStr(prize,"prizeDesc"));
                			coPrize.setPrizeCount(Integer.parseInt(MapUtil.asStr(prize,"prizeCount")));
                			coPrize.setPrizeUse(MapUtil.asStr(prize,"prizeUse"));
                			coPrize.setProductCode(MapUtil.asStr(prize,"productCode"));
                			coPrize.setProdSupplier(MapUtil.asStr(prize,"prodSupplier"));
                			coPrize.setPrizeVal(MapUtil.asStr(prize,"prizeVal"));
                			coPrize.setStatusCd(statusCd);
                			coPrize.setStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                			coPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                			coPrize.setPrizeStock(Integer.parseInt(MapUtil.asStr(prize,"prizeStock")));
                			coPrize.setSellCount(0);
                			coPrize.setStartTime(DateUtil.getDayTime(startTime, DateUtil.FMT_DATE));
                			coPrize.setEndTime(DateUtil.getDayTime(endTime, DateUtil.FMT_DATE));
                			activityDao.insertCoPrize(coPrize);
                			
                			//抽奖类活动       
                			CoActLuck coActLuck = new CoActLuck();
                        	coActLuck.setActivityId(activityId);
                        	coActLuck.setLuckLevel(MapUtil.asStr(prize,"luckLevel"));
                        	coActLuck.setLuckTitle(MapUtil.asStr(prize,"prizeTitle"));
                        	coActLuck.setLuckDesc(MapUtil.asStr(prize,"prizeDesc"));
                        	coActLuck.setWeightVal(Integer.parseInt(MapUtil.asStr(prize,"weightVal")));
                        	coActLuck.setStatusCd(statusCd);
                        	coActLuck.setSortSeq(Integer.parseInt(MapUtil.asStr(prize,"prizeIndex")));
                        	coActLuck.setStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                        	coActLuck.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId"))); 
                        	activityDao.insertCoActLuck(coActLuck);
                        	
                        	//活动奖品
                        	CoActPrize coActPrize = new CoActPrize();
                        	coActPrize.setPrizeId(coPrize.getPrizeId());
                        	coActPrize.setLuckId(coActLuck.getLuckId());
                        	coActPrize.setUserPrizeCount(Integer.parseInt(MapUtil.asStr(prize,"usePrizeCount")));
                        	coActPrize.setStatusCd(statusCd);
                        	coActPrize.setStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                        	coActPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                        	activityDao.insertCoActPrize(coActPrize);
            			}        	
            		}
            	}  
            	
            	 //修改图片表
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
                				paramMap = new HashMap<String,Object>();
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
                                coActivityImg.setImgCode(MapUtil.asStr(bannerMap,"imgCode"));
                                coActivityImg.setImgId(Long.parseLong(MapUtil.asStr(bannerMap,"imgId")));
                                coActivityImg.setClassCode(MapUtil.asStr(bannerMap,"classCode"));
                                coActivityImg.setSortSeq(index);
                                coActivityImg.setStatusCd(statusCd);
                                coActivityImg.setActImgId(actImgId);
                                activityDao.updateActImg(coActivityImg);
                        	}else{
                        		CoActivityImg coActivityImg = new CoActivityImg();
                                coActivityImg.setActivityId(activityId);
                                coActivityImg.setImgCode(MapUtil.asStr(bannerMap,"imgCode"));
                                coActivityImg.setImgId(Long.parseLong(MapUtil.asStr(bannerMap,"imgId")));
                                coActivityImg.setClassCode(MapUtil.asStr(bannerMap,"classCode"));
                                coActivityImg.setSortSeq(index);
                                coActivityImg.setStatusCd(statusCd);
                                activityDao.insertActImg(coActivityImg);
                        	}                   
                        }
                    }
                }
                if("101".equals(statusCd)){//发布
                	syncActivity(activityId);
                }
                resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        	    resultMap.put(Constants.RESULT_MSG_STR,"修改转盘活动成功");        	
        	}else{//新增活动
        		String statusCd = MapUtil.asStr(paramsMap,"statusCd");//101正常，103待发布
            	//入活动表
            	CoActivity coActivity = new CoActivity();
            	coActivity.setParentId(0L);
            	coActivity.setActivityCd(Toolkit.getBusiSerialNumber("HD"));
            	coActivity.setActivityTitle(MapUtil.asStr(paramsMap,"activityTitle"));
				coActivity.setProbability(MapUtil.asStr(paramsMap,"probability"));
            	coActivity.setActivityDesc(MapUtil.asStr(paramsMap,"activityDesc"));
            	coActivity.setActivityRule(MapUtil.asStr(paramsMap,"activityRule"));
            	coActivity.setStatusCd(statusCd);
				coActivity.setShowTime(showTimtDt);
				coActivity.setStartTime(startTimeDt);
				coActivity.setEndTime(endTimeDt);
            	coActivity.setActivityMode("1");
            	String showRuleMode = MapUtil.asStr(paramsMap,"showRuleMode");
            	coActivity.setRuleMode(showRuleMode);
            	coActivity.setShowRuleMode(showRuleMode);
            	coActivity.setSeckillType("");
            	coActivity.setStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
            	coActivity.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
            	activityDao.insertActivity(coActivity);
            	long activityId = coActivity.getActivityId();
            	
            	//入活动规则
            	int index = 1;
            	List<Map<String,Object>> ruleDataArray = (List<Map<String, Object>>) paramsMap.get("ruleDataArray");
            	if(ruleDataArray != null && ruleDataArray.size() > 0){
            		for(Map<String,Object> ruleData:ruleDataArray){        			
                    	CoActRule coActRule = new CoActRule();
                    	coActRule.setActivityId(activityId);
                    	coActRule.setRuleCode(MapUtil.asStr(ruleData,"ruleCode"));                	
                    	coActRule.setRuleTitle(MapUtil.asStr(ruleData,"ruleTitle"));
                    	coActRule.setRuleDesc(MapUtil.asStr(ruleData,"ruleTitle"));
                    	coActRule.setStatusCd(statusCd);
                    	coActRule.setRuleType(MapUtil.asStr(ruleData,"ruleType"));
                    	coActRule.setRuleData(MapUtil.asStr(ruleData,"ruleData"));
                    	coActRule.setRuleMode(MapUtil.asStr(paramsMap,"ruleMode"));
                    	coActRule.setSortSeq(index);
                    	coActRule.setIsShow(MapUtil.asStr(ruleData,"isShow"));
                    	activityDao.insertActRules(coActRule);
                    	index++;
            		}        		
            	}
            	
            	String YdNum = MapUtil.asStr(paramsMap,"YdNum");
            	CoActRule coActRule = new CoActRule();
            	coActRule.setActivityId(activityId);
            	coActRule.setRuleCode(reduceYdCode);                	
            	coActRule.setRuleTitle("翼豆扣减");
            	coActRule.setRuleDesc("扣减规则");
            	coActRule.setStatusCd(statusCd);
            	coActRule.setRuleType("9");
            	coActRule.setRuleData(">=" + YdNum);
            	coActRule.setRuleMode(MapUtil.asStr(paramsMap,"ruleMode"));
            	coActRule.setSortSeq(index);
            	coActRule.setIsShow("1");
            	activityDao.insertActRules(coActRule);

				if(!StringUtil.isEmptyStr(countRate)){//不限制改成限制
					coActRule = new CoActRule();
					coActRule.setActivityId(activityId);
					coActRule.setRuleCode(countRateCode);
					coActRule.setRuleTitle("抽奖频次限制");
					coActRule.setRuleDesc("抽奖频次限制");
					coActRule.setStatusCd(statusCd);
					coActRule.setRuleType("9");
					coActRule.setRuleData(">=" + countRate);
					coActRule.setRuleMode("");
					coActRule.setSortSeq(0);
					coActRule.setIsShow("0");
					activityDao.insertActRules(coActRule);
				}
            	//入区域规则数据            	
            	List<Map<String,Object>> areaRelRules = (List<Map<String, Object>>) paramsMap.get("areaRelRules");
            	if(areaRelRules != null && areaRelRules.size() > 0){
            		index++;
            		Map<String,Object> areaRelRuleMap = areaRelRules.get(0);
            		coActRule = new CoActRule();
                	coActRule.setActivityId(activityId);
                	coActRule.setRuleCode(MapUtil.asStr(areaRelRuleMap,"ruleCode"));
                	coActRule.setRuleTitle("区域规则");
                	coActRule.setRuleDesc("区域规则");
                	coActRule.setStatusCd(statusCd);
                	coActRule.setRuleType(MapUtil.asStr(areaRelRuleMap,"ruleType"));
                	coActRule.setRuleData(">=1");
                	coActRule.setRuleMode(MapUtil.asStr(paramsMap,"ruleMode"));
                	coActRule.setSortSeq(index);
                	coActRule.setIsShow("1");
                	activityDao.insertActRules(coActRule);
            		
            		//入规则集合表
                	index = 1;
            		for(Map<String,Object> ruleData:areaRelRules){
            			CoActRuleData coActRuleData = new CoActRuleData();
            			coActRuleData.setRuleId(coActRule.getRuleId());
            			coActRuleData.setRuleType(MapUtil.asStr(ruleData,"ruleType"));
            			coActRuleData.setRuleData(MapUtil.asStr(ruleData,"regionCode"));
            			coActRuleData.setStatusCd(statusCd);
            			coActRuleData.setSortSeq(index);
            			activityDao.insertActRuleData(coActRuleData);
                    	index++;
            		}        		
            	}
            	
            	List<Map<String,Object>> prizes = (List<Map<String, Object>>) paramsMap.get("prizes");
            	if(prizes != null && prizes.size() > 0){
            		for(Map<String,Object> prizeMap:prizes){
            			//奖品表
            			CoPrize coPrize = new CoPrize();
            			coPrize.setPrizeTitle(MapUtil.asStr(prizeMap,"prizeTitle"));
            			coPrize.setPrizeType(MapUtil.asStr(prizeMap,"prizeType"));
            			coPrize.setPrizeDesc(MapUtil.asStr(prizeMap,"prizeDesc"));
            			coPrize.setPrizeCount(Integer.parseInt(MapUtil.asStr(prizeMap,"prizeCount")));
            			coPrize.setPrizeUse(MapUtil.asStr(prizeMap,"prizeUse"));
            			coPrize.setProductCode(MapUtil.asStr(prizeMap,"productCode"));
            			coPrize.setProdSupplier(MapUtil.asStr(prizeMap,"prodSupplier"));
            			coPrize.setPrizeVal(MapUtil.asStr(prizeMap,"prizeVal"));
            			coPrize.setStatusCd(statusCd);
            			coPrize.setStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
            			coPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
            			coPrize.setPrizeStock(Integer.parseInt(MapUtil.asStr(prizeMap,"prizeStock")));
            			coPrize.setSellCount(0);
            			coPrize.setStartTime(DateUtil.getDayTime(startTime, DateUtil.FMT_DATE));
            			coPrize.setEndTime(DateUtil.getDayTime(endTime, DateUtil.FMT_DATE));
            			activityDao.insertCoPrize(coPrize);
            			
            			//抽奖类活动       
            			CoActLuck coActLuck = new CoActLuck();
                    	coActLuck.setActivityId(activityId);
                    	coActLuck.setLuckLevel(MapUtil.asStr(prizeMap,"luckLevel"));
                    	coActLuck.setLuckTitle(MapUtil.asStr(prizeMap,"prizeTitle"));
                    	coActLuck.setLuckDesc(MapUtil.asStr(prizeMap,"prizeDesc"));
                    	coActLuck.setWeightVal(Integer.parseInt(MapUtil.asStr(prizeMap,"weightVal")));
                    	coActLuck.setStatusCd(statusCd);
                    	coActLuck.setSortSeq(Integer.parseInt(MapUtil.asStr(prizeMap,"prizeIndex")));
                    	coActLuck.setStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                    	coActLuck.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));                    	
                    	activityDao.insertCoActLuck(coActLuck);
                    	
                    	//活动奖品
                    	CoActPrize coActPrize = new CoActPrize();
                    	coActPrize.setPrizeId(coPrize.getPrizeId());
                    	coActPrize.setLuckId(coActLuck.getLuckId());
                    	coActPrize.setUserPrizeCount(Integer.parseInt(MapUtil.asStr(prizeMap,"usePrizeCount")));
                    	coActPrize.setStatusCd(statusCd);
                    	coActPrize.setStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                    	coActPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
                    	activityDao.insertCoActPrize(coActPrize);                	
            		}
            	}  
            	
            	 //图片表
                if(paramsMap.get("banners")!=null){
                    List<Map<String,Object>> bannerList = (List<Map<String,Object>>)paramsMap.get("banners");
                    if(bannerList != null && bannerList.size()>0) {
                        index = 0;
                        for (Map<String,Object> bannerMap : bannerList) {
                            index++;
                            CoActivityImg coActivityImg = new CoActivityImg();
                            coActivityImg.setActivityId(activityId);
                            coActivityImg.setImgCode(MapUtil.asStr(bannerMap,"imgCode"));
                            coActivityImg.setImgId(Long.parseLong(MapUtil.asStr(bannerMap,"imgId")));
                            coActivityImg.setClassCode(MapUtil.asStr(bannerMap,"classCode"));
                            coActivityImg.setSortSeq(index);
                            coActivityImg.setStatusCd(statusCd);
                            activityDao.insertActImg(coActivityImg);                        
                        }
                    }
                }
                if("101".equals(statusCd)){//发布
                	syncActivity(activityId);
                }
                resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
        	    resultMap.put(Constants.RESULT_MSG_STR,"新增转盘活动成功");
        	}         
        } catch (Exception e) {
            log.error("新增转盘活动时异常:",e);
            throw new RuntimeException(e.getMessage());
        }
		return resultMap;
	}

    @Override
    public Map<String, Object> editActivities(Map<String, Object> paramsMap) throws Exception {
        Map<String, Object> resultMap=new HashMap<String, Object>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"修改转盘活动失败");
        String endTime = MapUtil.asStr(paramsMap,"endTime");
        Date endTimeDt=DateUtil.getDayTime(endTime, DateUtil.FMT_DATE);
		try {
			Long activityId = Long.parseLong(MapUtil.asStr(paramsMap, "activityId"));
			//更新活动表
			CoActivity coActivity = new CoActivity();
			coActivity.setProbability(MapUtil.asStr(paramsMap,"probability"));
			coActivity.setActivityDesc(MapUtil.asStr(paramsMap,"activityDesc"));
			coActivity.setActivityRule(MapUtil.asStr(paramsMap,"activityRule"));
			coActivity.setEndTime(endTimeDt);
			coActivity.setActivityId(activityId);
			coActivity.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
			activityDao.updateActivity(coActivity);
			//修改奖品表
			List<Map<String,Object>> prizes = (List<Map<String, Object>>) paramsMap.get("prizes");
			List<Map<String,Object>> rightsRel=new ArrayList<>();
			if(prizes != null && prizes.size() > 0){
				for(Map<String,Object> prize:prizes){
					if(!StringUtil.isEmptyStr(MapUtil.asStr(prize,"prizeId"))){//修改
						//奖品表
						Long prizeId = Long.parseLong(MapUtil.asStr(prize,"prizeId"));
						CoPrize coPrize = new CoPrize();
						coPrize.setPrizeCount(Integer.parseInt(MapUtil.asStr(prize,"prizeCount")));
						coPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
						coPrize.setPrizeStock(Integer.parseInt(MapUtil.asStr(prize,"prizeStock")));
						coPrize.setEndTime(DateUtil.getDayTime(endTime, DateUtil.FMT_DATE));
						coPrize.setPrizeId(prizeId);
						activityDao.updateCoPrize(coPrize);
						Long luckId = Long.parseLong(MapUtil.asStr(prize,"luckId"));
						//抽奖类活动
						CoActLuck coActLuck = new CoActLuck();
						coActLuck.setWeightVal(Integer.parseInt(MapUtil.asStr(prize,"weightVal")));
						coActLuck.setLuckId(luckId);
						coActLuck.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
						activityDao.updateCoActLuck(coActLuck);
						String prizeType=MapUtil.asStr(prize,"prizeType");
						String isEditStock=MapUtil.asStr(prize,"isEditStock");
						if("1".equals(prizeType)||"5".equals(prizeType)){
							continue;
						}
						if("1".equals(isEditStock)) {
							//封装修改同步数据
							Map<String, Object> secPro = new HashMap<String, Object>();
							secPro.put("rightsCode", MapUtil.asStr(prize, "productCode"));
							secPro.put("useNum", coPrize.getPrizeCount());
							rightsRel.add(secPro);
						}
					}
				}
			}
			String countRate = MapUtil.asStr(paramsMap,"countRate");
			//更新抽奖频次
			Map paramMap = new HashMap<String,Object>();
			paramMap.put("activityId", activityId);
			List<CoActRule> coActRules = activityDao.queryActRules(paramMap);//查询已存在的规则
			Long countRateRuleId=null;
			for(CoActRule coActRule:coActRules){
				if(countRateCode.equals(coActRule.getRuleCode())){//抽奖频次
					countRateRuleId = coActRule.getRuleId();
					break;
				}
			}
			if(StringUtil.isEmptyStr(countRate)&&countRateRuleId!=null){//限制改成不限制
				paramMap = new HashMap<String,Object>();
				paramMap.put("activityId", activityId);
				paramMap.put("ruleId",countRateRuleId);
				activityDao.deleteActRules(paramMap);
			}else if(!StringUtil.isEmptyStr(countRate)&&countRateRuleId!=null){//修改限制次数
				CoActRule coActRule = new CoActRule();
				coActRule.setRuleId(countRateRuleId);
				coActRule.setRuleData(">=" + countRate);
				activityDao.updateActRules(coActRule);
			}else if(!StringUtil.isEmptyStr(countRate)&&countRateRuleId==null){//不限制改成限制
				CoActRule coActRule = new CoActRule();
				coActRule.setActivityId(activityId);
				coActRule.setRuleCode(countRateCode);
				coActRule.setRuleTitle("抽奖频次限制");
				coActRule.setRuleDesc("抽奖频次限制");
				coActRule.setStatusCd("101");
				coActRule.setRuleType("9");
				coActRule.setRuleData(">=" + countRate);
				coActRule.setRuleMode("");
				coActRule.setSortSeq(0);
				coActRule.setIsShow("0");
				activityDao.insertActRules(coActRule);
			}
			activityModify(activityId,rightsRel);
			resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
			resultMap.put(Constants.RESULT_MSG_STR,"修改转盘活动成功");
        } catch (Exception e) {
            log.error("修改转盘活动时异常:",e);
            throw new RuntimeException(e.getMessage());
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
            String activityTypeCode="1";
            String activityTypeName="转盘";
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
            if(rightsRel!=null&&rightsRel.size()>0) {
                params.put("rightsRel", rightsRel);
            }
            Map<String,Object> resultMap=thirdInterface.activityModify(params);
            if (!Constants.CODE_SUCC.equals(resultMap.get("resultCode"))) {
                throw new RuntimeException("同步修改活动接口调用失败，修改失败");
            }
        }else{
            throw new RuntimeException("同步修改活动接口调用失败，修改失败");
        }
    }
    @Override
	public Map<String, Object> qryActLuck(Map<String, Object> paramsMap)throws Exception {
		Map<String,Object> resultMap=new HashMap<>();
        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR,"不存在该活动");
		try{	       
	        String[] fields = {"activityId"};
	        Map<String, Object> validationResult = FieldValidation.validation(paramsMap, fields);
	        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
	            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
	            return resultMap;
	        }
	        List<CoActivity> coActivities=activityDao.queryActivityList(paramsMap);
	        if(coActivities==null||coActivities.size()==0){
	            return resultMap;
	        }
	        CoActivity coActivity = coActivities.get(0);
	        Map<String, Object> ruleParam=new HashMap<String, Object>();
            ruleParam.put("ruleCode",reduceYdCode);
            ruleParam.put("activityId",coActivity.getActivityId());
            List<CoActRule> rule=activityDao.queryActRules(ruleParam);
            if(rule != null && rule.size() > 0){
            	 String ruleData=rule.get(0).getRuleData();
                 ruleData=ruleData.replaceAll(">","");
                 ruleData=ruleData.replaceAll("=","");
                 ruleData=ruleData.replaceAll("<","");
                 coActivity.setUseAccount(Integer.parseInt(ruleData));
            }
	        resultMap.put("coActivity",coActivity);
	       
	        if(!StringUtil.isEmptyStr(coActivities.get(0).getShowRuleMode())) {
	            List<CoActRule> coActRules = activityDao.queryActRules(paramsMap);
	            resultMap.put("coActRules", coActRules);
	            for(CoActRule coActRule:coActRules){
	            	if(areaRuleType.equals(coActRule.getRuleType())){//区域，取规则集合表
	            		paramsMap.put("ruleType", areaRuleType);
	            		List<CoActRuleData> actRuleDatas = activityDao.queryActRuleData(paramsMap);
	            		resultMap.put("actRuleDatas", actRuleDatas);
	            	}
	            }
	        }
	        List<Map<String,Object>> prizeList = activityDao.queryPrizeList(coActivity.getActivityId());
	        resultMap.put("prizeList", prizeList);
	        List<CoActivityImg> coActivityImgs = activityDao.getCoActivityImgList(paramsMap);
	        resultMap.put("coActivityImgs",coActivityImgs);
	        resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
	        resultMap.put(Constants.RESULT_MSG_STR,"查询转盘活成功");
	        return resultMap;	    
		}catch(Exception ex){
			log.error("查询转盘活动异常", ex);
			resultMap.put(Constants.RESULT_MSG_STR,"查询转盘活异常");
		}
		return resultMap;
	}

	/**
	 * 删除转盘活动信息
	 */
	@Override
	public Map<String, Object> deleteActInfo(Map<String, Object> paramsMap)throws Exception {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
	    resultMap.put(Constants.RESULT_MSG_STR,"删除转盘活动失败");
        try {
        	String[] fields = {"activityId"};
 	        Map<String, Object> validationResult = FieldValidation.validation(paramsMap, fields);
 	        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
 	            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
 	            return resultMap;
 	        }
            Long activityId = Long.parseLong(MapUtil.asStr(paramsMap,"activityId"));
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("activityId", activityId);
        	//删除活动表
        	activityDao.deleteActivity(activityId);
        	//删除活动规则
        	activityDao.deleteActRules(paramMap);
        	
        	paramMap.put("ruleType", areaRuleType);
        	List<CoActRuleData> coActRuleDatas = activityDao.queryActRuleData(paramMap);//查询已存在的区域规则数据
    		for(CoActRuleData coActRuleData:coActRuleDatas){
    			activityDao.deleteActRuleData(coActRuleData.getExpId());
    		}
    		paramMap.remove("ruleType");
        	//删除抽奖活动奖品
        	List<Map<String,Object>> prizeList = activityDao.queryPrizeList(activityId);
        	if(prizeList != null && prizeList.size() > 0){
        		for(Map<String,Object> prizeMap:prizeList){
        			Long luckId = Long.parseLong(MapUtil.asStr(prizeMap, "LUCK_ID"));
        			Long prizeId = Long.parseLong(MapUtil.asStr(prizeMap, "PRIZE_ID"));
        			activityDao.deleteCoActPrize(luckId);
        			activityDao.deleteCoPrize(prizeId);
        		}
        		activityDao.deleteCoActLuck(paramMap);
        	}
        	//删除图片表
			activityDao.deleteActImg(paramMap);
            resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
    	    resultMap.put(Constants.RESULT_MSG_STR,"删除转盘活动成功");            
        } catch (Exception e) {
            log.error("删除转盘活动时异常:",e);
            throw new RuntimeException(e.getMessage());
        }
		return resultMap;
	}

	@Override
	public Map<String, Object> updateActstatusCd(Map<String, Object> paramsMap) throws Exception {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
	    resultMap.put(Constants.RESULT_MSG_STR,"更新活动状态失败");
        try {
        	String[] fields = {"activityId","statusCd"};
 	        Map<String, Object> validationResult = FieldValidation.validation(paramsMap, fields);
 	        if (!Constants.RESULT_SUCC.equals(validationResult.get(Constants.RESULT_CODE_STR))) {
 	            resultMap.put(Constants.RESULT_MSG_STR, validationResult.get(Constants.RESULT_MSG_STR));
 	            return resultMap;
 	        }
            Long activityId = Long.parseLong(MapUtil.asStr(paramsMap,"activityId"));
            String statusCd = MapUtil.asStr(paramsMap,"statusCd");
            CoActivity coActivity = new CoActivity();
            coActivity.setStatusCd(statusCd);
            coActivity.setActivityId(activityId);
            activityDao.updateActivity(coActivity);
            
            CoActRule coActRule = new CoActRule();
            coActRule.setActivityId(activityId);
            coActRule.setStatusCd(statusCd);
            activityDao.updateActRule(coActRule);
            
            paramsMap.put("ruleType", areaRuleType);
        	List<CoActRuleData> coActRuleDatas = activityDao.queryActRuleData(paramsMap);//查询已存在的区域规则数据
    		for(CoActRuleData coActRuleData:coActRuleDatas){
    			coActRuleData.setStatusCd(statusCd);
    			activityDao.updateActRuleData(coActRuleData);
    		}
    		paramsMap.remove("ruleType");
          
            List<Map<String,Object>> prizeList = activityDao.queryPrizeList(coActivity.getActivityId());
            if(prizeList != null && prizeList.size() > 0){
        		for(Map<String,Object> prizeMap:prizeList){
        			Long luckId = Long.parseLong(MapUtil.asStr(prizeMap, "LUCK_ID"));
        			Long prizeId = Long.parseLong(MapUtil.asStr(prizeMap, "PRIZE_ID"));
        			CoActPrize coActPrize = new CoActPrize();
        			coActPrize.setLuckId(luckId);
        			coActPrize.setPrizeId(prizeId);
        			coActPrize.setStatusCd(statusCd);
        			coActPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
        			activityDao.updateCoActPrize(coActPrize);
        			
        			CoPrize coPrize = new CoPrize();
        			coPrize.setStatusCd(statusCd);
        			coPrize.setPrizeId(prizeId);
        			coPrize.setEditStaffId(Long.parseLong(MapUtil.asStr(paramsMap,"staffId")));
        			activityDao.updateCoPrize(coPrize);
        			
        			CoActLuck coActLuck = new CoActLuck();
        			coActLuck.setStatusCd(statusCd);
        			coActLuck.setLuckId(luckId);
            		activityDao.updateCoActLuck(coActLuck);
        		}
        	}
            
            CoActivityImg coActivityImg = new CoActivityImg();
            coActivityImg.setStatusCd(statusCd);
            coActivityImg.setActivityId(activityId);
            activityDao.updateActImg(coActivityImg);
            if("101".equals(statusCd)){//发布
            	syncActivity(activityId);
            }else if("104".equals(statusCd)){//下架调用接口释放库存
				List<Map<String, Object>> rightsRel = new ArrayList<>();
				for (Map<String, Object> prizeMap : prizeList) {
					String prizeType = MapUtil.asStr(prizeMap, "PRIZE_TYPE");
					if ("1".equals(prizeType) || "5".equals(prizeType)) {//过滤翼豆和奖品次数
						continue;
					}
					Map<String, Object> actPro = new HashMap<String, Object>();
					actPro.put("rightsCode", prizeMap.get("PRODUCT_CODE"));
					actPro.put("useNum", prizeMap.get("PRIZE_COUNT"));
					rightsRel.add(actPro);
				}
				if(rightsRel.size()>0) {//有权益商品时才需要释放权益库存
					Map<String, Object> actParam = new HashMap();
					actParam.put("activityId", activityId);
					List<CoActivity> list = activityDao.queryActivityList(actParam);
					try {
						//删除活动接口，如果调用失败，需要等权益平台自动释放库存
						Map<String, Object> terminate = new HashMap<>();
						terminate.put("activityCode", list.get(0).getActivityCd());
						thirdInterface.activityTerminate(terminate);
					} catch (Exception e) {
						log.error("活动删除失败：" + list.get(0).getActivityCd());
					}
				}
			}
            resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
    	    resultMap.put(Constants.RESULT_MSG_STR,"更新活动状态成功");            
        } catch (Exception e) {
            log.error("更新活动状态异常:",e);
            throw new RuntimeException(e.getMessage());
        }
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CoRegion> queryCoRegionList(Map<String, Object> paramsMap) {
		List<CoRegion> coRegionList = null;
		try{
			if(redisUtil.getRedisForKey("coRegionList") != null) {
				coRegionList = (List<CoRegion>) redisUtil.getRedisForKey("coRegionList");
			}else{
				coRegionList = coRegionDao.queryCoRegionList(paramsMap);
        		redisUtil.addRedisInfo("coRegionList", coRegionList, 60);
			}
			return coRegionList;
		}catch(Exception ex){
			log.error("获取地市列表异常", ex);
			return null;
		}
	}
	
	/**
     * 活动同步
     * @param activityId
     * @throws Exception
     */
    private void syncActivity(long activityId)throws Exception{
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("activityId",activityId);
        List<CoActivity> activities = activityDao.queryActivityList(paramMap);
        if(activities != null && activities.size() > 0) {
            CoActivity coActivity = activities.get(0);
            String activityCode = coActivity.getActivityCd();
            String acivityName = coActivity.getActivityTitle();
            String activityTypeCode = "1";
            String activityTypeName = "抽奖";
            String activityDesc = coActivity.getActivityDesc();
            String createdate = DateUtil.format(new Date(), DateUtil.DATE_FORMATE_STRING_DEFAULT);
            String startDate = DateUtil.format(coActivity.getStartTime(), DateUtil.DATE_FORMATE_STRING_DEFAULT);
            String endDate = DateUtil.format(coActivity.getEndTime(), DateUtil.DATE_FORMATE_STRING_DEFAULT);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("activityId", activityId);
            param.put("ruleType", "8");
            List<CoActRuleData> areas = activityDao.queryActRuleData(param);
            List<Integer> areaRel = new ArrayList<>();
            for (CoActRuleData coActRuleData : areas) {
                areaRel.add(Integer.parseInt(coActRuleData.getRuleData()));
            }
            List<Map<String, Object>> prizeList = activityDao.queryPrizeList(activityId);
            List<Map<String, Object>> rightsRel = new ArrayList<>();
            for (Map<String, Object> prizeMap : prizeList) {
                String prizeType = MapUtil.asStr(prizeMap, "PRIZE_TYPE");
                if ("1".equals(prizeType) || "5".equals(prizeType)) {//过滤翼豆和奖品次数
                    continue;
                }
                Map<String, Object> actPro = new HashMap<String, Object>();
                actPro.put("rightsCode", prizeMap.get("PRODUCT_CODE"));
                actPro.put("useNum", prizeMap.get("PRIZE_COUNT"));
                rightsRel.add(actPro);
            }
			//无权益商品时不走同步接口
            if (rightsRel.size() > 0) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("activityCode", activityCode);
                params.put("acivityName", acivityName);
                params.put("activityTypeCode", activityTypeCode);
                params.put("activityTypeName", activityTypeName);
                if (StringUtil.isEmptyStr(activityDesc)) {
                    activityDesc = acivityName;
                }
                params.put("activityDesc", activityDesc);
                params.put("createdate", createdate);
                params.put("startDate", startDate);
                params.put("endDate", endDate);
                params.put("areaRel", areaRel);
                params.put("rightsRel", rightsRel);
                Map<String, Object> resultMap = thirdInterface.activitySync(params);
                if (!Constants.CODE_SUCC.equals(resultMap.get("resultCode"))) {
                    throw new RuntimeException("同步活动接口调用失败，不能发布");
                }
            }
            //刷新规则缓存
            try {
                List<String> ruleFroms = new ArrayList<String>();
                ruleFroms.add("101");
                taskCenterBmo.reloadRuleRedisInfo(ruleFroms);
            } catch (Exception e) {
            }
        } else {
            throw new RuntimeException("同步活动查询失败，不能发布");
        }
    }

	@Override
	public CoActivity queryActivity(Map<String, Object> paramsMap) {
		try{
			List<CoActivity> coActivities = activityDao.queryActivityList(paramsMap);
	        if(coActivities != null && coActivities.size() > 0){
	            return coActivities.get(0);
	        }
		}catch(Exception ex){
			log.error("获取活动信息异常：", ex);
		}
		return null;
	}
}
