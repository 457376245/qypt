package com.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.bean.StaffInfo;
import com.web.bmo.LogWarningBmo;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.util.common.JsonResponse;

/** 
 * 创建人：yibo
 * 类描述：告警处理入口
 * 创建时间：2020年4月17日下午10:44:11
 */
@Controller("com.web.controller.MonitorController")
@RequestMapping(value = "/monitor")
public class MonitorController extends BaseController {
	
	@Resource(name = "com.web.bmo.LogWarningBmoImpl")
	private LogWarningBmo logWarningBmo;
	
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response,Model m) throws IOException {
    	String source=request.getParameter("source");
        if(source==null){
            source="";
        }
        m.addAttribute("source",source);
    	return "monitor/monitor-list";
    }
    
    /**
     * 监控告警列表查询
     * @param params
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/list", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> qryLotteryList(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
        	String source = request.getParameter("source");
        	if("choose".equals(source)){
        		params.put("optResult", "101");
        	}
            resultMap = logWarningBmo.qryMonitorList(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "监控告警列表查询异常");
            log.error("监控告警列表查询异常:",e);
        }
        return resultMap;
    }
    
    @RequestMapping(value="/updateLogWarning", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse updateLogWarning(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpSession session){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "更新活动状态失败");
        try {
        	StaffInfo staffInfo=(StaffInfo) session.getAttribute("staffInfo");			
			params.put("staffId", staffInfo.getStaffId());
            Map<String,Object> result = logWarningBmo.updateLogWarning(params);
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
    
    @RequestMapping(value = "/monitorChoose")
    public String monitorChoose(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "monitor/monitor-choose";
    }
}
