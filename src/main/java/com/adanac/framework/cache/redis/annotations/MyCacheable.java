package com.adanac.framework.cache.redis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binary类型缓存操作接口定义
 * @author adanac
 * @version 1.0
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MyCacheable {
	/**
	 * 缓存key 如(allUser   allUser#user.id     #user.id)
	 * @return
	 */
	String key();

	/**
	 * 缓存操作  QUERY 查询, SAVE 保存, DELETE 删除
	 * @return
	 */
	CacheOperate beforecmd();

	/**
	 * 原生redis数据类型 STRING,MAP,LIST,SET
	 * @return
	 */
	RedisDataType dataType() default RedisDataType.STRING;

	String returnType() default "java.lang.Object";

	/**
	 * 
	 * 缓存执行时机
	 */
	CacheExecuteOrder order() default CacheExecuteOrder.cacheLast;

	/**
	 * 
	 * 缓存超时 秒
	 */
	int timeout() default -1;

}
