package com.web.util;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 *
 * @author
 * @create 2020-03-12
 **/
public class RedisLock {
    // 锁的有效时间(s)
    private static final int EXPIRE = 300;//设置300秒，防止业务执行时间过长锁失效被抢后释放了其它进程的锁
    /**获取锁超时时间设置*/
    public static final Long LOCK_TIME_OUT = new Long(1L);
    // 存储到redis中的锁标志
    private static final String LOCKED = "LOCKED";
    private static final Logger LOG = LoggerFactory.getLogger(RedisLock.class);
    private static RedisTemplate<String, Object> cache = (RedisTemplate<String, Object>)SpringContextUtil.getBean("redisTemplate");
    /**
     * 根据lockName加锁,获取不到锁等待，用于控制多个动作并发
     * @param lockName
     * @return
     */
    public static boolean lockAndSleep(String lockName){
        boolean flag =false;
        long end = System.currentTimeMillis() + LOCK_TIME_OUT * 1000L;
        while(System.currentTimeMillis() < end) {
            if (!lock(lockName)) {
                try {
                    Thread.sleep(400L);
                } catch (InterruptedException var12) {
                    var12.printStackTrace();
                }
            }else{
                LOG.debug("订单加锁时间:" + DateUtil.format(new Date()) + ",订单加锁成功,lockName:{}" , lockName);
                flag =true;
                break;
            }
        }
        return flag;
    }
    /**
     * 单次锁
     * @param lockName
     * @return
     */
    public static boolean lockOne(String lockName){
        boolean flag =false;
        if (!lock(lockName)) {
        }else{
            LOG.debug("订单加锁时间:" + DateUtil.format(new Date()) + ",订单加锁成功,lockName:{}" , lockName);
            flag =true;
        }
        return flag;
    }

    /**
     *
     * @param lockKey
     * @return
     */
    private static boolean lock(String lockKey){
        return (Boolean) cache.execute((RedisCallback) connection -> {
            if (connection.setNX(lockKey.getBytes(), LOCKED.getBytes())) {
                //设置锁的有效期，也是锁的自动释放时间，也是一个客户端在其他客户端能抢占锁之前可以执行任务的时间
                //可以防止因异常情况无法释放锁而造成死锁情况的发生
                cache.expire(lockKey, EXPIRE, TimeUnit.SECONDS);
                return true;
            }
            //查看锁的有效期
            long lock_timeout = connection.ttl(lockKey.getBytes());
            if (lock_timeout < 0L) {
                /*
                 * 如果锁没有租期，说明上次锁操作出问题，给补上
                 */
                cache.expire(lockKey, EXPIRE / 2, TimeUnit.SECONDS);
                LOG.debug("重新设置锁[{}]超时时间", lockKey);
            }
            LOG.error("抢锁[{}]失败!", lockKey);
            return false;
        });
    }
    /**
     * 释放锁
     * @param lockName
     * @return
     */
    public static boolean unlock(String lockName) {
        LOG.error("解锁[{}]成功!", lockName);
        return cache.delete(lockName);
    }
}
