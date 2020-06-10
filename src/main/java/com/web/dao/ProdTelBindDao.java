package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.web.model.ProdTelBind;

@Repository("com.web.dao.ProdTelBindDao")
public interface ProdTelBindDao {
    
    List<ProdTelBind> getProdTelBindList(Map<String,Object> paramMap);
    
}
