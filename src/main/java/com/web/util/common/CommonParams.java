package com.web.util.common;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>项目名称：</b>oc-portal<br>
 * <b>类名称：</b>com.web.util.common.CommonParams.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>公共参数<br>
 * <b>创建时间：</b>2018年3月13日-上午11:19:54<br>
 */

public class CommonParams {
	
	/**返回出参编码*/
	public static final String RESULT_CODE_STR="resultCode";
	
	/**返回出参消息*/
	public static final String RESULT_MSG_STR="resultMsg";
	
	/**内部接口成功参数*/
	public static final String RESULT_SUCC="0";
	
	/**内部接口成功参数*/
	public static final String RESULT_FAIL="1";
	
	/**统一目录人证KEY*/
	public static final String CRM_APP_RSA_KEY = new String("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKKJ63nXhKAj9n2pk4mxYJxMLBSnnpClUnLB818UFRJIBt651SZPRe+pc9CN3F4YEdNL4oaMPppDW13RaAx/0bueha+ESVwO+izM7AsQDeqd4gJWbghgVWgKOKQojS+DTqEf008PteZ3m+95M95eMVt35x26s12ioNxxfUrWuPUzAgMBAAECgYEAlGwHCBLie+17F5LzM+gyhWcVVaMeq40btckf+LF48GGvTFE7r6jx/wCozkfMdGvcUytXZm/IdsuNMiWXstpBZHCebWMy/0CRbRuoIFZYqgwmmFvMfL1ES8HZ6Ve2FyoEPFj624tNAJNouFjG5SXO2EQIDc7FV+UJTyk4Gu+5uIkCQQDi2dl+ZoqSmROijXwv/Rj0JgLg/+hi4hQ3mujaA4dTE+MhZr08GbuaneEHeYsVesh088iYRugoI6JNfhn6374NAkEAt2yNK7iEr4IIqWZDJiomY2bTi9d//ML6ekl2zha+xR9MjnDuQBLQkluZKyLKsqI/CUNX+tqomqgfizVeEzHwPwJAaTzM9CGX2YhgHoHqgcy1fdYAcebmJLFi1aTsru8CIV1RehZSZYY+jOz/Dtuo1S8fvXm2rkX+v1hAnBiK7uJLJQJASU8ASRYRKdgtwA6YDdvQxe+l4wE5LPt8gn10F0At9LjirxdhkYgzhhtH516bZrPDv98DfcYAlvdUzB2DQOa3nwJBAJMBhx4dCC5zdHz8rgEw7pirorzMsDC6bB6VQ262wsPFlpLpFQtOY0SiwB7o5/q2cF56aV38ED5bP8Kj6/qpKWY=");
	
	//public static final String CRM_APP_RSA_KEY="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIOKf+SviRiKGls1LKOWARj29jO36yMUncuJQ2Loq8EXHeBI5TxUuJJxGnUMKjQuiGpPjIb1yM7n6RZStt3+8BKg8xzALIKMZmzHc9pIPOt5bqXZMWJsq/4+fdQLkQKIq85kZyuHU/crbb2MBwL6MSnsLOklRv0oA9sPwGOF6yiZAgMBAAECgYBTSzE/AvRCCLk1xg8VKxhY0ZEHINARL63Xw3xbNlAD/fpaGm6l7FPPzzTcpy5WSpzxDJxEnYBhnDJZg1jua89iX0efu8s09cqslVBWGpv0qjYgxlH9uyKdY9a/LPz2KsDxGb/dmL/qRJ3czMiLsuM24xz3Xt0Br90xrKzx7q1cfQJBAOCMx7CUqj1ehGQoCoEYQCglu1EcSXQKFCQgEzY5g1Nwsh5AE5X1EOFoP6P9iJcwKLd5GvAmx6pJeLdcrXYNhH8CQQCV9uRz37gs4e4f/RUgzoUYLmo6yEbg+qTBpsrCuTKhQjfDo52BpNwua9xiVPHJrai8m85IWHWEpOBWj0i0EmbnAkAOLLTaT7gjYZkH3YM0PPynzCHz784vNgepdCDiVmUU2lNUF4Vk/PBQ2kzSJEoACTk4w7tQl5IZDMRDBuWoQxgvAkAlD3K9wBf/34W9mgnSpbb2luHhzQTXLJny0YliPeppN7gYPSL7mwZe4oEAPAXU1XnRwp9j+ZhxQ129hoNtHO8ZAkACVXbCb8AKFxRYgubpF2dKsRshXv9+2xUtaG6Ctw8UGUoxQNNIxm5Ss5sHUdu4mZZqeTww58xGUVJvGeauPMNt";
	
	/**统一目录认证客户端编码*/
	public static final Map<String, String> SC_CRM_APP_CLIENT_ID = new HashMap<String, String>();

