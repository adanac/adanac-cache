package com.adanac.framework.cache.redis.annotations;

/**
 * 缓存操作执行时机
 * @author adanac
 * @version 1.0
 */
public enum CacheExecuteOrder {
	// 之前
	cacheFirst,
	// 之后
	cacheLast
}
