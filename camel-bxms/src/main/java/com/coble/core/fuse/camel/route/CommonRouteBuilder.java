package com.coble.core.fuse.camel.route;

import org.springframework.stereotype.Component;

/**
 * Created by Mar on 11/05/2016.
 */
@Component
public class CommonRouteBuilder extends ParentRouteBuilder {

    @Override
    public void configure() throws Exception {

        super.configure();

        from(URL_AUDIT_MESSAGE)
                .routeId(ID_ROUTE_PREPARE_AUDIT_MESSAGE)
                .setHeader(HEADER_ORIGINAL_MESSAGE_AUDIT, body())
                .beanRef(BEAN_COMMON_ENRICHER, "prepareAuditMessage")
                .wireTap(URL_QUEUE_AUDIT)
                .setBody(header(HEADER_ORIGINAL_MESSAGE_AUDIT))
                .removeHeader(HEADER_ORIGINAL_MESSAGE_AUDIT);


        from(URL_PREPARE_JBPM_MESSAGE)
                .routeId(ID_ROUTE_PREPARE_JBPM_MESSAGE)
                .setHeader(HEADER_ORIGINAL_MESSAGE_JBPM, body())
                .beanRef(BEAN_COMMON_ENRICHER, "prepareBPMSRequest")
                .wireTap(URL_QUEUE_JBPM_BEAN)
                .setBody(header(HEADER_ORIGINAL_MESSAGE_JBPM))
                .removeHeader(HEADER_ORIGINAL_MESSAGE_JBPM);
    }
}
