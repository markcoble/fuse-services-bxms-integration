package com.coble.core.config;

import org.apache.log4j.Logger;

import com.coble.core.fuse.CamelConstants;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

/**
 * File-based implementation of <code>OSConfiguration</code>
 */
public class ConfigurationFromFileImpl implements CamelConstants, OSConfiguration {

	Logger logger = Logger.getLogger(OSConfiguration.class);

	protected Properties configuration;

	public ConfigurationFromFileImpl() throws Exception {
		Map<String, String> envMap = System.getenv();
		String configLocation = envMap.get(ENV_CONFIG_LOCATION);

		configuration = new Properties();
		configuration.load(new FileInputStream(configLocation));
	}

	@Override
	public String getConfig(String configLabel) {
		return configuration.getProperty(configLabel);
	}

	@Override
	public String getAuditLocation() {
		return configuration.getProperty("AUDIT_LOCATION");
	}

	@Override
	public String getAmqUser() {
		return configuration.getProperty("AMQ_USER");
	}

	@Override
	public String getAMQPassword() {
		return configuration.getProperty("AMQ_PASSWORD");
	}

	@Override
	public String getAmqService() {
		return configuration.getProperty("AMQ_SERVICE");
	}

	@Override
	public String getServiceHost(String service) {
		Map<String, String> envMap = System.getenv();

		String key = service.toUpperCase() + "_SERVICE_HOST";
		if (logger.isDebugEnabled()) {
			logger.debug("-> Service Host Key: " + key);
			logger.debug("-> Service Host Value: " + envMap.get(key));
		}
		return envMap.get(key);
	}

	@Override
	public String getServicePort(String service) {
		Map<String, String> envMap = System.getenv();

		String key = service.toUpperCase() + "_SERVICE_PORT";
		if (logger.isDebugEnabled()) {
			logger.debug("-> Service Port Key: " + key);
			logger.debug("-> Service Port Value: " + envMap.get(key));
		}
		return envMap.get(key);
	}

	@Override
	public String getBpmsPassword() {
		return configuration.getProperty("BPMS_PASSWORD");
	}

	@Override
	public String getBpmsUserName() {
		{
			return configuration.getProperty("BPMS_USER_NAME");
		}
	}

	@Override
	public String toString() {
		return "DCMConfigurationFromFileImpl{" + "configuration=" + configuration + '}';
	}
}
