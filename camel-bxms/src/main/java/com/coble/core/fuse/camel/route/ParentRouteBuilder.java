package com.coble.core.fuse.camel.route;

import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

import com.coble.core.fuse.CamelConstants;

/**
 * Base abstract route which adds a common exception handling pattern to all
 * services.
 */
@Component
public abstract class ParentRouteBuilder extends SpringRouteBuilder implements CamelConstants {

	@Override
	public void configure() throws Exception {

		onException(Exception.class).handled(true)
				.idempotentConsumer(header(HEADER_ID), MemoryIdempotentRepository.memoryIdempotentRepository(100))
				.beanRef(BEAN_COMMON_ENRICHER, "enrichErrorHeaders").inOut(URL_AUDIT_MESSAGE).markRollbackOnly();
	}
}
