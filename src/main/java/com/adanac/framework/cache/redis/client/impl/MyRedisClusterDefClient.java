package com.adanac.framework.cache.redis.client.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adanac.framework.cache.redis.client.JedisClusterAction;
import com.adanac.framework.cache.redis.client.JedisClusterClient;
import com.adanac.framework.cache.redis.exception.RedisClientException;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public class MyRedisClusterDefClient extends AbstractClient implements JedisClusterClient, JedisClusterAction {
	private static final Logger logger = LoggerFactory.getLogger(MyRedisClusterDefClient.class);

	public MyRedisClusterDefClient() {
		super();
	}

	public MyRedisClusterDefClient(String configPath) {
		super(configPath);
	}

	public MyRedisClusterDefClient(String configPath, boolean globalConfig) {
		super(configPath, globalConfig);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public <T> T execute(JedisClusterAction<T> action) {
		MyJedisCluster jedis = null;

		try {
			jedis = getRedisClusterPool();
			return action.doAction(jedis);
		} catch (RuntimeException e) {
			if (e instanceof JedisConnectionException) {
				if (jedis != null) {
					try {
						jedis.close();
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
					jedis.close();
				} catch (Exception ex) {
					logger.warn("Can not return resource.", ex);
				}
			}
		}
	}

	public String flushDB() {
		MyJedisCluster jedis = null;

		try {
			jedis = getRedisClusterPool();
			return jedis.flushDB();
		} catch (RuntimeException e) {
			if (e instanceof JedisConnectionException) {
				if (jedis != null) {
					try {
						jedis.close();
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
					jedis.close();
				} catch (Exception ex) {
					logger.warn("Can not return resource.", ex);
				}
			}
		}
	}

	// JedisCommands
	@Override
	public String set(final String key, final String value) {
		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.set(key, value);
			}
		});
	}

	@Override
	public String get(final String key) {
		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.get(key);
			}
		});
	}

	@Override
	public Boolean exists(final String key) {
		return this.execute(new JedisClusterAction<Boolean>() {
			public Boolean doAction(MyJedisCluster jedis) {
				return jedis.exists(key);
			}
		});
	}

	@Override
	public Long persist(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.persist(key);
			}
		});
	}

	@Override
	public String type(final String key) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.type(key);
			}
		});
	}

	@Override
	public Long expire(final String key, final int seconds) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.expire(key, seconds);
			}
		});
	}

	@Override
	public Long expireAt(final String key, final long unixTime) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.expireAt(key, unixTime);
			}
		});
	}

	@Override
	public Long ttl(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.ttl(key);
			}
		});
	}

	@Override
	public Boolean setbit(final String key, final long offset, final boolean value) {

		return this.execute(new JedisClusterAction<Boolean>() {
			public Boolean doAction(MyJedisCluster jedis) {
				return jedis.setbit(key, offset, value);
			}
		});
	}

	@Override
	public Boolean setbit(final String key, final long offset, final String value) {

		return this.execute(new JedisClusterAction<Boolean>() {
			public Boolean doAction(MyJedisCluster jedis) {
				return jedis.setbit(key, offset, value);
			}
		});
	}

	@Override
	public Boolean getbit(final String key, final long offset) {

		return this.execute(new JedisClusterAction<Boolean>() {
			public Boolean doAction(MyJedisCluster jedis) {
				return jedis.getbit(key, offset);
			}
		});
	}

	@Override
	public Long setrange(final String key, final long offset, final String value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.setrange(key, offset, value);
			}
		});
	}

	@Override
	public String getrange(final String key, final long startOffset, final long endOffset) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.getrange(key, startOffset, endOffset);
			}
		});
	}

	@Override
	public String getSet(final String key, final String value) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.getSet(key, value);
			}
		});
	}

	@Override
	public Long setnx(final String key, final String value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.setnx(key, value);
			}
		});
	}

	@Override
	public String setex(final String key, final int seconds, final String value) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.setex(key, seconds, value);
			}
		});
	}

	@Override
	public Long decrBy(final String key, final long integer) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.decrBy(key, integer);
			}
		});
	}

	@Override
	public Long decr(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.decr(key);
			}
		});
	}

	@Override
	public Long incrBy(final String key, final long integer) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.incrBy(key, integer);
			}
		});
	}

	@Override
	public Long incr(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.incr(key);
			}
		});
	}

	@Override
	public Long append(final String key, final String value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.append(key, value);
			}
		});
	}

	@Override
	public String substr(final String key, final int start, final int end) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.substr(key, start, end);
			}
		});
	}

	@Override
	public Long hset(final String key, final String field, final String value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.hset(key, field, value);
			}
		});
	}

	@Override
	public String hget(final String key, final String field) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.hget(key, field);
			}
		});
	}

	@Override
	public Long hsetnx(final String key, final String field, final String value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.hsetnx(key, field, value);
			}
		});
	}

	@Override
	public String hmset(final String key, final Map<String, String> hash) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.hmset(key, hash);
			}
		});
	}

	@Override
	public List<String> hmget(final String key, final String... fields) {

		return this.execute(new JedisClusterAction<List<String>>() {
			public List<String> doAction(MyJedisCluster jedis) {
				return jedis.hmget(key, fields);
			}
		});
	}

	@Override
	public Long hincrBy(final String key, final String field, final long value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.hincrBy(key, field, value);
			}
		});
	}

	@Override
	public Boolean hexists(final String key, final String field) {

		return this.execute(new JedisClusterAction<Boolean>() {
			public Boolean doAction(MyJedisCluster jedis) {
				return jedis.hexists(key, field);
			}
		});
	}

	@Override
	public Long hdel(final String key, final String... fields) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.hdel(key, fields);
			}
		});
	}

	@Override
	public Long hlen(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.hlen(key);
			}
		});
	}

	@Override
	public Set<String> hkeys(final String key) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.hkeys(key);
			}
		});
	}

	@Override
	public List<String> hvals(final String key) {

		return this.execute(new JedisClusterAction<List<String>>() {
			public List<String> doAction(MyJedisCluster jedis) {
				return jedis.hvals(key);
			}
		});
	}

	@Override
	public Map<String, String> hgetAll(final String key) {

		return this.execute(new JedisClusterAction<Map<String, String>>() {
			public Map<String, String> doAction(MyJedisCluster jedis) {
				return jedis.hgetAll(key);
			}
		});
	}

	@Override
	public Long rpush(final String key, final String... values) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.rpush(key, values);
			}
		});
	}

	@Override
	public Long lpush(final String key, final String... values) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.lpush(key, values);
			}
		});
	}

	@Override
	public Long llen(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.llen(key);
			}
		});
	}

	@Override
	public List<String> lrange(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<List<String>>() {
			public List<String> doAction(MyJedisCluster jedis) {
				return jedis.lrange(key, start, end);
			}
		});
	}

	@Override
	public String ltrim(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.ltrim(key, start, end);
			}
		});
	}

	@Override
	public String lindex(final String key, final long index) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.lindex(key, index);
			}
		});
	}

	@Override
	public String lset(final String key, final long index, final String value) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.lset(key, index, value);
			}
		});
	}

	@Override
	public Long lrem(final String key, final long count, final String value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.lrem(key, count, value);
			}
		});
	}

	@Override
	public String lpop(final String key) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.lpop(key);
			}
		});
	}

	@Override
	public String rpop(final String key) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.rpop(key);
			}
		});
	}

	@Override
	public Long sadd(final String key, final String... members) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.sadd(key, members);
			}
		});
	}

	@Override
	public Set<String> smembers(final String key) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.smembers(key);
			}
		});
	}

	@Override
	public Long srem(final String key, final String... members) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.srem(key, members);
			}
		});
	}

	@Override
	public String spop(final String key) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.spop(key);
			}
		});
	}

	@Override
	public Long scard(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.scard(key);
			}
		});
	}

	@Override
	public Boolean sismember(final String key, final String member) {

		return this.execute(new JedisClusterAction<Boolean>() {
			public Boolean doAction(MyJedisCluster jedis) {
				return jedis.sismember(key, member);
			}
		});
	}

	@Override
	public String srandmember(final String key) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.srandmember(key);
			}
		});
	}

	@Override
	public Long strlen(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.strlen(key);
			}
		});
	}

	@Override
	public Long zadd(final String key, final double score, final String member) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zadd(key, score, member);
			}
		});
	}

	@Override
	public Long zadd(final String key, final Map<String, Double> scoreMembers) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zadd(key, scoreMembers);
			}
		});
	}

	@Override
	public Set<String> zrange(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrange(key, start, end);
			}
		});
	}

	@Override
	public Long zrem(final String key, final String... members) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zrem(key, members);
			}
		});
	}

	@Override
	public Double zincrby(final String key, final double score, final String member) {

		return this.execute(new JedisClusterAction<Double>() {
			public Double doAction(MyJedisCluster jedis) {
				return jedis.zincrby(key, score, member);
			}
		});
	}

	@Override
	public Long zrank(final String key, final String member) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zrank(key, member);
			}
		});
	}

	@Override
	public Long zrevrank(final String key, final String member) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zrevrank(key, member);
			}
		});
	}

	@Override
	public Set<String> zrevrange(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrevrange(key, start, end);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrangeWithScores(key, start, end);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeWithScores(key, start, end);
			}
		});
	}

	@Override
	public Long zcard(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zcard(key);
			}
		});
	}

	@Override
	public Double zscore(final String key, final String member) {

		return this.execute(new JedisClusterAction<Double>() {
			public Double doAction(MyJedisCluster jedis) {
				return jedis.zscore(key, member);
			}
		});
	}

	@Override
	public List<String> sort(final String key) {

		return this.execute(new JedisClusterAction<List<String>>() {
			public List<String> doAction(MyJedisCluster jedis) {
				return jedis.sort(key);
			}
		});
	}

	@Override
	public List<String> sort(final String key, final SortingParams sortingParameters) {

		return this.execute(new JedisClusterAction<List<String>>() {
			public List<String> doAction(MyJedisCluster jedis) {
				return jedis.sort(key, sortingParameters);
			}
		});
	}

	@Override
	public Long zcount(final String key, final double min, final double max) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zcount(key, min, max);
			}
		});
	}

	@Override
	public Long zcount(final String key, final String min, final String max) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zcount(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScore(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScore(key, min, max);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScore(key, max, min);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final String max, final String min) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScore(key, max, min);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScore(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<String>>() {

			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScore(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScore(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final String max, final String min, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<String>>() {
			public Set<String> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScore(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min, final int offset,
			final int count) {

		return this.execute(new JedisClusterAction<Set<Tuple>>() {
			public Set<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		});
	}

	@Override
	public Long zremrangeByRank(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zremrangeByRank(key, start, end);
			}
		});
	}

	@Override
	public Long zremrangeByScore(final String key, final double start, final double end) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zremrangeByScore(key, start, end);
			}
		});
	}

	@Override
	public Long zremrangeByScore(final String key, final String start, final String end) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.zremrangeByScore(key, start, end);
			}
		});
	}

	@Override
	public Long linsert(final String key, final LIST_POSITION where, final String pivot, final String value) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.linsert(key, where, pivot, value);
			}
		});
	}

	@Override
	public Long lpushx(final String key, final String... strings) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.lpushx(key, strings);
			}
		});
	}

	@Override
	public Long rpushx(final String key, final String... strings) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.rpushx(key, strings);
			}
		});
	}

	@Override
	public List<String> blpop(final String key) {

		return this.execute(new JedisClusterAction<List<String>>() {
			public List<String> doAction(MyJedisCluster jedis) {
				return jedis.blpop(key);
			}
		});
	}

	@Override
	public List<String> brpop(final String key) {

		return this.execute(new JedisClusterAction<List<String>>() {
			public List<String> doAction(MyJedisCluster jedis) {
				return jedis.brpop(key);
			}
		});
	}

	@Override
	public Long del(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.del(key);
			}
		});
	}

	@Override
	public String echo(final String message) {

		return this.execute(new JedisClusterAction<String>() {
			public String doAction(MyJedisCluster jedis) {
				return jedis.echo(message);
			}
		});
	}

	@Override
	public Long move(final String key, final int dbIndex) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.move(key, dbIndex);
			}
		});
	}

	@Override
	public Long bitcount(final String key) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.bitcount(key);
			}
		});
	}

	@Override
	public Long bitcount(final String key, final long start, final long end) {

		return this.execute(new JedisClusterAction<Long>() {
			public Long doAction(MyJedisCluster jedis) {
				return jedis.bitcount(key, start, end);
			}
		});
	}

	@Deprecated
	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final int cursor) {

		return this.execute(new JedisClusterAction<ScanResult<Entry<String, String>>>() {
			public ScanResult<Entry<String, String>> doAction(MyJedisCluster jedis) {
				return jedis.hscan(key, cursor);
			}
		});
	}

	@Deprecated
	@Override
	public ScanResult<String> sscan(final String key, final int cursor) {

		return this.execute(new JedisClusterAction<ScanResult<String>>() {
			public ScanResult<String> doAction(MyJedisCluster jedis) {
				return jedis.sscan(key, cursor);
			}
		});
	}

	@Deprecated
	@Override
	public ScanResult<Tuple> zscan(final String key, final int cursor) {

		return this.execute(new JedisClusterAction<ScanResult<Tuple>>() {
			public ScanResult<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final String cursor) {

		return this.execute(new JedisClusterAction<ScanResult<Entry<String, String>>>() {
			public ScanResult<Entry<String, String>> doAction(MyJedisCluster jedis) {
				return jedis.hscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<String> sscan(final String key, final String cursor) {

		return this.execute(new JedisClusterAction<ScanResult<String>>() {
			public ScanResult<String> doAction(MyJedisCluster jedis) {
				return jedis.sscan(key, cursor);
			}
		});
	}

	@Override
	public ScanResult<Tuple> zscan(final String key, final String cursor) {

		return this.execute(new JedisClusterAction<ScanResult<Tuple>>() {
			public ScanResult<Tuple> doAction(MyJedisCluster jedis) {
				return jedis.zscan(key, cursor);
			}
		});
	}

	private static String[] mapToArray(Map<String, String> map) {
		String[] paramByte = null;
		if (map != null && map.size() > 0) {
			paramByte = new String[map.size() * 2];
			Iterator<Entry<String, String>> it = map.entrySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				paramByte[index++] = entry.getKey();
				paramByte[index++] = entry.getValue();
			}
		}
		return paramByte;
	}

	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double incrByFloat(String key, double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> spop(String key, long count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> srandmember(String key, int count) {
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
	public List<String> blpop(int timeout, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(int timeout, String key) {
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
	public Object doAction(MyJedisCluster jedisCluster) {
		// TODO Auto-generated method stub
		return null;
	}

}
