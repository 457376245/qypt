package com.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author other
 *
 */
@SuppressWarnings("unchecked")
public class XmlUtil {

	private final static Logger logger = LoggerFactory.getLogger(XmlUtil.class);

	/**
	 * 将接口返回的xml转换为List对象（xml为默认的报文格式）
	 * 
	 * @param xmlString
	 * @return 数据格式为：<strong>map</strong><br>
	 *         "title" : "id字段"<br>
	 *         "value" " "值"
	 */
	public static List<Map<String, Object>> defaultXmlToList(String xmlString) {
		try {
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			Document document = DocumentHelper.parseText(xmlString);
			List<Element> dataNodes = document
					.selectNodes("//Response/rows/row");
			for (Element element : dataNodes) {
				Map map = new HashMap();
				for (Iterator<Element> iter = element.elementIterator(); iter
						.hasNext();) {
					Element ele = iter.next();
					map.put(ele.getName(), ele.getText());
				}
				result.add(map);
			}
			return result;
		} catch (DocumentException e) {
			logger.error("转换list对象异常:", e);
			return null;
		}
	}
    /**
     * 积分余额 QueryBonusBalance
     * @param xmlString   	
        <BonusInfo>
		<TotalBonus>261</TotalBonus>
		<AvailableBonus>261</AvailableBonus>
		<FreezeBonus>0</FreezeBonus>
		<InvalidateBonus>261</InvalidateBonus>
	</BonusInfo>
     * @return
     */
	
	public static List<Map<String,Object>> pointBonusBalanceXmlToList(String xmlString) {
		try {
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			Map map = new HashMap();
			Document document = DocumentHelper.parseText(xmlString);
			org.dom4j.Element root = document.getRootElement();
			List<org.dom4j.Element> l = root.elements();
			for(org.dom4j.Element e : l){
				if(e.getName().equals("BonusInfo")){
					List<org.dom4j.Element> ll = e.elements();
					 for(org.dom4j.Element ee : ll){
						 map.put(ee.getName(),ee.getText());
						 logger.debug("name:{},text:{}", ee.getName(),ee.getText());
				 }
			}
		}
			result.add(map);
			return result;
		} catch (DocumentException e) {
			logger.error("转换list对象异常:", e);
			return null;
		}
	}
	public static Object[] xmlToStringArray(String xmlString, String xpath) {
		try {
			Document document = DocumentHelper.parseText(xmlString);
			Element root = (Element) document.selectSingleNode(xpath);
			List<String> stringList = new ArrayList<String>();
			for (Iterator<Element> iter = root.elementIterator(); iter
					.hasNext();) {
				Element element = iter.next();
				stringList.add(element.getName());
			}
			return stringList.toArray();
		} catch (DocumentException e) {
			logger.error("转换list对象异常:", e);
			return null;
		}
	}
	
	/**
	 * 根据Xpath得到该参数具体的值. 
	 * <b>注意：<b/><br>
	 * <i>1. 直接通过xpath 来过滤是否得到数据来返回规则true or false  直接 rule = 变量名称
	 * <i>2. 可以通过xpath 来得到具体取值，来得到指定变量的赋值。     用来判断 rule = 变量.equals("常量")；
	 * @param xmlstr  xml 字符串内容
	 * @param xpath   xpath
	 * @return value  通过xpath得到该xml文本中的返回结果
	 * @throws RuleEingineException 规则异常
	 */
	public static Object getValue(String xmlstr, String xpath) throws DocumentException {
		String returnvalue = null;
		Document doc = convToxml(xmlstr);
		Object node = doc.selectObject(xpath);
		if (node == null || node.toString().equals("[]")) {
			//如果xpath无法取道值，则返回false
			return null;
		} else {
			if (node instanceof List) {
				returnvalue = node.toString();
			} else if (node instanceof DefaultText) {
				returnvalue = ((DefaultText) node).getText();
			} else if (node instanceof DefaultElement) {
				returnvalue = ((DefaultElement) node).toString();
			} else if (node instanceof String) {
				returnvalue = node.toString();
			}
			return returnvalue;
		}
	}
	
