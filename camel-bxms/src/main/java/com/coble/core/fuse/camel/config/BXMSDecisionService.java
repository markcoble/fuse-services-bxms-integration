package com.coble.core.fuse.camel.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.spring.SpringJAXRSClientFactoryBean;
import org.apache.camel.spring.javaconfig.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.coble.core.config.OSConfiguration;
import com.coble.core.fuse.beans.JBPMEndpointBean;
import com.coble.core.fuse.camel.route.JbpmBeanRouteBuilder;
import com.coble.core.fuse.camel.route.JbpmComponentRouteBuilder;
import com.coble.core.fuse.camel.route.JbpmCxfRsRouteBuilder;
import com.coble.core.jaxrs.endpoint.JbpmDecisionService;

@Configuration
@ComponentScan
public class BXMSDecisionService extends ParentCamelConfiguration {

	@Autowired
	public OSConfiguration osconfig;

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.setConfigClassesString(BXMSDecisionService.class.getCanonicalName());
		main.run();
	}

	/*
	 * Bean endpoint for processing business process requests
	 */
	@Bean(name = "jbpmEndpoint")
	JBPMEndpointBean createJbpmEndpoint() {
		return new JBPMEndpointBean();
	}

	/*
	 * CXFRS client to start a process.
	 */
	@Bean
	public SpringJAXRSClientFactoryBean rsClient() {
		SpringJAXRSClientFactoryBean springJAXRSClientFactoryBean = new SpringJAXRSClientFactoryBean();

		springJAXRSClientFactoryBean.setBeanId("rsClient");
		springJAXRSClientFactoryBean.setAddress(
				"http://localhost:8080/business-central/rest/runtime/org.kie.example:camel-process:1.0.0-SNAPSHOT/process/global.exception.handler/start");
		springJAXRSClientFactoryBean.setServiceClass(JbpmDecisionService.class);
		springJAXRSClientFactoryBean.setLoggingFeatureEnabled(true);
		springJAXRSClientFactoryBean.setSkipFaultLogging(true);
		springJAXRSClientFactoryBean.setUsername("bpmsAdmin");
		springJAXRSClientFactoryBean.setPassword("!Mark1971");
		springJAXRSClientFactoryBean.setProvider(org.apache.cxf.jaxrs.provider.JAXBElementProvider.class);
		return springJAXRSClientFactoryBean;
	}

	/*
	 * CXFRS client to signal an event.
	 */
	@Bean
	public SpringJAXRSClientFactoryBean rsSignalEvent() {
		SpringJAXRSClientFactoryBean springJAXRSClientFactoryBean = new SpringJAXRSClientFactoryBean();

		springJAXRSClientFactoryBean.setBeanId("rsSignalEvent");
		springJAXRSClientFactoryBean.setAddress(
				"http://localhost:8080/business-central/rest/runtime/org.kie.example:camel-process:1.0.0-SNAPSHOT/process/instance/27/signal?signal=addException&event=REST");
		springJAXRSClientFactoryBean.setServiceClass(JbpmDecisionService.class);
		springJAXRSClientFactoryBean.setLoggingFeatureEnabled(true);
		springJAXRSClientFactoryBean.setSkipFaultLogging(true);
		springJAXRSClientFactoryBean.setUsername("bpmsAdmin");
		springJAXRSClientFactoryBean.setPassword("!Mark1971");
		springJAXRSClientFactoryBean.setProvider(org.apache.cxf.jaxrs.provider.JAXBElementProvider.class);
		return springJAXRSClientFactoryBean;
	}

	@Override
	public List<RouteBuilder> routes() {
		List<RouteBuilder> routes = new ArrayList<>();
		routes.add(new JbpmBeanRouteBuilder());
		routes.add(new JbpmCxfRsRouteBuilder());
		routes.add(new JbpmComponentRouteBuilder());
		return routes;
	}

}
