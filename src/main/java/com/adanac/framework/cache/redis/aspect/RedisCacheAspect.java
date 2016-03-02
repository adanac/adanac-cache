package com.adanac.framework.cache.redis.aspect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.adanac.framework.cache.redis.annotations.CacheOperate;
import com.adanac.framework.cache.redis.annotations.MyCacheable;

/**
 * 
 * @author adanac
 * @version 1.0
 */
@Aspect
public class RedisCacheAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheAspect.class);

	// 缓存管理器
	private Cache cache;

	private SpelExpressionParser expressionParser = new SpelExpressionParser();

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	// 声明环绕通知
	@Around("@annotation(com.bn.framework.cache.redis.annotations.MyCacheable)")
	public Object doAroundCache(ProceedingJoinPoint joinPoint) throws Throwable {

		return executeJoinPoint(joinPoint);
	}

	private Object executeJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

		Signature signature = joinPoint.getSignature();// 方法签名

		MyCacheable methodCacheable = getAnnotation(signature);// 缓存注解参数

		// 查询操作 不区分缓存操作时机
		if (methodCacheable.beforecmd() == CacheOperate.QUERY) {
			return queryCache(methodCacheable, joinPoint);
		}

		// 增加操作 先增加到数据库 再增加到缓存
		if (methodCacheable.beforecmd() == CacheOperate.SAVE) {
			Object joinPointProceedResult = joinPoint.proceed();
			cacheOperate(methodCacheable, joinPoint);
			return joinPointProceedResult;
		}

		// 删除操作 先删除缓存 再删除数据库

		if (methodCacheable.beforecmd() == CacheOperate.DELETE) {
			cacheOperate(methodCacheable, joinPoint);
			Object joinPointProceedResult = joinPoint.proceed();
			return joinPointProceedResult;
		}

		/*
		 * //缓存操作执行时机 之后 if(methodCacheable.order() ==
		 * CacheExecuteOrder.cacheLast){ Object joinPointProceedResult =
		 * joinPoint.proceed(); cacheOperate(methodCacheable, joinPoint); return
		 * joinPointProceedResult; }
		 * 
		 * 
		 * //缓存操作执行时机 之前 if(methodCacheable.order() ==
		 * CacheExecuteOrder.cacheFirst){ cacheOperate(methodCacheable,
		 * joinPoint); Object joinPointProceedResult = joinPoint.proceed();
		 * return joinPointProceedResult; }
		 */

		return null;
	}

	private void cacheOperate(MyCacheable methodCacheable, ProceedingJoinPoint joinPoint) throws Throwable {
		// 缓存操作
		switch (methodCacheable.beforecmd()) {

		case DELETE:// 删除缓存
			delCache(methodCacheable, joinPoint);
			break;

		case SAVE:// 保存缓存
			addCache(methodCacheable, joinPoint);
			break;
		default:
			break;
		}
	}

	/**
	 * 查询缓存 (先到缓存中查找，如果缓存中有则直接返回 ，如果没有进行查询，并将查询结果放到缓存中)
	 * 
	 * @param methodCacheable
	 * @param joinPoint
	 * @return
	 * @throws Throwable 
	 */
	private Object queryCache(MyCacheable methodCacheable, ProceedingJoinPoint joinPoint) throws Throwable {
		Object object = getCache(methodCacheable, joinPoint);
		if (object != null) {
			return object;
		} else {

			Object proceedResult = joinPoint.proceed();

			if (null != proceedResult) {
				// 加入缓存
				addCache(proceedResult, methodCacheable, joinPoint);
			}

			return proceedResult;

		}

	}

	/**
	 * 结果加入到缓存
	 * 
	 * @param proceedResult
	 * @param methodCacheable
	 * @param joinPoint
	 * @throws Exception 
	 */
	private void addCache(Object proceedResult, MyCacheable methodCacheable, ProceedingJoinPoint joinPoint)
			throws Exception {

		String cacheKey = getCacaheKey(methodCacheable, joinPoint);
		if (!cacheKey.equals("")) {

			cache.set(cacheKey, methodCacheable.timeout(), proceedResult);

		}

	}

	/**
	 * 获取缓存
	 * 
	 * @param methodCacheable
	 * @param joinPoint
	 * @return
	 */
	private Object getCache(MyCacheable methodCacheable, ProceedingJoinPoint joinPoint) {
		try {
			String cacheKey = getCacaheKey(methodCacheable, joinPoint);
			if (!cacheKey.equals("")) {

				return cache.get(cacheKey, getReturnObjType(joinPoint));

			}
		} catch (Exception e) {
			LOGGER.error("获取缓存错误", e);
		}
		return null;
	}

	/**
	 * 方法返回值类型
	 * 
	 * @param joinPoint
	 * @return
	 */
	private Class<?>[] getReturnObjType(ProceedingJoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method targetMethod = methodSignature.getMethod();

		Class<?> type = targetMethod.getReturnType();
		if (type.isAssignableFrom(List.class) || type.isAssignableFrom(Set.class)) {
			// 泛型参数返回类型 (List<User> Set<User>)
			MyCacheable cacheable = getAnnotation(signature);
			String returnType = cacheable.returnType();

			try {
				Class<?> returnTypeClass = Class.forName(returnType);
				return new Class<?>[] { type, returnTypeClass };
			} catch (ClassNotFoundException e) {

				LOGGER.error("getReturnObjType error", e);
			}
			return new Class<?>[] { type };
		} else {
			return new Class<?>[] { type };
		}

	}

	/**
	 * 保存缓存 (只支持方法参数是一个的)
	 * 
	 * @param methodCacheable
	 * @param joinPoint
	 * @throws Exception 
	 */
	private void addCache(MyCacheable methodCacheable, ProceedingJoinPoint joinPoint) throws Exception {

		String cacheKey = getCacaheKey(methodCacheable, joinPoint);
		if (!cacheKey.equals("")) {

			Object[] args = joinPoint.getArgs();// 方法参数
			// 保存第一个方法参数作为缓存值
			if (null != args) {
				cache.set(cacheKey, methodCacheable.timeout(), args[0]);
			}

		}

	}

	/**
	 * 删除缓存
	 * 
	 * @param methodCacheable
	 * @param joinPoint
	 * @throws Exception 
	 */
	private void delCache(MyCacheable methodCacheable, ProceedingJoinPoint joinPoint) throws Exception {

		String cacheKey = getCacaheKey(methodCacheable, joinPoint);
		if (!cacheKey.equals("")) {
			cache.remove(cacheKey);
		}

	}

	/**
	 * 获取缓存key值
	 * 
	 * @param methodCacheable
	 * @param joinPoint
	 * @return
	 */
	private String getCacaheKey(MyCacheable methodCacheable, ProceedingJoinPoint joinPoint) {

		String key = methodCacheable.key();

		if (key.contains("#")) {
			Object[] args = joinPoint.getArgs();// 方法参数

			Signature signature = joinPoint.getSignature();// 方法签名
			MethodSignature methodSignature = (MethodSignature) signature;
			String[] parameterNames = methodSignature.getParameterNames();

			StandardEvaluationContext context = new StandardEvaluationContext();

			for (int i = 0; i < parameterNames.length; i++) {
				context.setVariable(parameterNames[i], args[i]);
			}

			// #user.id#user.name
			// userCache#user.id#user.name
			// 缓存key
			StringBuilder cacheKeyBuilder = new StringBuilder();

			if (!key.startsWith("#")) {
				// 获取常量字符串
				int firstCharMark = key.indexOf("#");
				String commonChar = key.substring(0, firstCharMark);
				key = key.substring(firstCharMark + 1);
				cacheKeyBuilder.append(commonChar);
				cacheKeyBuilder.append(":");
			} else {
				key = key.substring(key.indexOf("#") + 1);
			}

			String[] keys = key.split("#");

			for (String keyStr : keys) {
				Object object = expressionParser.parseExpression("#" + keyStr).getValue(context);
				cacheKeyBuilder.append(object.toString());
				cacheKeyBuilder.append(":");
			}

			String cacheKey = cacheKeyBuilder.toString();

			return cacheKey.substring(0, cacheKey.length() - 1);

		} else {
			return key;
		}
	}

	/**
	 * 获取方法注解参数
	 * 
	 * @param signature
	 * @return
	 */
	private MyCacheable getAnnotation(Signature signature) {
		MethodSignature methodSignature = (MethodSignature) signature;
		Method targetMethod = methodSignature.getMethod();
		return targetMethod.getAnnotation(MyCacheable.class);
	}
}
