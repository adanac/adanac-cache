package com.adanac.framework.cache.redis.aspect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adanac.framework.cache.redis.client.impl.MyJedisClusterClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * RedisCluster cache
 * @author adanac
 * @version 1.0
 */
public class RedisClusterCache implements Cache {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterCache.class);

	private MyJedisClusterClient redisClient;

	public RedisClusterCache(MyJedisClusterClient redisClient) {

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

				redisClient.getJedisCluster().set(cacheKey, JSON.toJSONString(cacheValue));
			} else {
				redisClient.getJedisCluster().setex(cacheKey, timeout, JSON.toJSONString(cacheValue));
			}
		} catch (Exception e) {
			LOGGER.error("set cache error", e);
			throw e;

		}

	}

	@Override
	public String get(String cacheKey) throws Exception {
		String value = null;

		try {
			value = redisClient.getJedisCluster().get(cacheKey);
		} catch (Exception e) {
			LOGGER.error("get cache error", e);
			throw e;
		}
		return value;
	}

	@Override
	public Object get(String cacheKey, Class<?>... type) throws Exception {

		if (type.length == 1) {
			return JSON.parseObject(get(cacheKey), type[0]);
		} else {
			if (type[0].isAssignableFrom(List.class)) {

				return JSON.parseArray(get(cacheKey), type[1]);
			} else if (type[0].isAssignableFrom(Set.class)) {

				Set jsonResult = (Set) JSON.parseObject(get(cacheKey), type[0]);
				Iterator<Object> iterator = jsonResult.iterator();

				Set result = new HashSet();
				while (iterator.hasNext()) {
					JSONObject obj = (JSONObject) iterator.next();

					Object o = JSON.toJavaObject(obj, type[1]);

					result.add(o);

				}
				return result;
			}

			else {
				return JSON.parseObject(get(cacheKey), type[0]);
			}

		}
	}

	@Override
	public long remove(String... cacheKeys) throws Exception {
		long count = 0;
		try {

			for (String cacheKey : cacheKeys) {
				count += redisClient.getJedisCluster().del(cacheKey);
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

			success = redisClient.getJedisCluster().expire(cacheKey, seconds);
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

			isExist = redisClient.getJedisCluster().exists(cacheKey);
		} catch (Exception e) {
			LOGGER.error("exists cache error", e);
			throw e;
		}
		return isExist;
	}

	@Override
	public String keyType(String key) throws Exception {
		String keyType = null;
		try {

			keyType = redisClient.getJedisCluster().type(key);
		} catch (Exception e) {
			LOGGER.error("keyType cache error", e);
			throw e;
		}
		return keyType;
	}
}
