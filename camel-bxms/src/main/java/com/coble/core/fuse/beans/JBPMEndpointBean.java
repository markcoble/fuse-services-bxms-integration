package com.coble.core.fuse.beans;

import com.coble.core.config.OSConfiguration;
import com.coble.core.fuse.BPMSConstants;
import com.coble.core.fuse.CamelConstants;
import com.coble.core.fuse.model.BPMSRequest;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.util.ExchangeHelper;
import org.apache.log4j.Logger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRestRuntimeEngineBuilder;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Camel Bean for handling all execution requests to a RedHat BPMS decision
 * server.
 */
@Component
public class JBPMEndpointBean implements CamelConstants {

	Logger logger = Logger.getLogger(JBPMEndpointBean.class);
	private KieSession kieSession;
	private TaskService taskService;

	private BPMSRequest request;
	private RuntimeEngine runtimeEngine;
	OSConfiguration coreConfiguration;

	/**
	 * Main processing method for all business process requests. The
	 * <code>BPMSRequest</code> contains all required details for selecting the
	 * correct Restful API client execution method. Connection URL and
	 * credential details of the execution server are retrieved from secure
	 * configuration objects.
	 * 
	 * @param exchange Containing the business process request details in its body.
	 * @throws Exception
	 */
	public void process(Exchange exchange) throws Exception {

		coreConfiguration = (OSConfiguration) exchange.getContext().getRegistry().lookupByName(BEAN_CONFIGURATION);
		request = exchange.getIn().getBody(BPMSRequest.class);

		if (logger.isDebugEnabled()) {
			//logger.debug(">> process():  creating JBPMEndpoint from non-ENUM " + request.toString());
		}

		RemoteRestRuntimeEngineBuilder engineBuilder = RemoteRuntimeEngineFactory.newRestBuilder();
		if (request.getUserName() != null) {
			engineBuilder.addUserName(request.getUserName());
		} else
			engineBuilder.addUserName(coreConfiguration.getBpmsUserName());
		if (request.getPassword() != null) {
			engineBuilder.addPassword(request.getPassword());
		} else
			engineBuilder.addPassword(coreConfiguration.getBpmsPassword());

		String deploymentId = exchange.getIn().getHeader(BPMSConstants.DEPLOYMENT_ID, String.class);
		if (deploymentId == null)
			deploymentId = request.getDeploymentId();
		engineBuilder.addDeploymentId(deploymentId);

		if (request.getConnectionURL() != null) {
			engineBuilder.addUrl(request.getConnectionURL());
		} else {
			URL rest = new URL("http://" + coreConfiguration.getServiceHost("bpms_gateway") + ":"
					+ coreConfiguration.getServicePort("bpms_gateway") + "/business-central");
			logger.info("- process() > Connection URL: " + rest.toString());
			engineBuilder.addUrl(rest);
		}
		if (request.getProcessInstanceId() != null) {
			engineBuilder.addProcessInstanceId(request.getProcessInstanceId());
		}
		if (request.getTimeout() != null) {
			engineBuilder.addTimeout(request.getTimeout());
		}
		if (request.getExtraJaxbClasses() != null) {
			engineBuilder.addExtraJaxbClasses(request.getExtraJaxbClasses());
		}
		runtimeEngine = engineBuilder.build();
		if (logger.isDebugEnabled()) {
			logger.debug("<< process(): creating endpoint done");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(">> starting producer");
		}
		kieSession = runtimeEngine.getKieSession();
		taskService = runtimeEngine.getTaskService();

		getOperation(exchange).execute(kieSession, taskService, request, exchange);

		if (kieSession != null) {
			kieSession = null;
		}

		if (taskService != null) {
			taskService = null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("<< stopped producer");
		}
	}

	/**
	 * Determine the correct operation to execute from the
	 * <code>BPMSRequest</code>
	 * 
	 */
	Operation getOperation(Exchange exchange) {
		String operation = exchange.getIn().getHeader(BPMSConstants.OPERATION, String.class);
		if (operation == null && request.getOperation() != null) {
			operation = BPMSConstants.OPERATION + request.getOperation();
		}
		if (operation == null) {
			operation = BPMSConstants.OPERATION + Operation.START_PROCESS;
		}
		logger.info("-- getOperation() >  Operation: " + operation);
		return Operation.valueOf(operation.substring(BPMSConstants.OPERATION.length()).toUpperCase());
	}

