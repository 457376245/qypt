package com.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.bmo.OrderBmo;
import com.web.bmo.RightsBmo;
import com.web.common.BaseController;
import com.web.util.MapUtil;
import com.web.util.common.Log;

/**
 * 创建人：yibo
 * 类描述：权益
 * 创建时间：2020年4月5日下午1:16:29
 */
@Controller("com.web.controller.RightsController")
@RequestMapping("/rights")
public class RightsController extends BaseController{
	
	protected final Log log = Log.getLog(getClass());
	
	@Resource(name = "com.web.bmo.OrderBmoImpl")
	private OrderBmo orderBmo;	
	
	@Resource(name = "com.web.bmo.RightsBmoImpl")
	private RightsBmo rightsBmo;
	
	
	@RequestMapping(value="/qry",method = {RequestMethod.GET})
	public String equity(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
		try{			
			return "rights/qry";
		}catch(Exception ex){
			log.error("用户已领在用权益查询异常：", ex);
			request.setAttribute("message", "用户已领在用权益查询失败，请稍后");
			return "common/info";
		}
	}	
	
	@RequestMapping(value="/rightsInstQry", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> rightsInstQry(@RequestBody Map<String, Object> params, HttpServletRequest httpRequest){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        try{
            resultMap = rightsBmo.rightsInstQry(params);
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            return resultMap;
        }catch(Exception e){
            resultMap.put("code", -1);
            resultMap.put("msg", "用户在用权益列表查询异常");
            log.error("用户在用权益列表查询异常:",e);
        }
        return resultMap;
    }
}
