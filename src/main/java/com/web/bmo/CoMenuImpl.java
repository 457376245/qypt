package com.web.bmo;

import com.web.dao.CoMenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("com.web.bmo.CoMenuImpl")
public class CoMenuImpl  implements CoMenu{
    @Autowired
    private CoMenuDao coMenuDao;
    @Override
    public List<com.web.model.CoMenu> getMenus() {
        return coMenuDao.getMenus();
    }
}
