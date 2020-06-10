package com.web.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;

public class DateUtil {
	public static final String FMT_DATE = "yyyy-MM-dd HH:mm:ss";

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");

	public static SimpleDateFormat sdfMt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	public static SimpleDateFormat sdfSs = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/** 日期 format: yyyyMMddHHmmss. */
	public static final String DATE_FORMATE_STRING_DEFAULT = "yyyyMMddHHmmss";

	/** 日期 format: yyyy-MM-dd HH:mm:ss. */
	public static final String DATE_FORMATE_STRING_A = "yyyy-MM-dd HH:mm:ss";

	/** 日期 format: yyyy-MM-dd. */
	public static final String DATE_FORMATE_STRING_B = "yyyy-MM-dd";

	/** 日期 format: MM/dd/yyyy HH:mm:ss a. */
	public static final String DATE_FORMATE_STRING_C = "MM/dd/yyyy HH:mm:ss a";

	/** 日期 format: yyyy-MM-dd HH:mm:ss a. */
	public static final String DATE_FORMATE_STRING_D = "yyyy-MM-dd HH:mm:ss a";

	/** 日期 format: yyyy-MM-dd'T'HH:mm:ss'Z'. */
	public static final String DATE_FORMATE_STRING_E = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/** 日期 format: yyyy-MM-dd'T'HH:mm:ssZ. */
	public static final String DATE_FORMATE_STRING_F = "yyyy-MM-dd'T'HH:mm:ssZ";

	/** 日期 format: yyyy-MM-dd'T'HH:mm:ssz. */
	public static final String DATE_FORMATE_STRING_G = "yyyy-MM-dd'T'HH:mm:ssz";

	/** 日期 format: yyyyMMdd. */
	public static final String DATE_FORMATE_STRING_H = "yyyyMMdd";

	/** 日期 format: yyyy-MM-dd HH:mm:ss.SSS. */
	public static final String DATE_FORMATE_STRING_I = "yyyy-MM-dd HH:mm:ss.SSS";

	/** 日期 format: yyyyMMddHHmmss.SSS. */
	public static final String DATE_FORMATE_STRING_J = "yyyyMMddHHmmss.SSS";

	/** 日期 format: yyyyMMddHHmmssSSS . */
	public static final String DATE_FORMATE_STRING_K = "yyyyMMddHHmmssSSS";

