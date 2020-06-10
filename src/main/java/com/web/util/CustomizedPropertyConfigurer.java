package com.web.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * @titel  用于加载spring加载的properties文件内容
 * @author chenly
 *
 */
public class CustomizedPropertyConfigurer extends PropertyPlaceholderConfigurer{
	
	private static Map<String, Object> ctxPropertiesMap;  

	@Override  
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,Properties props)throws BeansException {  
  
        super.processProperties(beanFactory, props);  
        //load properties to ctxPropertiesMap  
        ctxPropertiesMap = new HashMap<String, Object>();  
        for (Object key : props.keySet()) {
            String keyStr = key.toString();  
            String value = props.getProperty(keyStr);  
            ctxPropertiesMap.put(keyStr, value);
        } 
    }
    
    public static Map<String, Object> getCtxPropertiesMap() {
    	return ctxPropertiesMap;
    }
    
  //static method for accessing context properties  
    public static Object getContextProperty(String name) {  
        return ctxPropertiesMap.get(name);  
    }  
}
