package com.coble.core.bpms.workitemhandlers;

import java.util.concurrent.ExecutionException;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

public class SendCamelMessage {

	@Produce(uri = "direct-vm:bpmMsg")
    private ProducerTemplate template;
	
	
    public void sendMessage(String value) throws ExecutionException, InterruptedException, CamelExecutionException {
           	
    	template.sendBody(value);
    	System.out.println("prescription delivered to sendQueue from direct " + value);
    	
    }
}
