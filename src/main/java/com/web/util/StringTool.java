package com.web.util;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * <b>类描述：</b>一些通用统计集合<br>
 * <b>创建人：</b>wmk<br>
 * <b>创建时间：</b>2013-10-12-上午9:54:42<br>
 *
 */
public class StringTool {

    /**
     * 判断字符串是否纯在
     *
     * @param base    基本
     * @param findStr 需要查找的
     * @return boolean
     */
    public static boolean isIN(String base, String findStr) {
        boolean f = false;
        if (StringTool.isNotEmpty(base) && StringTool.isNotEmpty(findStr)) {
            int ff = base.indexOf(findStr);
            if (ff >= 0) {
                f = true;
            }
        }
        return f;
    }

    /**
     * 获取文件长度
     *
     * @param AbsolutePath 文件地址
     * @return 长度
     */
    public static long getFileSize(String AbsolutePath) {
        long l = 0;
        File f = new File(AbsolutePath);
        if (f.exists() && f.isFile()) {
            return f.length();
        } else {
            return l;
        }
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
////                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println("result===========" + result);
        } catch (Exception e) {
//            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        } finally {
            //使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * json 形式请求
     *
     * @param url   url
     * @param param 参数
     * @return 返回值
     */
    public static String sendPostJSon(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //out = new PrintWriter(conn.getOutputStream());
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
            out.print(param);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
//        if ("".equals(result)) {
//        }
        return result;
    }


    /**
     * @param ParameterName 参数名称
     * @param request       req
     * @return Map
     * @author wang mao kang
     */

    public static String getRequestPara(HttpServletRequest request, String ParameterName) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> e = request.getParameterNames();
        request.getQueryString();
        String para = null;
        while (e.hasMoreElements()) {
            para = e.nextElement();
            if (para != null) {
                map.put(para.toLowerCase(), request.getParameter(para));
            }
        }
        return map.get(ParameterName.toLowerCase());
    }


    /**
     * 获取所有请求参数
     *
     * @param request req
     * @return Map
     * @author wang mao kang
     */

    public static Map<String, String> getRequestParas(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> e = request.getParameterNames();
        request.getQueryString();
        String para = null;
        while (e.hasMoreElements()) {
            para = e.nextElement();
            if (para != null) {
                map.put(para, request.getParameter(para));
            }
        }

        return map;

    }

