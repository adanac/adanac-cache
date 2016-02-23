package com.adanac.framework.cache.redis;

import com.adanac.framework.cache.redis.client.impl.MyShardedClient;

import junit.framework.TestCase;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;

public class MyShardedClientTest extends TestCase {

	private MyShardedClient shardedClient = null;

	protected void setUp() throws Exception {
		shardedClient = new MyShardedClient();
		System.out.println(shardedClient);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDestroy() {
		shardedClient.destroy();
	}

	public void testGetShardedJedis() {
		ShardedJedis shardedJedis = shardedClient.getShardedJedis();
		assertNotNull(shardedJedis);
	}

	public void testExecuteShardedJedisActionOfT() {
		fail("Not yet implemented");
	}

	public void testGetPipeline() {
		ShardedJedisPipeline shardedJedisPipeline = shardedClient.getPipeline();
		assertNotNull(shardedJedisPipeline);
	}

	public void testExecuteObjectShardedJedisPipelineActionOfT() {
		fail("Not yet implemented");
	}

	public void testFlushDB() {
		String flushDB = shardedClient.flushDB();
		System.out.println(flushDB);
	}

	public void testFlushDBInt() {
		fail("Not yet implemented");
	}

	public void testSetStringString() {
		fail("Not yet implemented");
	}

	public void testSet() {
		String retSet = shardedClient.set("key1", "value1");
		System.out.println(retSet);
		assertNotNull(retSet);
	}

	public void testGet() {
		String getKeyString = shardedClient.get("key1");
		System.out.println(getKeyString);
		assertNotNull(getKeyString);
	}

	public void testExists() {
		fail("Not yet implemented");
	}

	public void testPersist() {
		fail("Not yet implemented");
	}

	public void testType() {
		fail("Not yet implemented");
	}

	public void testExpire() {
		fail("Not yet implemented");
	}

	public void testExpireAt() {
		fail("Not yet implemented");
	}

	public void testTtl() {
		fail("Not yet implemented");
	}

	public void testSetbitStringLongBoolean() {
		fail("Not yet implemented");
	}

	public void testSetbitStringLongString() {
		fail("Not yet implemented");
	}

	public void testGetbit() {
		fail("Not yet implemented");
	}

	public void testSetrange() {
		fail("Not yet implemented");
	}

	public void testGetrange() {
		fail("Not yet implemented");
	}

	public void testGetSet() {
		fail("Not yet implemented");
	}

	public void testSetnx() {
		fail("Not yet implemented");
	}

	public void testSetex() {
		fail("Not yet implemented");
	}

	public void testDecrBy() {
		fail("Not yet implemented");
	}

	public void testDecr() {
		fail("Not yet implemented");
	}

	public void testIncrBy() {
		fail("Not yet implemented");
	}

	public void testIncr() {
		fail("Not yet implemented");
	}

	public void testAppend() {
		fail("Not yet implemented");
	}

	public void testSubstr() {
		fail("Not yet implemented");
	}

	public void testHset() {
		fail("Not yet implemented");
	}

	public void testHget() {
		fail("Not yet implemented");
	}

	public void testHsetnx() {
		fail("Not yet implemented");
	}

	public void testHmset() {
		fail("Not yet implemented");
	}

	public void testHmget() {
		fail("Not yet implemented");
	}

	public void testHincrBy() {
		fail("Not yet implemented");
	}

	public void testHexists() {
		fail("Not yet implemented");
	}

	public void testHdel() {
		fail("Not yet implemented");
	}

	public void testHlen() {
		fail("Not yet implemented");
	}

	public void testHkeys() {
		fail("Not yet implemented");
	}

	public void testHvals() {
		fail("Not yet implemented");
	}

	public void testHgetAll() {
		fail("Not yet implemented");
	}

	public void testRpush() {
		fail("Not yet implemented");
	}

	public void testLpush() {
		fail("Not yet implemented");
	}

	public void testLlen() {
		fail("Not yet implemented");
	}

	public void testLrange() {
		fail("Not yet implemented");
	}

	public void testLtrim() {
		fail("Not yet implemented");
	}

	public void testLindex() {
		fail("Not yet implemented");
	}

	public void testLset() {
		fail("Not yet implemented");
	}

	public void testLrem() {
		fail("Not yet implemented");
	}

	public void testLpop() {
		fail("Not yet implemented");
	}

	public void testRpop() {
		fail("Not yet implemented");
	}

	public void testSadd() {
		fail("Not yet implemented");
	}

	public void testSmembers() {
		fail("Not yet implemented");
	}

	public void testSrem() {
		fail("Not yet implemented");
	}

	public void testSpopString() {
		fail("Not yet implemented");
	}

	public void testScard() {
		fail("Not yet implemented");
	}

	public void testSismember() {
		fail("Not yet implemented");
	}

	public void testSrandmemberString() {
		fail("Not yet implemented");
	}

	public void testStrlen() {
		fail("Not yet implemented");
	}

	public void testZaddStringDoubleString() {
		fail("Not yet implemented");
	}

	public void testZaddStringMapOfStringDouble() {
		fail("Not yet implemented");
	}

	public void testZrange() {
		fail("Not yet implemented");
	}

	public void testZrem() {
		fail("Not yet implemented");
	}

	public void testZincrby() {
		fail("Not yet implemented");
	}

	public void testZrank() {
		fail("Not yet implemented");
	}

	public void testZrevrank() {
		fail("Not yet implemented");
	}

	public void testZrevrange() {
		fail("Not yet implemented");
	}

	public void testZrangeWithScores() {
		fail("Not yet implemented");
	}

	public void testZrevrangeWithScores() {
		fail("Not yet implemented");
	}

	public void testZcard() {
		fail("Not yet implemented");
	}

	public void testZscore() {
		fail("Not yet implemented");
	}

	public void testSortString() {
		fail("Not yet implemented");
	}

	public void testSortStringSortingParams() {
		fail("Not yet implemented");
	}

	public void testZcountStringDoubleDouble() {
		fail("Not yet implemented");
	}

	public void testZcountStringStringString() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreStringDoubleDouble() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreStringStringString() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreStringDoubleDouble() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreStringStringString() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreStringDoubleDoubleIntInt() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreStringStringStringIntInt() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreStringDoubleDoubleIntInt() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreStringStringStringIntInt() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreWithScoresStringDoubleDouble() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreWithScoresStringStringString() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreWithScoresStringDoubleDoubleIntInt() {
		fail("Not yet implemented");
	}

	public void testZrangeByScoreWithScoresStringStringStringIntInt() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreWithScoresStringStringString() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreWithScoresStringDoubleDouble() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreWithScoresStringDoubleDoubleIntInt() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByScoreWithScoresStringStringStringIntInt() {
		fail("Not yet implemented");
	}

	public void testZremrangeByRank() {
		fail("Not yet implemented");
	}

	public void testZremrangeByScoreStringDoubleDouble() {
		fail("Not yet implemented");
	}

	public void testZremrangeByScoreStringStringString() {
		fail("Not yet implemented");
	}

	public void testLinsert() {
		fail("Not yet implemented");
	}

	public void testLpushx() {
		fail("Not yet implemented");
	}

	public void testRpushx() {
		fail("Not yet implemented");
	}

	public void testBlpopString() {
		fail("Not yet implemented");
	}

	public void testBrpopString() {
		fail("Not yet implemented");
	}

	public void testDel() {
		fail("Not yet implemented");
	}

	public void testEcho() {
		fail("Not yet implemented");
	}

	public void testMove() {
		fail("Not yet implemented");
	}

	public void testBitcountString() {
		fail("Not yet implemented");
	}

	public void testBitcountStringLongLong() {
		fail("Not yet implemented");
	}

	public void testHscanStringInt() {
		fail("Not yet implemented");
	}

	public void testSscanStringInt() {
		fail("Not yet implemented");
	}

	public void testZscanStringInt() {
		fail("Not yet implemented");
	}

	public void testHscanStringString() {
		fail("Not yet implemented");
	}

	public void testSscanStringString() {
		fail("Not yet implemented");
	}

	public void testZscanStringString() {
		fail("Not yet implemented");
	}

	public void testSetStringStringStringStringLong() {
		fail("Not yet implemented");
	}

	public void testPexpire() {
		fail("Not yet implemented");
	}

	public void testPexpireAt() {
		fail("Not yet implemented");
	}

	public void testIncrByFloat() {
		fail("Not yet implemented");
	}

	public void testSpopStringLong() {
		fail("Not yet implemented");
	}

	public void testSrandmemberStringInt() {
		fail("Not yet implemented");
	}

	public void testZlexcount() {
		fail("Not yet implemented");
	}

	public void testZrangeByLexStringStringString() {
		fail("Not yet implemented");
	}

	public void testZrangeByLexStringStringStringIntInt() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByLexStringStringString() {
		fail("Not yet implemented");
	}

	public void testZrevrangeByLexStringStringStringIntInt() {
		fail("Not yet implemented");
	}

	public void testZremrangeByLex() {
		fail("Not yet implemented");
	}

	public void testBlpopIntString() {
		fail("Not yet implemented");
	}

	public void testBrpopIntString() {
		fail("Not yet implemented");
	}

	public void testPfadd() {
		fail("Not yet implemented");
	}

	public void testPfcount() {
		fail("Not yet implemented");
	}

	public void testDoAction() {
		fail("Not yet implemented");
	}

	public void testMset() {
		fail("Not yet implemented");
	}

	public void testMsetnx() {
		fail("Not yet implemented");
	}

	public void testMain() {
		fail("Not yet implemented");
	}

}
