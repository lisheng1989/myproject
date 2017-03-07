package com.owen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owen.conf.ServerConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

	/** 日志处理 */
	private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
	
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 1024;
	
	//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 200;
	
	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static long MAX_WAIT = 10000;

	private static int TIMEOUT = 10000;
	
	//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;

	private static JedisPool jedisPool = null;


	/**
	 * 初始化Redis连接池
	 */
	static {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWait(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			if(ServerConfig.RedisPassword.equals(""))
				jedisPool = new JedisPool(config, ServerConfig.RedisAddress, ServerConfig.RedisPort, TIMEOUT);
			else
				jedisPool = new JedisPool(config, ServerConfig.RedisAddress, ServerConfig.RedisPort, TIMEOUT,ServerConfig.RedisPassword);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取Jedis实例
	 * @return
	 */
	public static Jedis getJedis() {
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 释放jedis资源
	 * @param jedis
	 */
	public static void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}
	
	public static String getKeyString(String key)
	{
		Jedis redis = getJedis();
		if(redis != null)
		{
			//logger.info("[getKeyString]:key:"+key+"value:"+redis.get(key));
			String value =  redis.hget("BetClient",key);
			returnResource(redis);
			return value;
		}else{
			logger.info("[getKeyString]:获取redis失败");
			return null;
		}
	}
	public static void setKeyString(String key,String map)
	{
		Jedis redis = getJedis();
		if(redis != null)
		{	
			//redis.setnx(key, map);
			
			redis.hset("BetClient",key,map);
			returnResource(redis);
			logger.info("[setKeyString]:key:"+key+"map:"+map);
		}else{
			logger.info("[setKeyString]:获取redis失败");

		}
	}
	
	
}
