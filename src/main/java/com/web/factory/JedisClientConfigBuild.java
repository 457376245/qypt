package com.web.factory;

import com.web.util.StringUtil;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * redis构造连接
 *
 * @author
 * @create 2020-03-12
 **/
public class JedisClientConfigBuild {
    private final String backend;
    private final String url;
    private final JedisPoolConfig jedisPoolConfig;
    private final String password;

    public JedisClientConfigBuild(String backend, String url, String password, JedisPoolConfig jedisPoolConfig) {
        this.backend = backend;
        this.url = url;
        this.jedisPoolConfig = jedisPoolConfig;
        this.password = password;
    }

    public RedisConnectionFactory build()throws Exception{
        try {//单机
            if ("redis".equals(backend)) {
                //url=136.64.70.65:16385/0
                String hostName = getHostName(url);
                String port = getPort(url);
                String indexDb = getIndexDb(url);

                //单机版jedis
                RedisStandaloneConfiguration redisStandaloneConfiguration =
                        new RedisStandaloneConfiguration();
                //设置redis服务器的host或者ip地址
                redisStandaloneConfiguration.setHostName(hostName);
                //设置默认使用的数据库
                if(!StringUtil.isEmptyStr(indexDb)){
                    redisStandaloneConfiguration.setDatabase(Integer.parseInt(indexDb));
                }
                //设置密码
                redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
                //设置redis的服务的端口号
                redisStandaloneConfiguration.setPort(Integer.parseInt(port));
                //获得默认的连接池构造器
                JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpcb =
                        (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
                //指定jedisPoolConifig来修改默认的连接池构造器
                jpcb.poolConfig(jedisPoolConfig);
                //通过构造器来构造jedis客户端配置
                JedisClientConfiguration jedisClientConfiguration = jpcb.build();
                //单机配置 + 客户端配置 = jedis连接工厂
                return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
            } else if("rediscluster".equals(backend)){//集群
                //url=136.64.70.65:16385,136.64.70.65:16385
                RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
                Set<RedisNode> jedisClusterNodes = new HashSet<RedisNode>();
                String[] add = url.split(",");
                for (String temp : add) {
                    jedisClusterNodes.add(new RedisNode(getHostName(temp), Integer.parseInt(getPort(temp))));
                }
                redisClusterConfiguration.setClusterNodes(jedisClusterNodes);
                redisClusterConfiguration.setPassword(RedisPassword.of(password));
                return new JedisConnectionFactory(redisClusterConfiguration,jedisPoolConfig);
            }else{
                throw new Exception("redis配置有误,连接失败请检查");
            }
        }catch (Exception e){
            throw new Exception("redis配置有误,连接失败请检查");
        }
    }
    private String getHostName(String url){
        return url.split(":")[0];
    }
    private String getIndexDb(String url){
        String[] ss=url.split(":")[1].split("/");
        if(ss.length>1) {
            return url.split(":")[1].split("/")[1];
        }else{
            return "";
        }
    }
    private String getPort(String url){
        return url.split(":")[1].split("/")[0];
    }
}
