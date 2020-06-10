package com.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.web.bean.StaffInfo;
import com.web.bmo.CommonBmo;
import com.web.bmo.TaskCenterBmo;
import com.web.bmo.UploadFileService;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoMember;
import com.web.model.CoRegion;
import com.web.model.CoTask;
import com.web.model.CoTaskGroup;
import com.web.model.CoTaskReward;
import com.web.model.CoTaskRule;
import com.web.util.JsonUtil;
import com.web.util.RedisLock;
import com.web.util.StringUtil;
import com.web.util.common.JsonResponse;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.controller.TaskCenterController.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>任务中心<br>
 * <b>创建时间：</b>2020年3月31日-上午9:14:47<br>
 */

@Controller("com.web.controller.TaskCenterController")
@RequestMapping("/taskCenter")
public class TaskCenterController extends BaseController{
	
	@Resource(name = "com.web.bmo.TaskCenterBmoImpl")
    private TaskCenterBmo taskCenterBmo;
	
	@Resource(name = "com.web.bmo.CommonBmoImpl")
    private CommonBmo commonBmo;
	
	@Resource
	private UploadFileService uploadFileService;
	
	/**展示任务组*/
    @RequestMapping(value = "/taskGroupList")
    public String taskGroupList(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	
        } catch (Exception e) {
            log.error("在跳转任务分组展示页面时获得异常 :", e);
            return "/error/error";
        }

