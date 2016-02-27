package com.adanac.framework.cache.redis.client;

import redis.clients.jedis.JedisCommands;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public interface JedisClusterClient extends JedisCommands {

	public void destroy();

	public <T> T execute(JedisClusterAction<T> action);
}
