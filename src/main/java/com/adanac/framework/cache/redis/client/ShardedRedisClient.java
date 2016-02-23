package com.adanac.framework.cache.redis.client;

import java.util.Map;

import redis.clients.jedis.JedisCommands;

public interface ShardedRedisClient extends JedisCommands {

	/**
	 * 设置key
	 * @param keyValues
	 * @return
	 */
	public String mset(Map<String, String> keyValues);

	/**
	 * 
	 * @param keyValues
	 * @return
	 */
	public Long msetnx(Map<String, String> keyValues);

	/**
	 * 刷新到数据库
	 * @return
	 */
	public String flushDB();
}