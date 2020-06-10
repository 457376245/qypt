package com.web.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.web.bmo.CoMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.common.BaseController;
 
@Controller("com.web.controller.TestController")
@RequestMapping("/test")
public class TestController extends BaseController{

		
    @RequestMapping(value = "/index")
	 public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session ,Model model){
    	return "activity/active-list";
    }

}
