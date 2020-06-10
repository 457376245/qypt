package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.web.model.LogWarning;
@Repository("com.web.dao.LogWarningDao")
public interface LogWarningDao {
	
	void insertLogWarning(LogWarning logWarning)throws Exception;
	
    List<LogWarning> queryLogWarningList(LogWarning logWarning)throws Exception;
    
    List<Map<String,Object>> qryMonitorList(Map<String,Object> paramsMap)throws Exception;
    
    void updateLogWarning(LogWarning logWarning)throws Exception;

    void deleteLogWarning(LogWarning logWarning)throws Exception;
}
