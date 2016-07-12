package com.coble.core.fuse.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;

import com.coble.core.config.OSConfiguration;
import static com.coble.core.fuse.CamelConstants.ID_ROUTE_JBPM_BEAN;
import static com.coble.core.fuse.CamelConstants.URL_QUEUE_JBPM_BEAN; 
import static com.coble.core.fuse.CamelConstants.BEAN_CONFIGURATION;

/**
 * Camel route using the bean uri with reference to a <code>JBPMEndpoint</code> bean.
 * 
 * @author Mark Coble
 *
 */
public class JbpmBeanRouteBuilder extends RouteBuilder {

    Logger logger = Logger.getLogger(JbpmBeanRouteBuilder.class);

    @Override
	public void configure() throws Exception {

        OSConfiguration config = (OSConfiguration)getContext().getRegistry().lookupByName(BEAN_CONFIGURATION);

		from(URL_QUEUE_JBPM_BEAN).routeId(ID_ROUTE_JBPM_BEAN)
		.beanRef("jbpmEndpoint");
	}

}
