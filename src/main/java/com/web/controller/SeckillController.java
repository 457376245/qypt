package com.web.controller;

import com.web.bean.StaffInfo;
import com.web.bmo.CommonBmoImpl;
import com.web.bmo.SeckillBmo;
import com.web.bmo.TaskCenterBmo;
import com.web.common.BaseController;
import com.web.common.Constants;
import com.web.model.CoActivity;
import com.web.model.CoDicItem;
import com.web.model.CoRegion;
import com.web.util.DateUtil;
import com.web.util.JsonUtil;
import com.web.util.RestTemplateUtil;
import com.web.util.StringUtil;
import com.web.util.common.JsonResponse;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * 秒杀活动配置
 *
 * @author
 * @create 2020-04-05
 **/
@Controller("com.web.controller.SeckillController")
@RequestMapping(value = "/seckill")
public class SeckillController  extends BaseController {
    @Resource(name = "com.web.bmo.SeckillBmoImpl")
    private SeckillBmo seckillBmo;
    @Resource(name = "com.web.bmo.CommonBmoImpl")
    private CommonBmoImpl commonBmo;
    @Resource
    private RestTemplateUtil restTemplateUtil;
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request,Model m) throws IOException {
        String source=request.getParameter("source");
        if(source==null){
            source="";
        }
        m.addAttribute("source",source);
        return "activity/seckill-list";
    }
    /**修改任务相关数据后，更新缓存信息*/
    @RequestMapping(value = "/reloadTaskAndRewardRedis",method = RequestMethod.POST)
    public @ResponseBody JsonResponse reloadTaskAndRewardRedis(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        Map<String, Object> turnMap=new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "刷新缓存失败");
        try {
            Map<String, Object> reloadResult=this.seckillBmo.reflashRedis();
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
    /**
     * 抽奖活动列表查询
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/list", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Map qryLotteryList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map resultMap=new HashMap();
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
                String result = restTemplateUtil.postHttp(url+"seckill/list",
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
                resultMap = seckillBmo.qrySeckillList(params);
                resultMap.put("code", 0);
                resultMap.put("msg", "");
                return resultMap;
            }
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "秒杀列表查询异常");
            log.error("秒杀列表查询异常:",e);
        }
        return resultMap;
    }

    /**
     * 添加活动
     * @param request
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request,Model m) throws IOException {
        Map resultMap=new HashMap();
        resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
        resultMap.put(Constants.RESULT_MSG_STR, "查询数据信息成功");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        CoActivity coActivity=new CoActivity();
        String startTime= DateUtil.format(calendar.getTime(),"yyyy年MM月dd日");
        coActivity.setStartTimeStr(startTime);
        coActivity.setShowTimeStr(DateUtil.format(calendar.getTime(),DateUtil.DATE_FORMATE_STRING_A));
        resultMap.put("coActivity",coActivity);
        resultMap.put("flag","add");
        try {
            Map param = new HashMap();
            List<CoRegion> coRegions=commonBmo.getRegionList(param);
            m.addAttribute("coRegionList",coRegions);
        }catch (Exception e){
            log.error("查询地市信息异常："+e.getMessage());
        }
        m.addAllAttributes(resultMap);
        return "activity/seckillAdd";
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
            Map result=seckillBmo.saveActivity(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("保存秒杀活动信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "保存秒杀活动信息异常："+e.getMessage());
        }
        return super.failed(returnMap,1);
    }

    /**
     * 活动商品编辑
     * @param request
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/proSet")
    public String proSet(HttpServletRequest request, Model m) throws IOException {
        String activityId=request.getParameter("activityId");
        String flag=request.getParameter("flag");
        if(flag==null){
            flag="edit";
        }
        Map<String, Object> params=new HashMap<>();
        params.put("activityId",activityId);
        Map resultMap=new HashMap();
        try{
            resultMap=seckillBmo.qrySeckillPro(params);
            m.addAttribute("result",resultMap);
            m.addAttribute("resultData", JsonUtil.toString(resultMap));
            m.addAttribute("flag",flag);
            m.addAttribute("parentId",activityId);
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "秒杀列表查询异常");
            log.error("秒杀列表查询异常:",e);
        }
        return "activity/seckillPro";
    }
    /**
     * 活动商品编辑库存
     * @param request
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/editProdStock")
    public String editProdStock(HttpServletRequest request, Model m) throws IOException {
        String activityId=request.getParameter("activityId");
        Map<String, Object> params=new HashMap<>();
        params.put("activityId",activityId);
        Map resultMap=new HashMap();
        try{
            resultMap=seckillBmo.qrySeckillPro(params);
            m.addAttribute("result",resultMap);
            m.addAttribute("resultData", JsonUtil.toString(resultMap));
            m.addAttribute("parentId",activityId);
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "秒杀列表查询异常");
            log.error("秒杀列表查询异常:",e);
        }
        return "activity/seckillStock";
    }
    /**
     * 活动编辑
     * @param request
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/edit")
    public String edit(HttpServletRequest request, Model m) throws IOException {
        Map resultMap=new HashMap();
        resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        String activityId=request.getParameter("activityId");
        if(!StringUtil.isEmptyStr(activityId)){
            CoActivity coActivity=new CoActivity();
            coActivity.setActivityId(Long.parseLong(activityId));
            resultMap.put("coActivity",coActivity);
            resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            resultMap.put("flag","edit");
        }
        try {
            Map param = new HashMap();
            List<CoRegion> coRegions=commonBmo.getRegionList(param);
            m.addAttribute("coRegionList",coRegions);
        }catch (Exception e){
            log.error("查询地市信息异常："+e.getMessage());
        }
        m.addAllAttributes(resultMap);
        return "activity/seckillAdd";
    }
    /**
     * 编辑活动
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
            Map result=seckillBmo.editPro(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("活动编保存异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "保存秒杀活动信息异常："+e.getMessage());
        }
        return super.failed(returnMap,1);
    }

    /**
     * 删除下架活动
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value="/del", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse del(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "活动删除失败");
        StaffInfo staffInfo=(StaffInfo) request.getSession().getAttribute("staffInfo");
        params.put("staffInfo",staffInfo);
        try {
            Map result=seckillBmo.deleteAct(params);
            params.put("staffInfo",staffInfo);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("活动删除异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "活动删除异常："+e.getMessage());
        }
        return super.failed(returnMap,1);
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
        Map resultMap=new HashMap();
        resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        resultMap.put(Constants.RESULT_MSG_STR, "查询数据信息失败");
        String activityId=request.getParameter("activityId");
        if(!StringUtil.isEmptyStr(activityId)){
            CoActivity coActivity=new CoActivity();
            coActivity.setActivityId(Long.parseLong(activityId));
            resultMap.put("coActivity",coActivity);
            resultMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            resultMap.put("flag","detail");
            try {
                Map param = new HashMap();
                List<CoRegion> coRegions=commonBmo.getRegionList(param);
                m.addAttribute("coRegionList",coRegions);
            }catch (Exception e){
                log.error("查询地市信息异常："+e.getMessage());
            }
        }
        m.addAllAttributes(resultMap);
        return "activity/seckillAdd";
    }
    /**
     * 活动详情
     * @param request
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/info")
    public String info(HttpServletRequest request, Model m) throws IOException {
        String activityId=request.getParameter("activityId");
        m.addAttribute("activityId",activityId);
        return "activity/seckillDetail";
    }
    /**
     * 查询活动相关详情
     * @param params
     * @param request
     * @return
     */
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
                String result = restTemplateUtil.postHttp(url+"seckill/qryActEditInfo",
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
                Map result = seckillBmo.qrySeckillAct(params);
                if (!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))) {
                    return super.failed(returnMap, 1);
                }
                return super.successed(result, 0);
            }
        } catch (Exception e) {
            log.error("查询秒杀活动信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "查询秒杀活动信息异常："+e);
        }
        return super.failed(returnMap,1);
    }
    /**
     * 保存活动下的权益商品
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value="/saveActPro", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse saveActPro(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
        StaffInfo staffInfo=(StaffInfo) request.getSession().getAttribute("staffInfo");
        try {
            params.put("staffInfo",staffInfo);
            Map result=seckillBmo.saveSeckill(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("保存秒杀权益商品信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "保存秒杀权益商品信息异常："+e.getMessage());
        }
        return super.failed(returnMap,1);
    }
    /**
     * 保存活动下的权益商品
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value="/saveActProStock", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    JsonResponse saveActProStock(@RequestBody Map<String, Object> params, HttpServletRequest request){
        Map<String, Object> returnMap=new HashMap<String, Object>();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "保存数据信息失败");
        StaffInfo staffInfo=(StaffInfo) request.getSession().getAttribute("staffInfo");
        try {
            params.put("staffInfo",staffInfo);
            Map result=seckillBmo.saveSeckillStock(params);
            if(!Constants.RESULT_SUCC.equals(MapUtils.getString(result, Constants.RESULT_CODE_STR))){
                return super.failed(result, 1);
            }
            return super.successed(result, 0);
        } catch (Exception e) {
            log.error("保存秒杀权益商品信息异常:",e);
            returnMap.put(Constants.RESULT_MSG_STR, "保存秒杀权益商品信息异常："+e.getMessage());
        }
        return super.failed(returnMap,1);
    }
}
