package com.adanac.framework.cache.redis;

/**
 * 告警信息
 * @author adanac
 * @version 1.0
 */
public interface WarningService {

	/**
	 * 发送手机告警短信，需要注意重复信息的控制
	 *
	 * @param msg 告警信息
	 */
	public void sendWarningMessage(String msg);

	public void setPhones(String phones);

	public String getPhones();

}