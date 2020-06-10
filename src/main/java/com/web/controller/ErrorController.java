package com.web.controller;

import com.web.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <b>项目名称：</b>oc-portal<br>
 * <b>类名称：</b>com.web.controller.ErrorController.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>错误提示<br>
 * <b>创建时间：</b>2017年4月26日-下午4:00:07<br>
 */

@Controller("com.web.controller.ErrorController")
@RequestMapping("/error")
public class ErrorController extends BaseController {
	
	@RequestMapping(value = "/403")
    public String error403(@RequestParam Map<String, Object> params, WebRequest webRequest, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session)  {
		//返回登录页面
		return "/error/403";
	}
	
	@RequestMapping(value = "/404")
    public String error404(@RequestParam Map<String, Object> params, WebRequest webRequest, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session)  {
		//返回登录页面
		return "/error/404";
	}
	
	@RequestMapping(value = "/500")
    public String error500(@RequestParam Map<String, Object> params, WebRequest webRequest, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session)  {
		//返回登录页面
		return "/error/500";
	}
	
	@RequestMapping(value = "/error")
    public String error(@RequestParam Map<String, Object> params, WebRequest webRequest, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession, Model model, HttpSession session)  {
		//返回登录页面
		return "/error/error";
	}
}