	static {
		SC_CRM_APP_CLIENT_ID.put("/fl/itvBusi/addItv", "CLI_CRM_ITVJZ20170412");
		SC_CRM_APP_CLIENT_ID.put("/fl/itvBusi/addItvAttach", "CLI_CRM_ITVZZ20170412");
		SC_CRM_APP_CLIENT_ID.put("/fl/schoolBusi/schoolNet", "CLI_XYZX20180522");
		SC_CRM_APP_CLIENT_ID.put("/fl/orderQuery/myOrder", "CLI_CRM_WDDD20170412");
		SC_CRM_APP_CLIENT_ID.put("/fl/busiOrder/installOrderAdd", "CLI_CRM_WSXH20170412");
		SC_CRM_APP_CLIENT_ID.put("/fl/ThreeToFourOrder/threeToFourOrder", "CLI_CRM_SSS20170412");
		SC_CRM_APP_CLIENT_ID.put("/fl/custposstion/newcust", "CLI_TDNS20180731");
	}

//	SC_CRM_APP_CLIENT_ID.put("/assist/addItv", "CLI_CRM_ITVJZ20170412");
//	SC_CRM_APP_CLIENT_ID.put("/assist/addItvAttach", "CLI_CRM_ITVZZ20170412");
//	SC_CRM_APP_CLIENT_ID.put("/assist/installOrderAdd", "CLI_CRM_WSXH20170412");
	
	/**统一目录认证回调地址*/
	public static final Map<String, String> SC_CRM_APP_REDIRECT_URI = new HashMap<String, String>();

	static {
		SC_CRM_APP_REDIRECT_URI.put("/fl/itvBusi/addItv", "https://mo.sctel.com.cn/oc-portal/fl/itvBusi/addItv");
		SC_CRM_APP_REDIRECT_URI.put("/fl/itvBusi/addItvAttach", "https://mo.sctel.com.cn/oc-portal/fl/itvBusi/addItvAttach");
		SC_CRM_APP_REDIRECT_URI.put("/fl/schoolBusi/schoolNet", "https://mo.sctel.com.cn/oc-portal/fl/schoolBusi/schoolNet");
		SC_CRM_APP_REDIRECT_URI.put("/fl/orderQuery/myOrder", "https://mo.sctel.com.cn/oc-portal/fl/orderQuery/myOrder");
		SC_CRM_APP_REDIRECT_URI.put("/fl/busiOrder/installOrderAdd", "https://mo.sctel.com.cn/oc-portal/fl/busiOrder/installOrderAdd");
		SC_CRM_APP_REDIRECT_URI.put("/fl/ThreeToFourOrder/threeToFourOrder", "https://mo.sctel.com.cn/oc-portal/fl/ThreeToFourOrder/threeToFourOrder");
		SC_CRM_APP_REDIRECT_URI.put("/fl/custposstion/newcust", "https://mo.sctel.com.cn/oc-portal/fl/custposstion/newcust");
	}
	
	public static final Map<String, String> CUST_QUERY_TYPE = new HashMap<String, String>();
	static {
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_IDENTITY, "^\\d{17}[\\d|x|X]|\\d{15}$");//证件号-身份证
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_CUST_NAME, "^[\u4E00-\u9FA5]{2,30}(?:·[\u4E00-\u9FA5]{2,30})*$");//客户名称
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_SIMPLE_SPELL, "^[a-zA-z]{2,50}$(?!^\\d+)$");//参与人简拼
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_CONBR, "^\\d{16}$");//订单流水号
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_ACC_NBR, "^(028|08\\d{2})\\d{10}|[A-Z]{1,}-{0,1}\\d{2,}|(\\d{7}|\\d{11})-\\d{1,2}|\\d{9}$");//接入号(宽带/ITV接入号正则)
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_ACCT_CD, "^\\d{7,9}|\\d{12}|\\d{14}$");//账户合同号
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_TERMINAL_CODE, "^\\d{19}|\\d{20}$");//手机SIM卡号
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_UIM_CODE, "^898603\\d{13}$");//手机UIM卡号 
//		CUST_QUERY_TYPE.put(CustCommMDA.CUST_QUERY_TYPE_IS_PROD_2_ACCESSS_NUMBER, "");//非主接入号
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_PHONE, "^[1][3,4,5,7,8,9][0-9]{9}$");//手机号
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_PREPAREORDERNUM, "^[a-zA-Z0-9]{4,5}$");//排队号
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_GUHUA1, "^[0][1-9]{2,3}-{0,1}[1-9]{1}[0-9]{5,7}$");//固定电话(8位以上)
		CUST_QUERY_TYPE.put(CommonParams.CUST_QUERY_TYPE_IS_GUHUA2, "^[1-9]{1}[0-9]{5,7}$");//固定电话（7位）
		}
	public static final String CUST_QUERY_TYPE_IS_IDENTITY="CUST_QUERY_TYPE_IS_IDENTITY";
	public static final String CUST_QUERY_TYPE_IS_CUST_NAME="CUST_QUERY_TYPE_IS_CUST_NAME";
	public static final String CUST_QUERY_TYPE_IS_SIMPLE_SPELL="CUST_QUERY_TYPE_IS_SIMPLE_SPELL";
	public static final String CUST_QUERY_TYPE_IS_CONBR="CUST_QUERY_TYPE_IS_CONBR";
	public static final String CUST_QUERY_TYPE_IS_ACC_NBR="CUST_QUERY_TYPE_IS_ACC_NBR";
	public static final String CUST_QUERY_TYPE_IS_ACCT_CD="CUST_QUERY_TYPE_IS_ACCT_CD";
	public static final String CUST_QUERY_TYPE_IS_TERMINAL_CODE="CUST_QUERY_TYPE_IS_TERMINAL_CODE";
	public static final String CUST_QUERY_TYPE_IS_UIM_CODE="CUST_QUERY_TYPE_IS_UIM_CODE";
	public static final String CUST_QUERY_TYPE_IS_PHONE="CUST_QUERY_TYPE_IS_PHONE";
	public static final String CUST_QUERY_TYPE_IS_PREPAREORDERNUM="CUST_QUERY_TYPE_IS_PREPAREORDERNUM";
	public static final String CUST_QUERY_TYPE_IS_GUHUA1="CUST_QUERY_TYPE_IS_GUHUA1";
	public static final String CUST_QUERY_TYPE_IS_GUHUA2="CUST_QUERY_TYPE_IS_GUHUA2";
	
	/**电小二菜单编码匹配路径*/
	public static final Map<String, String> SC_DXE_APP_REDIRECT_URI = new HashMap<String, String>();

	static {
		//校园直销
		SC_DXE_APP_REDIRECT_URI.put("1001", "/fl/schoolBusi/schoolNet");
		//我的订单
		SC_DXE_APP_REDIRECT_URI.put("1002", "/fl/orderQuery/myOrder");
		//ITV加装
		SC_DXE_APP_REDIRECT_URI.put("1003", "/fl/itvBusi/addItv");
		//厅店小能手
		SC_DXE_APP_REDIRECT_URI.put("1004", "/fl/custposstion/newcust");
		//网上选号
		SC_DXE_APP_REDIRECT_URI.put("1005", "/fl/busiOrder/installOrderAdd");
	}
	
