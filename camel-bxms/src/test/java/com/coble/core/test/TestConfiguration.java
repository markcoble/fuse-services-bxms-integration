package com.coble.core.test;


import com.coble.core.config.OSConfiguration;
import com.coble.core.fuse.camel.config.BXMSDecisionService;
import org.apache.camel.CamelContext;
import org.apache.camel.component.direct.DirectComponent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by MC99911 06/2016.
 */
@Configuration
@ComponentScan()
public class TestConfiguration extends BXMSDecisionService {
	
    @Override
    public void setupCamelContext(CamelContext camelContext) throws Exception {
        OSConfiguration configuration = getApplicationContext().getBean(OSConfiguration.class);
        camelContext.addComponent("activemq", new DirectComponent());
    }
}
