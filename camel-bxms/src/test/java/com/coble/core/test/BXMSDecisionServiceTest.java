package com.coble.core.test;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;

import com.coble.core.config.OSConfiguration;
import com.coble.core.fuse.model.BPMSRequest;

import static com.coble.core.fuse.CamelConstants.*;

/**
 * Integration test class to test 3 types of camel endpoints which execute
 * business process api on a BxMS Decision server. Integration environment must
 * be up and running for these to work!
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@BootstrapWith(CamelTestContextBootstrapper.class)
@ContextConfiguration(classes = { TestConfiguration.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@UseAdviceWith(true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BXMSDecisionServiceTest {

	private Logger logger = Logger.getLogger(BXMSDecisionServiceTest.class);
	public static File configFolder;

	@Autowired
	protected ModelCamelContext context;

	@Autowired
	protected OSConfiguration configuration;

	@Produce(uri = "direct:start")
	protected ProducerTemplate directstartProducer;

	DefaultProducerTemplate producer;

	@EndpointInject(uri = "mock:complete")
	protected MockEndpoint mockComplete;

	@BeforeClass
	public static void setUp() throws Exception {

		configFolder = Files.createTempDirectory("config").toFile();

		File configPath = new File(configFolder, "config.properties");

		Properties prop = new Properties();
		prop.setProperty("BPMS_USER_NAME", "bpmsAdmin");
		prop.setProperty("BPMS_PASSWORD", "!bpmsAdmin");
		prop.setProperty("BPMS_SERVICE", "bpms-gateway");
		prop.store(new FileWriter(configPath), "configuration");

		Map<String, String> origEnv = System.getenv();
		Class<?> cl = origEnv.getClass();
		Field field = cl.getDeclaredField("m");
		field.setAccessible(true);
		Object obj = field.get(origEnv);
		Map<String, String> env = (Map<String, String>) obj;

		env.put(ENV_CONFIG_LOCATION, configPath.getAbsolutePath());
		env.put("BPMS_GATEWAY_SERVICE_HOST", "localhost");
		env.put("BPMS_GATEWAY_SERVICE_PORT", "8080");

	}

	@Test
	public void signalEventJbpmBeanTest() throws Exception {

		adviceRoute(ID_ROUTE_JBPM_BEAN);

		mockComplete.expectedMessageCount(1);
		context.start();

		// Build JBPM Configuration
		// BPMSRequest request = BPMSRequest.SIGNAL_EVENT_EXCEPTION.init();

		BPMSRequest request = new BPMSRequest();
		request.setDeploymentId("org.kie.example:camel-process:1.0.0-SNAPSHOT");
		request.setProcessId("global.exception.handler");
		request.setProcessInstanceId(28L);
.
		request.setEventType("addException");
		request.setEvent("exception message");
		request.setOperation("SIGNAL_EVENT");
		directstartProducer.sendBody(request);
		mockComplete.assertIsSatisfied();
	}

	@Test
	public void startProcessInstanceJbpmBeanTest() throws Exception {

		adviceRoute(ID_ROUTE_JBPM_BEAN);

		mockComplete.expectedMessageCount(1);
		context.start();

		final Map map = new HashMap();
		map.put("contextId", context.getName());
		map.put("routeId", ID_ROUTE_JBPM_BEAN);
		map.put("errorMessage", "startProcessInstanceJbpmBeanTest()");

		// Build JBPM Configuration
		// BPMSRequest request =
		// BPMSRequest.START_GLOBAL_EXCEPTION_HANDLER.init();
		BPMSRequest request = new BPMSRequest();
		request.setDeploymentId("org.kie.example:camel-process:1.0.0-SNAPSHOT");
		request.setProcessId("global.exception.handler");
		request.setParameters(map);
		request.setOperation("START_PROCESS");

		directstartProducer.sendBody(request);
		mockComplete.assertIsSatisfied();
	}

	@Test
	public void startProcessInstanceCxfRsTest() throws Exception {

		adviceRoute(ID_ROUTE_CXFRS_ENDPOINT);

		mockComplete.expectedMessageCount(1);
		context.start();

		final Map map = new HashMap();
		map.put("map_contextId", context.getName());
		map.put("map_routeId", ID_ROUTE_CXFRS_ENDPOINT);
		map.put("map_errorMessage", "startProcessInstanceCxfRsTest()");
		Map<String, Object> headers = new HashMap<>();
		headers.put(CXF_QUERY_PARAMETERS, map);

		directstartProducer.sendBodyAndHeaders("TEST", headers);
		mockComplete.assertIsSatisfied();

	}

	@Test
	public void startProcessInstanceJbpmComponentTest() throws Exception {

		adviceRoute(ID_ROUTE_JBPM_COMPONENT);

		mockComplete.expectedMessageCount(1);
		context.start();
		final Map map = new HashMap();
		map.put("contextId", context.getName());
		map.put("routeId", ID_ROUTE_JBPM_COMPONENT);
		map.put("errorMessage", "startProcessInstanceCxfRsTest()");
		Map<String, Object> headers = new HashMap<>();
		headers.put("CamelJBPMParameters", map);
		directstartProducer.sendBodyAndHeaders("TEST", headers);
		mockComplete.assertIsSatisfied();
	}

	private void adviceRoute(String routeId) throws Exception {
		context.getRouteDefinition(routeId).adviceWith(context, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				replaceFromWith("direct:start");
				weaveAddLast().to("mock:complete");
			}
		});
	}
}
