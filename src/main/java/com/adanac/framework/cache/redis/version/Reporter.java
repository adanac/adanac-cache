package com.adanac.framework.cache.redis.version;

import com.adanac.framework.statistics.VersionStatistics;

public class Reporter {
	public static void report() {
		VersionStatistics.reportVersion(Reporter.class);
	}
}
