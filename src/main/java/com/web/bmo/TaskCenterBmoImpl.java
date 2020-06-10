package com.web.bmo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.common.PropertyToRedis;
import com.web.dao.TaskCenterDao;
import com.web.model.CoActivity;
import com.web.model.CoRegion;
import com.web.model.CoRuleData;
import com.web.model.CoTask;
import com.web.model.CoTaskGroup;
import com.web.model.CoTaskImg;
import com.web.model.CoTaskReward;
import com.web.model.CoTaskRule;
import com.web.model.RuleCommonBean;
import com.web.model.RuleGroupCommonBean;
import com.web.thirdinterface.ThirdQYInterface;
import com.web.util.DateUtil;
import com.web.util.JsonUtil;
import com.web.util.RedisUtil;
import com.web.util.StringUtil;
import com.web.util.Toolkit;
import com.web.util.common.Log;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.bmo.TaskCenterBmoImpl.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务中心<br>
 * <b>创建时间：</b>2020年3月31日-下午3:05:12<br>
 */

@Service("com.web.bmo.TaskCenterBmoImpl")
public class TaskCenterBmoImpl implements TaskCenterBmo {
	
	private final Log log =Log.getLog(getClass());
	
	@Autowired
    @Qualifier("com.web.dao.TaskCenterDao")
    private TaskCenterDao taskCenterDao;
	
	@Autowired
	@Qualifier("com.web.common.PropertyToRedis")
	protected PropertyToRedis propertyToRedis;
	
	@Resource
	private ThirdQYInterface thirdInterface;
	
	@Autowired
   	@Qualifier("RedisUtil")
   	private RedisUtil redisUtil;
	
	/**查询任务分组数据信息*/
	public Map<String, Object> getTaskGroupList(Map<String,Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoTaskGroup> taskGroupList=this.taskCenterDao.getTaskGroupList(paramsMap);
		
		PageInfo<CoTaskGroup> info = new PageInfo<CoTaskGroup>(taskGroupList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	/**查询分组信息*/
	public CoTaskGroup getTaskGroupInfo(Map<String, Object> paramsMap) throws Exception{
		return this.taskCenterDao.getTaskGroupInfo(paramsMap);
	}
	
	/**新增分组*/
	public Map<String, Object> addTaskGroupInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "新增分组信息失败");
		
		paramsMap.put("groupCode", Toolkit.getFlowNum("TG", "yyMMddHHmm"));
		
		Integer addResult=this.taskCenterDao.addTaskGroupInfo(paramsMap);
		
		if(addResult!=null && addResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "新增分组信息成功");
		}
		
