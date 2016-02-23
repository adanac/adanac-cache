package com.adanac.framework.cache.redis.client;

import redis.clients.jedis.ShardedJedis;

/**
 * @author adanac
 * @version 1.0
 */
public interface ShardedJedisAction<T> {

	public T doAction(ShardedJedis shardedJedis);

}
