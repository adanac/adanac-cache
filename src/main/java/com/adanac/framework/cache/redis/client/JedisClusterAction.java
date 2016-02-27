package com.adanac.framework.cache.redis.client;

import com.adanac.framework.cache.redis.client.impl.MyJedisCluster;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public interface JedisClusterAction<T> {
	public T doAction(MyJedisCluster jedisCluster);
}
