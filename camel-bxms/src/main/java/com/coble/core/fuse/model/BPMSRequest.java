package com.coble.core.fuse.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;

/**
 * Common request object enabling execution of all supported BPMS runtime Restful API operations via 
 * the <code>JBPMEndpoint</code> bean.
 * 
 * @author Mark Coble
 */

@UriParams
public class BPMSRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	//TODO implement factory pattern.  Enums do not serialize their field so the below bombs when put onto an Active MQ queue.
/*	
	// PROCESS OPERATIONS
	START_GLOBAL_EXCEPTION_HANDLER {
		private static final long serialVersionUID = -1L;
		@Override
		public BPMSRequest init() throws MalformedURLException {
			this.setDeploymentId("com.coble.core:camel-bxms:1.0.0-SNAPSHOT");
			this.setProcessId("global.exception.handler");
			this.setOperation("START_PROCESS");
			return this;
		}
	},
	SIGNAL_EVENT_EXCEPTION {
		private static final long serialVersionUID = -1L;
		@Override
		public BPMSRequest init() throws MalformedURLException {
			this.setDeploymentId("com.coble.core:camel-bxms:1.0.0-SNAPSHOT");
			this.setProcessId("global.exception.handler");
			this.setEventType("signal");
			this.setEvent("addExcpetion");
			this.setOperation("SIGNAL_EVENT");
			return this;
		}
	};
	*/

	@UriParam
	private String operation;
	@UriParam
	private String key;
	@UriParam
	private Objects value;
	private String processId;
	private Map<String, Object> parameters;
	private Long processInstanceId;
	private String eventType;
	private String event;
	private Integer maxNumber;
	private String identifier;
	private Long workItemId;
	private Long taskId;
	private String userId;
	private String language;
	private String targetUserId;
	private Long attachmentId;
	private Long contentId;
	private Task task;
	private List<OrganizationalEntity> entities;
	private List<Status> statuses;

	// connection
	private String userName;
	private String password;
	private URL connectionURL;
	private String deploymentId;
	private Integer timeout;
	private Class[] extraJaxbClasses;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Objects getValue() {
		return value;
	}

	public void setValue(Objects value) {
		this.value = value;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Long getWorkItemId() {
		return workItemId;
	}

	public void setWorkItemId(Long workItemId) {
		this.workItemId = workItemId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public List<OrganizationalEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<OrganizationalEntity> entities) {
		this.entities = entities;
	}

	public List<Status> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<Status> statuses) {
		this.statuses = statuses;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public URL getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(URL connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Class[] getExtraJaxbClasses() {
		return extraJaxbClasses;
	}

	public void setExtraJaxbClasses(Class[] extraJaxbClasses) {
		this.extraJaxbClasses = extraJaxbClasses;
	}

	@Override
	public String toString() {
		return "BPMSRequest{" + "processId='" + processId + '\'' + ", operation='" + operation+ '\'' + ", deployment='" + deploymentId + '\'' + "}";
	}

	//public abstract BPMSRequest init() throws MalformedURLException;
}
