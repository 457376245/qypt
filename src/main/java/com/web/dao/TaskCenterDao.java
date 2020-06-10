package com.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.web.model.CoRegion;
import com.web.model.CoRuleData;
import com.web.model.CoTask;
import com.web.model.CoTaskGroup;
import com.web.model.CoTaskImg;
import com.web.model.CoTaskReward;
import com.web.model.CoTaskRule;
import com.web.model.RuleGroupCommonBean;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.dao.TaskCenterDao.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务中心<br>
 * <b>创建时间：</b>2020年3月31日-下午3:08:17<br>
 */

@Repository("com.web.dao.TaskCenterDao")
public interface TaskCenterDao {
	/**查询任务分组总记录数*/
	public Integer getTaskGroupCount(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务分组数据信息*/
	public List<CoTaskGroup> getTaskGroupList(Map<String,Object> paramsMap) throws Exception;
	
	/**查询分组信息*/
	public CoTaskGroup getTaskGroupInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**新增任务分组*/
	public Integer addTaskGroupInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务分组*/
	public Integer editTaskGroupInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务列表或单个任务数据信息*/
	public List<CoTask> getTaskInfos(Map<String, Object> paramsMap) throws Exception;
	
	/**新增任务信息*/
	public Integer addTaskInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务信息*/
	public Integer editTaskInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务状态*/
	public Integer editTaskStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务规则列表*/
	public List<CoTaskRule> getTaskRuleInfos(Map<String, Object> paramsMap) throws Exception;
	
	/**新增规则信息*/
	public Integer addTaskOrRewardRuleInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改规则信息*/
	public Integer editTaskOrRewardRuleInfo(Map<String,Object> paramsMap) throws Exception;
	
	/**修改任务规则状态(启用，停用，删除等)*/
	public Integer editTaskOrRewardRuleStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务奖励列表*/
	public List<CoTaskReward> getTaskRewardList(Map<String, Object> paramsMap) throws Exception;
	
	/**新增奖品库数据*/
	public Integer addPrizeInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**新增任务奖励*/
	public Integer addTaskRewardInfo(Map<String,Object> paramsMap) throws Exception;
	
	/**修改任务奖励信息*/
	public Integer editTaskRewardInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务奖励状态（启用，停用，删除等操作）*/
	public Integer editTaskRewardStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**查询任务奖励规则列表*/
	public List<CoTaskRule> getTaskRewardRuleList(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务规则条件*/
	public Integer editTaskRuleMode(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务奖励规则条件*/
	public Integer editTaskRewardRuleMode(Map<String, Object> paramsMap) throws Exception;
	
	
	/**查询所有展示时间内的任务*/
	public List<RuleGroupCommonBean> getTaskAndRuleList(Map<String, Object> paramsMap) throws Exception;
	
	/**查询所有展示时间内的活动*/
	public List<RuleGroupCommonBean> getActivityAndRuleList(Map<String, Object> paramsMap) throws Exception;
	
	/**查询所有任务对应的奖品中间记录*/
	public List<RuleGroupCommonBean> getTaskRewardAndRuleList(Map<String, Object> paramsMap) throws Exception;
	
	/**获取任务图片数据*/
	public List<CoTaskImg> getTaskImgList(Map<String, Object> paramsMap) throws Exception;
	
	/**新增任务图片信息*/
	public Integer addTaskImgInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**修改任务图片状态*/
	public Integer editTaskImgStatus(Map<String, Object> paramsMap) throws Exception;
	
	/**获取地市-过滤规则已选地市*/
	public List<CoRegion> getCommonRegionForRule(Map<String, Object> paramsMap) throws Exception;
	
	/**新增规则集合地市数据*/
	public Integer addRuleRegionData(Map<String, Object> paramsMap) throws Exception;
	
	/**获取规则地市数据集合*/
	public List<CoRuleData> getRegionRuleData(Map<String, Object> paramsMap) throws Exception;
	
	/**删除规则地市数据集合*/
	public Integer delRegionRuleData(Map<String, Object> paramsMap) throws Exception;
	
	/**获取任务配置的地市限制*/
	public List<CoRuleData> getRegionRuleDataByTask(Map<String, Object> paramsMap) throws Exception;
}

