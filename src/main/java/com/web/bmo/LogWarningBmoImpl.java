package com.web.bmo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.web.common.Constants;
import com.web.dao.LogWarningDao;
import com.web.model.LogWarning;
import com.web.thirdinterface.ThirdBSSInterface;
import com.web.util.MapUtil;

/**
 * 创建人：yibo
 * 类描述：告警记录
 * 创建时间：2020年4月10日上午11:19:40
 */
@Service("com.web.bmo.LogWarningBmoImpl")
public class LogWarningBmoImpl implements LogWarningBmo {
	
    private static final Logger log = LoggerFactory.getLogger(LogWarningBmoImpl.class);

    @Autowired
    private LogWarningDao logWarningDao;    
    
    @Resource
	private ThirdBSSInterface thirdBSSInterface;
    
	@Override
	public Map<String, Object> insertLogWarning(Map<String, Object> paramsMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
	    resultMap.put(Constants.RESULT_MSG_STR,"入告警记录失败");
        try {
        	LogWarning logWarning = new LogWarning();
        	//查询未处理告警记录是否存在
        	logWarning.setMemberId(Long.parseLong(MapUtil.asStr(paramsMap,"MEMBER_ID")));
        	logWarning.setOrderId(Long.parseLong(MapUtil.asStr(paramsMap,"ORDER_ID")));
        	logWarning.setOptResult("101");
        	List<LogWarning> queryLogWarningList = logWarningDao.queryLogWarningList(logWarning);
        	if(queryLogWarningList == null || queryLogWarningList.size() <= 0){
            	logWarning.setWarningType("101");
				String sendContent = "权益编码：" + paramsMap.get("PRODUCT_CODE") + "，" + "权益名称：" + paramsMap.get("PRODUCT_TITLE") + "，" + paramsMap.get("resultMsg");				
				logWarning.setWarningContent(sendContent);
            	logWarning.setAddTime(new Date());
            	logWarning.setStatusDate(new Date());
            	logWarning.setUdateDate(new Date());
            	logWarning.setStatusCd("101");
    			String buyType = MapUtil.asStr(paramsMap,"BUY_TYPE");
            	if("2".equals(buyType) || "3".equals(buyType)){
                	logWarning.setBusiType("1");//活动
    			}else if("4".equals(buyType)){
    				logWarning.setBusiType("2");//任务
    			}else{
    				logWarning.setBusiType("0");//其他
    			}
            	if(!StringUtil.isEmpty(MapUtil.asStr(paramsMap,"PRODUCT_ID"))){
                	logWarning.setBusiId(Long.parseLong(MapUtil.asStr(paramsMap,"PRODUCT_ID")));
            	}
            	logWarningDao.insertLogWarning(logWarning);
            	
            	//发送告警短信
    			try{
    				List<String> sendNbrList = new ArrayList<String>();
    				sendNbrList.add("18905910571");
    				sendNbrList.add("18903118608");
    				Map<String,Object> msgMap = null;
    				for(String sendNbr:sendNbrList){
    					msgMap = new HashMap<String,Object>();
    					msgMap.put("telPhone", sendNbr);
        				msgMap.put("msgContent", sendContent);
        				msgMap.put("sceneId", "95769");
        				thirdBSSInterface.sendSmsCodeMsg(msgMap);
    				}
    			}catch(Exception e){
    				log.error("告警短信发送异常：", e);
    			}
        	}
        } catch (Exception e) {
            log.error("入告警记录时异常:",e);
            throw new RuntimeException(e.getMessage());
        }
		return resultMap;	
	}

	@Override
	public Map<String, Object> qryMonitorList(Map<String, Object> paramsMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(paramsMap,"page");
        int pageSize = MapUtil.asInt(paramsMap,"limit");
		try{
			PageHelper.startPage(pageNum, pageSize);
			List<Map<String, Object>> list = logWarningDao.qryMonitorList(paramsMap);
	        PageInfo<Map<String, Object>> info = new PageInfo<>(list);
	        resultMap.put("data",info.getList());
	        resultMap.put("count",info.getTotal());
	        return resultMap;
		}catch(Exception ex){
			log.error("查询监控告警记录异常：", ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> updateLogWarning(Map<String, Object> paramsMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_FAIL);
	    resultMap.put(Constants.RESULT_MSG_STR,"处理告警失败");
        try {
        	List<Map<String,Object>> warningDatas = (List<Map<String, Object>>) paramsMap.get("warningDatas");
        	if(warningDatas != null && warningDatas.size() > 0){
        		for(Map<String,Object> warningData:warningDatas){
        			Long warningId = Long.parseLong(MapUtil.asStr(warningData,"warningId"));
    	            String optRemark = MapUtil.asStr(warningData,"warningRemark");
    	            String optStaff = MapUtil.asStr(paramsMap,"staffId");            
    	            LogWarning logWarning = new LogWarning();
    	            logWarning.setWarningId(warningId);
    	            logWarning.setOptResult("102");
    	            logWarning.setOptRemark(optRemark);
    	            logWarning.setOptStaff(optStaff);           
    	            logWarningDao.updateLogWarning(logWarning);
        		}
        	}           
            resultMap.put(Constants.RESULT_CODE_STR,Constants.RESULT_SUCC);
    	    resultMap.put(Constants.RESULT_MSG_STR,"处理告警成功");            
        } catch (Exception e) {
            log.error("处理告警异常:",e);
            throw new RuntimeException(e.getMessage());
        }
		return resultMap;	
	}
}