	/**
	 * Enums for execution of supported Business Process runtime Restful API
	 * operations.
	 * 
	 * @author Mark Coble
	 *
	 */
	enum Operation {


		// PROCESS OPERATIONS
		START_PROCESS {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				ProcessInstance processInstance = kieSession.startProcess(getProcessId(configuration, exchange),
						getParameters(configuration, exchange));
				setResult(exchange, processInstance);
			}
		},
		ABORT_PROCESS_INSTANCE {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				kieSession.abortProcessInstance(safe(getProcessInstanceId(configuration, exchange)));
			}
		},
		SIGNAL_EVENT {
			Logger logger = Logger.getLogger(Operation.class);

			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Long processInstanceId = getProcessInstanceId(configuration, exchange);
				
				if (logger.isDebugEnabled()) {
					logger.debug("- execute() >: Signal Event for process instance: " + processInstanceId);
				}
				if (processInstanceId != null) {
					kieSession.signalEvent(getEventType(configuration, exchange), getEvent(configuration, exchange),
							processInstanceId);
				} else {
					kieSession.signalEvent(getEventType(configuration, exchange), getEvent(configuration, exchange));
				}
			}
		},
		GET_PROCESS_INSTANCE {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				ProcessInstance processInstance = kieSession
						.getProcessInstance(safe(getProcessInstanceId(configuration, exchange)));
				setResult(exchange, processInstance);
			}
		},
		GET_PROCESS_INSTANCES {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Collection<ProcessInstance> processInstances = kieSession.getProcessInstances();
				setResult(exchange, processInstances);
			}
		},

		// RULE OPERATIONS
		FIRE_ALL_RULES {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Integer max = getMaxNumber(configuration, exchange);
				int rulesFired;
				if (max != null) {
					rulesFired = kieSession.fireAllRules(max);
				} else {
					rulesFired = kieSession.fireAllRules();
				}
				setResult(exchange, rulesFired);
			}
		},
		GET_FACT_COUNT {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				long factCount = kieSession.getFactCount();
				setResult(exchange, factCount);
			}
		},
		GET_GLOBAL {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Object global = kieSession.getGlobal(getIdentifier(configuration, exchange));
				setResult(exchange, global);
			}
		},
		SET_GLOBAL {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				kieSession.setGlobal(getIdentifier(configuration, exchange), getValue(configuration, exchange));
			}
		},

		// WORK ITEM OPERATIONS
		ABORT_WORK_ITEM {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				kieSession.getWorkItemManager().abortWorkItem(safe(getWorkItemId(configuration, exchange)));
			}
		},
		COMPLETE_WORK_ITEM {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				kieSession.getWorkItemManager().completeWorkItem(safe(getWorkItemId(configuration, exchange)),
						getParameters(configuration, exchange));
			}
		},

		// TASK OPERATIONS
		ACTIVATE_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.activate(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		ADD_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				long taskId = taskService.addTask(getTask(configuration, exchange),
						getParameters(configuration, exchange));
				setResult(exchange, taskId);
			}
		},
		CLAIM_NEXT_AVAILABLE_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.claimNextAvailable(getUserId(configuration, exchange),
						getLanguage(configuration, exchange));
			}
		},
		CLAIM_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.claim(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		COMPLETE_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.complete(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange),
						getParameters(configuration, exchange));
			}
		},
		DELEGATE_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.delegate(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange),
						getTargetUserId(configuration, exchange));
			}
		},
		EXIT_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.exit(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		FAIL_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.fail(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange),
						getParameters(configuration, exchange));
			}
		},
		GET_ATTACHMENT {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Attachment attachment = taskService.getAttachmentById(safe(getAttachmentId(configuration, exchange)));
				setResult(exchange, attachment);
			}
		},
		GET_CONTENT {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Content content = taskService.getContentById(safe(getContentId(configuration, exchange)));
				setResult(exchange, content);
			}
		},
		GET_TASK_ASSIGNED_AS_BUSINESS_ADMIN {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				List<TaskSummary> taskSummaries = taskService.getTasksAssignedAsBusinessAdministrator(
						getUserId(configuration, exchange), getLanguage(configuration, exchange));
				setResult(exchange, taskSummaries);
			}
		},
		GET_TASK_ASSIGNED_AS_POTENTIAL_OWNER {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.getTasksAssignedAsPotentialOwnerByStatus(getUserId(configuration, exchange),
						getStatuses(configuration, exchange), getLanguage(configuration, exchange));
			}
		},
		GET_TASK_BY_WORK_ITEM_ID {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Task task = taskService.getTaskByWorkItemId(safe(getWorkItemId(configuration, exchange)));
				setResult(exchange, task);
			}
		},
		GET_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Task task = taskService.getTaskById(safe(getTaskId(configuration, exchange)));
				setResult(exchange, task);
			}
		},
		GET_TASK_CONTENT {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				Map<String, Object> taskContent = taskService.getTaskContent(safe(getTaskId(configuration, exchange)));
				setResult(exchange, taskContent);
			}
		},
		GET_TASKS_BY_PROCESS_INSTANCE_ID {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				List<Long> processInstanceIds = taskService
						.getTasksByProcessInstanceId(safe(getProcessInstanceId(configuration, exchange)));
				setResult(exchange, processInstanceIds);
			}
		},
		GET_TASKS_BY_STATUS_BY_PROCESS_INSTANCE_ID {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				List<TaskSummary> taskSummaryList = taskService.getTasksByStatusByProcessInstanceId(
						safe(getProcessInstanceId(configuration, exchange)), getStatuses(configuration, exchange),
						getLanguage(configuration, exchange));
				setResult(exchange, taskSummaryList);
			}
		},
		GET_TASKS_OWNED {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				List<TaskSummary> summaryList = taskService.getTasksOwned(getUserId(configuration, exchange),
						getLanguage(configuration, exchange));
				setResult(exchange, summaryList);
			}
		},
		NOMINATE_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.nominate(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange),
						getEntities(configuration, exchange));
			}
		},
		RELEASE_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.release(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		RESUME_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.resume(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		SKIP_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.skip(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		START_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.start(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		STOP_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.stop(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		},
		SUSPEND_TASK {
			@Override
			void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration, Exchange exchange) {
				taskService.suspend(safe(getTaskId(configuration, exchange)), getUserId(configuration, exchange));
			}
		};

		List<Status> getStatuses(BPMSRequest configuration, Exchange exchange) {
			List<Status> statusList = exchange.getIn().getHeader(BPMSConstants.STATUS_LIST, List.class);
			if (statusList == null) {
				statusList = configuration.getStatuses();
			}
			return statusList;
		}

		List<OrganizationalEntity> getEntities(BPMSRequest configuration, Exchange exchange) {
			List<OrganizationalEntity> entityList = exchange.getIn().getHeader(BPMSConstants.ENTITY_LIST, List.class);
			if (entityList == null) {
				entityList = configuration.getEntities();
			}
			return entityList;
		}

		Long getAttachmentId(BPMSRequest configuration, Exchange exchange) {
			Long attachmentId = exchange.getIn().getHeader(BPMSConstants.ATTACHMENT_ID, Long.class);
			if (attachmentId == null) {
				attachmentId = configuration.getAttachmentId();
			}
			return attachmentId;
		}

		Long getContentId(BPMSRequest configuration, Exchange exchange) {
			Long contentId = exchange.getIn().getHeader(BPMSConstants.CONTENT_ID, Long.class);
			if (contentId == null) {
				contentId = configuration.getContentId();
			}
			return contentId;
		}

		String getTargetUserId(BPMSRequest configuration, Exchange exchange) {
			String userId = exchange.getIn().getHeader(BPMSConstants.TARGET_USER_ID, String.class);
			if (userId == null) {
				userId = configuration.getTargetUserId();
			}
			return userId;
		}

		String getLanguage(BPMSRequest configuration, Exchange exchange) {
			String language = exchange.getIn().getHeader(BPMSConstants.LANGUAGE, String.class);
			if (language == null) {
				language = configuration.getLanguage();
			}
			return language;
		}

		Task getTask(BPMSRequest configuration, Exchange exchange) {
			Task task = exchange.getIn().getHeader(BPMSConstants.TASK, Task.class);
			if (task == null) {
				task = configuration.getTask();
			}
			return task;
		}

		String getUserId(BPMSRequest configuration, Exchange exchange) {
			String userId = exchange.getIn().getHeader(BPMSConstants.USER_ID, String.class);
			if (userId == null) {
				userId = configuration.getUserId();
			}
			return userId;
		}

		Long getTaskId(BPMSRequest configuration, Exchange exchange) {
			Long taskId = exchange.getIn().getHeader(BPMSConstants.TASK_ID, Long.class);
			if (taskId == null) {
				taskId = configuration.getTaskId();
			}
			return taskId;
		}

		Long getWorkItemId(BPMSRequest configuration, Exchange exchange) {
			Long workItemId = exchange.getIn().getHeader(BPMSConstants.WORK_ITEM_ID, Long.class);
			if (workItemId == null) {
				workItemId = configuration.getWorkItemId();
			}
			return workItemId;
		}

		String getIdentifier(BPMSRequest configuration, Exchange exchange) {
			String identifier = exchange.getIn().getHeader(BPMSConstants.IDENTIFIER, String.class);
			if (identifier == null) {
				identifier = configuration.getIdentifier();
			}
			return identifier;
		}

		Integer getMaxNumber(BPMSRequest configuration, Exchange exchange) {
			Integer max = exchange.getIn().getHeader(BPMSConstants.MAX_NUMBER, Integer.class);
			if (max == null) {
				max = configuration.getMaxNumber();
			}
			return max;
		}

		Object getEvent(BPMSRequest configuration, Exchange exchange) {
			String event = exchange.getIn().getHeader(BPMSConstants.EVENT, String.class);
			if (event == null) {
				event = configuration.getEvent();
			}
			return event;
		}

		String getEventType(BPMSRequest configuration, Exchange exchange) {
			String eventType = exchange.getIn().getHeader(BPMSConstants.EVENT_TYPE, String.class);
			if (eventType == null) {
				eventType = configuration.getEventType();
			}
			return eventType;
		}

		String getProcessId(BPMSRequest configuration, Exchange exchange) {
			String processId = exchange.getIn().getHeader(BPMSConstants.PROCESS_ID, String.class);
			if (processId == null) {
				processId = configuration.getProcessId();
			}
			return processId;
		}

		Long getProcessInstanceId(BPMSRequest configuration, Exchange exchange) {
			Long processInstanceId = exchange.getIn().getHeader(BPMSConstants.PROCESS_INSTANCE_ID, Long.class);
			if (processInstanceId == null) {
				processInstanceId = configuration.getProcessInstanceId();
			}
			return processInstanceId;
		}

		Map<String, Object> getParameters(BPMSRequest configuration, Exchange exchange) {
			Map<String, Object> parameters = exchange.getIn().getHeader(BPMSConstants.PARAMETERS, Map.class);
			if (parameters == null) {
				parameters = configuration.getParameters();
			}
			return parameters;
		}

		Object getValue(BPMSRequest configuration, Exchange exchange) {
			Object value = exchange.getIn().getHeader(BPMSConstants.VALUE);
			if (value == null) {
				value = configuration.getValue();
			}
			return value;
		}

		Message getResultMessage(Exchange exchange) {
			return ExchangeHelper.isOutCapable(exchange) ? exchange.getOut() : exchange.getIn();
		}

		long safe(Long aLong) {
			return aLong != null ? aLong : 0;
		}

		void setResult(Exchange exchange, Object result) {
			getResultMessage(exchange).setBody(result);
		}

		abstract void execute(KieSession kieSession, TaskService taskService, BPMSRequest configuration,
				Exchange exchange);
	}

}
