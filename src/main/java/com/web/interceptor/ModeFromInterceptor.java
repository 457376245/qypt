package com.web.interceptor;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import com.web.common.Constants;
import com.web.util.common.BrowserCheckUtil;

/**
 * <b>项目名称：</b>ocPortal<br>
 * <b>类名称：</b>com.web.interceptor.ModeFromInterceptor.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>判断浏览器类型<br>
 * <b>创建时间：</b>2016年7月21日-上午9:43:29<br>
 */

@Controller("com.web.interceptor.ModeFromInterceptor")
public class ModeFromInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	@Qualifier("localeResolver")
	private SessionLocaleResolver localeResolver;
	
	/**
	 * 判断是手机浏览器还是PC浏览器
	 */
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		//判断国际化字体
		Locale nowLocal= localeResolver.resolveLocale(request);
		String local=String.valueOf(nowLocal);
		HttpSession session=request.getSession();
		
		if("zh".equals(local)||"zh_CN".equals(local))  {
			localeResolver.setLocale(request, response, Locale.SIMPLIFIED_CHINESE);  
			session.setAttribute(Constants.LOCAL_LANGUAGE_ID, "2");
		} else if("en".equals(local)||"en_US".equals(local))  {
			localeResolver.setLocale(request, response, Locale.ENGLISH); 
			session.setAttribute(Constants.LOCAL_LANGUAGE_ID, "3");
		}else if("zh_HK".equals(local)||"zh_TW".equals(local)||"zh_MO".equals(local)){
			localeResolver.setLocale(request, response, Locale.TRADITIONAL_CHINESE); 
			session.setAttribute(Constants.LOCAL_LANGUAGE_ID, "1");
		}else{
			 //默认繁体版本
			localeResolver.setLocale(request, response, Locale.TRADITIONAL_CHINESE); 
			session.setAttribute("LOCAL_LANGUAGE_ID", "1");
		}
		
		//判断是手机浏览器还是PC浏览器，true为手机浏览器
		if(BrowserCheckUtil.isMobileDevice(request)){
			request.getSession().setAttribute(Constants.BROWSER_CODE, "MOBILE");
		}else{
			request.getSession().setAttribute(Constants.BROWSER_CODE, "PC");
		}
		
		return true;
	}

	/**
	 * 在业务处理器处理请求执行完成后,生成视图之前执行的动作 可在modelAndView中加入数据，比如当前时间
	 * 
	 */
	public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	/**
	 * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
	 * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
	 */
	public void afterCompletion(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

	public void afterConcurrentHandlingStarted(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		super.afterConcurrentHandlingStarted(request, response, handler);
	}
}
