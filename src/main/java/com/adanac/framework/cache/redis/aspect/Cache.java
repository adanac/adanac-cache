package com.adanac.framework.cache.redis.aspect;

/**
 * 缓存
 * @author adanac
 * @version 1.0
 */
public interface Cache {
	/**
	 * 添加缓存
	 * @param cacheKey
	 * @param cacheValue
	 * @throws Exception
	 */
	void set(String cacheKey, Object cacheValue) throws Exception;

	/**
	 * 添加缓存且指定超时时间
	 * @param cacheKey
	 * @param timeout
	 * @param cacheValue
	 * @throws Exception
	 */
	void set(String cacheKey, int timeout, Object cacheValue) throws Exception;

	/**
	 * 取出缓存
	 * @param cacheKey
	 * @return
	 * @throws Exception
	 */
	String get(String cacheKey) throws Exception;

	/**
	 * 取出缓存指定返回对象类型
	 * @param cacheKey
	 * @param type
	 * @return
	 * @throws Exception
	 */
	Object get(String cacheKey, Class<?>... type) throws Exception;

	/**
	 * 删除缓存
	 * @param cacheKeys 不定参数，可以传入多个String对象
	 * @return
	 * @throws Exception
	 */
	long remove(String... cacheKeys) throws Exception;

	/**
	 * 设置缓存超时时间
	 * @param cacheKey
	 * @param seconds
	 * @return
	 * @throws Exception
	 */
	boolean expire(String cacheKey, int seconds) throws Exception;

	/**
	 * 缓存是否存在
	 * @param cacheKey
	 * @return
	 * @throws Exception
	 */
	boolean exists(String cacheKey) throws Exception;

	/**
	 * 缓存类型
	 * @param key
	 * @return
	 * @throws Exception
	 */
	String keyType(String key) throws Exception;
}
