package com.coble.core.bpms.workitemhandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.camel.CamelExecutionException;

import com.coble.core.amq.ActiveMQHelper;

public class SendJMSMessage {


    public void sendMessage(Map<String, String> headers, String value) throws ExecutionException, InterruptedException, CamelExecutionException {

		ActiveMQHelper amqHelper = new ActiveMQHelper();
		amqHelper.publishMessage("DECISION-SERVICE-REPLY-TO.QUEUE", "cancel", headers);	 	
    }
}
