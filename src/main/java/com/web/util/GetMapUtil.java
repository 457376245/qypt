package com.web.util;

import com.web.model.CoMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetMapUtil {
    public static Map<Integer, CoMenu> getMenus(List<CoMenu> list){
        //用于放置菜单信息
        Map<Integer, CoMenu> menuMap = new HashMap<>();
        try {

            //用于对象中二级菜单列表
            List<CoMenu> secondMenuList = null;
            //遍历数据
            for (CoMenu coMenu : list) {
                //判断是否有上级菜单
                //没有则放入map
                if (coMenu.getPid() == 0) {
                    menuMap.put(coMenu.getId(), coMenu);
                }
                //有就得到上级菜单对象
                else {
                    CoMenu firstMenu = menuMap.get(coMenu.getPid());
                    if(firstMenu == null){
                        System.out.println("对不起，没找到父节点" + coMenu.toString());
                        continue;
                    }
                    //得到上级菜单对象的二级菜单
                    secondMenuList = firstMenu.getSecondMenu();
                    //判断二级菜单是否为空
                    //为空则新建List
                    if (secondMenuList == null) {
                        secondMenuList = new ArrayList<>();
                    }
                    //将当前对象放入二级菜单列表
                    secondMenuList.add(coMenu);
                    firstMenu.setSecondMenu(secondMenuList);
                }

            }
            return menuMap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return menuMap;
    }
}
