package com.adanac.framework.cache.redis.client.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adanac.framework.cache.redis.client.ShardedBinaryClient;
import com.adanac.framework.cache.redis.client.ShardedJedisAction;
import com.adanac.framework.cache.redis.client.ShardedJedisPipelineAction;
import com.adanac.framework.cache.redis.exception.RedisClientException;
import com.adanac.framework.cache.redis.util.Serializer;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public class MyShardedBinaryClient extends AbstractShardedClient implements ShardedBinaryClient, ShardedJedisAction {
	private static final Logger logger = LoggerFactory.getLogger(MyShardedBinaryClient.class);

	private boolean isSharding;

	private String shardName;

	public MyShardedBinaryClient() {
		super();

	}

	public MyShardedBinaryClient(String configPath) {
		super(configPath);

	}

	public MyShardedBinaryClient(String configPath, boolean globalConfig) {
		super(configPath, globalConfig);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	public ShardedJedis getShardedJedis() {
		ShardedJedisPool pool = getShardedJedisPool();
		ShardedJedis jedis = null;

		jedis = pool.getResource();
		return jedis;
	}

	public <T> T execute(ShardedJedisAction<T> action) {
		ShardedJedisPool pool = getShardedJedisPool();
		ShardedJedis jedis = null;

		try {
			jedis = pool.getResource();
			return action.doAction(jedis);
		} catch (RuntimeException e) {
			if (e instanceof JedisConnectionException) {
				if (jedis != null) {
					try {
						pool.returnBrokenResource(jedis);
					} catch (Exception ex) {
						logger.warn("Can not return broken resource.", ex);
					}
					jedis = null;
				}
			}
			throw new RedisClientException(e);
		} finally {
			if (jedis != null) {
				try {
					pool.returnResource(jedis);
				} catch (Exception ex) {
					logger.warn("Can not return resource.", ex);
				}
			}
		}
	}

	public ShardedJedisPipeline getPipeline() {
		ShardedJedisPipeline pipeline = getShardedJedis().pipelined();
		return pipeline;
	}

	/**
	 * 功能描述: <br>  PIPELINE处理
	 * 
	 */
	public <T> List<T> execute(Object inParam, ShardedJedisPipelineAction<T> action) {
		ShardedJedisPool pool = getShardedJedisPool();
		ShardedJedis jedis = null;
		ShardedJedisPipeline pipeline;
		try {
			jedis = pool.getResource();
			pipeline = jedis.pipelined();
			return action.doAction(pipeline, inParam);

		} catch (RuntimeException e) {
			if (e instanceof JedisConnectionException) {
				if (jedis != null) {
					try {
						pool.returnBrokenResource(jedis);
					} catch (Exception ex) {
						logger.warn("Can not return broken resource.", ex);
					}
					jedis = null;
				}
			}
			throw new RedisClientException(e);
		} finally {
			if (jedis != null) {
				try {
					pool.returnResource(jedis);
				} catch (Exception ex) {
					logger.warn("Can not return resource.", ex);
				}
			}
		}
	}

	public String flushDB() {
		ShardedJedisPool pool = getShardedJedisPool();
		ShardedJedis shardedJedis = null;

		try {
			shardedJedis = pool.getResource();
			Collection<Jedis> allShards = shardedJedis.getAllShards();
			final CountDownLatch endSignal = new CountDownLatch(allShards.size());
			for (final Jedis jedis : allShards) {
				// 多线程同时flushDB 提高效率
				new Thread(new Runnable() {
					@Override
					public void run() {
						jedis.flushDB();
						endSignal.countDown();
					}
				}).start();
			}
			try {
				endSignal.await();
			} catch (InterruptedException e) {
				throw new RedisClientException(e);
			}
			return "OK";
		} catch (RuntimeException e) {
			if (e instanceof JedisConnectionException) {
				if (shardedJedis != null) {
					try {
						pool.returnBrokenResource(shardedJedis);
					} catch (Exception ex) {
						throw new RedisClientException(ex);
					}
					shardedJedis = null;
				}
			}
			throw new RedisClientException(e);
		} finally {
			if (shardedJedis != null) {
				try {
					pool.returnResource(shardedJedis);
				} catch (Exception ex) {
					logger.warn("Can not return resource.", ex);
				}
			}
		}
	}

	@Override
	public String set(final Serializable key, final Serializable value) {
		return this.execute(new ShardedJedisAction<String>() {
			public String doAction(ShardedJedis jedis) {
				return jedis.set(encode(key), encode(value));
			}
		});

	}

	@Override
	public String setex(final Serializable key, final int time, final Serializable value) {
		return this.execute(new ShardedJedisAction<String>() {
			public String doAction(ShardedJedis jedis) {
				return jedis.setex(encode(key), time, encode(value));
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T get(final Serializable key) {
		return this.execute(new ShardedJedisAction<T>() {
			public T doAction(ShardedJedis jedis) {
				byte[] result = jedis.get(encode(key));
				return (T) convertType(result);
			}
		});

	}

	@Override
	public Long decr(final Serializable key) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.decr(encode(key));
			}
		});

	}

	@Override
	public Long decrBy(final Serializable key, final long integer) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.decrBy(encode(key), integer);
			}
		});

	}

	@Override
	public Long incr(final Serializable key) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.incr(encode(key));
			}
		});

	}

	@Override
	public Long incrBy(final Serializable key, final long integer) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.incrBy(encode(key), integer);
			}
		});

	}

	@Override
	public boolean exists(final Serializable key) {

		return this.execute(new ShardedJedisAction<Boolean>() {
			public Boolean doAction(ShardedJedis jedis) {
				return jedis.exists(encode(key));
			}
		});

	}

	@Override
	public Long hset(final Serializable key, final Serializable field, final Serializable value) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.hset(encode(key), encode(field), encode(value));
			}
		});

	}

	@Override
	public Long hsetnx(final Serializable key, final Serializable field, final Serializable value) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.hsetnx(encode(key), encode(field), encode(value));
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T hget(final Serializable key, final Serializable field) {
		return this.execute(new ShardedJedisAction<T>() {
			public T doAction(ShardedJedis jedis) {
				byte[] result = jedis.hget(encode(key), encode(field));
				return (T) convertType(result);
			}
		});

	}

	@Override
	public String hmset(final Serializable key, final Map<Serializable, Serializable> hash) {
		return this.execute(new ShardedJedisAction<String>() {
			public String doAction(ShardedJedis jedis) {
				Map<byte[], byte[]> values = new LinkedHashMap<byte[], byte[]>();
				Iterator<Map.Entry<Serializable, Serializable>> it = hash.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Serializable, Serializable> entry = it.next();
					values.put(encode(entry.getKey()), encode(entry.getValue()));
				}
				return jedis.hmset(encode(key), values);
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> List<T> hmget(final Serializable key, final Serializable... fields) {
		return this.execute(new ShardedJedisAction<List<T>>() {
			public List<T> doAction(ShardedJedis jedis) {
				List<T> resultList = new ArrayList<T>();
				final byte[][] paramByte = listToArray(fields);
				if (paramByte == null) {
					return null;
				}
				List<byte[]> result = jedis.hmget(encode(key), paramByte);
				for (byte[] bs : result) {
					resultList.add((T) convertType(bs));
				}
				return resultList;
			}
		});

	}

	@Override
	public Long hincrBy(final Serializable key, final Serializable field, final long value) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.hincrBy(encode(key), encode(field), value);
			}
		});

	}

	@Override
	public Boolean hexists(final Serializable key, final Serializable field) {
		return this.execute(new ShardedJedisAction<Boolean>() {
			public Boolean doAction(ShardedJedis jedis) {
				return jedis.hexists(encode(key), encode(field));
			}
		});

	}

	@Override
	public Long hlen(final Serializable key) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.hlen(encode(key));
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> List<T> hvals(final Serializable key) {
		return this.execute(new ShardedJedisAction<List<T>>() {
			public List<T> doAction(ShardedJedis jedis) {
				List<T> resultList = new ArrayList<T>();
				Collection<byte[]> results = jedis.hvals(encode(key));
				for (byte[] b : results) {
					resultList.add((T) convertType(b));
				}
				return resultList;
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Set<T> hkeys(final Serializable key) {
		return this.execute(new ShardedJedisAction<Set<T>>() {
			public Set<T> doAction(ShardedJedis jedis) {
				Set<T> resultList = new LinkedHashSet<T>();
				Collection<byte[]> results = jedis.hkeys(encode(key));
				for (byte[] b : results) {
					resultList.add((T) convertType(b));
				}
				return resultList;
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Map<T, T> hgetAll(final Serializable key) {
		return this.execute(new ShardedJedisAction<Map<T, T>>() {
			public Map<T, T> doAction(ShardedJedis jedis) {
				Map<T, T> resultMap = new LinkedHashMap<T, T>();
				Map<byte[], byte[]> results = jedis.hgetAll(encode(key));
				Iterator<Map.Entry<byte[], byte[]>> it = results.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<byte[], byte[]> entry = it.next();
					resultMap.put((T) convertType(entry.getKey()), (T) convertType(entry.getValue()));
				}
				return resultMap;
			}
		});

	}

	@Override
	public Long hdel(final Serializable key, final Serializable field) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.hdel(encode(key), encode(field));
			}
		});

	}

	@Override
	public Long hdel(final Serializable key, final Serializable... field) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(field);
				if (paramByte == null) {
					return null;
				}
				return jedis.hdel(encode(key), paramByte);
			}
		});

	}

	@Override
	public Long expire(final Serializable key, final int seconds) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.expire(encode(key), seconds);
			}
		});

	}

	@Override
	public Long expireAt(final Serializable key, final long unixTime) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.expireAt(encode(key), unixTime);
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> List<T> lrange(final Serializable key, final int startIndex, final int endIndex) {
		return this.execute(new ShardedJedisAction<List<T>>() {
			public List<T> doAction(ShardedJedis jedis) {
				List<T> resultList = new ArrayList<T>();
				List<byte[]> results = jedis.lrange(encode(key), startIndex, endIndex);
				for (byte[] b : results) {
					resultList.add((T) convertType(b));
				}
				return resultList;
			}
		});

	}

	@Override
	public Long lpush(final Serializable key, final Serializable... fields) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				long r = 0;
				for (Serializable field : fields) {
					r = jedis.lpush(encode(key), encode(field));
				}
				return r;
			}
		});

	}

	@Override
	public Long rpush(final Serializable key, final Serializable... fields) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				long r = 0;
				for (Serializable field : fields) {
					r = jedis.rpush(encode(key), encode(field));
				}
				return r;
			}
		});

	}

	@Override
	public Long llen(final Serializable key) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.llen(encode(key));
			}
		});

	}

	@Override
	public Long lrem(final Serializable key, final int count, final Serializable value) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.lrem(encode(key), count, encode(value));
			}
		});

	}

	@Override
	public Long ttl(final Serializable key) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.ttl(encode(key));
			}
		});

	}

	@Override
	public Long sadd(final Serializable key, final Serializable... members) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(members);
				if (paramByte == null) {
					return null;
				}
				return jedis.sadd(encode(key), paramByte);
			}
		});

	}

	@Override
	public Long srem(final Serializable key, final Serializable... members) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(members);
				if (paramByte == null) {
					return null;
				}
				return jedis.srem(encode(key), paramByte);
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Set<T> smembers(final Serializable key) {
		return this.execute(new ShardedJedisAction<Set<T>>() {
			public Set<T> doAction(ShardedJedis jedis) {
				Set<T> resultList = new LinkedHashSet<T>();
				Collection<byte[]> results = jedis.smembers(encode(key));
				for (byte[] b : results) {
					resultList.add((T) convertType(b));
				}
				return resultList;
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Set<T> sinter(final Serializable... keys) {
		return this.execute(new ShardedJedisAction<Set<T>>() {
			public Set<T> doAction(ShardedJedis jedis) {
				Set<T> resultSet = new LinkedHashSet<T>();
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				Collection<byte[]> results = shardedjedis.sinter(paramByte);
				for (byte[] b : results) {
					resultSet.add((T) convertType(b));
				}
				return resultSet;
			}
		});

	}

	@Override
	public Long sinterstore(final Serializable dstkey, final Serializable... keys) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.sinterstore(encode(dstkey), paramByte);
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Set<T> sunion(final Serializable... keys) {
		return this.execute(new ShardedJedisAction<Set<T>>() {
			public Set<T> doAction(ShardedJedis jedis) {
				Set<T> resultSet = new LinkedHashSet<T>();
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				Collection<byte[]> results = shardedjedis.sunion(paramByte);
				for (byte[] b : results) {
					resultSet.add((T) convertType(b));
				}
				return resultSet;
			}
		});

	}

	@Override
	public Long sunionstore(final Serializable dstkey, final Serializable... keys) {

		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.sunionstore(encode(dstkey), paramByte);
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Set<T> sdiff(final Serializable... keys) {
		return this.execute(new ShardedJedisAction<Set<T>>() {

			public Set<T> doAction(ShardedJedis jedis) {
				Set<T> resultSet = new LinkedHashSet<T>();
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				Collection<byte[]> results = shardedjedis.sdiff(paramByte);
				for (byte[] b : results) {
					resultSet.add((T) convertType(b));
				}
				return resultSet;
			}
		});

	}

	@Override
	public Long sdiffstore(final Serializable dstkey, final Serializable... keys) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.sdiffstore(encode(dstkey), paramByte);
			}
		});

	}

	@Override
	public Long zadd(final Serializable key, final double score, final Serializable member) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.zadd(encode(key), score, encode(member));
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Set<T> zrange(final Serializable key, final int start, final int end) {
		return this.execute(new ShardedJedisAction<Set<T>>() {
			public Set<T> doAction(ShardedJedis jedis) {
				Set<T> resultList = new LinkedHashSet<T>();
				Set<byte[]> results = jedis.zrange(encode(key), start, end);
				for (byte[] b : results) {
					resultList.add((T) convertType(b));
				}
				return resultList;
			}
		});

	}

	@Override
	public Long zrem(final Serializable key, final Serializable... members) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(members);
				if (paramByte == null) {
					return null;
				}
				return jedis.zrem(encode(key), paramByte);
			}
		});

	}

	@Override
	public Long zcard(final Serializable key) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.zcard(encode(key));
			}
		});

	}

	@Override
	public Long zinterstore(final Serializable dstkey, final Serializable... sets) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(sets);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.zinterstore(encode(dstkey), paramByte);
			}
		});

	}

	@Override
	public Long zinterstore(final Serializable dstkey, final ZParams params, final Serializable... sets) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(sets);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.zinterstore(encode(dstkey), params, paramByte);
			}
		});

	}

	@Override
	public Long zunionstore(final Serializable dstkey, final Serializable... sets) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(sets);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.zunionstore(encode(dstkey), paramByte);
			}
		});

	}

	@Override
	public Long zunionstore(final Serializable dstkey, final ZParams params, final Serializable... sets) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(sets);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.zunionstore(encode(dstkey), params, paramByte);
			}
		});

	}

	@Override
	public Double zscore(final Serializable key, final Serializable member) {
		return this.execute(new ShardedJedisAction<Double>() {
			public Double doAction(ShardedJedis jedis) {
				return jedis.zscore(encode(key), encode(member));
			}
		});

	}

	@Override
	public Long del(final Serializable key) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				return jedis.del(encode(key));
			}
		});

	}

	@Override
	public Long del(final Serializable... keys) {
		return this.execute(new ShardedJedisAction<Long>() {
			public Long doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				return shardedjedis.del(paramByte);
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> List<T> mget(final Serializable... keys) {
		return this.execute(new ShardedJedisAction<List<T>>() {
			public List<T> doAction(ShardedJedis jedis) {
				final byte[][] paramByte = listToArray(keys);
				if (paramByte == null) {
					return null;
				}
				List<T> resultList = new ArrayList<T>();
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				List<byte[]> result = shardedjedis.mget(listToArray(keys));
				for (byte[] b : result) {
					resultList.add((T) convertType(b));
				}
				return resultList;
			}
		});

	}

	/*
	 * @Override public String mset(final Map<Serializable, Serializable>
	 * keyValues) { return this.execute(new ShardedJedisAction<String>() {
	 * public String doAction(ShardedJedis jedis) { if (keyValues == null ||
	 * keyValues.isEmpty()) { return "OK"; } Map<byte[], byte[]> values = new
	 * HashMap<byte[], byte[]>(); Iterator<Entry<Serializable, Serializable>>
	 * iterator = keyValues.entrySet().iterator(); while (iterator.hasNext()) {
	 * Entry<Serializable, Serializable> entry = iterator.next();
	 * values.put(encode(entry.getKey()), encode(entry.getValue())); }
	 * MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
	 * return shardedjedis.mset(mapToArray(values)); } });
	 * 
	 * }
	 */

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Set<T> keys(final Serializable pattern) {
		return this.execute(new ShardedJedisAction<Set<T>>() {

			public Set<T> doAction(ShardedJedis jedis) {
				Set<T> resultList = new LinkedHashSet<T>();
				MultiKeyBinaryCommands shardedjedis = (MultiKeyBinaryCommands) jedis;
				Collection<byte[]> results = shardedjedis.keys(encode(pattern));
				for (byte[] b : results) {
					resultList.add((T) convertType(b));
				}
				return resultList;
			}
		});

	}

	private static byte[] encode(Serializable object) {
		return Serializer.encode(object);
	}

	private static Serializable convertType(byte[] bytes) {
		return Serializer.decode(bytes);
	}

	private static byte[][] listToArray(Serializable... serializables) {
		byte[][] paramByte = null;
		if (serializables != null && serializables.length > 0) {
			paramByte = new byte[serializables.length][0];
			for (int i = 0; i < serializables.length; i++) {
				paramByte[i] = encode(serializables[i]);
			}
		}
		return paramByte;
	}

	private static byte[][] mapToArray(Map<byte[], byte[]> map) {
		byte[][] paramByte = null;
		if (map != null && map.size() > 0) {
			paramByte = new byte[map.size() * 2][0];
			Iterator<Entry<byte[], byte[]>> it = map.entrySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				Entry<byte[], byte[]> entry = it.next();
				paramByte[index++] = entry.getKey();
				paramByte[index++] = entry.getValue();
			}
		}
		return paramByte;
	}

	@Override
	public String set(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean exists(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long persist(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String type(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expire(String key, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long ttl(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getbit(String key, long offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSet(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setnx(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setex(String key, int seconds, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decrBy(String key, long integer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long incrBy(String key, long integer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double incrByFloat(String key, double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long incr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long append(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String substr(String key, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hdel(String key, String... field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hlen(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> hkeys(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> hvals(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long rpush(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpush(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long llen(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ltrim(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lindex(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lset(String key, long index, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lrem(String key, long count, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sadd(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> smembers(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long srem(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String spop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> spop(String key, long count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long scard(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean sismember(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String srandmember(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> srandmember(String key, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long strlen(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zadd(String key, double score, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrem(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrevrank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcard(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double zscore(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcount(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcount(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpushx(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long rpushx(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(String arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(int timeout, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(String arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long del(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String echo(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitcount(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> sscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Tuple> zscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long pfadd(String key, String... elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long pfcount(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object doAction(ShardedJedis shardedJedis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mset(Map<Serializable, Serializable> keyValues) {
		// TODO Auto-generated method stub
		return null;
	}
}
