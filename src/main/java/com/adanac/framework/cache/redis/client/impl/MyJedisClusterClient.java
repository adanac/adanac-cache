package com.adanac.framework.cache.redis.client.impl;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public class MyJedisClusterClient extends AbstractClient {
	public MyJedisCluster getJedisCluster() {
		return jedisClusterPools.get(this.configPath);
	}

	public MyJedisClusterClient() {
		super();
	}

	public MyJedisClusterClient(String configPath) {
		super(configPath);
	}

	public MyJedisClusterClient(String configPath, boolean globalConfig) {
		super(configPath, globalConfig);
	}
}
