package com.adanac.framework.cache.redis.aspect;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adanac.framework.cache.redis.client.impl.MyShardedBinaryClient;

/**
 * redis缓存 byte格式保存
 * @author adanac
 * @version 1.0
 */
public class RedisShardedBinaryCache implements Cache {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisShardedBinaryCache.class);

	private MyShardedBinaryClient redisClient;

	public RedisShardedBinaryCache(MyShardedBinaryClient redisClient) {

		this.redisClient = redisClient;
	}

	@Override
	public void set(String cacheKey, Object cacheValue) throws Exception {
		set(cacheKey, -1, cacheValue);

	}

	@Override
	public void set(String cacheKey, int timeout, Object cacheValue) throws Exception {
		try {
			if (timeout <= 0) {

				redisClient.set((Serializable) cacheKey, (Serializable) cacheValue);
			} else {
				redisClient.setex((Serializable) cacheKey, timeout, (Serializable) cacheValue);
			}
		} catch (Exception e) {
			LOGGER.error("set cache error", e);
			throw e;
		}
	}

	@Override
	public String get(String cacheKey) throws Exception {
		throw new UnsupportedOperationException("不支持");
	}

	@Override
	public Object get(String cacheKey, Class<?>... type) throws Exception {
		return redisClient.get((Serializable) cacheKey);
	}

	@Override
	public long remove(String... cacheKeys) throws Exception {
		long count = 0;
		try {
			for (String cacheKey : cacheKeys) {
				count += redisClient.del((Serializable) cacheKey);
			}
		} catch (Exception e) {
			LOGGER.error("remove cache error", e);
			throw e;
		}
		return count;
	}

	@Override
	public boolean expire(String cacheKey, int seconds) throws Exception {
		long success = 0;
		try {
			success = redisClient.expire((Serializable) cacheKey, seconds);
		} catch (Exception e) {
			LOGGER.error("expire cache error", e);
			throw e;
		}
		return success == 1 ? true : false;
	}

	@Override
	public boolean exists(String cacheKey) throws Exception {
		boolean isExist = false;
		try {
			isExist = redisClient.exists((Serializable) cacheKey);
		} catch (Exception e) {
			LOGGER.error("exists cache error", e);
			throw e;
		}
		return isExist;
	}

	@Override
	public String keyType(String key) throws Exception {
		throw new UnsupportedOperationException("不支持");
	}
}
