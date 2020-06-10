/**.   
 * @Title: ServiceCode.java
 * @Package com.linkage.portal.service.common
 * @Description: TODO(用一句话描述该文件做什么)
 * @author guiwh
 * @date 2011-12-21 下午01:25:10
 * @version V1.0
 */
package com.web.common;


public class ServiceParam {

	/**
	 * 调用服务地址
	 */
	public static final String SERVICE_CALL_URL = "http://135.33.5.196:9001/csbEngine/wss/service?wsdl";

	public static final String url = "http://127.0.0.1:8082/ocPortal/service/staffCheck";

	/**系统ID*/
	public static final String SERVICE_SRC_SYS_ID = "EC";

	/**系统密码*/
	public static final String SERVICE_SRC_SYS_SIGN = "123";

	/**登录密码加密*/
	public static final String USER_PWD_KEY="oiuh876dcy08";

	/**短信发送时间间隔*/
	public static final int SEND_SMS_TIME=60000;

	public static final int SMS_OVERDUE_TIME=300000;

	/**权益接口系统标识*/
	public static final String SERVICE_SYSTEM_ID = "12";


	/**计费接口系统标识*/
	public static final String SERVICE_JF_SYSTEM_ID = "100021";

	//接口返回编码开始
	/**调用成功*/
	public static String SYS_SUCCESS="SYS-0000";

	/**调用失败*/
	public static String SYS_CALL_FAIL="SYS-1001";

	/**调用接口异常*/
	public static String SYS_SERV_EXCEPTION = "SYS-1012";

	/**数据转换异常或数据格式错误*/
	public static String SYS_DATACONVERSION = "POR-1015";
	//接口返回编码结束
}
