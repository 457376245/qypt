package com.web.bmo;

import java.util.Map;

public interface LogWarningBmo {
	
	public Map<String, Object> insertLogWarning(Map<String, Object> paramsMap) throws Exception;
	
	public Map<String, Object> qryMonitorList(Map<String, Object> paramsMap) throws Exception;
	
	public Map<String, Object> updateLogWarning(Map<String, Object> paramsMap) throws Exception;
	
}
