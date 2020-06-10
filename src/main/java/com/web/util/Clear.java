package com.web.util;

import javax.servlet.http.HttpSession;

public class Clear {	
	public static void clearSession(HttpSession session) {
		session.removeAttribute("userBigDataMap");
		session.removeAttribute("custTags");
		session.removeAttribute("custInfoMap");//移除客户信息
		session.removeAttribute("prodInstMap");//移除产品信息
		session.removeAttribute("LanId");//移除3.0的地市转换成2.9的地市数据
		session.removeAttribute("preHandleSessionMap");//移除排队号信息
		session.removeAttribute("positionTypeMap");//移除定位类型信息
		session.removeAttribute("rowNum");//移除排号信息
	}
	public static void clearprepareHandle(HttpSession session) {
		session.removeAttribute("prepareHandleMap");//移除预约受理信息
	}
}
