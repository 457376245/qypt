package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.web.model.CoOrder;
import com.web.model.CoSubOrder;

@Repository("com.web.dao.OrderDao")
public interface OrderDao {
    
    void updateOrder(CoOrder coOrder);
    
    List<Map<String,Object>> getCoOrderList(Map<String, Object> paramMap);
    
    void updateSubOrder(CoSubOrder coSubOrder);
}
