package com.web.interceptor;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.alibaba.fastjson.JSONObject;
import com.web.bean.StaffInfo;
import com.web.bmo.CommonBmo;
import com.web.common.PropertyToRedis;
import com.web.common.Variable;
import com.web.model.CoMember;
import com.web.util.MessageSourceHelper;
import com.web.util.RSAUtils;
import com.web.util.StringUtil;
import com.web.util.Toolkit;
import com.web.util.common.CommonParams;
import com.web.util.common.JsonResponse;



/**
 * <b>项目名称：</b>ocPortal<br>
 * <b>类名称：</b>com.ai.ec.mall.interceptor.SessionInterceptor<br>
 * <b>类描述：</b>会话拦截器<br>
 * <b>创建人：</b>WJZ<br>
 * <b>创建时间：</b>2017年4月17日-下午3:42:43<br>
 * <b>@Copyright:</b>2017-亚信
 */

@Controller("com.web.interceptor.SessionInterceptor")
public class SessionInterceptor extends HandlerInterceptorAdapter {

    private static Logger log = LoggerFactory.getLogger(SessionInterceptor.class);

    private RSAPublicKey publicKey = RSAUtils.getDefaultPublicKey();

    // 登录界面地址
    private String loginUrl;

    //无权限提示地址
    private String powerUrl;

    @Autowired
    @Qualifier("localeResolver")
    private SessionLocaleResolver localeResolver;

    @Autowired
    @Qualifier(value = "MessageSourceHelper")
    private MessageSourceHelper message;
    
    //不拦截地址
  	private List<String> exceptUrls;

    @Resource(name="com.web.bmo.CommonBmoImpl")
    private CommonBmo commonBmo;

//	@Resource(name="com.web.bmo.LoginBmoImpl")
//	private LoginBmo loginBmo;
//
//	@Resource(name="com.web.bmo.BusiOrderBmoImpl")
//	private BusiOrderBmo busiOrderBmo;

    @Autowired
    @Qualifier("com.web.common.PropertyToRedis")
    protected PropertyToRedis propertyToRedis;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> jedisTemplate;

    /**
     * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
     * 如果返回true 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
     * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
     *
     */

    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
        boolean isLogin=false;
        boolean isAjax=false;

        String code=request.getParameter("code");
        String source=request.getParameter("source");
        //log.info("单点登录code："+code+"||source:"+source);

        String requestUri=request.getRequestURI();//访问的URI
        
        log.info("请求URI："+requestUri);
        if(requestUri.startsWith(request.getContextPath())){  
            requestUri = requestUri.substring(request.getContextPath().length(), requestUri.length());  
        }  
        
        //系统根目录  
        if ("/".equals(requestUri)) {
        	request.getRequestDispatcher(loginUrl).forward(request, response);
            return true;  
        }  
        
        //放行exceptUrls中配置的url  
        for (String url:exceptUrls) {  
            if(url.endsWith("/**")){  
                if (requestUri.startsWith(url.substring(0, url.length() - 3))) {  
                    return true;  
                }  
            } else if (requestUri.startsWith(url)) {  
                return true;  
            }  
        } 
        
        //获取session中的登录数据(暂时写死默认数据)
       StaffInfo staffInfo=new StaffInfo();
       staffInfo.setStaffId(1L);
       staffInfo.setStaffName("管理员1");
       staffInfo.setStaffCode("GLY01");
       
       request.getSession().setAttribute("staffInfo", staffInfo);

