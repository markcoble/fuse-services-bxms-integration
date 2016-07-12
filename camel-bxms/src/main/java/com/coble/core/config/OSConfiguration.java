package com.coble.core.config;

/**
 * Defines OpenShift configurations which primarily those needed
 * for OpenShift Services. This demo assumes uses an Active MQ broker and BxMS
 * Decision Server that are not deployed to OpenShift. Therefore, services are
 * created with the required endpoints to allow communication externally.
 */ 
public interface OSConfiguration {

    String getConfig(String configLabel);

    String getAuditLocation();

    String getAmqUser();

    String getAMQPassword();

    String getAmqService();

    String getServiceHost(String service);

    String getServicePort(String service);

	String getBpmsPassword();

	String getBpmsUserName();

}
