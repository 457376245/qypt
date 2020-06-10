package com.web.util;

import com.web.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @title redis操作工具类
 * @author chenly
 *
 */

@Component("RedisUtil")
public class RedisUtil {
	
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<String, Object> jedisTemplate;
	
	/**
	 * 放数据到redis
	 * 
	 * @param key
	 *            标识 内容
	 * @param time
	 *            超时时间
	 */
	public void addRedisInfo(String key, Object content, int time) {
		jedisTemplate.delete(key);// 删除数据
		jedisTemplate.opsForValue().set(key, content);// 存放数据
		jedisTemplate.expire(key, time, TimeUnit.MINUTES);// 20分钟不使用就超时
	}
	
	/**
	 * 存储数据导redis不设置超时时间
	 * @param key
	 * @param content
	 */
	public void addRedisForKey(String key, Object content) {
		jedisTemplate.delete(key);// 删除数据
		jedisTemplate.opsForValue().set(key, content);// 存放数据
	}
	
	/**
	 * 获取缓存数据
	 * @param key
	 * @return
	 */
	public Object getRedisForKey(String key) {
		return jedisTemplate.opsForValue().get(key);
	}
	
	/**
	 * 获取key，并重置缓存时间
	 * @param key
	 * @param time
	 * @return
	 */
	public Object getRedisForKey(String key,int time) {
		jedisTemplate.expire(key, time, TimeUnit.MINUTES);// 20分钟不使用就超时
		return jedisTemplate.opsForValue().get(key);
	}
	
	/**
	 * 删除缓存数据
	 * @param key
	 */
	public void delRedisForKey(String key) {
		jedisTemplate.delete(key);// 删除数据
	}
	
	/**
	 * 批量删除可以删除的redis数据
	 */
	public void batchDelRedisForKey() {
		Set<String> keys = jedisTemplate.keys(Constants.REDIS_DEL_KEY + "*");
		jedisTemplate.delete(keys);
	}
	//或者自增的数组
	/**
	 * key
	 * @param key
	 * @param liveTime 保存服务器时长(秒)
	 * @param lenght 返回的长度 最长不能好过10个
	 * @return
	 */
	public String incrAtomic(String key, long liveTime,int lenght,TimeUnit timeUnit) {
		if(key == null  || lenght > 10 || "".equals(key.trim())) {
			return null;
		}
		
		String maxValueStr = "";
		
		for(int i = 0 ; i < lenght;i++) {
			maxValueStr+="9";
		}
		long maxValue = Long.parseLong(maxValueStr);
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, jedisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.incrementAndGet();
       
        if (((null == increment || increment.longValue() == 1) && liveTime > 0) ) {//初始设置过期时间
        	 entityIdCounter.expire(liveTime, timeUnit);
        }
        if(maxValue <= increment ) {
        	jedisTemplate.delete(key);// 删除数据
        }
        String s = String.format("%0"+lenght+"d", increment);
        return s;
    }
	
	
	/**
	 * key
	 * @param key
	 * @param lenght 毫秒
	 * @param lenght 返回的长度 最长不能好过10个
	 * @return
	 */
	public String incr(String key, int lenght,long time) {
		
		if(key == null  || lenght > 10 || "".equals(key.trim())) {
			return null;
		}
		
		String maxValueStr = "";
		
		for(int i = 0 ; i < lenght;i++) {
			maxValueStr+="9";
		}
		long maxValue = Long.parseLong(maxValueStr);
//        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, jedisTemplate.getConnectionFactory());
        Long increment = jedisTemplate.opsForValue().increment(key, 1L);
        if (((null == increment || increment.longValue() == 1) && time > 0) ) {//初始设置过期时间
        	jedisTemplate.expire(key, time, TimeUnit.MINUTES);// 20分钟不使用就超时
       }
       if(maxValue <= increment ) {
       	jedisTemplate.delete(key);// 删除数据
       }
        
       
        String s = String.format("%0"+lenght+"d", increment);
        return s;
    }
	
	
	
	
	
}