        //判断是否JS请求
        if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
            isAjax=true;
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.setDateHeader("Expires", 0);
        }
        
        //还未有单点登录操作，默认返回有session
        isLogin=true;

        //判断是否登录，如果已登录，返回，未登录，返回未登录数据
        if(isLogin){
            return true;
        }else{
            //判断请求类型,true就是js请求
            if (isAjax) {
                //伪装返回状态码，使前台AJAX请求不会进入success
                response.setStatus(601);

                response.setHeader("sessionstatus", "timeout");

                //封装公共参数
                JsonResponse jsonResp = new JsonResponse();
                jsonResp.setSuccessed(false);
                jsonResp.setData("未登录或登录已失效，请重新登录");
                jsonResp.setCode(1101);//登录已失效

                String jsonInfo= JSONObject.toJSONString(jsonResp);

                response.getWriter().print(jsonInfo);
                response.getWriter().flush();
                response.getWriter().close();
                return false;
            }else{
                //判断是否有返回CODE，没有返回直接false，有就进行查询
                if(!StringUtil.isEmptyStr(code)){
                    String localSysCode=(String) propertyToRedis.getPropertyValue("LOCAL_SYS_CODE");

                    Map<String, Object> infoMap=new HashMap<String, Object>();

                    Map<String, Object> queryResult=null;
                    String loginPhone="";
                    if(!"LOCAL".equals(localSysCode)){
                        String page = getRulePagePath(request);

                        log.info("门户请求地址："+page);

                        Map<String, Object> paramsMap=new HashMap<String, Object>();
                        paramsMap.put("clientId", CommonParams.SC_CRM_APP_CLIENT_ID.get(page));
                        paramsMap.put("redirectUri", CommonParams.SC_CRM_APP_REDIRECT_URI.get(page));
                        paramsMap.put("grantType", "authorization_code");
                        paramsMap.put("tokenCode",code);

                        Map<String, Object> tokenMap=this.commonBmo.queryOauthToken(paramsMap);

                        if(tokenMap==null || !CommonParams.RESULT_SUCC.equals(MapUtils.getString(tokenMap,CommonParams.RESULT_CODE_STR))){
                            request.setAttribute("message","校验登录流水失败");
                            log.info("校验登录流水失败,单点登录code："+code+"||source:"+source);
                            request.getRequestDispatcher(loginUrl).forward(request, response);
                            return false;
                        }
                        if("S".equals(Variable.authFlag)){
                            //查询员工信息(使用手机号码)
                            loginPhone=String.valueOf(tokenMap.get("mobilePhone"));
                            infoMap.put("bindNumber", tokenMap.get("mobilePhone"));
                            //infoMap.put("bindNumber", "18081990923");
//					    	queryResult=this.loginBmo.getStaffInfoByPhone(infoMap);
                        }
                    }else{
                        //查询员工信息(使用工号)
                        infoMap.put("staffCode", "liaomi");

//				    	queryResult=this.loginBmo.getStaffBaseInfo(infoMap);
                    }
                    //将本次CODE保存到session中
                    request.getSession().setAttribute(CommonParams.LOGIN_TOKEN,code);
                    if(source!=null&&source.trim().equals(CommonParams.YDZW_SYSCODE)) {
                        //登录来源
                        request.getSession().setAttribute(CommonParams.LOGIN_SYS,CommonParams.YDZW_SYSCODE);
                    }else {
                        //登录来源
                        request.getSession().setAttribute(CommonParams.LOGIN_SYS,CommonParams.SJMH_SYSCODE);//手机门户
                    }
                    //保存登陆日志
                    Map<String, Object> paramsMap = new HashMap<String, Object>();
                    paramsMap.put("systemUserId", staffInfo.getStaffId());
                    paramsMap.put("systemName", "移动受理");
                    paramsMap.put("isSuccess", "0");
                    paramsMap.put("failType", "");
                    paramsMap.put("wanIp", Toolkit.getIpAddress(request));
                    paramsMap.put("browerProd", "");
//			    	loginBmo.saveLoginLog(paramsMap);
                    return true;
                }else{
                    //log.info("为空，4单点登录code："+code+"||source:"+source);
                    request.getRequestDispatcher(loginUrl).forward(request, response);

                    return false;
                }
            }

            //return true;
        }
    }

    public String getRulePagePath(HttpServletRequest request){
        String url = request.getRequestURL().toString();
        String contextPath = request.getContextPath();
        int index = url.lastIndexOf(contextPath);
        String path = url.substring(0, index + contextPath.length());
        String page = url.substring(index + contextPath.length());
        return page;
    }

    @Override
    public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getPowerUrl() {
        return powerUrl;
    }

    public void setPowerUrl(String powerUrl) {
        this.powerUrl = powerUrl;
    }

	public List<String> getExceptUrls() {
		return exceptUrls;
	}

	public void setExceptUrls(List<String> exceptUrls) {
		this.exceptUrls = exceptUrls;
	}

    
}