	/** 日期 format: yyyy-MM. */
	public static final String DATE_FORMATE_STRING_YM = "yyyy-MM";
	/** 日期 format. */
	private static Map<String, SimpleDateFormat> formats;
	static {
		formats = new HashMap<String, SimpleDateFormat>();

		formats.put(DATE_FORMATE_STRING_DEFAULT, new SimpleDateFormat(
				DATE_FORMATE_STRING_DEFAULT));
		formats.put(DATE_FORMATE_STRING_A, new SimpleDateFormat(
				DATE_FORMATE_STRING_A));
		formats.put(DATE_FORMATE_STRING_B, new SimpleDateFormat(
				DATE_FORMATE_STRING_B));
		formats.put(DATE_FORMATE_STRING_C, new SimpleDateFormat(
				DATE_FORMATE_STRING_C));
		formats.put(DATE_FORMATE_STRING_D, new SimpleDateFormat(
				DATE_FORMATE_STRING_D));
		formats.put(DATE_FORMATE_STRING_E, new SimpleDateFormat(
				DATE_FORMATE_STRING_E));
		formats.put(DATE_FORMATE_STRING_F, new SimpleDateFormat(
				DATE_FORMATE_STRING_F));
		formats.put(DATE_FORMATE_STRING_G, new SimpleDateFormat(
				DATE_FORMATE_STRING_G));
		formats.put(DATE_FORMATE_STRING_H, new SimpleDateFormat(
				DATE_FORMATE_STRING_H));
		formats.put(DATE_FORMATE_STRING_I, new SimpleDateFormat(
				DATE_FORMATE_STRING_I));
		formats.put(DATE_FORMATE_STRING_J, new SimpleDateFormat(
				DATE_FORMATE_STRING_J));
		formats.put(DATE_FORMATE_STRING_K, new SimpleDateFormat(
				DATE_FORMATE_STRING_K));
	}
	public static String format(Date date) {
		return format(date, DATE_FORMATE_STRING_A);
	}
	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}
	/**
	 * 当前时间减去min分钟
	 * 
	 * @param date
	 * @param min
	 * @return
	 */
	public static Date getBefMin(Date date, int min) {
		long lDate = date.getTime() - (1000L * 60L * min);
		return new Date(lDate);
	}

	/**
	 * 当前时间加上min分钟
	 * 
	 * @param date
	 * @param min
	 * @return
	 */
	public static Date getAftMin(Date date, int min) {
		long lDate = date.getTime() + (1000L * 60L * min);
		return new Date(lDate);
	}

	/**
	 * 当前时间减去秒
	 * @param date
	 * @param second
	 * @return
	 */
	public static Date getBefSecond(Date date, int second) {
		long lDate = date.getTime() - (1000L * second);
		return new Date(lDate);
	}
	/**
	 * 当前时间减去hour小时
	 * 
	 * @param date
	 * @param min
	 * @return
	 */
	public static Date getBefHour(Date date, int hour) {
		long lDate = date.getTime() - (1000L * 60L * 60L * hour);
		return new Date(lDate);
	}

	/**
	 * 获取指定时间是星期几
	 * 
	 * @param date
	 *            指定时间
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w == 0)
			w = 7;
		return w;
	}

	/**
	 * 获取指定时间是当前月的哪一天
	 * 
	 * @param date
	 *            指定时间
	 * @return
	 */
	public static int getDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_MONTH);
		return w;
	}

	/**
	 * 获取指定时间的月跟日前MM:DD
	 * 
	 * @param date
	 *            指定时间
	 * @return
	 */
	public static String getDayAndMonth(Date date) {
		String strDate = sdf.format(new Date());
		return strDate.substring(5, 10);
	}

	/**
	 * 获取当前小时跟分钟
	 * 
	 * @return
	 */
	public static boolean getHourMin(String str1, int min) {
		String str = "2009-01-01 " + str1 + ":00";
		String strDate = "2009-01-01 "
				+ DateUtil.sdf.format(new Date()).substring(11, 19);
		try {
			Date date1 = DateUtil.sdf.parse(str);
			Date date2 = DateUtil.sdf.parse(strDate);
			Date date3 = DateUtil.getBefMin(date2, min);
			if (date1.before(date2) && date1.after(date3)) {
				return true;
			}
		} catch (ParseException e) {
			return false;
		}
		return false;
	}

	/**
	 * 获取当前时间的前n个月或者后n个月
	 * @param format 返回格式
	 * @param n 月数
	 * @param flag  1 表示当前时间前,2表示当前时间后
	 * @return
	 */
	public static String getBeforOrFollowDay(String format, int n, int flag) {

		if (isEmptyStr(format)) {
			format = FMT_DATE;
		}

		SimpleDateFormat df = new SimpleDateFormat(format);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date());
		if (1 == flag) {
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - n);
		} else {
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + n);
		}

		return df.format(calendar.getTime());
	}
	
	/**
	 * 获取当前时间的前n个月或者后n个月
	 * @param format 返回格式
	 * @param n 月数
	 * @param flag  1 表示当前时间前,2表示当前时间后
	 * @return
	 */
	public static Date getBeforOrFollowMonth(Date nowDay, int n, int flag) {
		//SimpleDateFormat df = new SimpleDateFormat(format);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowDay);
		if (1 == flag) {
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - n);
		} else {
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + n);
		}

		return calendar.getTime();
	}

	 /**
     * 获取某月的最后一天
     *
     */
    public static String getLastDayOfMonth(int year,int month){
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
          
        return lastDayOfMonth;
    }
	
	/**
	 * 获取系统当前日期时间(格式自定)
	 * 
	 * @param format
	 *            返回日期的格式 默认为 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getFormatDate(String format) {

		if (isEmptyStr(format)) {
			format = FMT_DATE;
		}
		Date d = new Date();
		DateFormat df = new SimpleDateFormat(format);

		return df.format(d);
	}

	public static String getFormatDate() {
		return getFormatDate(FMT_DATE);
	}

	/**
	 * 验证当前月份是否在当前X个月以内
	 * 
	 * @param month
	 * @return
	 */
	public static boolean checkMonth(int month, int scope) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("MM");
		int nowmonth = Integer.parseInt(df.format(date));
		// int nowmonth = 5;
		int i = scope - 1;
		if (month < 1 || month > 12) {
			return false;
		} else {
			if (nowmonth > i) {
				if (month > nowmonth || month < nowmonth - i) {
					return false;
				} else {
					return true;
				}
			} else {
				if (month < nowmonth
						|| (month > nowmonth + (12 - scope) && month <= 12)) {
					return true;
				} else {
					return false;
				}
			}
		}
	}
	
	/**
	 * 获取当前年份
	 * @return
	 */
	public static int getYear(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy");
		return Integer.valueOf(df.format(date));
	}
	
	/**
	 * 获取当前月份
	 * @return
	 */
	public static int getMonth(Date date) {
		DateFormat df = new SimpleDateFormat("MM");
		return Integer.valueOf(df.format(date));
	}
	
	/**
	 * 获取当前月份
	 * @return
	 */
	public static String getMonthForStr(Date date) {
		DateFormat df = new SimpleDateFormat("MM");
		return df.format(date);
	}
	
	/**
	 * 获取当前日期
	 * @return
	 */
	public static int getDate(Date date) {
		DateFormat df = new SimpleDateFormat("dd");
		return Integer.valueOf(df.format(date));
	}

	/**
	 * 获取当前小时
	 * @return
	 */
	public static int getHour(Date date) {
		DateFormat df = new SimpleDateFormat("HH");
		return Integer.valueOf(df.format(date));
	}

	/**
	 * 判断指定的日期是否已过期
	 * 
	 * @param checkedDate
	 * @return
	 */
	public static boolean isOverdue(Date checkedDate, long milliseconds) {
		Calendar cd = Calendar.getInstance();
		Date now = cd.getTime();
		// 时间往前移多少毫秒
		now.setTime(now.getTime() - milliseconds);

		return checkedDate.before(now);
	}

	/**
	 * 解析格式字符串为时间戳
	 * 
	 * @param src
	 * @param fmt
	 * @return
	 */
	public static Timestamp parseToTimestamp(String src, String fmt) {
		java.text.Format f = new java.text.SimpleDateFormat(fmt);
		try {
			Date date = (Date) f.parseObject(src);
			return new Timestamp(date.getTime());

		} catch (ParseException e) {
		}
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 判断空指针和空字符串
	 */
	public static boolean isEmptyStr(String str) {
		if (str == null || str.trim().equals(""))
			return true;
		else
			return false;
	}

	/**
	 * 返回时间差
	 */
	public static long betweenTime(Date d1) {
		long diff = new Date().getTime() - d1.getTime();
		long days = diff;
		return days;
	}

	/**
	 * 获取上个月的月
	 * 
	 * @return
	 */
	public static String getLastMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		Date date = new Date();
		return sdf.format(getLastDate(date));
	}

	public static Date getLastDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 获取上上个月的年月
	 * 
	 * @return
	 */
	public static String getAgoMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		Date date = new Date();
		return sdf.format(getAgoDate(date));
	}

	public static Date getAgoDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -2);
		return cal.getTime();
	}

	/**
	 * 获取上上上个月的年月
	 * 
	 * @return
	 */
	public static String getOverMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		Date date = new Date();
		return sdf.format(getOverDate(date));
	}

	public static Date getOverDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -3);
		return cal.getTime();
	}

	public static String getDate(String pattern, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}

	/**
	 * 按照pattern格式取当前日期字符串.
	 * 
	 * @param pattern
	 *            日期字符串格式
	 * @return 格式化后的当前日期字符串
	 */
	public static String getNow(String pattern) {
		return getFormatTimeString(new Date(), pattern);
	}

	/**
	 * 将Date转换为 pattern 格式的字符串 . 格式为：yyyyMMddHHmmss<BR>
	 * yyyy-MM-dd HH:mm:ss <BR>
	 * yyyy-MM-dd <BR>
	 * MM/dd/yyyy HH:mm:ss a <BR>
	 * yyyy-MM-dd HH:mm:ss a <BR>
	 * yyyy-MM-dd'T'HH:mm:ss'Z' <BR>
	 * yyyy-MM-dd'T'HH:mm:ssZ <BR>
	 * yyyy-MM-dd'T'HH:mm:ssz <BR>
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return String --格式化的日期字符串
	 * @see java.util.Date
	 */
	public static String getFormatTimeString(Date date, String pattern) {
		SimpleDateFormat sDateFormat = getDateFormat(pattern);
		// 单实例,SimpleDateFormat非线程安全
		synchronized (sDateFormat) {
			return sDateFormat.format(date);
		}
	}

	/**
	 * 根据pattern取得的date formate.
	 * <P>
	 * 
	 * @param pattern
	 *            日期格式
	 * @return SimpleDateFormat SimpleDateFormat 对象
	 */
	public static SimpleDateFormat getDateFormat(String pattern) {
		SimpleDateFormat sDateFormat = formats.get(pattern);
		if (sDateFormat == null) {
			sDateFormat = new SimpleDateFormat(pattern);
			formats.put(pattern, sDateFormat);
		}
		return sDateFormat;
	}

	public static String getBillMonth(String pattern, int num) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date();
		return sdf.format(getBillDate(date, num));
	}

	public static Date getBillDate(Date date, int num) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -num);
		return cal.getTime();
	}

	/**
	 * 
	 * @param date1
	 *            <String>
	 * @param date2
	 *            <String>
	 * @return int
	 * @throws ParseException
	 */
	public static int getMonthSpace(String date1, String date2)
			throws ParseException {
		int result = 0;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		int year = c1.get(Calendar.YEAR);
		int month = c1.get(Calendar.MONTH);

		int year1 = c2.get(Calendar.YEAR);
		int month1 = c2.get(Calendar.MONTH);

		if (year == year1) {
			result = month1 - month;// 两个日期相差几个月，即月份差
		} else {
			result = 12 * (year1 - year) + month1 - month;// 两个日期相差几个月，即月份差
		}

		return result;
	}

	/** 格式時間為 XXXX年XX月XX日 ,傳入數據為yyyyMMdd */
	public static String fomatDate(String date, String year, String month,
			String da) throws Exception {
		String monthday;

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		Date startDate1 = fmt.parse(date);

		Calendar starCal = Calendar.getInstance();
		starCal.setTime(startDate1);
		
		String sYear=date.substring(0, 4);
		String sMonth=date.substring(4, 6);
		String sDay=date.substring(6, 8);
		
		//int sYear = starCal.get(Calendar.YEAR);
		//int sMonth = starCal.get(Calendar.MONDAY);
		//int sDay = starCal.get(Calendar.DAY_OF_MONTH);

		monthday = sYear + year + sMonth + month + sDay + da;

		return monthday;
	}

	/** 獲取兩段時間月份差 */
	public static long getMonthDiff(String startDate, String endDate,
			String addType) throws Exception {
		long monthday;

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		Date startDate1 = fmt.parse(startDate);

		Calendar starCal = Calendar.getInstance();
		starCal.setTime(startDate1);

		int sYear = starCal.get(Calendar.YEAR);
		int sMonth = starCal.get(Calendar.MONTH);
		int sDay = starCal.get(Calendar.DAY_OF_MONTH);

		Date endDate1 = fmt.parse(endDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate1);
		int eYear = endCal.get(Calendar.YEAR);
		int eMonth = endCal.get(Calendar.MONTH);
		int eDay = endCal.get(Calendar.DAY_OF_MONTH);

		monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));

		if ("add".equals(addType)) {
			monthday = monthday + 1;
		}else if("0".equals(addType)){
			
		}else{
			monthday = monthday - 1;
		}

		return monthday;
	}

	/**
	 * 
	 * @param date   日期字符串
	 * @param oldFormat  现日期格式
	 * @param newFormat  想格式化的格式
	 * @return
	 */
	public static String formateStringDateToString(String date, String oldFormat,
			String newFormat) {
		String formatResult = "";
		SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
		SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
		try {
			Date d = sdf1.parse(date);
			formatResult = sdf2.format(d);
		} catch (Exception e) {
			formatResult = date;
			e.printStackTrace();
		}
		return formatResult;
	}
	
	/**计算两个时间差*/
	public static Map<String, Long> getDateTimeDifference(String invalidTime,Date nowTime) throws ParseException{
		Map<String, Long> resultMap=new HashMap<String,Long>();
		
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		//获得两个时间的毫秒时间差异
		long diff=sd.parse(invalidTime).getTime() - nowTime.getTime();
		
		long day = diff/nd;//计算差多少天
		long hour = diff%nd/nh;//计算差多少小时
		
		resultMap.put("day", day);
		resultMap.put("hour", hour);
		
		return resultMap;
	}
	/**计算两个时间差*/
	public static Map<String, Long> getDateTimeDifference(Date invalidTime,Date nowTime) throws ParseException{
		Map<String, Long> resultMap=new HashMap<String,Long>();
		
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;
		
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		//获得两个时间的毫秒时间差异
		long diff=invalidTime.getTime() - nowTime.getTime();
		
		long day = diff/nd;//计算差多少天
		long hour = diff%nd/nh;//计算差多少小时
		long min=diff % nd % nh / nm;
		resultMap.put("day", day);
		resultMap.put("hour", hour);
		resultMap.put("min", min);
		return resultMap;
	}
	
	/**计算两个时间差-天*/
	public static Map<String, Long> getDateTimeDifferenceDay(Date endTime,Date startTime) throws ParseException{
		Map<String, Long> resultMap=new HashMap<String,Long>();
		
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		
		//获得两个时间的毫秒时间差异
		long diff=sd.parse(sd.format(endTime)).getTime() - sd.parse(sd.format(startTime)).getTime();
		
		long day = diff/nd;//计算差多少天
		long hour = diff%nd/nh;//计算差多少小时
		resultMap.put("day", day);
		resultMap.put("hour", hour);
		
		return resultMap;
	}
	
	/**获取前或后续的时间*/
	@SuppressWarnings("static-access")
	public static String getDayTime(Date date,int time, String format){
		String dateTime="";
		
		if(date!=null){
			Calendar calendar= new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE,time);
			date=calendar.getTime();
			
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			
			dateTime=dateFormat.format(date);
		}
		
		return dateTime;
	}
	
	/**解析string格式时间为date类型*/
	public static Date getDayTime(String dateString,String format){
		Date date=null;
		
		try {
			 SimpleDateFormat sdf = new SimpleDateFormat(format);  
			 date = sdf.parse(dateString);  
		} catch (Exception e) {
		
		}
		
		return date;
	}
	
	public static Double getTimeMargin(Date startTime,Date endTime){
		long a = startTime.getTime();
		  
		long b = endTime.getTime();
		  
		double b1=b-a;
		  
		return b1;
	}
	
	/**获取前或后续的时间*/
	@SuppressWarnings("static-access")
	public static String getNextDayTimeForStr(Date date,int time, String format){
		String dateTime="";
		
		if(date!=null){
			Calendar calendar= new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE,time);
			date=calendar.getTime();
			
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			
			dateTime=dateFormat.format(date);
		}
		
		return dateTime;
	}
	
	/**获取前或后续的时间*/
	@SuppressWarnings("static-access")
	public static Date getNextDayTimeForDate(Date date,int time){
		Date dateTime=null;
		
		if(date!=null){
			Calendar calendar= new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE,time);
			dateTime=calendar.getTime();
		}
		
		return dateTime;
	}
	
	/**获取前X个月的月份**/
	public static String getLast12Months(int i) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -i);
		Date m = c.getTime();
		return sdf.format(m);
	}
	
	/** 获取文件名，格式：yyyyMMddHHmmss */
    public static String fileName() {
        GregorianCalendar gerCalendar = new GregorianCalendar();
        java.util.Date date = gerCalendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = sdf.format(date) + Toolkit.getRandomNuber(6);

        return fileName;
    }
    
	public static String getLastMonths(String pattern,int i) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -i);
        String endDate = format.format(c.getTime());
        System.out.println(endDate);
        return endDate;
	}
	
	/**校验某一个时间是否在两个时间中间*/
	public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
		if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
			return true;
		}

		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);

		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		try {
			String dateString1 = "2019-12-12 12:12:12";
			DateFormat format = new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
			Date satrt = format.parse(dateString1);
			
			String dateString2 = "2020-03-18 15:20:00";
			DateFormat forma2 = new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_A);
			Date end= forma2.parse(dateString2);
