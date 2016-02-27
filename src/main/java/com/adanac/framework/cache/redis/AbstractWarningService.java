package com.adanac.framework.cache.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public abstract class AbstractWarningService implements WarningService {
	private String phones;

	private Map<String, Long> messages = new ConcurrentHashMap<String, Long>();

	@Override
	public void sendWarningMessage(String msg) {
		Long lastSendTime = messages.get(msg);
		if (lastSendTime == null || System.currentTimeMillis() - lastSendTime > 30 * 60 * 1000) {
			send(this.phones, msg);
			messages.put(msg, System.currentTimeMillis());
		}
	}

	@Override
	public void setPhones(String phones) {
		this.phones = phones;
	}

	@Override
	public String getPhones() {
		return phones;
	}

	protected abstract void send(String phones, String msg);
}
