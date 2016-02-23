package com.adanac.framework.cache.redis.client;

import java.util.List;

import redis.clients.jedis.ShardedJedisPipeline;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public interface ShardedJedisPipelineAction<T> {

	public List<T> doAction(ShardedJedisPipeline pipeline, Object inParam);

}