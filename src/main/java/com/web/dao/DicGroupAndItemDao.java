package com.web.dao;

import com.web.model.CoDicItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository("comm.dicGroupAndItemDao")
public interface DicGroupAndItemDao {

    List<CoDicItem> queryDicItemByCode(Map param);
}