//			
//			System.out.println(isEffectiveDate(new Date(),satrt,end));
//			
//			Date c=getNextDayTimeForDate(new Date(),0);
//			
//			String dateTime2=format.format(c);
//			
//			System.out.println(dateTime2);
//			
//			//System.out.println(getNextDayTimeForDate(new Date(),0,DateUtil.DATE_FORMATE_STRING_A));
//			
//			System.out.println("=====================");
//			System.out.println(getDateTimeDifferenceDay(end,satrt));
//			
//			Map<String, Long> map=getDateTimeDifferenceDay(end,satrt);
//			
//			Integer day=MapUtils.getInteger(map, "day");
//			
//			System.out.println(getNextDayTimeForStr(satrt,day,DateUtil.DATE_FORMATE_STRING_A));
//			
//			//10,11,12,13,14,15,16,17,18,19
//			
//			System.out.println(day%3);
//			
//			System.out.println(getNextDayTimeForStr(satrt,(day-(day%3)),DateUtil.DATE_FORMATE_STRING_A));
//			
//			Date xx=getNextDayTimeForDate(satrt,(day-(day%3)));
//			
//			System.out.println(format.format(xx));
//			Integer ccc=3;
//			
//			System.out.println(getNextDayTimeForDate(xx,ccc-1));
//			
//			
//			System.out.println("今天是周："+getDayOfWeek(satrt));
//			
//			System.out.println(getNextDayTimeForStr(new Date(),7-getDayOfWeek(new Date()),DateUtil.DATE_FORMATE_STRING_A));
//			
//			System.out.println(getNextDayTimeForStr(satrt,7-getDayOfWeek(satrt)+7,DateUtil.DATE_FORMATE_STRING_A));
		
			//
			int startYear=getYear(satrt);
			int startMonth=getMonth(satrt);
			
			int nowYear=getYear(end);
			int nowMonth=getMonth(end);
			
			//算出差距月数
			int disparityMonth=0;
			
			if(startYear==nowYear){
				disparityMonth=12-startMonth;
			}else{
				disparityMonth=((nowYear-startYear-1)*12)+(12-startMonth)+nowMonth;
			}
			
			System.out.println("两个时间差距月数:"+disparityMonth);
			
			String satrtTimeStr = startYear+"-"+startMonth+"-"+"01";
			Date startTime=new SimpleDateFormat(DateUtil.DATE_FORMATE_STRING_B).parse(satrtTimeStr);
			int cyc=6;
			
			Date cycStartDate=getBeforOrFollowMonth(startTime,disparityMonth-(disparityMonth%cyc),2);
			
			System.out.println(format.format(cycStartDate));
			
			System.out.println(getLastDayOfMonth(getYear(cycStartDate),getMonth(cycStartDate)));
			
//			System.out.println(getYear(satrt));
//			System.out.println(getMonth(satrt));
//			System.out.println(format.format(getBeforOrFollowMonth(satrt,4,2)));
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
