package com.web.bmo;

import java.util.List;
import java.util.Map;

import com.web.model.CoActivity;
import com.web.model.CoRegion;

public interface ActLuckBmo {
    Map<String, Object> qryLotteryList(Map<String, Object> param) throws Exception;
    
	public Map<String, Object> saveActivities(Map<String, Object> paramsMap) throws Exception;

	Map<String, Object> editActivities(Map<String, Object> paramsMap) throws Exception;
	
	public Map<String, Object> qryActLuck(Map<String, Object> paramsMap)throws Exception;

	public Map<String, Object> deleteActInfo(Map<String, Object> paramsMap) throws Exception;

	public Map<String, Object> updateActstatusCd(Map<String, Object> paramsMap) throws Exception;
	
	public List<CoRegion> queryCoRegionList(Map<String,Object> paramsMap);
	
	public CoActivity queryActivity(Map<String,Object> paramsMap);
}
