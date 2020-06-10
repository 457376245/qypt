package com.web.bmo;

import java.util.List;
import java.util.Map;

import com.web.model.CoTask;
import com.web.model.CoTaskGroup;
import com.web.model.CoTaskReward;
import com.web.model.CoTaskRule;


/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.bmo.TaskCenterBmo.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务中心<br>
 * <b>创建时间：</b>2020年3月31日-下午3:04:36<br>
 */

public interface TaskCenterBmo {
	
	/**查询任务分组数据信息*/
	public Map<String, Object> getTaskGroupList(Map<String,Object> paramsMap) throws Exception;
	
	/**查询分组信息*/
	public CoTaskGroup getTaskGroupInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**新增分组*/
	public Map<String, Object> addTaskGroupInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改分组信息*/
	public Map<String, Object> editTaskGroupInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**查询所有任务分组信息*/
	public List<CoTaskGroup> getAllTaskGroups(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务列表*/
	public Map<String, Object> getTaskList(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务信息*/
	public CoTask getTaskInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**新增任务数据*/
	public Map<String, Object> addTaskInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务数据*/
	public Map<String, Object> editTaskInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务状态*/
	public Map<String, Object> editTaskStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务规则列表(分页)*/
	public Map<String, Object> getTaskRuleList(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务规则列表(不分页)*/
	public List<CoTaskRule> getTaskRuleInfos(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务规则信息*/
	public CoTaskRule getTaskRuleInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**新增任务或任务奖品规则*/
	public Map<String, Object> addTaskOrRewardRuleInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务规则数据信息*/
	public Map<String,Object> editTaskOrRewardRuleInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务规则状态（启用，停用，删除等操作）*/
	public Map<String, Object> editTaskOrRewardRuleStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务奖励列表*/
	public Map<String, Object> getTaskRewardList(Map<String, Object> paramsMap) throws Exception;
	
	/**新增任务奖励（新增奖品+奖励）*/
	public Map<String, Object> addTaskRewardInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**获取任务奖励信息*/
	public CoTaskReward getTaskRewardInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务奖励信息*/
	public Map<String, Object> editTaskRewardInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务奖励状态（启用，停用，删除等操作）*/
	public Map<String, Object> editTaskRewardStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务奖励规则列表*/
	public Map<String, Object> getTaskRewardRuleList(Map<String, Object> paramsMap) throws Exception;
	
	/**获取条件和规则数据*/
	public Map<String, Object> getModeAndRuleInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务规则条件*/
	public Map<String, Object> editTaskRuleMode(Map<String, Object> parasMap) throws Exception;
	
	/**修改奖励规则条件*/
	public Map<String, Object> editTaskRewardRuleMode(Map<String, Object> parasMap) throws Exception;
	
	/**重载缓存*/
	public Map<String, Object> reloadRuleRedisInfo(List<String> ruleFroms) throws Exception;
	
	/**获取任务图片列表*/
	public Map<String, Object> getTaskImgList(Map<String, Object> parasmMap) throws Exception;
	
	/**新增任务图片数据*/
	public Map<String, Object> addTaskImgInfo(Map<String, Object> parasmMap) throws Exception;
	
	/**修改任务图片状态*/
	public Map<String, Object> editTaskImgStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**获取地市-过滤规则已选地市*/
	public Map<String, Object> getCommonRegionForRule(Map<String, Object> paramsMap) throws Exception;
	
	/**新增规则集合地市数据*/
	public Map<String, Object> addRuleRegionData(Map<String, Object> paramsMap) throws Exception;

	/**获取规则地市数据集合*/
	public Map<String, Object> getRegionRuleData(Map<String, Object> paramsMap) throws Exception;
	
	/**删除规则地市数据集合*/
	public Map<String, Object> delRegionRuleData(Map<String, Object> paramsMap) throws Exception;
	
	/**同步信息到权益*/
	public Map<String, Object> syncTaskInfo(Map<String, Object> paramsMap) throws Exception;
}

