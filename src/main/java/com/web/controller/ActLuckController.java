package com.web.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.web.bmo.CommonBmoImpl;
import com.web.model.CoDicItem;
import com.web.util.RestTemplateUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.bean.StaffInfo;
import com.web.bmo.ActLuckBmo;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoActivity;
import com.web.model.CoRegion;
import com.web.util.DateUtil;
import com.web.util.JsonUtil;
import com.web.util.StringUtil;
import com.web.util.common.JsonResponse;

/**
 * 抽奖入口
 *
 * @author
 * @create 2020-03-13
 **/
@Controller("com.web.controller.ActLuckController")
@RequestMapping(value = "/lottery")
public class ActLuckController extends BaseController {
    @Resource(name = "com.web.bmo.ActLuckBmoImpl")
    private ActLuckBmo actLuckBmo;
    @Resource
    private RestTemplateUtil restTemplateUtil;
    @Resource(name = "com.web.bmo.CommonBmoImpl")
    private CommonBmoImpl commonBmo;
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response,Model m) throws IOException {
    	String source=request.getParameter("source");
        if(source==null){
            source="";
        }
        m.addAttribute("source",source);
        return "activity/turntable-list";
    }
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, Model m) throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        String startTime= DateUtil.format(calendar.getTime(),"yyyy-MM-dd 00:00:00");
        calendar.add(Calendar.DAY_OF_MONTH,5);
        String endTime=DateUtil.format(calendar.getTime(),"yyyy-MM-dd 00:00:00");
        m.addAttribute("startTime",startTime);
        m.addAttribute("endTime",endTime);
        m.addAttribute("flag","add");
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("areaLevel", "2");
        List<CoRegion> coRegionList = actLuckBmo.queryCoRegionList(paramMap);
        m.addAttribute("coRegionList", coRegionList);
        return "activity/turntableAdd";
    }
    @RequestMapping(value = "/prizeSet")
    public String prizeSet(HttpServletRequest request, Model m) throws IOException {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        String activityId = request.getParameter("activityId");
        if(!StringUtil.isEmptyStr(activityId)){
            Map<String,Object> paramsMap = new HashMap<String,Object>();
            paramsMap.put("activityId", activityId);
            CoActivity coActivity = actLuckBmo.queryActivity(paramsMap);
            if(coActivity == null){
                coActivity = new CoActivity();
                coActivity.setActivityId(Long.parseLong(activityId));
            }
            resultMap.put("coActivity",coActivity);
            resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            resultMap.put("flag","edit");
        }
        m.addAllAttributes(resultMap);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("areaLevel", "2");
        List<CoRegion> coRegionList = actLuckBmo.queryCoRegionList(paramMap);
        m.addAttribute("coRegionList", coRegionList);
        return "activity/turntable-auckSet";
    }
    /**
     * 抽奖活动列表查询
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/list", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> qryLotteryList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            if("1".equals(params.remove("env"))){//非当前环境
                Map<String, Object> headerMap = new HashMap<>();
                headerMap.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                log.debug("入参：{}"+JsonUtil.toString(params));
                Map<String,Object> dicItem=new HashMap<>();
                dicItem.put("groupCode","CLUSTER_CONFIG");
                dicItem.put("itemCode","CONFIG_URL");
                List<CoDicItem> dicItems=commonBmo.getDicItems(dicItem);
                String url=dicItems.get(0).getItemVal();
                //String result = restTemplateUtil.postHttp("http://127.0.0.1:8081/qy-conf/"+"seckill/list",
                String result = restTemplateUtil.postHttp(url+"lottery/list",
                        params, headerMap);
                log.debug("回参：{}"+result);
                if (!JsonUtil.isValidJson(result)) {
                    resultMap.put("code", -1);
                    resultMap.put("msg", "秒杀列表查询异常");
                } else {
                    Map<String, Object> interfaceMap = JsonUtil.toObject(result, Map.class);
                    return interfaceMap;
                }
            }else {
                resultMap = actLuckBmo.qryLotteryList(params);
                resultMap.put("code", 0);
                resultMap.put("msg", "");
                return resultMap;
            }
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "抽奖活动列表查询异常");
            log.error("抽奖活动列表查询异常:",e);
        }
        return resultMap;
    }
    @SuppressWarnings({ "deprecation", "unchecked" })
    @RequestMapping(value = "/editActivities",method = RequestMethod.POST)
    public @ResponseBody JsonResponse editActivities(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        Map<String, Object> turnMap=new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "保存抽奖活动信息失败");

        try {
            //获取前台传入的参数
            //获取登录数据信息
            StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");
            params.put("staffId", staffInfo.getStaffId());
            String paramStr = StringEscapeUtils.unescapeHtml4(JsonUtil.toString(params));

            Map<String, Object> resultMap=this.actLuckBmo.editActivities(JsonUtil.toObject(paramStr, Map.class));

            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(resultMap, Constants.RESULT_CODE_STR))){
                return super.failed(resultMap, 1);
            }

            turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            turnMap.put(Constants.RESULT_MSG_STR, "保存抽奖活动信息成功");

            return super.successed(turnMap, 0);
        } catch (Exception e) {
            log.error("在保存抽奖活动信息时获得差异:",e);
            turnMap.put(Constants.RESULT_MSG_STR, "保存抽奖活动信息异常："+e);
        }

        return super.failed(turnMap,1);
    }
    /**
     * 添加抽奖活动
     * @param params
     * @param request
     * @param response
     * @param session
     * @return
     * @throws Exception
     */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@RequestMapping(value = "/saveActivities",method = RequestMethod.POST)
	public @ResponseBody JsonResponse addActivities(@RequestBody Map<String, Object> params,HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		Map<String, Object> turnMap=new HashMap<String, Object>();
		turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		turnMap.put(Constants.RESULT_MSG_STR, "保存抽奖活动信息失败");
		
		try {			
			//获取前台传入的参数
			//获取登录数据信息
			StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");			
			params.put("staffId", staffInfo.getStaffId());
		    String paramStr = StringEscapeUtils.unescapeHtml4(JsonUtil.toString(params));

			Map<String, Object> resultMap=this.actLuckBmo.saveActivities(JsonUtil.toObject(paramStr, Map.class));
				
			if(!Constants.RESULT_SUCC.equals(MapUtils.getString(resultMap, Constants.RESULT_CODE_STR))){
				return super.failed(resultMap, 1);
			}
			
			turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
			turnMap.put(Constants.RESULT_MSG_STR, "保存抽奖活动信息成功");

			return super.successed(turnMap, 0);
		} catch (Exception e) {
			log.error("在保存抽奖活动信息时获得差异:",e);			
			turnMap.put(Constants.RESULT_MSG_STR, "保存抽奖活动信息异常："+e);
		}
		
		return super.failed(turnMap,1);
	}
	
	/**
	 * 转盘活动详情
	 * @param request
	 * @param m
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/detail")
    public String detail(HttpServletRequest request, Model m) throws IOException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        String activityId = request.getParameter("activityId");
        if(!StringUtil.isEmptyStr(activityId)){
            CoActivity coActivity=new CoActivity();
            coActivity.setActivityId(Long.parseLong(activityId));
            resultMap.put("coActivity",coActivity);
            resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
        }
        m.addAllAttributes(resultMap);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("areaLevel", "2");
        List<CoRegion> coRegionList = actLuckBmo.queryCoRegionList(paramMap);
        m.addAttribute("coRegionList", coRegionList);
        return "activity/turntable-detail";
    }
	
	
	@RequestMapping(value = "/edit")
    public String edit(HttpServletRequest request, Model m) throws IOException {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        String activityId = request.getParameter("activityId");
        if(!StringUtil.isEmptyStr(activityId)){
        	Map<String,Object> paramsMap = new HashMap<String,Object>();
        	paramsMap.put("activityId", activityId);
            CoActivity coActivity = actLuckBmo.queryActivity(paramsMap);
            if(coActivity == null){
            	coActivity = new CoActivity();
            	coActivity.setActivityId(Long.parseLong(activityId));
            }
            resultMap.put("coActivity",coActivity);
            resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            resultMap.put("flag","edit");
        }
        m.addAllAttributes(resultMap);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("areaLevel", "2");
        List<CoRegion> coRegionList = actLuckBmo.queryCoRegionList(paramMap);
        m.addAttribute("coRegionList", coRegionList);
        return "activity/turntableAdd";
    }
	@RequestMapping(value="/qryActEditInfo", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse qryActEditInfo(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        try {
            if("1".equals(params.remove("env"))){//非当前环境
                Map<String, Object> headerMap = new HashMap<>();
                headerMap.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                log.debug("入参：{}"+JsonUtil.toString(params));
                Map<String,Object> dicItem=new HashMap<>();
                dicItem.put("groupCode","CLUSTER_CONFIG");
                dicItem.put("itemCode","CONFIG_URL");
                List<CoDicItem> dicItems=commonBmo.getDicItems(dicItem);
                String url=dicItems.get(0).getItemVal();
                String result = restTemplateUtil.postHttp(url+"lottery/qryActEditInfo",
                        //String result = restTemplateUtil.postHttp("http://127.0.0.1:8081/qy-conf/"+"seckill/qryActEditInfo",
                        params, headerMap);
                log.debug("回参：{}"+result);
                if (!JsonUtil.isValidJson(result)) {
                    return super.failed(returnMap, 1);
                } else {
                    JsonResponse interfaceMap = JsonUtil.toObject(result, JsonResponse.class);
                    return interfaceMap;
                }
            }else {
                Map<String, Object> result = actLuckBmo.qryActLuck(params);
                if (!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))) {
                    return super.failed(result, 1);
                }
                return super.successed(result, 0);
            }
        } catch (Exception e) {
            log.error("查询转盘活动信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "查询转盘活动信息异常："+e);
        }
        return super.failed(returnMap,1);
    }
	
	@RequestMapping(value="/deleteActInfo", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse deleteActInfo(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "删除转盘活动信息失败");
        try {
            Map<String,Object> result = actLuckBmo.deleteActInfo(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(returnMap, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("删除转盘活动信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "删除转盘活动信息异常："+e);
        }
        return super.failed(returnMap,1);
    }
	
	@RequestMapping(value="/updateActstatusCd", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse updateActstatusCd(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpSession session){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "更新活动状态失败");
        try {
        	StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");			
			params.put("staffId", staffInfo.getStaffId());
            Map<String,Object> result = actLuckBmo.updateActstatusCd(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(returnMap, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("更新活动状态异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "更新活动状态异常："+e);
        }
        return super.failed(returnMap,1);
    }
}
