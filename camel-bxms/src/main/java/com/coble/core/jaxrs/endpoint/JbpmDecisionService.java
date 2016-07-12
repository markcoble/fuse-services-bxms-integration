package com.coble.core.jaxrs.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Stub interface for using CXF RS client in the <code>JBPMCxfRsRouteBuilder</code>. 
 * 
 * @author Mark Coble
 */
public interface JbpmDecisionService {
	
	@POST
	@Path(value="/")
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_XML)
	public String kickStartProcess();

}
