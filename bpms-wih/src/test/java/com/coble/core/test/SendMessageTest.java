package com.coble.core.test;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.coble.core.amq.ActiveMQHelper;
import com.coble.core.bpms.workitemhandlers.SendJMSMessage;

import java.util.Map;
import java.util.HashMap;



public class SendMessageTest {


	@Test
	public void testSendJMSMessage() throws Exception {	
		SendJMSMessage producer = new SendJMSMessage();
		producer.sendMessage("cancel");
	}

}
