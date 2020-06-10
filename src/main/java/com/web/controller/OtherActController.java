package com.web.controller;

import com.web.bean.StaffInfo;
import com.web.bmo.OtherActBmoImpl;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoActivity;
import com.web.util.DateUtil;
import com.web.util.StringUtil;
import com.web.util.common.JsonResponse;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 除秒杀和抽奖外的其它链接活动管理
 *
 * @author
 * @create 2020-05-15
 **/
@Controller("com.web.controller.OtherActController")
@RequestMapping(value = "/otherAct")
public class OtherActController extends BaseController {
    @Resource(name = "com.web.bmo.OtherActBmoImpl")
    private OtherActBmoImpl otherActBmo;
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "activity/otherAct-list";
    }

    /**
     * 活动添加
     * @param request
     * @param m
     * @return
     * @throws IOException
     */
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
        return "activity/otherActAdd";
    }
    /**
     * 其它活动列表查询
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/list", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> qryLotteryList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap = otherActBmo.qryOtherActList(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "活动列表查询异常");
            log.error("活动列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     * 活动详情
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
            resultMap.put("flag","detail");
        }
        m.addAllAttributes(resultMap);
        return "activity/otherActAdd";
    }

    /**
     * 活动修改
     * @param request
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/edit")
    public String edit(HttpServletRequest request, Model m) throws IOException {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        String activityId = request.getParameter("activityId");
        if(!StringUtil.isEmptyStr(activityId)){
            CoActivity coActivity=new CoActivity();
            coActivity.setActivityId(Long.parseLong(activityId));
            resultMap.put("coActivity",coActivity);
            resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            resultMap.put("flag","edit");
        }
        m.addAllAttributes(resultMap);
        return "activity/otherActAdd";
    }

    /**
     * 查询活动详情
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value="/qryActEditInfo", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse qryActEditInfo(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        try {
            Map<String, Object> result = otherActBmo.queryActivity(params);
            if (!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))) {
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("查询活动信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "查询活动信息异常："+e);
        }
        return super.failed(returnMap,1);
    }
    /**
     * 编辑活动保存
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value="/editPro", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse editPro(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "活动保存失败");
        StaffInfo staffInfo=(StaffInfo) request.getSession().getAttribute("staffInfo");
        try {
            params.put("staffInfo",staffInfo);
            Map result=otherActBmo.editActivities(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("活动编保存异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "保存活动信息异常："+e.getMessage());
        }
        return super.failed(returnMap,1);
    }

    /**
     * 删除活动
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value="/deleteAct", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse deleteActInfo(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "删除活动信息失败");
        StaffInfo staffInfo=(StaffInfo) request.getSession().getAttribute("staffInfo");
        params.put("staffInfo",staffInfo);
        try {
            Map<String,Object> result = otherActBmo.deleteAct(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(returnMap, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("删除活动信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "删除活动信息异常："+e);
        }
        return super.failed(returnMap,1);
    }
    /**
     * 保存活动
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value="/saveActivity", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse saveActivity(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
        StaffInfo staffInfo=(StaffInfo) request.getSession().getAttribute("staffInfo");
        try {
            params.put("staffInfo",staffInfo);
            Map result=otherActBmo.saveActivities(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("保存活动信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "保存活动信息异常："+e.getMessage());
        }
        return super.failed(returnMap,1);
    }
}
