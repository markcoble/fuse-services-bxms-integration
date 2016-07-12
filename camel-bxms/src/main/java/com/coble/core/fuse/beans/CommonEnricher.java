package com.coble.core.fuse.beans;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.log4j.Logger;

import com.coble.core.fuse.CamelConstants;
import com.coble.core.fuse.model.AuditMessage;
import com.coble.core.fuse.model.BPMSRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is an enricher bean that is shared by all route builders and
 * configuration classes.
 */
public class CommonEnricher implements CamelConstants {

	private static final Logger logger = Logger.getLogger(CommonEnricher.class.getName());

	/**
	 * Get the current status of the message (as set into an header as a
	 * parameter) and update the status inside the message tags. A new instance
	 * of tags is created, avoiding to modify the tag map directly and
	 * maintaining the header content immutable.
	 *
	 * @param exchange
	 *            the message exchange that contains the header
	 *            <code>HEADER_STATUS</code>
	 */
	public void updateStatus(Exchange exchange, String status) {

		// String status = exchange.getIn().getHeader(HEADER_STATUS,
		// String.class);
		Map<String, String> tags = exchange.getIn().getHeader(HEADER_TAGS, Map.class);

		Map<String, String> newTags = new HashMap<>(tags);
		newTags.put(TAG_STATUS, status);

		exchange.getIn().setHeader(HEADER_TAGS, newTags);
	}

	/**
	 * Prepare all the data that will be use to manage an exception. This method
	 * is the first method called in the general exception route.
	 *
	 * @param exchange
	 */
	public void enrichErrorHeaders(Exchange exchange) {

		Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

		String exceptionMessage;
		String stacktrace;

		// manage the case where the Exception is not present (very unlikely) or
		// where the error message is not defined
		if (exception != null) {
			exceptionMessage = exception.getMessage();
			if (exceptionMessage == null || exceptionMessage.trim().length() == 0) {
				exceptionMessage = MSG_NO_ERROR_MESSAGE;
			}

			// convert the stacktrace into a string
			StringWriter stw = new StringWriter();
			exception.printStackTrace(new PrintWriter(stw));
			stacktrace = stw.toString();
		} else {
			exceptionMessage = MSG_NO_ERROR_MESSAGE;
			stacktrace = MSG_NO_STACKTRACE;
		}

		logger.error("[ERROR] " + exceptionMessage + "\n" + stacktrace);

		Message camelMessage = exchange.getIn();

		// create a tag map if it is not already present. if it is present,
		// clone the old map in a new map to maintain immutability
		Map<String, String> oldTags = camelMessage.getHeader(HEADER_TAGS, Map.class);
		if (oldTags == null) {
			oldTags = new HashMap<>();
		}
		Map<String, String> tags = new HashMap<>(oldTags);

		tags.put(TAG_ERROR_MESSAGE, exceptionMessage);
		tags.put(TAG_STACKTRACE, stacktrace);

		camelMessage.setHeader(HEADER_TAGS, tags);

	}

	/**
	 * Create AuditMessage with route data.
	 * 
	 * @param id
	 * @param tags
	 * @param message
	 * @return
	 */
	public AuditMessage prepareAuditMessage(@Header(HEADER_ID) String id, @Header(HEADER_TAGS) Map<String, String> tags,
			@Body Object message) {

		String messageString;
		if (message != null) {
			messageString = message.toString();
		} else {
			messageString = "no message available";
		}

		return new AuditMessage(id, tags, messageString);
	}

	/**
	 * Select the appropriate BPMSRequest type from runtime header information
	 * and add any necessary process variable information to it.
	 * 
	 * @param id
	 * @param tags
	 * @return enriched BPMSRequest
	 * @throws MalformedURLException
	 */
	public BPMSRequest prepareBPMSRequest(@Header(HEADER_ID) String id, @Header(HEADER_TAGS) Map<String, String> tags)
			throws MalformedURLException {

		// TODO Implement Service locator pattern. The header information will
		// be use to select a preconfigured request object which has details
		// liek deploymentId and processId. EnumMaps?
		// BPMSRequest request =
		// BPMSRequest.START_GLOBAL_EXCEPTION_HANDLER.init();
		BPMSRequest request = new BPMSRequest();
		request.setDeploymentId("org.kie.example:camel-process:1.0.0-SNAPSHOT");
		request.setProcessId("global.exception.handler");
		request.setOperation("START_PROCESS");

		if (logger.isDebugEnabled()) {
			logger.debug("- prepareBPMSRequest() > " + request.toString());
		}
		return request;
	}

}
