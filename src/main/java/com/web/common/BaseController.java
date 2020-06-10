package com.web.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.web.util.MessageSourceHelper;
import com.web.util.StringUtil;
import com.web.util.common.JsonError;
import com.web.util.common.JsonResponse;
import com.web.util.common.Log;
import com.web.util.common.Result;

/**
 * <b>项目名称：</b>web-portal<br>
 * <b>类名称：</b>com.web.controller.BaseController<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>基础类<br>
 * <b>创建时间：</b>2016年9月21日-下午4:37:17<br>
 */


public class BaseController {
    /**
     * 日志输出.
     */
    protected final Log log = Log.getLog(getClass());
    /**
     * 响应头返回编码
     * */
    public static final String RESP_CODE = "respCode";
    /**
     * 响应头返回提示信息
     */
    public static final String RESP_MSG = "respMsg";
    

    public static final String THEME_DEFAULT = "default";
   
	@Autowired
	private  HttpServletRequest request;
	
	@Autowired(required=false)
	@Qualifier("themeResolver")
	protected ThemeResolver themeResolver;
	
	@Autowired
	@Qualifier("localeResolver")
	protected SessionLocaleResolver localeResolver;
	
	@Autowired
	@Qualifier(value = "MessageSourceHelper")
	protected MessageSourceHelper message;
	
	@Autowired  
	@Qualifier("redisTemplate")
	protected RedisTemplate<String, Object> jedisTemplate;
	
	@Autowired
	@Qualifier("com.web.common.PropertyToRedis")
	protected PropertyToRedis propertyToRedis;
	
	/**
	 * current HttpServletRequest
	 * @return HttpServletRequest current request
	 */
	protected HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * redirect 跳转路径
	 * @param path 以/开头的路径
	 * @return String redirect:+path
	 */
	protected String redirect(String path){
		return "redirect:"+path;
	}
	/**
	 * 获取主题名
	 * @return String themeName
	 */
	protected String getTheme(){
		return themeResolver.resolveThemeName(request);
	}
	/**
	 * 以下所有方法都有如下这个请求. <BR>
	 * 方法添加如 @ModelAttribute("ajaxRequest") boolean ajaxRequest，参数<BR>
	 * 判断该方法是ajax请求,true:是
	 * <P>
	 * 
	 * @param request
	 *            WebRequest
	 * @param model
	 *            Model
	 */
//	@ModelAttribute
//	public void ajaxAttribute(WebRequest request, Model model) {
//		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
//	}


	/**
	 * 获取验证错误信息.
	 * <P>
	 * 
	 * @param result
	 *            BindingResult Valid
	 * @return List errorList 错误列表
	 */
	protected List<JsonError> getErrorList(BindingResult result) {
		List<JsonError> errorList = new ArrayList<JsonError>();
		List<FieldError> filedErrorList = result.getFieldErrors();
		if (filedErrorList != null) {
			for (FieldError error : filedErrorList) {
				JsonError jsonError = new JsonError(error.getField(),
						error.getDefaultMessage());
				errorList.add(jsonError);
			}
		}
		return errorList;
	}

	/**
	 * 获取验证错误信息,针对 JavaBean验证.
	 * <P>
	 * 
	 * @param result
	 *            BindingResult Valid
	 * @param code
	 *            错误编码 code
	 * @return List errorList 错误列表
	 */
	protected JsonResponse failed(BindingResult result, int code) {
		List<JsonError> errorList = new ArrayList<JsonError>();
		List<FieldError> filedErrorList = result.getFieldErrors();
		if (filedErrorList != null) {
			for (FieldError error : filedErrorList) {
				JsonError jsonError = new JsonError(error.getField(),
						error.getDefaultMessage());
				errorList.add(jsonError);
			}
		}
		JsonResponse jsonRep = new JsonResponse();
		jsonRep.setSuccessed(false);
		jsonRep.setCode(code);
		jsonRep.setErrorsList(errorList);
		return jsonRep;
	}
	
	/**
	 * 获取验证错误信息,针对 JavaBean验证.
	 * <P>
	 * 
	 * @param actionErrors
	 *            ActionErrors
	 * @param code
	 *            错误编码 code
	 * @return List errorList 错误列表
	 */
	protected JsonResponse failed(List<JsonError> errorList, int code) {
		JsonResponse jsonRep = new JsonResponse();
		jsonRep.setSuccessed(false);
		jsonRep.setCode(code);
		jsonRep.setErrorsList(errorList);
		return jsonRep;
	}

	/**
	 * 返回成功的数据,编码默认为0,successed为true.
	 * 
	 * @param data 结果数据
	 * @return JsonResponse
	 */
	protected JsonResponse successed(Object data) {
		JsonResponse jsonResp = new JsonResponse();
		jsonResp.setSuccessed(true);
		jsonResp.setData(data);
		jsonResp.setCode(0);
		return jsonResp;
	}
	
	/**
	 * 返回失败的数据,编码默认为0,successed为true.
	 * @param data 结果数据
	 *  @param code 编码
	 * @return JsonResponse
	 */
	protected JsonResponse failed(Object data,int code) {
		JsonResponse jsonResp = new JsonResponse();
		jsonResp.setSuccessed(false);
		jsonResp.setData(data);
		jsonResp.setCode(code);
		return jsonResp;
	}
	
	/**
	 * 返回失败的数据,编码默认为0,successed为true.
	 * @param data 结果数据
	 *  @param code 编码
	 * @return JsonResponse
	 */
	protected JsonResponse failedByApp(Object data,int code) {
		JsonResponse jsonResp = new JsonResponse();
		jsonResp.setSuccessed(false);
		jsonResp.setData(data);
		jsonResp.setCode(code);
		return jsonResp;
	}
	
	
	
	protected JsonResponse failed(Result result) {
		JsonResponse jsonResp = new JsonResponse();
		jsonResp.setSuccessed(false);
		jsonResp.setData(result.getMsg());
		jsonResp.setCode(result.getCode());
		return jsonResp;
	}

	/**
	 * 返回成功的数据,编码默认为0,successed为true.
	 * 
	 * @param data
	 *            结果数据
	 *  @param code 编码
	 * @return JsonResponse
	 */
	protected JsonResponse successed(Object data,int code) {
		JsonResponse jsonResp = new JsonResponse();
		jsonResp.setSuccessed(true);
		jsonResp.setData(data);
		jsonResp.setCode(code);
		return jsonResp;
	}
	
	/**
	 * 获取异常信息堆栈信息
	 * @param ex
	 * @return
	 * @throws Exception
	 */
	public String getExceptionStackTrace(Exception ex) throws Exception{
		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer,true)); 
		return writer.toString();
	}
	
	public String getLanguageType(HttpServletRequest request) throws Exception{
		String languageType=String.valueOf(request.getSession().getAttribute(Constants.LOCAL_LANGUAGE_ID));
		if(StringUtil.isBlankOrNullStr(languageType)){
			languageType="1";//默认为简体
		}
		return languageType;
	}
	
	/**
	 * 放数据到redis
	 * @param key 标识 内容
	 * @param time 超时时间
	 */
	protected void addRedisInfo(String redisKey,Object content,int time){
		jedisTemplate.delete(redisKey);//删除数据
		jedisTemplate.opsForValue().set(redisKey,content);//存放数据
		jedisTemplate.expire(redisKey,time, TimeUnit.MINUTES);//超时分钟数
	}
}
