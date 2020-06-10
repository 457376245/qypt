package com.web.common;

public class Constants {
    /**session语言版本key**/
    public static final String LOCAL_LANGUAGE_ID="LOCAL_LANGUAGE_ID";
    /**当前门户平台编码**/
    public static final String WEB_PLATFORM="SP00002";
    /**菜单平台编码**/
    public static final String MENU_PLATFORM="20001";
    /**公告系统编码**/
    public static final String BULLETIN_PLATFORM="1008";

    //编码1
    public static final String CODE_SUCC = "POR-0000"; // 成功

    public static final String CODE_TIMEOUT = "POR-1001";  // 服务层超时

    public static final String CODE_FAIL = "POR-2100"; // 失败或错误

    public static final String CODE_ANALYSIS = "POR-2103";// 解析异常

    public static final String CODE_OTHER = "POR-2999";// 其他原因

    public static final String CODE_NOTEX = "POR-2101"; // 不存在或已拆机

    public static final String CODE_NOREC = "POR-2102"; // 无查询结果
    
    public static final String CODE_PROD_FAIL = "POR-3001"; //受理失败
    
    public static final String CODE_PROD_TIMEOUT = "POR-3002"; //受理超时	

    //测好了，库存不足为2001，账户余额不足为3019，账户扣减失败为3020
    public static final String CODE_NO_STOCK = "STOCK-2204"; //库粗不足的时候

    public static final String CODE_NO_BALANCE = "BALANCE-2205"; //账户余额不足

    public static final String CODE_SUB_FAIL = "SUB-2205"; //扣减失败

    public static final String CODE_PREFIX_FAIL = "QY-FAIL-"; //权益接口失败前缀

    public static final String CODE_SUCCESS = "AW-0000";

    public static final String CODE_SERV_FAIL = "AW-4404";

    /**获取SESSION中的浏览器版本KEY*/
    public static final String BROWSER_CODE="BROWSER_VERSION";

    /**
     * 获取权益的关键字
     */
    public static final String REDIS_QY_SERIAL_NBR = "qy";

    /**
     * 获取权益的关键字
     */
    public static final String REDIS_JF_SERIAL_NBR = "jf";

    /**
     * redis 操作常量 开始
     */
    //用于存储redis可删除key的开头
    public static final String REDIS_DEL_KEY = "DEL_REDIS_KEY";

    //内部接口回参编码
    /**返回出参编码*/
   	public static final String RESULT_CODE_STR="resultCode";

   	/**返回出参消息*/
   	public static final String RESULT_MSG_STR="resultMsg";

   	/**内部接口成功参数*/
   	public static final String RESULT_SUCC="0";

   	/**内部接口失败参数(普通失败)*/
   	public static final String RESULT_FAIL="1";
   	
	/**内部接口失败参数(规则校验未通过)*/
   	public static final String RESULT_RULE_FAIL="9";
   	
    /**
     * redis锁的key
     */
   	public static final String REDIS_LOCK_KEY="ACT_LOCK_";

    /**登錄sessionId在redis中保存的時間*/
    public static final long LOGIN_TIME_OUT=1;

    /**
     * 文件保存redis前缀
     */
    public static final String REDIS_FILE_NBR = "file";


    /**
     * 文件保存redis前缀
     */
    public static final String REDIS_PRODUCT_NBR = "file";

    /**
     * 权益库存编码
     */
    public static final String REDIS_RIGHTS_CODE = "rightsStockCode";

    /**用户登录次数 redis key前缀*/
    public static final String LOGIN_USER_ERROCOUNT="LOGIN_USER_ERROCOUNT";

    /** 微信获取token url**/
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /** 微信获取ticket url**/
    public static final String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    /**状态已发布在用**/
    public static final String STATUS_EFFECT="101";
    /**状态不可用**/
    public static final String STATUS_INVALID="102";
    /**状态待发布**/
    public static final String STATUS_NOTPUBLISH="103";

    public static final String STATUS_DROP="104";


    /**
     * 图片url保存redis前缀
     */
    public static final String REDIS_IMAGE_URL = "imagesUrl";

    /**
     * 图片url保存redis前缀
     */
    public static final String REDIS_IMAGE_BYTE = "imagesByte";

    /**
     * 短信系统接口请求流水序列 Key
     */
    public static final String SMS_REQUEST_ID = "sms_request_id";


    /**
     * 充值来源
     */
    public static final String RechargeSource = "18";

    /**
     * 计费系统接口请求流水序列 Key
     */
    public static final String BILL_REQUEST_ID = "bill_request_id";

    /**
     * 2位各省自用
     */
    public static final String SELFUSE = "40";

    /**
     * 2位省VC平台编码
     */
    public static final String VCSYSTEMID = "31";

}
