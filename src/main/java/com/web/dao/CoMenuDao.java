package com.web.dao;

import com.web.model.CoMenu;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * 项目名称：qy-conf
 * 类名称：com.web.dao.CoMenuDao.java
 * 创建人：HJH
 * 类描述：公共数据
 */
@Repository("com.web.dao.CoMenuDao")
public interface CoMenuDao {
    List<CoMenu> getMenus();
}
