package com.adanac.framework.cache.redis.client.impl;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public class MyBinaryJedisClusterClient extends AbstractClient {
	public MyJedisCluster getJedisCluster() {
		return jedisClusterPools.get(this.configPath);
	}

	MyBinaryJedisClusterClient() {
		super();
	}

	public MyBinaryJedisClusterClient(String configPath) {
		super(configPath);
	}

	public MyBinaryJedisClusterClient(String configPath, boolean globalConfig) {
		super(configPath, globalConfig);
	}

}
