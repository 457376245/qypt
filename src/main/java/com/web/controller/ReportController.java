package com.web.controller;

import com.web.bmo.ReportBmo;
import com.web.bmo.ReportBmoImpl;
import com.web.common.BaseController;
import com.web.util.JsonUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表
 *
 * @author
 * @create 2020-04-14
 **/
@Controller("com.web.controller.ReportController")
@RequestMapping(value = "/report")
public class ReportController extends BaseController {
    @Resource(name = "com.web.bmo.ReportBmoImpl")
    private ReportBmo reportBmo;

    /**
     * 活动报表
     * @param request
     * @param response
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/actIndex")
    public String index(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "report/actReport-list";
    }

    /**
     * 中奖记录统计报表
     * @param request
     * @param response
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/userPrivilegeIndex")
    public String userPrivilegeIndex(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "report/userPrivilege-list";
    }
    /**
     * 新增会员统计
     * @param request
     * @param response
     * @param m
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/memberInfoIndex")
    public String memberInfoIndex(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "report/memberInfo-list";
    }
    /**
     * 抽奖活动列表查询
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/lotteryList", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> lotteryList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            params.put("activityMode","1");
            resultMap=reportBmo.qryActList(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "抽奖活动列表查询异常");
            log.error("抽奖活动列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     * 秒杀活动列表查询
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/seckillList", method = RequestMethod.POST)
    public @ResponseBody
    Map seckillList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map resultMap=new HashMap();
        try{
            params.put("activityMode","2");
            resultMap=reportBmo.qryActList(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "秒杀列表查询异常");
            log.error("秒杀列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     * 秒杀活动列表查询
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/seckillProList", method = RequestMethod.POST)
    public @ResponseBody
    Map seckillProList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map resultMap=new HashMap();
        try{
            resultMap=reportBmo.qrySeckillProList(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "秒杀商品列表查询异常");
            log.error("秒杀商品列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     * 抽奖活动列表查询
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/lotteryProList", method = RequestMethod.POST)
    public @ResponseBody
    Map lotteryProList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map resultMap=new HashMap();
        try{
            resultMap=reportBmo.qryLotteryProList(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "抽奖奖品列表查询异常");
            log.error("抽奖奖品列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     * 导出活动列表
     * @param response
     * @return
     */
    @RequestMapping(value="/actExport")
    public void actExport(HttpServletRequest httpRequest,  HttpServletResponse response){
        OutputStream os = null;
        try {
            Map<String, Object> params=new HashMap<>();
            if(httpRequest.getParameter("param")!=null){
                String param= URLDecoder.decode(httpRequest.getParameter("param"),"UTF-8");
                params= JsonUtil.toObject(param,Map.class);
            }
            String exportFileName = "活动报表";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(exportFileName.getBytes("gb2312"), "ISO8859-1")+ ".xls");
            os = response.getOutputStream();
            reportBmo.exportActPros(os,params);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *  按奖品查询会员中奖记录
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/privilegeByPrizeList", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> privilegeByPrizeList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap=reportBmo.queryPrivilegeByPrize(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "中奖会员列表查询异常");
            log.error("中奖会员列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     *  查询中奖会员列表
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/hasPrivilegeUserList", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> hasPrivilegeUserList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap=reportBmo.queryHasPrivilegeUser(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "中奖会员列表查询异常");
            log.error("中奖会员列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     *  查询指定会员的中奖记录
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/prizeByUserList", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> prizeByUserList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap=reportBmo.queryPrizeByUser(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "中奖会员列表查询异常");
            log.error("中奖会员列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     * 导出中奖记录
     * @param response
     * @return
     */
    @RequestMapping(value="/userPrivilegExport")
    public void userPrivilegExport(HttpServletRequest httpRequest,  HttpServletResponse response){
        OutputStream os = null;
        try {
            Map<String, Object> params=new HashMap<>();
            if(httpRequest.getParameter("param")!=null){
                String param= URLDecoder.decode(httpRequest.getParameter("param"),"UTF-8");
                params= JsonUtil.toObject(param,Map.class);
            }
            String exportFileName = "中奖记录";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(exportFileName.getBytes("gb2312"), "ISO8859-1")+ ".xls");
            os = response.getOutputStream();
            reportBmo.exportUserPrivilege(os,params);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *  按时间统计新增会员
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/queryNewMemberByTime", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> queryNewMemberByTime(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap=reportBmo.queryNewMemberByTime(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "按时间统计新增会员列表查询异常");
            log.error("按时间统计新增会员列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     *  按时间地区统计新增会员
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/queryNewMemberByArea", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> queryNewMemberByArea(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap=reportBmo.queryNewMemberByArea(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "按时间地区统计新增会员列表查询异常");
            log.error("按时间统计新增会员列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     *  按时间统计新增会员
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/queryNewMemberDetail", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> queryNewMemberDetail(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap=reportBmo.queryNewMemberDetail(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "按时间统计新增会员列表查询异常");
            log.error("按时间统计新增会员列表查询异常:",e);
        }
        return resultMap;
    }
    /**
     * 导出新增客户记录
     * @param response
     * @return
     */
    @RequestMapping(value="/exportNewMember")
    public void exportNewMember(HttpServletRequest httpRequest,  HttpServletResponse response){
        OutputStream os = null;
        try {
            Map<String, Object> params=new HashMap<>();
            if(httpRequest.getParameter("param")!=null){
                String param= URLDecoder.decode(httpRequest.getParameter("param"),"UTF-8");
                params= JsonUtil.toObject(param,Map.class);
            }
            String exportFileName = "新增客户记录";
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(exportFileName.getBytes("gb2312"), "ISO8859-1")+ ".xls");
            os = response.getOutputStream();
            reportBmo.exportNewMember(os,params);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
