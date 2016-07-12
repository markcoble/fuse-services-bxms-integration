package com.coble.core.fuse.camel.route;

import static com.coble.core.fuse.CamelConstants.ID_ROUTE_CXFRS_ENDPOINT;
import static com.coble.core.fuse.CamelConstants.URL_QUEUE_CXFRS_ENDPOINT;

import org.apache.camel.builder.RouteBuilder;

/**
 * Camel route using the Camel CXFRS component with Client producer.
 * 
 * @author Mark Coble
 *
 */
public class JbpmCxfRsRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from(URL_QUEUE_CXFRS_ENDPOINT).routeId(ID_ROUTE_CXFRS_ENDPOINT).to("cxfrs://bean://rsClient");
	}
}