    /**
     * 判断对象是否为空或者空串
     *
     * @param obj obj
     * @return boolean
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return (obj.toString().trim().length() == 0);
        }
        if (obj instanceof Long) {
            return ((Long) obj == 0);
        }
        if (obj instanceof Integer) {
            return ((Integer) obj == 0);
        }
        if (obj instanceof List) {
            return (((List) obj).size() == 0);
        }
        return obj == null;
    }

    /**
     * 判断对象是否为空或者空串
     *
     * @param obj obj
     * @return boolean
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断是否是手机格式
     *
     * @param mobiles 手机号码
     * @return boolean
     */
    public static boolean isMobile(String mobiles) {
        Pattern p = Pattern.compile("^((14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();

    }


    /**
     * 如果为null则返回""
     *
     * @param obj obj
     * @return string
     */
    public static String getJSONValue(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    /**
     * 如果为null则返回""
     *
     * @param str 值
     * @return string
     */
    public static String getDecodeValue(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        } else {
            try {
                return URLDecoder.decode(str, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * int 是否为空 ，如果为空返回0 不为空返回原来的值
     *
     * @param i 值
     * @return int
     */
    public static int parseInt(Integer i) {
        if (i == null) {
            return 0;
        } else {
            return i;
        }
    }

    /**
     * 去掉数组第一个值
     *
     * @param arr 数组
     * @return string
     */
    public static String deleteFirstNode(String[] arr) {
        if (arr.length < 2) {
            return null;
        }
        String[] arrNew = new String[arr.length - 1];
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                continue;
            }
            arrNew[i - 1] = arr[i];
        }
        String ts = "";
        for (String s : arrNew) {
            ts += s + ",";
        }
        return ts.substring(0, ts.length() - 1);
    }

    /**
     * 过滤含有html、script、style标签的字符串
     *
     * @param inputString 含有html、script、style标签的字符串
     * @return 文本字符串
     */
    public static String html2Text(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        Pattern pscript;
        Matcher mscript;
        Pattern pstyle;
        Matcher mstyle;
        Pattern phtml;
        Matcher mhtml;
        try {
            String regExscript = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regExstyle = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regExhtml = "<[^>]+>"; // 定义HTML标签的正则表达式
            pscript = Pattern.compile(regExscript, Pattern.CASE_INSENSITIVE);
            mscript = pscript.matcher(htmlStr);
            htmlStr = mscript.replaceAll(""); // 过滤script标签

            pstyle = Pattern.compile(regExstyle, Pattern.CASE_INSENSITIVE);
            mstyle = pstyle.matcher(htmlStr);
            htmlStr = mstyle.replaceAll(""); // 过滤style标签

            phtml = Pattern.compile(regExhtml, Pattern.CASE_INSENSITIVE);
            mhtml = phtml.matcher(htmlStr);
            htmlStr = mhtml.replaceAll(""); // 过滤html标签

            htmlStr = htmlStr.replaceAll("\\\\n", "");
            htmlStr = htmlStr.replaceAll("\\\\r", "");
            htmlStr = htmlStr.replaceAll("&nbsp;", "");
            textStr = htmlStr;
        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr;
        // 返回文本字符串
    }

    /**
     * @param src 值
     * @return string
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 倒序String数组
     *
     * @param array String数组
     * @return 倒序后的String数组
     */
    public static String[] inverseStringArray(String[] array) {
        String t;
        int j = array.length;
        for (int i = 0; i < j; i++) {
//            if (j >= i) {
            t = array[i];
            array[i] = array[--j];
            array[j] = t;
//            } else {
//                break;
//            }
        }
        return array;
    }

    /**
     * 判断对象是否为空！(null,"", "null")
     *
     * @param obj obj
     * @return boolean
     */
    public static boolean isBlank(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return isBlank((String) obj);
        } else {
            return isBlank(obj.toString());
        }
    }

    /**
     * 判断对象是否为空！(null,"", "null")
     *
     * @param value 值
     * @return boolean
     */
    private static boolean isBlank(String value) {
        if (value == null || value.length() == 0) {
            return true;
        } else {
            return (value.trim().equals(""));
        }
    }

    /**
     * 判断对象是否非空！(null,"", "null")
     *
     * @param obj boj
     * @return boolean
     */
    public static boolean isNotBlank(Object obj) {
        return !isBlank(obj);
    }

    /**
     * 判断两个值是否相等
     *
     * @param o1 1
     * @param o2 2
     * @return boolean
     */
    public static boolean eq(Object o1, Object o2) {
        return (o1 == null) ? false : (o2 == null) ? true : o1.equals(o2);
    }

    /**
     * crlf to html
     *
     * @param content 内容
     * @return string
     */
    public static String cRLFToHtml(String content) {
        String s = content;
        s = s.replaceAll("\r\n", "<br/>");
        s = s.replaceAll("\t", "　　");
        return s;
    }

    /**
     * @param file 文件
     * @return float
     * @时间 : 2013-9-26 上午11:31:56
     * @描述 ：判断文件的大小是否超过2MB
     */
    public static float isFileLong2MB(File file) {

        long len = file.length();

        float a = (len / 1024) / 1024;


        return a;
    }

    /**
     * 检测是否是图片文件
     *
     * @param imgFileFileName 文件名
     * @return boolean
     */
    public static boolean isImageFile(String imgFileFileName) {
        boolean isImage = false;
        String[] imgExts = {".gif", ".jpg", ".jpeg", ".bmp", ".png"};
        for (String ext : imgExts) {
            if (imgFileFileName.toLowerCase().endsWith(ext)) {
                isImage = true;
            }
        }

        return isImage;
    }


    /**
     * 输入list和Countname计算总和
     *
     * @param coumnName 1
     * @param list      2
     * @return ret
     */
    public static Map getMapAdd(List coumnName, List list) {

        if (list == null) {
            return null;
        } else {
            Map map = new HashMap();

            for (int i = 0; i < coumnName.size(); i++) {
                double dou = 0;
                String nString = (String) coumnName.get(i);
                for (int j = 0; j < list.size(); j++) {

                    Map map2 = (Map) list.get(j);
                    double d = doubleFormat2(map2.get(nString));
                    dou = dou + d;
                }
                dou = doubleFormat2(Math.round(dou * 10000) / 10000.0);

                map.put(nString, getLongINobject(dou));
            }
            return map;
        }
    }

    /**
     * @param Message 显示内容
     * @return string
     * @时间 : 2013-9-26 上午11:31:56
     * @描述 ：为前台添加alert
     */
    public static String alert(String Message) {
        StringBuffer strMessage = new StringBuffer();
        strMessage.append("<Script Language=JavaScript>").append("\r\n");
        strMessage.append("alert('").append(Message).append("');").append("\r\n");
        strMessage.append("</Script>").append("\r\n");
        return strMessage.toString();

    }

    /**
     * 如果小数位0就显示整数
     *
     * @param object obj
     * @return obj
     */
    public static Object getLongINobject(Object object) {
        double b = 0;
        if (object == null) {
            return b;
        } else {
            b = doubleFormat2(object);

            if (b % 1.0 == 0) {
                return (long) b;
            } else {
                return object;
            }
        }
    }


    /**
     * 根据key值获取properties里面的值
     *
     * @param key        key
     * @param nameString properties 地址
     * @return 值
     */
    public static String getPropertiesfromFileneme(String key, String nameString) {
        InputStream fis = StringTool.class.getClassLoader().getResourceAsStream(nameString);
//		properties类
        Properties property = new Properties();
        String value = "";
        try {
//			加载输入流
            property.load(fis);
//		更具key值过去值
            value = property.getProperty(key);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch
                        (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return value;

    }

    /**
     * 将string转换成不同的数据类型
     *
     * @param valueString 值
     * @param typeString  转类型
     * @return obj
     */
    public static Object changeType(Object valueString, String typeString) {
        Object object = new Object();

        if (StringTool.strToNull(typeString).equalsIgnoreCase("VARCHAR")) {
            object = StringTool.strToNull(valueString);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("CHAR")) {
            object = StringTool.strToNull(valueString);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("INTEGER")) {
            //System.out.println(StringTool.intFormat(valueString));
            object = StringTool.intFormat(valueString);
            //System.out.println(object);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("DATE")) {
            object = StringTool.getDateFormatYMD(valueString);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("TIME")) {
            object = StringTool.getDateFromYMDHMS((String) valueString);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("DECIMAL")) {
            object = StringTool.doubleFormat(valueString);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("DOUBLE")) {
            object = StringTool.doubleFormat(valueString);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("SMALLINT")) {
            object = StringTool.shortFormat(valueString);
        } else if (StringTool.strToNull(typeString).equalsIgnoreCase("BLOB")) {
            object = StringTool.getByte(StringTool.strToNull(valueString));
        }
        return object;
    }

    /**
     * date
     *
     * @param object string类型时间
     * @return date
     */
    public static Date getDateFromYMDHMS(String object) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return format.parse(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    //

    /**
     * 输入string获取byty[]
     *
     * @param Str string
     * @return 值
     */
    public static byte[] getByte(String Str) {
        byte[] bytes = new byte[1];

        if (Str == null) {
            return bytes;
        } else if (StringTool.strToNull(Str).equals("")) {
            return bytes;
        } else {
            String[] strings = Str.split(",");
            bytes = new byte[strings.length];
            Integer[] ints = new Integer[strings.length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = Integer.parseInt(strings[i]);
            }
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(strings[i]);
            }
            return bytes;
        }

    }


    /**
     * 去除null值
     *
     * @param obj obj
     * @return stirng
     */
    public static String strToNull(Object obj) {

        String str = "";

        if (obj == null) {

            return "";

        } else {

            return String.valueOf(obj).trim();

        }
    }

    /**
     * 字符串左右去空格
     *
     * @param obj obj
     * @return string
     */
    public static String strToNull2(Object obj) {
        String str = "";
        if (obj == null) {
            return "";
        } else {
            return String.valueOf(obj).trim();
        }

    }

    /**
     * obj to int
     *
     * @param obj obj
     * @return int
     */
    public static Integer intFormat(Object obj) {

        if (obj == null) {

            return null;

        } else {

            String str = strToNull(obj);
            if ("".equals(str)) {
                return null;

            } else {
                return Integer.parseInt(str);

            }

        }

    }

    /**
     * obj to double
     *
     * @param obj obj
     * @return double
     */
    public static Double doubleFormat(Object obj) {

        if (obj == null) {

            return null;

        } else {

            String str = strToNull(obj);
            if ("".equals(str)) {
                return null;

            } else {
                return Double.valueOf((String) obj);
            }

        }

    }

    /**
     * obj to double
     *
     * @param obj obj
     * @return double
     */
    public static Double doubleFormat2(Object obj) {
        double b = 0;
        if (obj == null) {

            return b;

        } else {

            String str = strToNull(obj);
            if ("".equals(str)) {
                return b;

            } else {
                try {
                    return Double.valueOf(obj.toString());
                } catch (NumberFormatException e) {
                    return b;
                }
            }

        }
    }

    /**
     * obj to float
     *
     * @param obj obj
     * @return float
     */
    public static Float floatFormat(Object obj) {

        if (obj == null) {

            return null;

        } else {

            String str = strToNull(obj);
            if ("".equals(str)) {
                return null;

            } else {
                return Float.valueOf((String) obj);

            }

        }

    }

    /**
     * obj to short
     *
     * @param obj obj
     * @return short
     */
    public static Short shortFormat(Object obj) {

        if (obj == null) {

            return null;

        } else {

            String str = strToNull(obj);
            if ("".equals(str)) {
                return null;

            } else {
//				System.out.println(Short.valueOf(str));
                return Short.valueOf(str);

            }

        }

    }

    /**
     * 字符流编码
     *
     * @param is 字符流
     * @return string
     * @throws IOException e
     */
    public static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is,
                "utf-8"));
        int i = -1;
        String xmlString = "";

        String temp = "";

        while ((temp = br.readLine()) != null) {
            xmlString += temp;
        }
        return xmlString;
    }


    /**
     * 修改日期类型
     *
     * @param object obj
     * @return string
     */
    public static String getDateFormatYMD(Object object) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return format.format(format.parse(object.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    /**
     * 获取日期
     *
     * @param object obj
     * @return date
     */
    public static Date getDateFromYMD(String object) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return format.parse(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    /**
     * 获取年月日
     *
     * @param object date
     * @return string
     */
    public static String getDateFormatYMD(Date object) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return sdf.format(object);
                }
            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }


        }

    }

    /**
     * 获取年月日 时分秒
     *
     * @param object date
     * @return string
     */
    public static String getDateFormatYMDhms(Date object) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return sdf.format(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 获取时间到毫秒
     *
     * @param object date
     * @return string
     */
    public static String getDateFormatYMDhmsSSS(Date object) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return sdf.format(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 根据格式获取年月日时分秒
     *
     * @param object obj
     * @return string
     */
    public static String getDateFormatYMDyyyyMMddHHmmss(Object object) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return format.format(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    /**
     * 获取时分秒
     *
     * @param object date
     * @return string
     */
    public static String getDateFormatSJHHmmss(Object object) {
        DateFormat format = new SimpleDateFormat("HHmmss");
        if (object == null) {
            return null;
        } else {
            try {
                if ("".equals(String.valueOf(object))) {
                    return null;
                } else {
                    return format.format(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    /**
     * 改变日期的格式
     *
     * @param obj    obj
     * @param string 格式
     * @return 时间
     */
    public static Timestamp timestampFormat(Object obj, String string) {
        SimpleDateFormat sdf = new SimpleDateFormat(string);

        if (obj == null) {

            return null;

        } else {

            try {
                if ("".equals(String.valueOf(obj))) {
                    return null;
                } else {
                    return (Timestamp) sdf.parse(String.valueOf(obj));
                }

            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

        }

    }

    /**
     * 把map转换成&key=value形式的字符串
     *
     * @param params 参数
     * @return 1=1&key=value&...=...
     */
    public static String mapToStringWithUrl(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append("1=1");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("&" + entry.getKey() + "=" + entry.getValue());
        }
        return sb.toString();
    }


    /**
     * 把数组转化成字母
     */
    public static String getNumToLeter(Integer num) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "A");
        map.put(2, "B");
        map.put(3, "C");
        map.put(4, "D");
        map.put(5, "E");
        map.put(6, "F");
        map.put(7, "G");
        map.put(8, "H");
        map.put(9, "I");
        map.put(10, "J");
        map.put(11, "K");
        map.put(12, "L");
        map.put(13, "M");
        map.put(14, "N");
        map.put(15, "O");
        map.put(16, "P");
        map.put(17, "Q");
        map.put(18, "R");
        map.put(19, "S");
        map.put(20, "T");
        map.put(21, "U");
        map.put(22, "V");
        map.put(23, "W");
        map.put(24, "X");
        map.put(25, "Y");
        map.put(26, "Z");
        map.put(27, "AA");
        map.put(28, "AB");
        map.put(29, "AC");
        map.put(30, "AD");
        map.put(31, "AE");
        return map.get(num);
    }

    /**
     * 从map 中获取对象并转换为string  如果为空返回"" 字符串
     *
     * @param map
     * @param key
     * @return
     */
    public static String getStringFromMap(Map map, String key) {
        String returns = "";
        if (map != null) {
            Object o = map.get(key);
            if (o != null) {
                returns = o.toString();
            }
        }
        return returns;
    }

    /***
     * 获取JSON类型
     * 判断规则
     * 判断第一个字母是否为{或[ 如果都不是则不是一个JSON格式的文本
     *
     * @param str
     * @return
     */
    public static Integer getJSONType(String str) {
        if (StringTool.isEmpty(str)) {
            return 0;
        }

        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];

        if (firstChar == '{') {
            return 1;
        } else if (firstChar == '[') {
            return 2;
        } else {
            return 0;
        }
    }


}