//	SC_CRM_APP_REDIRECT_URI.put("/assist/addItv", "https://vpn.un-net.com/BizHall/assist/addItv");
//	SC_CRM_APP_REDIRECT_URI.put("/assist/addItvAttach", "https://vpn.un-net.com/BizHall/assist/addItvAttach");
//	SC_CRM_APP_REDIRECT_URI.put("/assist/installOrderAdd", "https://vpn.un-net.com/BizHall/assist/installOrderAdd");
	
	/**CRM3.0各资源中心调用地址配置编码*/
	/**系管中心*/
	public static final String HTTP_URL_SYS="HTTP_URL_SYS";
	
	/**客户中心*/
	public static final String HTTP_URL_CUST="HTTP_URL_CUST";
	
	/**订单中心*/
	public static final String HTTP_URL_ORDER="HTTP_URL_ORDER";
	
	/**资源中心*/
	public static final String HTTP_URL_RES="HTTP_URL_RES";
	
	/**PPM*/
	public static final String HTTP_URL_PPM="HTTP_URL_PPM";

	/**资产*/
	public static final String HTTP_URL_ZC="HTTP_URL_ZC";
	
	/**算费*/
	public static final String HTTP_URL_WZH="HTTP_URL_WZH";
	
	/***/
	public static final String HTTP_URL_CHARGE="HTTP_URL_CHARGE";
	
	/**EOP*/
	public static final String EOP_URL="EOP_URL";
	
	/**支付中心**/
	public static final String HTTP_URL_PAY = "HTTP_URL_PAY";
	
	/**计费充值**/
	public static final String HTTP_URL_BILLPAY = "HTTP_URL_BILLPAY";
	
	/**CRM鉴权**/
	public static final String HTTP_URL_VERIFY = "HTTP_URL_VERIFY";
	
	/**CRM客户中心地址**/
	public static final String HTTP_URL_CRMCUST_EOP = "HTTP_URL_CRMCUST_EOP";
	
	/**人脸比对*/
	public static final String HTTP_URL_FACE = "HTTP_URL_FACE";
	
	/**会话中系统TOKEN存储key*/
	public static final String LOGIN_TOKEN="LOGIN_TOKEN";
	
	/**会话中系统编码存储key*/
	public static final String LOGIN_SYS="LOGIN_SYS";
	
	/**第三方系统编码*/
	public static final String LOGIN_FLAG="LOGIN_FLAG";
	
	/**电小二系统编码*/
	public static final String DXE_SYSCODE="DXE";
	
	/**手机门户系统编码*/
	public static final String SJMH_SYSCODE="SJMH";
	
	/**装维APP系统编码*/
	public static final String YDZW_SYSCODE="ydzw";
	
	/**EOP*/
	public static final String HTTP_URL_EOP="HTTP_URL_EOP";
	
	/**排号*/
	public static final String HTTP_URL_PH = "HTTP_URL_PH";
	
	/**电小二手机账号*/
	public static final String DXE_PHONE="DXE_PHONE";
	
	/**电小二param值*/
	public static final String DXE_PARAM="DXE_PARAM";
	
	/**排号受理*/
	public static final String HTTP_URL_SL="HTTP_URL_SL";
}
