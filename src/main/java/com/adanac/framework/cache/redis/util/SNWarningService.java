package com.adanac.framework.cache.redis.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adanac.framework.cache.redis.AbstractWarningService;

/**
 * 
 * @author adanac
 * @version 1.0
 */
public class SNWarningService extends AbstractWarningService {
	private static final Logger logger = LoggerFactory.getLogger(SNWarningService.class);

	private String appCode;

	private String url;

	public SNWarningService(String appCode, String url, String phones) {
		super();
		setPhones(phones);
		this.appCode = appCode;
		this.url = url;
	}

	@Override
	protected void send(String phones, String msg) {

		logger.info("Sending warning msg. phones:{}, content:\"{}\".", phones, msg);
		if (phones == null || "".equals(phones) || msg == null || "".equals(msg))
			return;
		StringBuffer sb = new StringBuffer();
		sb.append("{\"alarmSource\":\"").append(appCode).append("\",\"alarmContent\":\"").append(msg)
				.append("\",\"sendway\":\"phone\",\"receiverPhone\":\"").append(phones).append("\"}");
		try {
			String responseStr = HttpClient.getResponseViaPost(url, sb.toString(), HttpClient.CONTENT_TYPE_XML, 5000,
					20000, false);
			logger.info("ResponseFromMonitor:{}.", responseStr);
		} catch (IOException e) {
			logger.error("Error occured when sending warning msg. Exception:{}", e);
		}
	}

}
