package com.coble.core.bpms.workitemhandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.naming.NamingException;

import org.apache.camel.CamelExecutionException;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

public class SendMessageWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

	private KieSession ksession;
	private SendJMSMessage producer;
	public static final String MESSAGE = "messageIn";
	public static final String HEADERS = "headersIn";

	public static final String INCIDENCE = "incidenceSignal";
	public static final String RESUME = "resumeTask";
	public static final String DEFAULT_INCIDENCE_SIGNAL = "review";

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemmanager) {
		workItemmanager.abortWorkItem(workItem.getId());

	}

	/**
	 * Initialize with KnowledgeSesssion and JMS producer.
	 */
	public SendMessageWorkItemHandler(KieSession ksession) throws NamingException {

		super();
		this.ksession = ksession;
		this.producer = new SendJMSMessage();

	}

	/**
	 * Sends a message to a message queue with Headers and Message body set by the Work Item Handler
	 */
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

		Map<String, Object> output = new HashMap<String, Object>();
		Map<String, String> headers = (Map<String, String>) workItem.getParameter(HEADERS);
		String message = (String) workItem.getParameter(MESSAGE);
		String incidence = (String) workItem.getParameter(INCIDENCE);
		String resumeTask = (String) workItem.getParameter(RESUME);
		Integer id = (int) workItem.getId();

		try {
			producer.sendMessage(headers, message);
			output.put("status", "Ok");
			ksession.getWorkItemManager().completeWorkItem(id, output);

		} catch (ExecutionException e) {
			handleException(e);

		} catch (InterruptedException e) {
			handleException(e);
		} catch (CamelExecutionException e) {
			if ("resume".equals(resumeTask)) {
				output.put("status", "Fail");
				ksession.getWorkItemManager().completeWorkItem(id, output);
				if (incidence == null) {
					incidence = DEFAULT_INCIDENCE_SIGNAL;
				}
				ksession.signalEvent(incidence, new String(e.getMessage()));
			} else {
				handleException(e);
			}

		}

	}

	public KieSession getKsession() {
		return ksession;
	}

	public void setKsession(KieSession ksession) {
		this.ksession = ksession;
	}

	public SendJMSMessage getSendMessage() {
		return producer;
	}

	public void setSendMessage(SendJMSMessage producer) {
		this.producer = producer;
	}

}
