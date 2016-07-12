package com.coble.core.fuse;

/**
 * Constants used for the Camel configuration and route builder classes.
 */
public interface CamelConstants {

    public static final String ENV_CONFIG_LOCATION = "BXMS_CONFIG_LOCATION";

    public static final String BEAN_CONFIGURATION = "osConfig";
    public static final String BEAN_COMMON_ENRICHER = "commonEnricher";
    public static final String BEAN_AUDIT_TRANSFORMER = "auditTransformer";

    public static final String HEADER_ID = "transaction_id";
    public static final String HEADER_TAGS = "tags";
    public static final String HEADER_ORIGINAL_MESSAGE = "original_message";
    public static final String HEADER_ORIGINAL_MESSAGE_AUDIT = "original_message_audit";
    public static final String HEADER_ORIGINAL_MESSAGE_JBPM = "original_message_jbpm";
    public static final String HEADER_OUTCOME = "outcome";
    public static final String HEADER_NEXT_DESTINATION = "next_destination";
    public static final String HEADER_STATUS = "status";
    public static final String HEADER_NEXT_STATUS = "next_status";
    // CXF 
    public static final String CXF_QUERY_PARAMETERS = "CamelCxfRsQueryMap";
    
    public static final String ID_ROUTE_JBPM_BEAN = "route-jbpm-bean";
    public static final String ID_ROUTE_JBPM_COMPONENT = "route-jbpm-component";
    public static final String ID_ROUTE_CXFRS_ENDPOINT = "route-cxfrs-endpoint";
    public static final String ID_ROUTE_PREPARE_AUDIT_MESSAGE = "route-prepare-audit-message";
    public static final String ID_ROUTE_PREPARE_JBPM_MESSAGE = "route-prepare-jbpm-message";

    public static final String URL_DIRECT_JBPM = "direct:jbpm";
    public static final String URL_AUDIT_MESSAGE = "direct:prepare-audit-message";
    public static final String URL_PREPARE_JBPM_MESSAGE = "direct:prepare-jbpm-message";
    public static final String URL_QUEUE_AUDIT = "activemq:queue:audit";
    public static final String URL_QUEUE_JBPM_BEAN = "activemq:queue:jbpmBean";  
    public static final String URL_QUEUE_JBPM_COMPONENT = "activemq:queue:jbpmComponent";  
    public static final String URL_QUEUE_CXFRS_ENDPOINT = "activemq:queue:cxfrsEndpoint";  
    
    public static final String URL_QUEUE_DROOLS = "activemq:queue:drools";;
    public static final String URL_QUEUE_MAIL = "activemq:queue:mail";
    
    public static final String URL_JBPM_LOG = "log:com.coble.core.bxms?showAll=true&multiline=true";


    public static final String TAG_STACKTRACE = "Stacktrace";
    public static final String TAG_ERROR_MESSAGE = "ErrorMessage";
    public static final String TAG_OLD_STATUS = "OldStatus";
    public static final String TAG_STATUS = "Status";

    public static final String TAG_OUTCOME = "Outcome";
    public static final String TAG_XML = "Xml";

    public static final String OUTCOME_CONTINUE = "CONTINUE";
    public static final String OUTCOME_REJECT = "REJECT";
    public static final String OUTCOME_IGNORE = "IGNORE";

    public static final String MSG_NO_ERROR_MESSAGE = "[NO ERROR MESSAGE AVAILABLE]";
    public static final String MSG_NO_STACKTRACE = "[NO STACkTRACE AVAILABLE]";

}
