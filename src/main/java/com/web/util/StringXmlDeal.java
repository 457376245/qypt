package com.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.*;

/**
 * XML格式字符串解析
 * @author jinzeng
 *
 */
public class StringXmlDeal {
	private Document xmlDoc = null;

	public StringXmlDeal(String xmlString) throws DocumentException {
		this.xmlDoc = DocumentHelper.parseText(xmlString);
	}

	/**
	 * 功能：获取XML格式字符串的节点值
	 * @param xmlString , XML格式字符串 
	 * @param nodePath , 节点或属性位置 
	 * @return String
	 */

	public String getNodeValue(String nodePath) throws Exception {
		String result;
		try {

			Node node = this.xmlDoc.selectSingleNode(nodePath);
			if (node == null) {
				throw new Exception("没配置此页面路径，请配置！！");
			}

			result = node.getStringValue();

		} catch (Exception e) {

			throw new Exception("读取节点信息错误！" + e.getMessage());
		}
		return result;
	}

	/**
	 * 功能：获取属性值
	 * @param  String,节点路径
	 * @param  String,属性名称
	 * @return String
	 */
	public String getAttrValue(String nodePath, String attrName) throws Exception {
		String result = "";
		try {
			Iterator<Element> nodes = xmlDoc.selectNodes(nodePath).iterator();
			if (nodes == null) {
				throw new Exception("没配置此页面路径，请配置！！");
			}
			while (nodes.hasNext()) {
				Element nodeElm = nodes.next();
				result = nodeElm.attributeValue(attrName);

			}

		} catch (Exception e) {

			throw new Exception("读取节点信息错误！" + e.getMessage());
		}
		return result;
	}

	/**
	 * 功能：在指定XML字符串的指定位置插入指定的元素
	 * @param String , XML格式字符串 
	 * @param String , 位置 
	 * @param Element , 需要插入的元素 
	 * @return String
	 */
	public String writeNode(String path, Element element) throws DocumentException {
		/*Element user = DocumentHelper.createDocument().addElement("user");
		user.addElement("user");
		user.addAttribute("jinzeng", "test");*/

		List list = xmlDoc.selectNodes(path);
		Iterator iter = list.iterator();
		if (iter.hasNext()) {
			Element valueElement = (Element) iter.next();
			valueElement.add(element);
		}
		return xmlDoc.asXML();
	}

	/**
	 * 功能：xml格式的字符串转换成Map
	 * @param String,xmlString
	 * @return Map
	 */
	public static Map<String, Object> Dom2Map(String xmlString) {
		Document doc;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			doc = DocumentHelper.parseText(xmlString);
			if (doc == null){
				return map;
			}
			Element root = doc.getRootElement();
			for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
				Element e = (Element) iterator.next();
				List list = e.elements();
				if (list.size() > 0) {
					map.put(e.getName(), Dom2Map(e));
				} else{
					map.put(e.getName(), e.getText());
				}
			}
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		return map;

	}
	/**
	 * 功能：XML转换成Map
	 * @param Document , XML
	 * @return Map
	 */
	public static Map<String, Object> Dom2Map(Document doc) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null){
			return map;
		}
		Element root = doc.getRootElement();
		for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = (Element) iterator.next();
			List list = e.elements();
			if (list.size() > 0) {
				map.put(e.getName(), Dom2Map(e));
			} else{
				map.put(e.getName(), e.getText());
			}
		}
		return map;
	}

	public static Map Dom2Map(Element e) {
		Map map = new HashMap();
		List list = e.elements();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element iter = (Element) list.get(i);
				List mapList = new ArrayList();

				if (iter.elements().size() > 0) {
					Map m = Dom2Map(iter);
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(m);
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(m);
						}
						map.put(iter.getName(), mapList);
					} else{
						map.put(iter.getName(), m);
					}
				} else {
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(iter.getText());
						}
						if (obj.getClass().getName().equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(iter.getText());
						}
						map.put(iter.getName(), mapList);
					} else{
						map.put(iter.getName(), iter.getText());
					}
				}
			}
		} else{
			map.put(e.getName(), e.getText());
		}
		return map;
	}

	public static void Map2Dom(Map<String, Object> param, Element e) {
		if (param == null || e == null)
			return;
		for (Entry<String, Object> entry : param.entrySet()) {
			if (entry.getValue() != null) {
				e.addElement(entry.getKey()).addText(entry.getValue().toString());
			} else {
				e.addElement(entry.getKey());
			}
		}
	}

	/**
	 * 完整的dom to map方法，处理节点的子节点、属性、值
	 * @param element 如果是document，可以送rootElement
	 * @return
     */
	public static Map domToMap(Element element){
		//自身元素
		Map<String, Map> elementMap = new HashMap<String, Map>();
		//自身元素属性
		Map<String, String> attribute = new HashMap<String, String>();
		//子元素集合
		Map<String, List> childrenElement = new HashMap<String, List>();
		/**  处理逻辑本体  **/
		//子节点处理
		if(element.elements().size() > 0){
			for(Iterator<Element> i = element.elementIterator();i.hasNext();){
				Element e = i.next();
				Map<String, Map> ele = domToMap(e);
				String eleName = ele.keySet().iterator().next();
				if(childrenElement.containsKey(eleName)){
					childrenElement.get(eleName).add(ele.get(eleName));
				}else{
					List tmpList = new ArrayList();
					tmpList.add(ele.get(eleName));
					childrenElement.put(eleName, tmpList);
				}
			}
		}
		//属性处理
		if(element.attributes().size() > 0){
			for(Iterator<Attribute> i = element.attributeIterator(); i.hasNext();){
				Attribute a = i.next();
				attribute.put(a.getName(), a.getValue());
			}
		}
		//合体
		Map tmpElementMap = new HashMap();
		tmpElementMap.put("_attr_", attribute);//属性
		tmpElementMap.put("_text_", element.getTextTrim());//值
		tmpElementMap.putAll(childrenElement);//子节点
		elementMap.put(element.getName(), tmpElementMap);
		return elementMap;
	}

}
