package com.web.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.web.bean.HttpSvrInitParam;
import com.web.util.DateUtil;
import com.web.util.JsonUtil;
import com.web.util.StringUtil;

/**
 * <b>项目名称：</b>web-portal<br>
 * <b>类名称：</b>com.web.common.ServiceCallSys<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>调用系管公共接口<br>
 * <b>创建时间：</b>2016年5月23日-下午2:52:57<br>
 */

@Service("com.web.common.ServiceCallSys")
public class ServiceCallSys extends BaseController{
	
	@Autowired  
	@Qualifier("redisTemplate")
	private RedisTemplate<String, Object> jedisTemplate;  
	
	/**
	 * 调用系管公共接口
	 * @param interCode 接口名称
	 * @param serviceCode 调用具体接口方法
	 * @param infoMap 传入参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> httpSysServiceCall(String interCode,String serviceCode,Map<String, Object> infoMap) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put("resultCode", ServiceParam.SYS_CALL_FAIL);
		turnMap.put("resultMsg", "调用接口失败");
		
		if(StringUtil.isEmptyStr(interCode)){
			turnMap.put("resultMsg", "调用接口名称不能为空");
			return turnMap;
		}
		
		if(StringUtil.isEmptyStr(serviceCode)){
			turnMap.put("resultMsg", "调用接口方法名称不能为空");
			return turnMap;
		}
		
		Date beginTime = new Date();
		
		//将数据转为JSON格式
		String paramStr="";
		if(infoMap!=null && infoMap.size()>0){
			paramStr=JsonUtil.toString(infoMap);
		}
		
		//获取调用接口等数据
		HttpSvrInitParam initParam=new HttpSvrInitParam();
		initParam.setCfgFileName("oc-ca");//接口配置文件目录名称
		initParam.setUrlStr("service.url");//接口地址参数
		
		log.info("调用["+interCode+"-"+serviceCode+"]接口入参："+paramStr);
		
		//返回参数
		String reqStr = null;
		
		try{
			reqStr = HttpService.getInstance(initParam).call(paramStr,"/"+interCode+"/"+serviceCode);
		}catch(Exception ex){
			log.error("调用远程接口获得异常:",ex);
			turnMap.put("resultCode", ServiceParam.SYS_SERV_EXCEPTION);
			turnMap.put("resultMsg", "调用远程接口异常");
			return turnMap;
		}finally{
			log.info("调用["+interCode+"-"+serviceCode+"]接口,使用时间："+DateUtil.getTimeMargin(beginTime, new Date())+",回参："+reqStr);
		}
		
		if (!JsonUtil.isValidJson(reqStr)) {
			turnMap.put("resultCode", ServiceParam.SYS_DATACONVERSION);
			turnMap.put("resultMsg", "接口返回信息异常");
			log.error("ERROR:调用接口["+interCode+"]-["+serviceCode+"] 返回JSON格式异常");
		}else{
			Map<String, Object> interfaceMap=JsonUtil.toObject(reqStr, Map.class);
			
			if(!"0".equals(MapUtils.getString(interfaceMap, "resultCode"))){
				turnMap.put("resultMsg", interfaceMap.get("resultMsg"));
				return turnMap;
			}
			
			turnMap.put("resultCode", ServiceParam.SYS_SUCCESS);
			turnMap.put("resultMsg", "调用接口成功");
			turnMap.put("result", interfaceMap.get("result"));
		}
		
		return turnMap;
	}
	
	/**
	 * 调用系管公共接口
	 * @param interCode 接口名称
	 * @param serviceCode 调用具体接口方法
	 * @param infoMap 传入参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> httpSysServiceCallJson(String interCode,String serviceCode,String paramStr) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put("resultCode", ServiceParam.SYS_CALL_FAIL);
		turnMap.put("resultMsg", "调用接口失败");
		
		if(StringUtil.isEmptyStr(interCode)){
			turnMap.put("resultMsg", "调用接口名称不能为空");
			return turnMap;
		}
		
		if(StringUtil.isEmptyStr(serviceCode)){
			turnMap.put("resultMsg", "调用接口方法名称不能为空");
			return turnMap;
		}
		
		Date beginTime = new Date();
		
		//获取调用接口等数据
		HttpSvrInitParam initParam=new HttpSvrInitParam();
		initParam.setCfgFileName("oc-ca");//接口配置文件目录名称
		initParam.setUrlStr("service.url");//接口地址参数
		
		log.info("调用["+interCode+"-"+serviceCode+"]接口入参："+paramStr);
		
		//返回参数
		String reqStr = null;
		
		try{
			reqStr = HttpService.getInstance(initParam).call(paramStr,"/"+interCode+"/"+serviceCode);
		}catch(Exception ex){
			log.error("调用远程接口获得异常:",ex);
			turnMap.put("resultCode", ServiceParam.SYS_SERV_EXCEPTION);
			turnMap.put("resultMsg", "调用远程接口异常");
			return turnMap;
		}finally{
			log.info("调用["+interCode+"-"+serviceCode+"]接口,使用时间："+DateUtil.getTimeMargin(beginTime, new Date())+",回参："+reqStr);
		}
		
		if (!JsonUtil.isValidJson(reqStr)) {
			turnMap.put("resultCode", ServiceParam.SYS_DATACONVERSION);
			turnMap.put("resultMsg", "接口返回信息异常");
			log.error("ERROR:调用接口["+interCode+"]-["+serviceCode+"] 返回JSON格式异常");
		}else{
			Map<String, Object> interfaceMap=JsonUtil.toObject(reqStr, Map.class);
			
			if(!"0".equals(MapUtils.getString(interfaceMap, "resultCode"))){
				turnMap.put("resultMsg", interfaceMap.get("resultMsg"));
				return turnMap;
			}
			
			turnMap.put("resultCode", ServiceParam.SYS_SUCCESS);
			turnMap.put("resultMsg", "调用接口成功");
			turnMap.put("result", interfaceMap.get("result"));
		}
		
		return turnMap;
	}
	
	/**
	 * 封装系管返回数据
	 * @param returnMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> sysReturnResult(Map<?,?> returnMap){
		if(null!=returnMap && (!returnMap.isEmpty() )&& returnMap.get("resultCode")!=null && ResultCode.R_SUCCESS.equals(String.valueOf(returnMap.get("resultCode")))){
			String errCode=String.valueOf(returnMap.get("errCode"));
			Map<String,Object> resultData=(Map<String, Object>) returnMap.get("result");
			if(resultData!=null&&resultData.size()>0){
				resultData.put("code", returnMap.get("errCode"));
				resultData.put("message", returnMap.get("resultMsg"));
			}else{
				resultData=new HashMap<String,Object>();
				resultData.put("code", errCode);
				resultData.put("message", returnMap.get("resultMsg"));
			}
			return resultData;
		}else{
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("code", ResultCode.R_SERV_CALL_FAIL);
			resultMap.put("message", "调用服务异常!");
			return resultMap;
		}
	}
	
	/**
	 * CRM3.0调用各中心接口
	 * @param interCode 接口名称
	 * @param serviceCode 调用具体接口方法
	 * @param infoMap 传入参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> httpServiceCallJson(String interCode,String serviceCode,String paramStr) throws Exception{
		Map<String, Object> turnMap=new HashMap<String,Object>();
		turnMap.put("resultCode", ServiceParam.SYS_CALL_FAIL);
		turnMap.put("resultMsg", "调用接口失败");
		
		if(StringUtil.isEmptyStr(interCode)){
			turnMap.put("resultMsg", "调用接口名称不能为空");
			return turnMap;
		}
		
		if(StringUtil.isEmptyStr(serviceCode)){
			turnMap.put("resultMsg", "调用接口方法名称不能为空");
			return turnMap;
		}
		
		Date beginTime = new Date();
		
		//获取调用接口等数据
		HttpSvrInitParam initParam=new HttpSvrInitParam();
		initParam.setCfgFileName("oc-ca");//接口配置文件目录名称
		initParam.setUrlStr("service.url");//接口地址参数
		
		log.info("调用["+interCode+"-"+serviceCode+"]接口入参："+paramStr);
		
		//返回参数
		String reqStr = null;
		
		try{
			reqStr = HttpService.getInstance(initParam).call(paramStr,"/"+interCode+"/"+serviceCode);
		}catch(Exception ex){
			log.error("调用远程接口获得异常:",ex);
			turnMap.put("resultCode", ServiceParam.SYS_SERV_EXCEPTION);
			turnMap.put("resultMsg", "调用远程接口异常");
			return turnMap;
		}finally{
			log.info("调用["+interCode+"-"+serviceCode+"]接口,使用时间："+DateUtil.getTimeMargin(beginTime, new Date())+",回参："+reqStr);
		}
		
		if (!JsonUtil.isValidJson(reqStr)) {
			turnMap.put("resultCode", ServiceParam.SYS_DATACONVERSION);
			turnMap.put("resultMsg", "接口返回信息异常");
			log.error("ERROR:调用接口["+interCode+"]-["+serviceCode+"] 返回JSON格式异常");
		}else{
			Map<String, Object> interfaceMap=JsonUtil.toObject(reqStr, Map.class);
			
			if(!"0".equals(MapUtils.getString(interfaceMap, "resultCode"))){
				turnMap.put("resultMsg", interfaceMap.get("resultMsg"));
				return turnMap;
			}
			
			turnMap.put("resultCode", ServiceParam.SYS_SUCCESS);
			turnMap.put("resultMsg", "调用接口成功");
			turnMap.put("result", interfaceMap.get("result"));
		}
		
		return turnMap;
	}
}
