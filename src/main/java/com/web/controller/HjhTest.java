package com.web.controller;

import com.web.bmo.CoMenuService;
import com.web.model.CoMenu;
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
    //存放所有菜单
    Map<Integer, CoMenu> menuMap = new HashMap<Integer, CoMenu>();
    //用于对象中二级菜单列表
    List<CoMenu> secondMenuList=null;
    @Autowired
    private CoMenuService coMenuService;

    @RequestMapping(value = "/test")
    @ResponseBody
    public Map test(){
        //得到数据库所有菜单信息
        List<CoMenu> menus = coMenuService.getMenus();
        //遍历数据
        for (CoMenu coMenu:menus){
            //判断是否有上级菜单
            if(coMenu.getPid()==0){
                //没有则放入map
                menuMap.put(coMenu.getId(),coMenu);
            }
            else {
                //有就得到上级菜单对象
                CoMenu parentMenu=menuMap.get(coMenu.getPid());
                //得到上级菜单对象的二级菜单
                secondMenuList=parentMenu.getSecondMenu();
                //判断二级菜单是否为空
                if (secondMenuList==null){
                    //为空则新建List
                    secondMenuList=new ArrayList<>();
                }
                //将当前对象放入二级菜单列表
                secondMenuList.add(coMenu);
                parentMenu.setSecondMenu(secondMenuList);

                menuMap.put(coMenu.getId(),coMenu);
            }
        }
        System.out.println(menuMap);
        return menuMap;

    }
}
