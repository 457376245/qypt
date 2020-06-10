package com.web.controller;

import com.web.bmo.ActLuckBmo;
import com.web.bmo.QyProductBmo;
import com.web.common.BaseController;
import com.web.util.common.CommonParams;
import com.web.util.common.JsonResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 权益商品相关
 *
 * @author
 * @create 2020-04-01
 **/
@Controller("com.web.controller.QyProductController")
@RequestMapping(value = "/qyproduct")
public class QyProductController extends BaseController {
    @Resource(name = "com.web.bmo.QyProductBmoImpl")
    private QyProductBmo qyProductBmo;
    @RequestMapping(value = "/prizeChoose")
    public String prizeChoose(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "qyproduct/prize-choose";
    }
    @RequestMapping(value = "/seckillChoose")
    public String seckillChoose(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "qyproduct/seckill-choose";
    }
    /**
     * 抽奖活动列表查询
     * @param params
     * @param httpRequest
     * @return
     */
    @RequestMapping(value="/list", consumes="application/json", method = RequestMethod.POST)
    public @ResponseBody
    Map qryLotteryList(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map resultMap=new HashMap();
        resultMap.put("code", -1);
        resultMap.put("msg", "权益商品列表查询失败");
        try{
            resultMap=qyProductBmo.rightsQry(params);
            if( CommonParams.RESULT_SUCC.equals(resultMap.get("resultCode"))) {
                resultMap.put("code", 0);
                resultMap.put("msg", "");
            }
        }catch(Exception e){
            resultMap.put("msg", "权益商品列表查询异常");
            log.error("权益商品列表查询异常:",e);
        }
        return resultMap;
    }
}