		return turnMap;
	}
	
	/**修改分组信息*/
	public Map<String, Object> editTaskGroupInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改分组信息失败");
		
		Integer editResult=this.taskCenterDao.editTaskGroupInfo(paramsMap);
		
		if(editResult!=null && editResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改分组信息成功");
		}
		
		return turnMap;
	}
	
	/**查询所有任务分组信息*/
	public List<CoTaskGroup> getAllTaskGroups(Map<String, Object> paramsMap) throws Exception{
		return this.taskCenterDao.getTaskGroupList(paramsMap);
	}
	
	/**查询任务数据信息*/
	public Map<String, Object> getTaskList(Map<String,Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoTask> taskList=this.taskCenterDao.getTaskInfos(paramsMap);
		
		PageInfo<CoTask> info = new PageInfo<CoTask>(taskList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	/**查询任务信息*/
	public CoTask getTaskInfo(Map<String, Object> paramsMap) throws Exception{
		List<CoTask> taskList=this.taskCenterDao.getTaskInfos(paramsMap);
		
		if(taskList!=null && !taskList.isEmpty()){
			return taskList.get(0);
		}
			
		return null;
	}
	
	/**新增任务数据*/
	public Map<String, Object> addTaskInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "新增任务信息失败");
		
		paramsMap.put("taskCode", Toolkit.getFlowNum("TK", "yyMMddHHmm"));
		
		Integer addResult=this.taskCenterDao.addTaskInfo(paramsMap);
		
		if(addResult!=null && addResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "新增任务信息成功");
		}
		
		return turnMap;
	}
	
	/**修改任务数据*/
	public Map<String, Object> editTaskInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改任务信息失败");
		
		Integer addResult=this.taskCenterDao.editTaskInfo(paramsMap);
		
		if(addResult!=null && addResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改任务信息成功");
		}
		
		return turnMap;
	}
	
	/**修改任务状态*/
	public Map<String, Object> editTaskStatus(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改任务状态失败");
		
		Integer addResult=this.taskCenterDao.editTaskStatus(paramsMap);
		
		if(addResult!=null && addResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改任务状态成功");
		}
		
		return turnMap;
	}
	
	/**查询任务规则列表*/
	public Map<String, Object> getTaskRuleList(Map<String,Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoTaskRule> taskRuleList=this.taskCenterDao.getTaskRuleInfos(paramsMap);
		
		PageInfo<CoTaskRule> info = new PageInfo<CoTaskRule>(taskRuleList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	public List<CoTaskRule> getTaskRuleInfos(Map<String, Object> paramsMap) throws Exception{
		List<CoTaskRule> taskRuleList=this.taskCenterDao.getTaskRuleInfos(paramsMap);
		return taskRuleList;
	}
	
	/**查询任务规则信息*/
	public CoTaskRule getTaskRuleInfo(Map<String, Object> paramsMap) throws Exception{
		List<CoTaskRule> taskRuleList=this.taskCenterDao.getTaskRuleInfos(paramsMap);
		
		if(taskRuleList!=null && !taskRuleList.isEmpty()){
			return taskRuleList.get(0);
		}
			
		return null;
	}
	
	/**新增任务或任务奖品规则*/
	public Map<String, Object> addTaskOrRewardRuleInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存规则信息失败");
		
		//生成编码
		paramsMap.put("ruleCode", Toolkit.getFlowNum("TR","yyMMddHHmm"));
		
		Integer addResult=this.taskCenterDao.addTaskOrRewardRuleInfo(paramsMap);
		
		if(addResult!=null && addResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "保存规则信息成功");
		}
		
		return turnMap;
	}
	
	/**修改任务规则数据信息*/
	public Map<String,Object> editTaskOrRewardRuleInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存规则信息失败");
		
		Integer addResult=this.taskCenterDao.editTaskOrRewardRuleInfo(paramsMap);
		
		if(addResult!=null && addResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "保存规则信息成功");
		}
		
		return turnMap;
	}
	
	/**修改任务规则状态（启用，停用，删除等操作）*/
	public Map<String, Object> editTaskOrRewardRuleStatus(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改状态失败");
		
		Integer addResult=this.taskCenterDao.editTaskOrRewardRuleStatus(paramsMap);
		
		if(addResult!=null && addResult>0){
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改状态成功");
		}
		
		return turnMap;
	}
	
	/**查询任务奖励列表*/
	public Map<String, Object> getTaskRewardList(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoTaskReward> taskRewardList=this.taskCenterDao.getTaskRewardList(paramsMap);
		
		PageInfo<CoTaskReward> info = new PageInfo<CoTaskReward>(taskRewardList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	/**新增任务奖励（新增奖品+奖励）*/
	public Map<String, Object> addTaskRewardInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "添加任务奖励失败");
		
		//新增奖品库
		Integer addPrizeResult=this.taskCenterDao.addPrizeInfo(paramsMap);
		
		if(addPrizeResult==null || addPrizeResult==0){
			turnMap.put(Constants.RESULT_MSG_STR, "添加奖品到奖品库失败");
			return turnMap;
		}
		
		//新增任务奖励
		Integer addRewardResultInteger=this.taskCenterDao.addTaskRewardInfo(paramsMap);
		
		if(addRewardResultInteger==null || addRewardResultInteger==0){
			turnMap.put(Constants.RESULT_MSG_STR, "添加任务奖励失败");
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "添加任务奖励成功");
		return turnMap;
	}
	
	/**获取任务奖励信息*/
	public CoTaskReward getTaskRewardInfo(Map<String, Object> paramsMap) throws Exception{
		List<CoTaskReward> taskRewardList=this.taskCenterDao.getTaskRewardList(paramsMap);
		
		if(taskRewardList!=null && !taskRewardList.isEmpty()){
			return taskRewardList.get(0);
		}else{
			return null;
		}
	}
	
	/**修改任务奖励信息*/
	public Map<String, Object> editTaskRewardInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改任务奖励信息失败");
		
		//新增奖品库
		Integer editeResult=this.taskCenterDao.editTaskRewardInfo(paramsMap);
		
		if(editeResult==null || editeResult==0){
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "修改任务奖励信息成功");
		return turnMap;
	}
	
	/**修改任务奖励状态（启用，停用，删除等操作）*/
	public Map<String, Object> editTaskRewardStatus(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改状态失败");
		
		//新增奖品库
		Integer editeResult=this.taskCenterDao.editTaskRewardStatus(paramsMap);
		
		if(editeResult==null || editeResult==0){
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "修改状态成功");
		return turnMap;
	}
	
	/**查询任务奖励规则列表*/
	public Map<String, Object> getTaskRewardRuleList(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoTaskRule> taskRewardRuleList=this.taskCenterDao.getTaskRewardRuleList(paramsMap);
		
		PageInfo<CoTaskRule> info = new PageInfo<CoTaskRule>(taskRewardRuleList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	/**获取条件和规则数据*/
	@SuppressWarnings("unchecked")
	public Map<String, Object> getModeAndRuleInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		//获取规则
		List<CoTaskRule> taskRuleList=this.taskCenterDao.getTaskRuleInfos(paramsMap);
		
		//获取规则条件
		String editFrom=MapUtils.getString(paramsMap, "editFrom","");
		String forType=MapUtils.getString(paramsMap, "forType","");
		
		String ruleMode="";
		
		if("task".equals(editFrom)){
			List<CoTask> taskInfos=this.taskCenterDao.getTaskInfos(paramsMap);
			
			CoTask taskInfo=taskInfos.get(0);
			
			if("SHOW".equals(forType)){
				ruleMode=taskInfo.getShowRuleMode();
			}else{
				ruleMode=taskInfo.getRuleMode();
			}
		}else{
			List<CoTaskReward> rewardInfos=this.taskCenterDao.getTaskRewardList(paramsMap);
			
			CoTaskReward taskRewardInfo=rewardInfos.get(0);
			
			ruleMode=taskRewardInfo.getRuleMode();
		}
		
		List<Map<String, Object>> infoList=new ArrayList<Map<String,Object>>();
		
		if(!StringUtil.isEmptyStr(ruleMode)){
			List<List<String>> modeList=JsonUtil.toObject(ruleMode, ArrayList.class);
			
			for(int i=0;i<modeList.size();i++){
				Map<String, Object> infoMap=new HashMap<String, Object>();
				infoMap.put("ruleModeId", i);
				infoMap.put("ruleModeName", "方式"+(i+1));
				
				List<String> codeList=modeList.get(i);
				
				String ruleModeData="";
				
				for(String code:codeList){
					for(CoTaskRule taskRuleInfo:taskRuleList){
						String ruleCode=taskRuleInfo.getRuleCode();
						String ruleTitle=taskRuleInfo.getRuleTitle();
						
						if(code.equals(ruleCode)){
							ruleModeData=StringUtil.isEmptyStr(ruleModeData)?ruleTitle:ruleModeData+"、"+ruleTitle;
						}
					}
				}
				
				infoMap.put("ruleModeData", ruleModeData);
				
				infoList.add(infoMap);
			}
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		
		turnMap.put("ruleMode", ruleMode);
		turnMap.put("infoList", infoList);
		return turnMap;
	}
	
	/**修改任务规则条件*/
	public Map<String, Object> editTaskRuleMode(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改信息失败");
		
		Integer editResult=this.taskCenterDao.editTaskRuleMode(paramsMap);
		
		if(editResult==null || editResult==0){
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "修改信息成功");
		return turnMap;
	}
	
	/**修改奖励规则条件*/
	public Map<String, Object> editTaskRewardRuleMode(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改信息失败");
		
		Integer editResult=this.taskCenterDao.editTaskRewardRuleMode(paramsMap);
		
		if(editResult==null || editResult==0){
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "修改信息成功");
		return turnMap;
	}
	
	/**重载缓存*/
	public Map<String, Object> reloadRuleRedisInfo(List<String> ruleFroms) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "重载缓存失败");
		
		for(String ruleFrom:ruleFroms){
			boolean isReloadTaskRule=reloadTaskAndRewardRule(ruleFrom);
			
			if(!isReloadTaskRule){
				if("101".equals(ruleFrom)){
					turnMap.put(Constants.RESULT_MSG_STR, "重载活动规则缓存失败");
				}else if("102".equals(ruleFrom)){
					turnMap.put(Constants.RESULT_MSG_STR, "重载任务规则缓存失败");
				}else{
					turnMap.put(Constants.RESULT_MSG_STR, "重载奖励规则缓存失败");
				}
				
				return turnMap;
			}
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "重载缓存成功");
		return turnMap;
	}
	
	public boolean reloadTaskAndRewardRule(String ruleFrom){
		boolean isReload=false;
		
		String redisKey="DEL_RULEFROM_"+ruleFrom;
		
		try {
			List<RuleGroupCommonBean> ruleGroupList=null;
			
			if("101".equals(ruleFrom)){
				ruleGroupList=this.taskCenterDao.getActivityAndRuleList(null);
			}else if("102".equals(ruleFrom)){
				ruleGroupList=this.taskCenterDao.getTaskAndRuleList(null);
			}else if("103".equals(ruleFrom)){
				ruleGroupList=this.taskCenterDao.getTaskRewardAndRuleList(null);
			}
			
			Map<String, Object> ruleGroupMap=new HashMap<String, Object>();
			
			//将查询到的list转成Map存储，便于后续使用
			if(ruleGroupList!=null && !ruleGroupList.isEmpty()){
				for(RuleGroupCommonBean ruleGroupInfo:ruleGroupList){
					Long ruleGroupId=ruleGroupInfo.getRuleGroupId();
					
					List<RuleCommonBean> ruleList=ruleGroupInfo.getRuleCommonList();
					
					Map<String, Object> ruleMap=new HashMap<String, Object>();
					
					if(ruleList!=null && !ruleList.isEmpty()){
						for(RuleCommonBean ruleInfo:ruleList){
							String ruleCode=ruleInfo.getRuleCode();
							
							ruleMap.put(ruleCode, ruleInfo);
						}
					}
					
					ruleGroupInfo.setRuleMap(ruleMap);
					
					ruleGroupMap.put("RULE_GROUP_"+ruleGroupId, ruleGroupInfo);
				}
			}
			
			redisUtil.addRedisForKey(redisKey, ruleGroupMap);
			
			isReload=true;
		} catch (Exception e) {
			log.error("重载任务和奖励缓存时获得异常:",e);
		}
		
		return isReload;
	}
	
	/**获取任务图片列表*/
	public Map<String, Object> getTaskImgList(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoTaskImg> taskImgList=this.taskCenterDao.getTaskImgList(paramsMap);
		
		PageInfo<CoTaskImg> info = new PageInfo<CoTaskImg>(taskImgList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	/**新增任务图片数据*/
	public Map<String, Object> addTaskImgInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存信息失败");
		
		Integer addResult=this.taskCenterDao.addTaskImgInfo(paramsMap);
		
		if(addResult==null || addResult==0){
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "保存信息成功");
		return turnMap;
	}
	
	/**修改任务图片状态*/
	public Map<String, Object> editTaskImgStatus(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改信息失败");
		
		Integer editResult=this.taskCenterDao.editTaskImgStatus(paramsMap);
		
		if(editResult==null || editResult==0){
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "修改信息成功");
		return turnMap;
	}
	
	/**获取地市-过滤规则已选地市*/
	public Map<String, Object> getCommonRegionForRule(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		String provinceCode = (String) propertyToRedis.getPropertyValue("PROVINCE_CODE");
		
		if(StringUtil.isEmptyMap(paramsMap)){
			paramsMap=new HashMap<String, Object>();
		}
		
		paramsMap.put("provinceCode", provinceCode);
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoRegion> regionList=this.taskCenterDao.getCommonRegionForRule(paramsMap);
		
		PageInfo<CoRegion> info = new PageInfo<CoRegion>(regionList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	/**新增规则集合地市数据*/
	@SuppressWarnings("unchecked")
	public Map<String, Object> addRuleRegionData(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "新增信息失败");
		
		List<Map<String, Object>> chooseDatas=(List<Map<String, Object>>) paramsMap.get("chooseDatas");
		Long ruleId=MapUtils.getLong(paramsMap, "ruleId");
		
		for(Map<String, Object> regionData:chooseDatas){
			regionData.put("ruleId", ruleId);
			regionData.put("ruleData", regionData.get("regionCode"));
			
			this.taskCenterDao.addRuleRegionData(regionData);
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "新增信息成功");
		return turnMap;
	}
	
	/**获取规则地市数据集合*/
	public Map<String, Object> getRegionRuleData(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		int pageSize=MapUtils.getIntValue(paramsMap, "limit",10);//每页展示数目
		int currentPage=MapUtils.getIntValue(paramsMap,"page",1);//当前页数
		long pages=0;
		
		//查询数据总记录数
		PageHelper.startPage(currentPage, pageSize);
		List<CoRuleData> infoList=this.taskCenterDao.getRegionRuleData(paramsMap);
		
		PageInfo<CoRuleData> info = new PageInfo<CoRuleData>(infoList);
	        
	    long infoCount=info.getTotal();
	        
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "查询完成");
		turnMap.put("infoList", info.getList());//返回当前的列表数据
		turnMap.put("infoCount", infoCount);//返回数据总记录数目
		turnMap.put("pages", pages);//返回总页数
		
		return turnMap;
	}
	
	/**删除规则地市数据集合*/
	public Map<String, Object> delRegionRuleData(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "删除信息失败");
		
		
		Integer editResult=this.taskCenterDao.delRegionRuleData(paramsMap);
		
		if(editResult==null || editResult==0){
			return turnMap;
		}
		
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "删除信息成功");
		return turnMap;
	}
	
	/**同步信息到权益*/
	public Map<String, Object> syncTaskInfo(Map<String, Object> paramsMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "同步任务信息失败");
		
		//查询任务信息
		CoTask taskInfo=getTaskInfo(paramsMap);
		
		List<Integer> areaRel=new ArrayList<Integer>();
		
		//查询是否有地市限制规则数据
		List<CoRuleData> regionRuleDatas=this.taskCenterDao.getRegionRuleDataByTask(paramsMap);
		
		//如果没有地市限制规则数据，就查询所有地市传过去
		if(regionRuleDatas==null || regionRuleDatas.isEmpty()){
			List<CoRegion> regions=this.taskCenterDao.getCommonRegionForRule(paramsMap);
			
			for(CoRegion info:regions){
				Integer regionCode=Integer.valueOf(info.getRegionCode());
				areaRel.add(regionCode);
			}
		}else{
			for(CoRuleData info:regionRuleDatas){
				Integer regionCode=Integer.valueOf(info.getRuleData());
				areaRel.add(regionCode);
			}
		}
		
		//查询任务的奖品
		List<CoTaskReward> taskRewards=this.taskCenterDao.getTaskRewardList(paramsMap);
		List<Map<String, Object>> rightsRel=new ArrayList<Map<String,Object>>();
		
		if(taskRewards!=null && !taskRewards.isEmpty()){
			for(CoTaskReward rewardInfo:taskRewards){
				String prizeType=rewardInfo.getPrizeType();
				
				if("2".equals(prizeType) || "3".equals(prizeType) || "4".equals(prizeType)){
					Map<String, Object> rightRel=new HashMap<String, Object>();
					rightRel.put("rightsCode", rewardInfo.getProductCode());
					rightRel.put("useNum",rewardInfo.getPrizeCount());
					rightRel.put("exchObjCode", "");
					rightRel.put("exchObjName", "");
				
					rightsRel.add(rightRel);
				}
			}
		}
		
		Date startTime=taskInfo.getStartTime();
		Date endTime=taskInfo.getEndTime();
		Date createTime=taskInfo.getAddTime();
		
		Map<String, Object> syncInMap=new HashMap<String,Object>();
		syncInMap.put("activityCode",taskInfo.getTaskCode());
		syncInMap.put("acivityName",taskInfo.getTaskName());
        syncInMap.put("activityTypeCode","3");
        syncInMap.put("activityTypeName","任务");
        syncInMap.put("activityDesc",taskInfo.getTaskDesc());
        syncInMap.put("createdate",DateUtil.sdfSs.format(createTime));
        syncInMap.put("startDate",DateUtil.sdfSs.format(startTime));
        syncInMap.put("endDate",DateUtil.sdfSs.format(endTime));
        syncInMap.put("areaRel",areaRel);
        syncInMap.put("rightsRel",rightsRel);
        
        Map<String, Object> resultMap=thirdInterface.activitySync(syncInMap);
        
        if (!Constants.CODE_SUCC.equals(resultMap.get("resultCode"))) {
        	turnMap.put(Constants.RESULT_MSG_STR, resultMap.get(Constants.RESULT_MSG_STR));
        	return turnMap;
        }
        
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		turnMap.put(Constants.RESULT_MSG_STR, "同步成功");
        return turnMap;
	}
}

