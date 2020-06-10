package com.web.controller;

import com.web.bmo.CoMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HjhTest {
    @Autowired
    private CoMenu coMenu;
    @RequestMapping(value = "/test")
    public void test(){
        List<com.web.model.CoMenu> menus = coMenu.getMenus();
        for (com.web.model.CoMenu coMenu:menus){
            System.out.println(menus);
        }
    }
}
