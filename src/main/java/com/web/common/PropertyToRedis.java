package com.web.common;

import com.web.bmo.CommonBmo;
import com.web.util.CustomizedPropertyConfigurer;
import com.web.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Service("com.web.common.PropertyToRedis")
public class PropertyToRedis implements ApplicationListener<ContextRefreshedEvent>{

	private static Logger log = LoggerFactory.getLogger(PropertyToRedis.class.getName());

//	@Autowired
//	@Qualifier("redisTemplate")
//	private RedisTemplate<String, Object> jedisTemplate;

	@Resource
	private RedisUtil redisUtil;

	@Autowired
	@Qualifier("com.web.bmo.CommonBmoImpl")
	private CommonBmo commonBmo;

	@Value("${properties.path}")
	private String source;

	@SuppressWarnings("unchecked")
	public Object getPropertyValue(String name){
		Map<String, Object> ctxPropertiesMap = (Map<String, Object>) redisUtil.getRedisForKey("ctxPropertiesConfigMap");
//		log.info("重新刷新目录"+"配置信息");
		Object propertiesValue = null;
		if (ctxPropertiesMap != null) {
			propertiesValue = ctxPropertiesMap.get(name);
		}
		if( ctxPropertiesMap==null  || "".equals(name) || propertiesValue == null){
			ctxPropertiesMap = new HashMap<String,Object>();
			ResourceBundle.clearCache();
			ResourceBundle redis_prb = ResourceBundle.getBundle(source + "/spring/redis");
			for (Object key : redis_prb.keySet()) {
				String keyStr = key.toString();
				String value = redis_prb.getString(keyStr);
				ctxPropertiesMap.put(keyStr, value);
			}
			ResourceBundle spring_prb = ResourceBundle.getBundle(source + "/spring/spring");
			for (Object key : spring_prb.keySet()) {
				String keyStr = key.toString();
				String value = spring_prb.getString(keyStr);
				ctxPropertiesMap.put(keyStr, value);
			}
			ResourceBundle oc_prb = ResourceBundle.getBundle(source + "/oc-ca");
			for (Object key : oc_prb.keySet()) {
				String keyStr = key.toString();
				String value = oc_prb.getString(keyStr);
				ctxPropertiesMap.put(keyStr, value);
			}
//    		ResourceBundle csb_prb = ResourceBundle.getBundle("csbinfo");
//    		for (Object key : csb_prb.keySet()) {
//                String keyStr = key.toString();
//                String value = csb_prb.getString(keyStr);
//                ctxPropertiesMap.put(keyStr, value);
//            }
			log.info(source + "刷新后配置信息================="+ctxPropertiesMap);
			redisUtil.delRedisForKey("ctxPropertiesMap");
			try {
				commonBmo.setDelRedisKey("ctxPropertiesConfigMap");
			}catch (Exception e) {
				e.printStackTrace();
			}
			redisUtil.addRedisForKey("ctxPropertiesConfigMap",ctxPropertiesMap);
		}
		return ctxPropertiesMap.get(name);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		redisUtil.delRedisForKey("ctxPropertiesMap");//删除数据
		Map<String, Object> map= CustomizedPropertyConfigurer.getCtxPropertiesMap();
		if(map!=null&&map.size()>0){
			try {
				commonBmo.setDelRedisKey("ctxPropertiesConfigMap");
			}catch (Exception e) {
				e.printStackTrace();
			}
			redisUtil.addRedisForKey("ctxPropertiesConfigMap",CustomizedPropertyConfigurer.getCtxPropertiesMap());
		}
	}
}
