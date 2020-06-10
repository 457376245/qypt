package com.web.util.inter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.web.util.client.RightsGrantClient;



/**
 * 启动线程工具类
 * @author oc
 *
 */
public class StartThread {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("com.web.util.client.RightsGrantClient")
	private RightsGrantClient rightsGrantClient;
	
	@PostConstruct 
	public void postConstruct(){  
	    log.info("调用postConstruct启动权益领取线程-开始");  
	    rightsGrantClient.start();
	    log.info("调用postConstruct启动权益领取线程-结束");  
	} 

}