        return "/task/taskGroupList";
    }
    
    /**获取分组列表信息*/
    @RequestMapping(value = "/getTaskGroupList",method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTaskGroupList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
		
		try {
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			log.info("staffInfo:"+staffInfo.getStaffId()+":"+staffInfo.getStaffName());
			
			Map<String, Object> searchResultMap=this.taskCenterBmo.getTaskGroupList(params);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
			turnMap.put("pages",searchResultMap.get("pages"));
			turnMap.put("infoCount",searchResultMap.get("infoCount"));
			turnMap.put("infoList",searchResultMap.get("infoList"));
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在查询任务分组记录信息时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**进入分组新增和修改页面*/
    @RequestMapping(value = "/editTaskGroupInfo")
    public String editTaskGroupInfo(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	//操作类型，ADD和EDIT
        	String editType=MapUtils.getString(params, "editType","ADD");
			Long groupId=MapUtils.getLong(params, "groupId",null);
        	
        	//如果是修改，需要查询原分组信息
        	if("EDIT".equals(editType)){
        		CoTaskGroup taskGroupInfo=this.taskCenterBmo.getTaskGroupInfo(params);
        		
        		if(taskGroupInfo==null){
        			model.addAttribute("message", "未查询到分组信息");
        			return "/error/error";
        		}
        		
        		model.addAttribute("taskGroupInfo", taskGroupInfo);
        	}
        	
        	model.addAttribute("editType", editType);
        	model.addAttribute("groupId", groupId);
        } catch (Exception e) {
            log.error("在跳转任务分组展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/taskGroupEdit";
    }
    
    /**保存分组信息*/
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveTaskGroup",method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveTaskGroup(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
		
		try {
			//获取前台传入的参数
			Map<String, Object> infoMap=new HashMap<String, Object>();
			String data=MapUtils.getString(params,"datas");
			if(!StringUtil.isEmptyStr(data)){
				infoMap=listToMap(JsonUtil.toObject(data, ArrayList.class));
			}
			
			if(!StringUtil.isEmptyMap(infoMap)){
				params.putAll(infoMap);
			}
			
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			//判断操作类型
			String editType=MapUtils.getString(params, "editType","ADD");
			
			Map<String, Object> editResultMap=null;
			
			if("ADD".equals(editType)){
				editResultMap=this.taskCenterBmo.addTaskGroupInfo(params);
			}else{
				editResultMap=this.taskCenterBmo.editTaskGroupInfo(params);
			}
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在保存任务分组信息时获得差异:",e);
			
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
    
    public static Map<String,Object> listToMap(List<Map<String, Object>> list){
    	Map<String,Object> turnMap=new HashMap<String, Object>();
    	
    	for(Map<String,Object> listInfo:list){
    		turnMap.put(MapUtils.getString(listInfo, "name"), listInfo.get("value"));
    	}
    	
    	return turnMap;
    }
    
    /**进入任务列表页面*/
    @RequestMapping(value = "/taskList")
    public String taskList(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
			//获取分组信息
        	List<CoTaskGroup> taskGruops=this.taskCenterBmo.getAllTaskGroups(null);
        	
        	model.addAttribute("taskGroups", taskGruops);
        } catch (Exception e) {
            log.error("在跳转任务展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/taskIndex";
    }
    
    /**获取任务列表信息*/
    @RequestMapping(value = "/getTaskList",method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTaskList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询任务信息失败");
		
		try {
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			Map<String, Object> searchResultMap=this.taskCenterBmo.getTaskList(params);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
			turnMap.put("pages",searchResultMap.get("pages"));
			turnMap.put("infoCount",searchResultMap.get("infoCount"));
			turnMap.put("infoList",searchResultMap.get("infoList"));
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在查询任务记录信息时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**进入任务新增和修改页面*/
    @RequestMapping(value = "/toEditTaskInfo")
    public String toEditTaskInfo(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	//操作类型，ADD和EDIT
        	String editType=MapUtils.getString(params, "editType","ADD");
			Long taskId=MapUtils.getLong(params, "taskId",null);
        	
			//获取分组信息
        	List<CoTaskGroup> taskGruops=this.taskCenterBmo.getAllTaskGroups(null);
        	
        	model.addAttribute("taskGroups", taskGruops);
			
        	//如果是修改，需要查询任务信息
        	if("EDIT".equals(editType)){
        		CoTask taskInfo=this.taskCenterBmo.getTaskInfo(params);
        		
        		if(taskInfo==null){
        			model.addAttribute("message", "未查询到任务信息");
        			return "/error/error";
        		}
        		
        		model.addAttribute("taskInfo", taskInfo);
        	}
        	
        	model.addAttribute("editType", editType);
        	model.addAttribute("taskId", taskId);
        } catch (Exception e) {
            log.error("在跳转任务分组展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/taskInfoEdit";
    }
    
    /**保存任务信息（新增和修改）*/
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveTaskInfo",method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveTaskInfo(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
		
		try {
			//获取前台传入的参数
			Map<String, Object> infoMap=new HashMap<String, Object>();
			String data=MapUtils.getString(params,"datas");
			if(!StringUtil.isEmptyStr(data)){
				infoMap=listToMap(JsonUtil.toObject(data, ArrayList.class));
			}
			
			if(!StringUtil.isEmptyMap(infoMap)){
				params.putAll(infoMap);
			}
			
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			//判断操作类型
			String editType=MapUtils.getString(params, "editType","ADD");
			
			Map<String, Object> editResultMap=null;
			
			if("ADD".equals(editType)){
				editResultMap=this.taskCenterBmo.addTaskInfo(params);
			}else{
				editResultMap=this.taskCenterBmo.editTaskInfo(params);
			}
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在保存任务分组信息时获得差异:",e);
			
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**修改任务状态（启用，停用，删除）*/
	@RequestMapping(value = "/editTaskStatus",method = RequestMethod.POST)
	public @ResponseBody JsonResponse editTaskStatus(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改任务状态失败");
		
		try {
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			//判断操作类型
			Map<String, Object> editStatusResultMap=this.taskCenterBmo.editTaskStatus(params);
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editStatusResultMap, Constants.RESULT_CODE_STR))){
				return super.failed(turnMap, 1);
			}
			
			//修改状态成功，如果是启用任务，需要同步数据
			String statusCd=MapUtils.getString(params, "statusCd","");
			
			if("101".equals(statusCd)){
				//启用需要刷新缓存
				List<String> ruleFroms=new ArrayList<String>();
				ruleFroms.add("102");
				ruleFroms.add("103");
				this.taskCenterBmo.reloadRuleRedisInfo(ruleFroms);
				
				//启用时同步信息到权益管理
				try {
					params.remove("statusCd");
					this.taskCenterBmo.syncTaskInfo(params);
				} catch (Exception e) {
					log.error("在同步任务信息到远程服务异常：",e);
				}
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改任务状态成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在修改任务状态时获得差异:",e);
			
			turnMap.put(Constants.RESULT_MSG_STR, "修改任务状态异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
	
	/**查询任务规则列表*/
    @RequestMapping(value = "/getTaskRuleList",method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTaskRuleList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询任务规则信息失败");
		
		try {
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			Map<String, Object> searchResultMap=this.taskCenterBmo.getTaskRuleList(params);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
			turnMap.put("pages",searchResultMap.get("pages"));
			turnMap.put("infoCount",searchResultMap.get("infoCount"));
			turnMap.put("infoList",searchResultMap.get("infoList"));
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在查询任务记录信息时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**进入任务规则新增和修改页面*/
    @RequestMapping(value = "/toEditTaskRuleInfo")
    public String toEditTaskRuleInfo(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	//操作类型，ADD和EDIT
        	String editType=MapUtils.getString(params, "editType","ADD");
			Long ruleId=MapUtils.getLong(params, "ruleId",null);
			Long taskId=MapUtils.getLong(params, "taskId",null);
			Long rewardId=MapUtils.getLong(params, "rewardId",null);
			String infoFrom=MapUtils.getString(params, "infoFrom");
        	
			if("task".equals(infoFrom) && taskId==null){
				model.addAttribute("message", "任务ID数据丢失");
    			return "/error/error";
			}
			
			if("reward".equals(infoFrom) && rewardId==null){
				model.addAttribute("message", "奖品ID数据丢失");
    			return "/error/error";
			}
			
        	//如果是修改，需要查询任务信息
        	if("EDIT".equals(editType)){
        		CoTaskRule taskRuleInfo=this.taskCenterBmo.getTaskRuleInfo(params);
        		
        		if(taskRuleInfo==null){
        			model.addAttribute("message", "未查询到任务规则信息");
        			return "/error/error";
        		}
        		
        		String ruleData=taskRuleInfo.getRuleData();
        		
        		if(ruleData.indexOf("-")!=-1){
        			taskRuleInfo.setRuleDataSymbol("-");
        			taskRuleInfo.setRuleData(ruleData.split("-")[0]);
        			taskRuleInfo.setRuleDataSub(ruleData.split("-")[1]);
        		}else{
        			taskRuleInfo.setRuleDataSymbol(ruleData.substring(0,2).replaceAll("<", "&lt"));
        			taskRuleInfo.setRuleData(ruleData.substring(2,ruleData.length()));
        		}
        		
        		model.addAttribute("taskRuleInfo", taskRuleInfo);
        	}
        	
        	model.addAttribute("editType", editType);
        	model.addAttribute("ruleId", ruleId);
        	model.addAttribute("taskId", taskId);
        	model.addAttribute("rewardId",rewardId);
        	model.addAttribute("infoFrom", infoFrom);
        } catch (Exception e) {
            log.error("在跳转任务规则新增或修改页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/taskRuleEdit";
    }
    
    /**保存任务规则信息（新增和修改）*/
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveTaskRuleInfo",method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveTaskRuleInfo(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
		
		try {
			//获取前台传入的参数
			Map<String, Object> infoMap=new HashMap<String, Object>();
			String data=MapUtils.getString(params,"datas");
			if(!StringUtil.isEmptyStr(data)){
				infoMap=listToMap(JsonUtil.toObject(data, ArrayList.class));
			}
			
			if(!StringUtil.isEmptyMap(infoMap)){
				params.putAll(infoMap);
			}
			
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			//判断操作类型
			String editType=MapUtils.getString(params, "editType","ADD");
			
			Map<String, Object> editResultMap=null;
			
			String ruleDataSymbol=MapUtils.getString(params, "ruleDataSymbol","").replaceAll("&gt;",">").replaceAll("&lt;", "<");
			String ruleData=MapUtils.getString(params, "ruleData","");
			String ruleDataSub=MapUtils.getString(params, "ruleDataSub","");
			String trueRuleData="";
			
			if("-".equals(ruleDataSymbol)){
				trueRuleData=ruleData+ruleDataSymbol+ruleDataSub;
			}else{
				trueRuleData=ruleDataSymbol+ruleData;
			}
			
			params.put("ruleData", trueRuleData);
			
			if("ADD".equals(editType)){
				editResultMap=this.taskCenterBmo.addTaskOrRewardRuleInfo(params);
			}else{
				editResultMap=this.taskCenterBmo.editTaskOrRewardRuleInfo(params);
			}
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在保存任务分组信息时获得差异:",e);
			
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**修改规则状态（停用，启用，删除等操作）*/
	@RequestMapping(value = "/editTaskOrRewardRuleStatus",method = RequestMethod.POST)
	public @ResponseBody JsonResponse editTaskOrRewardRuleStatus(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改状态失败");
		
		try {
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			//如果是删除规则，要判断下规则是否在条件组中，如果是条件中，提示无法删除
			String editFrom=MapUtils.getString(params,"editFrom","");
			String statusCd=MapUtils.getString(params, "statusCd","");
			
			if("102".equals(statusCd)){
				String ruleMode="";
				String showRuleMode="";
				
				if("task".equals(editFrom)){
					Long taskId=MapUtils.getLong(params, "taskId",null);
					
					Map<String, Object> searchMap=new HashMap<String, Object>();
					searchMap.put("taskId", taskId);
					CoTask taskInfo=this.taskCenterBmo.getTaskInfo(searchMap);
					
					if(taskInfo!=null){
						ruleMode=taskInfo.getRuleMode();
						showRuleMode=taskInfo.getShowRuleMode();
					}
				}else{
					Long rewardId=MapUtils.getLong(params, "rewardId",null);
					
					Map<String, Object> searchMap=new HashMap<String, Object>();
					searchMap.put("rewardId", rewardId);
					
					CoTaskReward taskRewardInfo=this.taskCenterBmo.getTaskRewardInfo(params);
					
					if(taskRewardInfo!=null){
						ruleMode=taskRewardInfo.getRuleMode();
					}
				}
				
				//查询规则数据
				CoTaskRule taskRuleInfo=this.taskCenterBmo.getTaskRuleInfo(params);
			
				if(taskRuleInfo!=null){
					String ruleCode=taskRuleInfo.getRuleCode();
					
					if((!StringUtil.isEmptyStr(ruleMode) && ruleMode.indexOf(ruleCode)!=-1) || (!StringUtil.isEmptyStr(showRuleMode) && showRuleMode.indexOf(ruleCode)!=-1)){
						turnMap.put(Constants.RESULT_MSG_STR, "规则已作为条件使用中，请删除条件后再进行操作");
						return super.failed(turnMap, 1);
					}
				}
			}
			
			Map<String, Object> editResultMap=this.taskCenterBmo.editTaskOrRewardRuleStatus(params);
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改状态成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在修改规则状态时获得差异:",e);
			
			turnMap.put(Constants.RESULT_MSG_STR, "修改状态异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**查询任务奖励列表*/
    @RequestMapping(value = "/getTaskRewardList",method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTaskRewardList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询任务奖励列表失败");
		
		try {
			Map<String, Object> searchResultMap=this.taskCenterBmo.getTaskRewardList(params);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
			turnMap.put("pages",searchResultMap.get("pages"));
			turnMap.put("infoCount",searchResultMap.get("infoCount"));
			turnMap.put("infoList",searchResultMap.get("infoList"));
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在查询任务奖励记录时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**新增任务奖励-新增奖品和奖励*/
    @RequestMapping(value = "/addTaskRewardInfo",method = RequestMethod.POST)
	public @ResponseBody JsonResponse addTaskRewardInfo(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "添加任务奖励失败");
		
		try {
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			Long prizeStock=MapUtils.getLong(params, "prizeStock",null);
			
			if(prizeStock==null){
				prizeStock=999999L;
				params.put("prizeStock", prizeStock);
			}
			
			Map<String, Object> addResultMap=this.taskCenterBmo.addTaskRewardInfo(params);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(addResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, addResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "新增完成");
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在添加任务奖励时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**进入任务奖励修改页面*/
    @RequestMapping(value = "/toEditTaskRewardInfo")
    public String toEditTaskRewardInfo(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	//操作类型，ADD和EDIT
        	String editType=MapUtils.getString(params, "editType","EDIT");
			Long rewardId=MapUtils.getLong(params, "rewardId",null);
			Long taskId=MapUtils.getLong(params, "taskId",null);
        	
        	//如果是修改，需要查询任务信息
        	if("EDIT".equals(editType)){
        		CoTaskReward taskRewardInfo=this.taskCenterBmo.getTaskRewardInfo(params);
        		
        		if(taskRewardInfo==null){
        			model.addAttribute("message", "未查询到任务奖励信息");
        			return "/error/error";
        		}
        		
        		model.addAttribute("taskRewardInfo", taskRewardInfo);
        	}
        	
        	model.addAttribute("editType", editType);
        	model.addAttribute("rewardId",rewardId);
        } catch (Exception e) {
            log.error("在跳转任务规则新增或修改页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/taskRewardEdit";
    }
    
    /**修改任务奖励信息*/
   	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/editTaskRewardInfo",method = RequestMethod.POST)
   	public @ResponseBody JsonResponse editTaskRewardInfo(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
   		Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "修改信息失败");
   		
   		try {
   			//获取前台传入的参数
			Map<String, Object> infoMap=new HashMap<String, Object>();
			String data=MapUtils.getString(params,"datas");
			if(!StringUtil.isEmptyStr(data)){
				infoMap=listToMap(JsonUtil.toObject(data, ArrayList.class));
			}
			
			if(!StringUtil.isEmptyMap(infoMap)){
				params.putAll(infoMap);
			}
   			
   			//获取登录数据信息
   			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
   			
   			params.put("staffId", staffInfo.getStaffId());
   			
   			Map<String, Object> editResultMap=this.taskCenterBmo.editTaskRewardInfo(params);
   				
   			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
   				return super.failed(turnMap, 1);
   			}
   			
   			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
   			turnMap.put(Constants.RESULT_MSG_STR, "修改状态成功");

   			return super.successed(turnMap, 0);
   		} catch (Exception e) {
   			log.error("在修改任务奖励信息时获得差异:",e);
   			
   			turnMap.put(Constants.RESULT_MSG_STR, "修改信息异常："+e);
   		}
   		
   		return super.failed(turnMap,1);
   	}
   	
   	/**修改任务奖励状态（启用，停用，删除等操作）*/
   	@RequestMapping(value = "/editTaskRewardStatus",method = RequestMethod.POST)
   	public @ResponseBody JsonResponse editTaskRewardStatus(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
   		Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "修改奖励状态失败");
   		
   		try {
   			//获取登录数据信息
   			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
   			
   			params.put("staffId", staffInfo.getStaffId());
   			
   			Map<String, Object> editResultMap=this.taskCenterBmo.editTaskRewardStatus(params);
   				
   			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
   				return super.failed(turnMap, 1);
   			}
   			
   			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
   			turnMap.put(Constants.RESULT_MSG_STR, "修改状态成功");

   			return super.successed(turnMap, 0);
   		} catch (Exception e) {
   			log.error("在修改任务奖励状态时获得差异:",e);
   			
   			turnMap.put(Constants.RESULT_MSG_STR, "修改状态异常："+e);
   		}
   		
   		return super.failed(turnMap,1);
   	}
    
    /**查询任务奖励规则列表*/
    @RequestMapping(value = "/getTaskRewardRuleList",method = RequestMethod.POST)
	public @ResponseBody JsonResponse getTaskRewardRuleList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "查询任务奖励规则列表失败");
		
		try {
			Map<String, Object> searchResultMap=this.taskCenterBmo.getTaskRewardRuleList(params);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
			turnMap.put("pages",searchResultMap.get("pages"));
			turnMap.put("infoCount",searchResultMap.get("infoCount"));
			turnMap.put("infoList",searchResultMap.get("infoList"));
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在查询任务奖励记录时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**进入展示获得完成条件展示页面*/
    @RequestMapping(value = "/toEditRuleMode")
    public String toEditRuleMode(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	String forType=MapUtils.getString(params, "forType","");
        	String editFrom=MapUtils.getString(params, "editFrom","");
        	Long taskId=MapUtils.getLong(params, "taskId",null);
        	Long rewardId=MapUtils.getLong(params, "rewardId",null);
        	
        	if("task".equals(editFrom) && taskId==null){
        		model.addAttribute("message", "缺少必要参数");
    			return "/error/error";
        	}
        	
        	if("reward".equals(editFrom) && rewardId==null){
        		model.addAttribute("message", "缺少必要参数");
    			return "/error/error";
        	}
        	
        	model.addAttribute("forType", forType);
        	model.addAttribute("editFrom", editFrom);
        	model.addAttribute("taskId", taskId);
        	model.addAttribute("rewardId", rewardId);
        } catch (Exception e) {
            log.error("进入展示获得完成条件展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/ruleMode";
    }
    
    /**查询规则条件*/
    @RequestMapping(value = "/getRuleModeList",method = RequestMethod.POST)
	public @ResponseBody JsonResponse getRuleModeList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "获取条件失败");
		
		try {
			Map<String, Object> searchResultMap=this.taskCenterBmo.getModeAndRuleInfo(params);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
			turnMap.put("infoList",searchResultMap.get("infoList"));
			turnMap.put("ruleMode",MapUtils.getString(searchResultMap, "ruleMode",""));
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在获取条件数据时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**进入规则选择页面*/
    @RequestMapping(value = "/toShowRuleListChoose")
    public String toShowRuleListChoose(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	String forType=MapUtils.getString(params, "forType","");
        	String editFrom=MapUtils.getString(params, "editFrom","");
        	Long taskId=MapUtils.getLong(params, "taskId",null);
        	Long rewardId=MapUtils.getLong(params, "rewardId",null);
        	
        	if("task".equals(editFrom) && taskId==null){
        		model.addAttribute("message", "缺少必要参数");
    			return "/error/error";
        	}
        	
        	if("reward".equals(editFrom) && rewardId==null){
        		model.addAttribute("message", "缺少必要参数");
    			return "/error/error";
        	}
        	
        	List<CoTaskRule> ruleList=this.taskCenterBmo.getTaskRuleInfos(params);
        	
        	model.addAttribute("ruleList", ruleList);
        } catch (Exception e) {
            log.error("进入展示获得完成条件展示页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/ruleChoose";
    }
    
    /**修改条件*/
    @RequestMapping(value = "/editRuleMode",method = RequestMethod.POST)
	public @ResponseBody JsonResponse editRuleMode(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改信息失败");
		
		try {
			//获取登录数据信息
   			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
   			
   			params.put("staffId", staffInfo.getStaffId());
			
			String editFrom=MapUtils.getString(params, "editFrom","");
			
			Map<String,Object> editResultMap=null;
			
			if("task".equals(editFrom)){
				editResultMap=this.taskCenterBmo.editTaskRuleMode(params);
			}else{
				editResultMap=this.taskCenterBmo.editTaskRewardRuleMode(params);
			}
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(editResultMap, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, editResultMap.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改信息完成");
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在修改条件信息时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**修改任务相关数据后，更新缓存信息*/
    @RequestMapping(value = "/reloadTaskAndRewardRedis",method = RequestMethod.POST)
	public @ResponseBody JsonResponse reloadTaskAndRewardRedis(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "刷新缓存失败");
		
		try {
			List<String> ruleFroms=new ArrayList<String>();
			ruleFroms.add("102");
			ruleFroms.add("103");
			
			Map<String, Object> reloadResult=this.taskCenterBmo.reloadRuleRedisInfo(ruleFroms);
			
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(reloadResult, Constants.RESULT_CODE_STR))){
				turnMap.put(Constants.RESULT_MSG_STR, reloadResult.get(Constants.RESULT_MSG_STR));
				return super.failed(turnMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修刷新成功");
			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在进行缓存刷新时获得异常:",e);
		}
		
		return super.failed(turnMap,1);
	}
    
    /**获取任务图片*/
    @RequestMapping(value = "/getTaskImgList",method = RequestMethod.POST)
   	public @ResponseBody JsonResponse getTaskImgList(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
   		Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
   		
   		try {
   			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
   			
   			Map<String, Object> searchResultMap=this.taskCenterBmo.getTaskImgList(params);
   			
   			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
   				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
   				return super.failed(turnMap, 1);
   			}
   			
   			//查询图片展示前缀
   			String imgIp=commonBmo.getImageServerUrl();
   			
   			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
   			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
   			turnMap.put("pages",searchResultMap.get("pages"));
   			turnMap.put("infoCount",searchResultMap.get("infoCount"));
   			turnMap.put("infoList",searchResultMap.get("infoList"));
   			turnMap.put("imgIp", imgIp);
   			return super.successed(turnMap, 0);
   		} catch (Exception e) {
   			log.error("在查询任务图片时获得异常:",e);
   		}
   		
   		return super.failed(turnMap,1);
   	}
    
    /**进入任务图片新增页面*/
    @RequestMapping(value = "/toEditTaskImgInfo")
    public String toEditTaskImgInfo(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	//操作类型，ADD和EDIT
        	String editType=MapUtils.getString(params, "editType","ADD");
			Long taskId=MapUtils.getLong(params, "taskId",null);
        	
        	model.addAttribute("editType", editType);
        	model.addAttribute("taskId", taskId);
        	model.addAttribute("imgIp", commonBmo.getImageServerUrl());
        } catch (Exception e) {
            log.error("在跳转任务图片新增页面时获得异常 :", e);

            return "/error/error";
        }

        return "/task/taskImgEdit";
    }
    
    /**上传任务图片*/
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadTaskImg", method = RequestMethod.POST)
    public @ResponseBody JsonResponse uploadTaskImg(@RequestParam(value = "file")MultipartFile file, HttpServletRequest request) throws IOException {
    	Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "上传图片失败");
   		
   		try {
   			String fileType = request.getParameter("fileType");
   			String linkType = request.getParameter("linkType");

	        String linkTypeStr = "3";
	        if (linkType != null && !StringUtils.isEmpty(linkType)) {
	        	linkTypeStr = linkType;
	        }

	        if (StringUtils.isEmpty(fileType)) {
	        	turnMap.put(Constants.RESULT_MSG_STR, "文件类型不能为空");
	        	return super.failed(turnMap,1);
	        }
	        
	        Map<String, MultipartFile> fileMap = new HashMap<>();
	        fileMap.put("file",file);
         
	        Map<String, Object> upResultMap = uploadFileService.uploadFile(fileMap,  linkTypeStr, fileType, "/qy-portal","task");
         
	        if(!Constants.RESULT_SUCC.equals(MapUtils.getString(upResultMap, Constants.RESULT_CODE_STR))){
	        	turnMap.put(Constants.RESULT_MSG_STR,upResultMap.get(Constants.RESULT_MSG_STR));
	        	return super.failed(turnMap,1);
	        }
	        
	        Map<String, Object> imgInfo=(Map<String, Object>) MapUtils.getMap(upResultMap, "result");
	        
	        turnMap.putAll(imgInfo);
	        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
	   		turnMap.put(Constants.RESULT_MSG_STR, "上传图片成功");
	   		return super.successed(turnMap,0);
		} catch (Exception e) {
			log.error("在上传任务任务图片时获得异常：",e);
		}
        
        return super.failed(turnMap,1);
    }
    
    /**保存任务图片信息*/
	@RequestMapping(value = "/saveTaskImgInfo",method = RequestMethod.POST)
	public @ResponseBody JsonResponse saveTaskImgInfo(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
		
		try {
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			//判断操作类型
			Map<String, Object> addResultMap=this.taskCenterBmo.addTaskImgInfo(params);
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(addResultMap, Constants.RESULT_CODE_STR))){
				return super.failed(addResultMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在保存任务图片信息时获得异常:",e);
			
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
	
	/**修改图片信息状态*/
	@RequestMapping(value = "/editTaskImgStatus",method = RequestMethod.POST)
	public @ResponseBody JsonResponse editTaskImgStatus(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "修改数据信息失败");
		
		try {
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
			
			params.put("staffId", staffInfo.getStaffId());
			
			//判断操作类型
			Map<String, Object> addResultMap=this.taskCenterBmo.editTaskImgStatus(params);
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(addResultMap, Constants.RESULT_CODE_STR))){
				return super.failed(addResultMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "修改数据信息成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在修改图片状态时获得异常:",e);
			
			turnMap.put(Constants.RESULT_MSG_STR, "保存数据信息异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
	
	/**跳转到地市选择页面*/
    @RequestMapping(value = "toRegionChoose")
    public String ruleRegionDataChoose(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	String ruleId=MapUtils.getString(params, "ruleId","");
        	
        	model.addAttribute("ruleId", ruleId);
        } catch (Exception e) {
            log.error("在跳转地市选择页面时获得异常 :", e);
            return "/error/error";
        }

        return "/task/regionChoose";
    }
    
    /**获取可选择地市*/
    @RequestMapping(value = "/getCommonRegion",method = RequestMethod.POST)
   	public @ResponseBody JsonResponse getCommonRegion(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
   		Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
   		
   		try {
   			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
   			
   			Map<String, Object> searchResultMap=this.taskCenterBmo.getCommonRegionForRule(params);
   			
   			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
   				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
   				return super.failed(turnMap, 1);
   			}
   			
   			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
   			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
   			turnMap.put("pages",searchResultMap.get("pages"));
   			turnMap.put("infoCount",searchResultMap.get("infoCount"));
   			turnMap.put("infoList",searchResultMap.get("infoList"));
   			return super.successed(turnMap, 0);
   		} catch (Exception e) {
   			log.error("在查询可用地市时获得异常:",e);
   		}
   		
   		return super.failed(turnMap,1);
   	}
    
    /**添加规则集合地市数据*/
    @RequestMapping(value = "/addRuleRegionData",method = RequestMethod.POST)
   	public @ResponseBody JsonResponse addRuleRegionData(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
   		Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "添加信息失败");
   		
   		try {
   			Map<String, Object> searchResultMap=this.taskCenterBmo.addRuleRegionData(params);
   			
   			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
   				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
   				return super.failed(turnMap, 1);
   			}
   			
   			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
   			turnMap.put(Constants.RESULT_MSG_STR, "新增完成");
   			return super.successed(turnMap, 0);
   		} catch (Exception e) {
   			log.error("在新增规则集合地市数据时获得异常:",e);
   		}
   		
   		return super.failed(turnMap,1);
   	}
    
    /**跳转到规则地市展示页面*/
    @RequestMapping(value = "toShowRegionRuleData")
    public String toShowRegionRuleData(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session) {
        try {
        	String ruleId=MapUtils.getString(params, "ruleId","");
        	
        	model.addAttribute("ruleId", ruleId);
        } catch (Exception e) {
            log.error("在跳转地市选择页面时获得异常 :", e);
            return "/error/error";
        }

        return "/task/regionRuleData";
    }
    
    /**获取规则地市*/
    @RequestMapping(value = "/getRegionRuleData",method = RequestMethod.POST)
   	public @ResponseBody JsonResponse getRegionRuleData(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
   		Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
   		
   		try {
   			Map<String, Object> searchResultMap=this.taskCenterBmo.getRegionRuleData(params);
   			
   			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
   				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
   				return super.failed(turnMap, 1);
   			}
   			
   			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
   			turnMap.put(Constants.RESULT_MSG_STR, "查询信息完成");
   			turnMap.put("pages",searchResultMap.get("pages"));
   			turnMap.put("infoCount",searchResultMap.get("infoCount"));
   			turnMap.put("infoList",searchResultMap.get("infoList"));
   			return super.successed(turnMap, 0);
   		} catch (Exception e) {
   			log.error("在地市规则集合数据时获得异常:",e);
   		}
   		
   		return super.failed(turnMap,1);
   	}
    
    /**获取规则地市*/
    @RequestMapping(value = "/delRegionRuleData",method = RequestMethod.POST)
   	public @ResponseBody JsonResponse delRegionRuleData(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
   		Map<String, Object> turnMap=new HashMap<String, Object>();
   		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
   		turnMap.put(Constants.RESULT_MSG_STR, "删除失败");
   		
   		try {
   			Map<String, Object> searchResultMap=this.taskCenterBmo.delRegionRuleData(params);
   			
   			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(searchResultMap, Constants.RESULT_CODE_STR))){
   				turnMap.put(Constants.RESULT_MSG_STR, searchResultMap.get(Constants.RESULT_MSG_STR));
   				return super.failed(turnMap, 1);
   			}
   			
   			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
   			turnMap.put(Constants.RESULT_MSG_STR, "删除完成");
   			return super.successed(turnMap, 0);
   		} catch (Exception e) {
   			log.error("在删除规则地市数据集合时获得异常:",e);
   		}
   		
   		return super.failed(turnMap,1);
   	}
}

