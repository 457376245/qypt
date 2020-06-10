package com.web.util;

import com.web.common.Constants;

import javax.servlet.http.HttpSession;

/**
 * <b>类描述：</b>亚信官网Session公共操作方法<br>
 * <b>创建人：</b>wmk<br>
 * <b>创建时间：</b>2013-10-12-上午9:54:42<br>
 */
public class SessionUtil {
    /**
     * 判断是否为移动端访问
     */
    public static Boolean isMobileCall(HttpSession session) {
        boolean f = false;
        Object BROWSER_CODE = session.getAttribute(Constants.BROWSER_CODE);
        if (StringTool.isNotEmpty(BROWSER_CODE)) {
            String ss = BROWSER_CODE.toString();
            if (ss.equals("MOBILE")) {
                f = true;
            }
        }
        return f;
    }
    
    /**
     * <b>移动端访问，页面目录前缀<br>
     * @param session
     * @return
     * @since  1.0.0
     */
    public static String mobilePagePre(HttpSession session){
    	if(isMobileCall(session)){
    		return "mobile/";
    	}
    	return "";
    }
}
