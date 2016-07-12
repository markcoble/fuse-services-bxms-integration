package com.coble.core.fuse.camel.config;

import com.coble.core.config.OSConfiguration;
import com.coble.core.config.ConfigurationFromFileImpl;
import com.coble.core.fuse.CamelConstants;
import com.coble.core.fuse.beans.CommonEnricher;
import com.coble.core.fuse.camel.route.CommonRouteBuilder;

import io.fabric8.mq.camel.AMQComponent;
import io.fabric8.mq.core.MQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.JmsTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract configuration for Camel context.  ActiveMQ component is added with Spring transaction policies.
 */
public abstract class ParentCamelConfiguration extends CamelConfiguration implements CamelConstants {

    @Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
        setupMessaging(camelContext);
    }

    @Override
    public List<RouteBuilder> routes() {
        List<RouteBuilder> routes = new ArrayList<>();
        routes.add(new CommonRouteBuilder());
        return routes;
    }

    @Bean(name = BEAN_COMMON_ENRICHER)
    public CommonEnricher createCommonEnricher() {
        return new CommonEnricher();
    }

    @Bean(name = BEAN_CONFIGURATION)
    public OSConfiguration createConfiguration() throws Exception {
        return new ConfigurationFromFileImpl();
    }

    @Bean(name="PROPAGATION_REQUIRED")
    public SpringTransactionPolicy propagationRequired() {
        return new SpringTransactionPolicy();
    }

    protected void setupMessaging(CamelContext camelContext) {

       OSConfiguration configuration = getApplicationContext().getBean(BEAN_CONFIGURATION, OSConfiguration.class);

        //TODO: find a more elegant solution for serializable permission
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", "*");

        MQConnectionFactory factory = new MQConnectionFactory();
        factory.setUserName(configuration.getAmqUser());
        factory.setPassword(configuration.getAMQPassword());

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);
        factory.setRedeliveryPolicy(redeliveryPolicy);

        JmsTransactionManager jmsManager = new JmsTransactionManager();
        jmsManager.setConnectionFactory(factory);

        AMQComponent amq = new AMQComponent();
        amq.setTransactionManager(jmsManager);
        amq.setServiceName(configuration.getAmqService());
        amq.setConnectionFactory(factory);
        amq.setConcurrentConsumers(1);

        SpringTransactionPolicy transactionPolicy = getApplicationContext().getBean("PROPAGATION_REQUIRED", SpringTransactionPolicy.class);
        transactionPolicy.setTransactionManager(jmsManager);
        transactionPolicy.setPropagationBehaviorName("PROPAGATION_REQUIRED");

        camelContext.addComponent("activemq", amq);
    }
}
