package com.coble.core.fuse.camel.route;


import static com.coble.core.fuse.CamelConstants.BEAN_CONFIGURATION;
import static com.coble.core.fuse.CamelConstants.ID_ROUTE_JBPM_COMPONENT;
import static com.coble.core.fuse.CamelConstants.URL_QUEUE_JBPM_COMPONENT;

import org.apache.camel.builder.RouteBuilder;

import com.coble.core.config.OSConfiguration;


/**
 * Camel route using the Camel JBPM component available in Camel 2.16.0.
 * 
 * @author Mark Coble
 *
 */
public class JbpmComponentRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		OSConfiguration config = (OSConfiguration) getContext().getRegistry().lookupByName(BEAN_CONFIGURATION);

		from(URL_QUEUE_JBPM_COMPONENT).routeId(ID_ROUTE_JBPM_COMPONENT).to(
				"jbpm:http://localhost:8080/business-central?userName=bpmsAdmin&password=!Mark1971&deploymentId=org.kie.example:camel-process:1.0.0-SNAPSHOT&processId=global.exception.handler");
	}
}