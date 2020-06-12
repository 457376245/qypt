package com.web.controller;

import com.web.bmo.CoMenuService;
import com.web.common.BaseController;
import com.web.model.CoMenu;
import com.web.util.GetMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 首页
 * </p>
 *
 * @package: com.web.controller
 * @description: 首页
 * @author: wangzx8
 * @date: 2020/6/4 16:17
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */

@Controller("com.web.controller.IndexController")
@RequestMapping(value = "/index")
public class IndexController extends BaseController {
    @Autowired
    private CoMenuService coMenuService;

    @RequestMapping(value = "")
    public String index(HttpServletRequest request, HttpServletResponse response, Model m) throws IOException {
        return "login/index";
    }
    @RequestMapping(value = "/menus")
    @ResponseBody
    public List getMenus(){
        List<CoMenu> menus = coMenuService.getMenus();
        Map<Integer, CoMenu> menuMap = GetMapUtil.getMenus(menus);
        //System.out.println(menuMap.toString());
        List<Object> list = new ArrayList<>(menuMap.values());
        return list;

    }
}
