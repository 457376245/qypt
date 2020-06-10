package com.web.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 浏览器工具 类 概述 .
 * <P>
 * @author tang zheng yu
 * @version V1.0 2013-4-26
 * @createDate  2013-4-26 上午9:59:20
 * @copyRight 亚信联创电信EC研发部
 */
public class BrowserUtil {
    /** IE语言(地区) */
    public static final String language = "language";
    /** 用户代理信息 */
    public static final String userAgent = "userAgent";
    /** cookie是否能用 */
    public static final String cookieEnabled = "cookieEnabled";
    /** 操作系统 */
    public static final String platform = "platform";
    /** web 资源类型 */
    public static final Set<String> resourceTypes = new HashSet<String>() {
        private static final long serialVersionUID = -5518411029476577473L;
        {
            this.add("js");
            this.add("css");
            this.add("jpg");
            this.add("jpeg");
            this.add("png");
            this.add("gif");
            this.add("bmp");
            this.add("swf");
            this.add("flv");
            this.add("xls");
            this.add("xlsx");
            this.add("doc");
            this.add("docx");
            this.add("txt");
            this.add("rar");
            this.add("zip");
            this.add("gz");
            this.add("pdf");
            this.add("eot");
            this.add("map");
            this.add("ico");
        }
    };

    /**
     * 获取浏览器名称和版本号
     * @param userAgent 代理信息
     * @return List<String>  0:是名称,1:版本号
     */
    public static List<String> getBrowserName(String userAgent) {
        String browserName = userAgent.toLowerCase();
        String msieRegx = ".*msie.*";
        String operaRegx = ".*opera.*";
        String firefoxRegx = ".*firefox.*";
        String chromeRegx = ".*chrome.*";
        String webkitRegx = ".*webkit.*";
        String mozillaRegx = ".*mozilla.*";
        String safariRegx = ".*safari.*";
        String version = "";
        List<String> browserList = new ArrayList<String>();
        if (Pattern.matches(msieRegx, browserName) && !Pattern.matches(operaRegx, browserName)) {
            browserList.add("IE");
        } else if (Pattern.matches(firefoxRegx, browserName)) {
            browserList.add("Firefox");
        } else if (Pattern.matches(chromeRegx, browserName) && Pattern.matches(webkitRegx, browserName)
                && Pattern.matches(mozillaRegx, browserName)) {
            browserList.add("Chrome");
        } else if (Pattern.matches(operaRegx, browserName)) {
            browserList.add("Opera");
        } else if (Pattern.matches(safariRegx, browserName) && !Pattern.matches(chromeRegx, browserName)
                && Pattern.matches(webkitRegx, browserName) && Pattern.matches(mozillaRegx, browserName)) {
            browserList.add("Safari");
        } else {
            browserList.add("unknow");
        }
        if (!browserList.get(0).equals("unknow")) {
            if (browserList.get(0).equals("IE")) {
                version = browserName.substring(browserName.indexOf("msie"));
                version = version.split(";")[0].split(" ")[1];
                if (version != null && version.indexOf(".") > 0) {
                    version = version.substring(0, version.indexOf("."));
                }
            } else {
                version = browserName.substring(browserName.indexOf(browserList.get(0).toLowerCase()));
                version = version.split(" ")[0].split("/")[1];
                if (version != null && version.indexOf(".") > 0) {
                    version = version.substring(0, version.indexOf("."));
                }
            }
        }
        browserList.add(version);
        return browserList;
    }

    public static String fromCharCode(int... codePoints) {
        StringBuilder builder = new StringBuilder(codePoints.length);
        for (int codePoint : codePoints) {
            builder.append(Character.toChars(codePoint));
        }
        return builder.toString();
    }

    /**
     * 获取浏览器名称和版本号
     * @param userAgent 代理信息
     * @return boolean  true:是ipad浏览器
     */
    public static boolean isIpadOrIphone(String userAgent) {
        String browserName = userAgent.toLowerCase();
        String ipadRegx = ".*ipad.*";
        String iphoneRegx = ".*iphone.*";
        if (Pattern.matches(ipadRegx, browserName) || Pattern.matches(iphoneRegx, browserName)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 判断终端类型为PC或PAD
     *   可扩展到安卓或其它类型
     * @param userAgent 代理信息 
     * @return PC,PAD
     */
    public static String getTerminal(String userAgent) {
        String browserName = userAgent.toLowerCase();
        String ipadRegx = ".*ipad.*";
        String iphoneRegx = ".*iphone.*";
        if (Pattern.matches(ipadRegx, browserName) || Pattern.matches(iphoneRegx, browserName)) {
            return "PAD";
        } else {
            return "PC";
        }
    }
    
    /**
     * 请求是否来自手机端浏览器
     * @param userAgent
     * @return
     */
    public static boolean isFromMobile(String userAgent){
    	userAgent.toLowerCase();
    	String[] mobileAgents = { "iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",  
                "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",  
                "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",  
                "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",  
                "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",  
                "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",  
                "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",  
                "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",  
                "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",  
                "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",  
                "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",  
                "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",  
                "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v",  
                "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",  
                "Googlebot-Mobile" };  
        if (userAgent != null) {  
            for (String mobileAgent : mobileAgents) {  
                if (userAgent.toLowerCase().indexOf(mobileAgent) >= 0) {  
                	return true;
                }  
            }  
        }  
        return false;
    }
}