	/**
	 * 载入XML
	 * @param xmlStr  字符串
	 * @throws DocumentException  异常
	 */
	public static Document convToxml(String xmlStr) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlStr);
		} catch (DocumentException e) {
			return null;
		}
		return doc;
	}
	

	/**
     * 解析报文获得积分 getPointfromOutXml
     * @param xmlString   	
        <ESB>
		<MSG_ID>12345678</MSG_ID>
		<FROM>CRM_FOUCSCUST</FROM>
		<TO>ODS</TO>
		<SERVICE_CD>getTotalPointInfo</SERVICE_CD>
		<TIME>1413951242700</TIME>
		<VERSION>1.0</VERSION>
		<RETURN_CD>1</RETURN_CD>
		<RETURN_DESC>可用积分查询</RETURN_DESC>
		<RESPONSES>
		<ROW>
		<CN_VAL>9292</CN_VAL>
		<TERM_CN_VAL>15070</TERM_CN_VAL>
		<INVA_CN_VAL>0</INVA_CN_VAL>
		<SUM_CN_VAL>19520</SUM_CN_VAL>
		<SUM_ALL_CN_VAL>9292</SUM_ALL_CN_VAL>
		<STATUS>1</STATUS>
		<MONTH_CN_VAL>700</MONTH_CN_VAL>
		</ROW>
		</RESPONSES>
		</ESB>
     * @return String
     */
	
	public static String getPointfromOutXml(String xmlString) {
		try {
			String point = "";
			Document document = DocumentHelper.parseText(xmlString);
			org.dom4j.Element root = document.getRootElement();
			List<org.dom4j.Element> l = root.elements();
			for(org.dom4j.Element e : l){
				if(e.getName().equals("RESPONSES")){
					List<org.dom4j.Element> ll = e.elements();
					for(org.dom4j.Element ee : ll){
						if(ee.getName().equals("ROW")){
							List<org.dom4j.Element> lll = ee.elements();
							for(org.dom4j.Element eee : lll){
								if(eee.getName().equals("CN_VAL")){
									point = eee.getText();
									logger.debug("point:"+eee.getName()+",text:"+eee.getText());
								}
							}
						}
					}
				}
			}
			return point;
		} catch (DocumentException e) {
			logger.error("解析积分接口返回报文异常:", e);
			return "解析积分接口返回报文异常:" + e.toString().substring(0,500);
		}
	}

    /**
     * 欠费余额查询 getAccXml
     * @author yuanbin
     * @version 2014-12-11
     * @param xmlString   	
        <root>
	        <row>
	        	<AggrCharge>2373</AggrCharge>
	        </row>
        </root>
     * @return
     */
	public static Map<String,Object> getAccXmlToMap(String xmlString) {
		try {
			Map map = new HashMap();
			Document document = DocumentHelper.parseText(xmlString);
			org.dom4j.Element root = document.getRootElement();
			List<org.dom4j.Element> l = root.elements();
			for(org.dom4j.Element e : l){
				if(e.getName().equals("row")){
					List<org.dom4j.Element> ll = e.elements();
					 for(org.dom4j.Element ee : ll){
						 map.put(ee.getName(),ee.getText());
						 logger.debug("name:{},text:{}", ee.getName(),ee.getText());
				 }
			}
		}
			return map;
		} catch (DocumentException e) {
			logger.error("转换map对象异常:", e);
			return null;
		}
	}
	
	
	/**
	 * 将xml字符串转换成map
	 * @author yanzai
	 * @param xmlString
	 * @return
	 */
	public static Map<String,String> convertXmlStrToMap(String xmlString) {
		try {
			Map<String,String> map = new HashMap<String,String>();
			if (null != xmlString && !"".equals(xmlString.trim())) {
				Document document = DocumentHelper.parseText(xmlString);
				org.dom4j.Element root = document.getRootElement();
				List<org.dom4j.Element> l = root.elements();
				int size = l.size();
				for (int i = 0; i < size; i++) {
					org.dom4j.Element e = l.get(i);
					List<org.dom4j.Element> ll = e.elements();
					for(org.dom4j.Element ee : ll){
						if (size > 1) {
							String t = map.get(ee.getName());
							if (null == t || "".equals(t)) {
								t = ee.getText().trim();
							}else {
								t = t + "," + ee.getText().trim();
							}
							map.put(ee.getName(),t);
						} else {
							map.put(ee.getName(),ee.getText());
						}
						logger.debug("name:{},text:{}", ee.getName(),ee.getText());
					}
				}
			}
			return map;
		} catch (Exception e) {
			logger.error("转换map对象异常:", e);
			return null;
		}
	}

	/**
	 * 判断是否是xml结构
	 */
	public static boolean isXML(String value) {
		try {
			DocumentHelper.parseText(value);
		} catch (DocumentException e) {
			return false;
		}
		return true;
	}
}
