package com.web.bean;

/**
 * 初始参数据类
 * @author fanghj
 *
 */
public class HttpSvrInitParam {
	
	private String cfgFileName;
	private String timeoutStr;
	private String maxTotalStr;
	private String maxPerRouteStr;
	private String urlStr;
	private String methodStr;
	
	public String getCfgFileName() {
		return cfgFileName;
	}
	public void setCfgFileName(String cfgFileName) {
		this.cfgFileName = cfgFileName;
	}
	public String getTimeoutStr() {
		return timeoutStr;
	}
	public void setTimeoutStr(String timeoutStr) {
		this.timeoutStr = timeoutStr;
	}
	public String getMaxTotalStr() {
		return maxTotalStr;
	}
	public void setMaxTotalStr(String maxTotalStr) {
		this.maxTotalStr = maxTotalStr;
	}
	public String getMaxPerRouteStr() {
		return maxPerRouteStr;
	}
	public void setMaxPerRouteStr(String maxPerRouteStr) {
		this.maxPerRouteStr = maxPerRouteStr;
	}
	public String getUrlStr() {
		return urlStr;
	}
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	public String getMethodStr() {
		return methodStr;
	}
	public void setMethodStr(String methodStr) {
		this.methodStr = methodStr;
	}
	

}
