package com.web.controller;

import com.web.bmo.CoMenuService;
import com.web.model.CoMenu;
import com.web.util.GetMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HjhTest {
    @Autowired
    private CoMenuService coMenuService;

    @RequestMapping(value = "/test")
    @ResponseBody
    public Map test(){
        List<CoMenu> menus = coMenuService.getMenus();
        Map<Integer, CoMenu> menuMap = GetMapUtil.getMenus(menus);
        return menuMap;

    }
}
