package com.sparkonix.resources;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.MachineAmcServiceHistoryDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.MachineAmcServiceHistory;
import com.sparkonix.entity.User;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Saves the new MachineAmcServiceHistory and return same with id
 * 
 * @author Pankaj Dhomkar
 *
 */

@Path("/machineamcservicehistories")
@Produces(MediaType.APPLICATION_JSON)
public class MachineAmcServiceHistoriesResource {
    private final MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO;
    private final MachineDAO machineDAO;
    private final UserDAO userDAO;
    private final Logger log = Logger.getLogger(MachineAmcServiceHistoriesResource.class.getName());

    public MachineAmcServiceHistoriesResource(MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO,
	    MachineDAO machineDAO, UserDAO userDAO) {
	this.machineAmcServiceHistoryDAO = machineAmcServiceHistoryDAO;
	this.machineDAO = machineDAO;
	this.userDAO = userDAO;
    }
 
/*
 * return the all list of services 
 * MachineAmcServiceHistories
 * 
 */
    @GET
	@UnitOfWork
	public Response listMachineAmcServiceHistories(@Auth User authUser) {
	try {
		log.info(" In listMachineAmcServiceHistories");
		return Response.status(Status.OK).entity(JsonUtils.getJson(machineAmcServiceHistoryDAO.findAll())).build();
	} catch (Exception e) {
		log.severe("Unable to find Machines AMC Service Histories " + e);
		return Response.status(Status.BAD_REQUEST)
				.entity(JsonUtils.getErrorJson("Unable to find Machines AMC Service Histories")).build();
	}
    }
/*-
 * Return the machine service list
 * for Reseller and Manufacturer, Technician 
 * 
 */
    @GET
    @Path("/{category}/{parameter}")
    @UnitOfWork
    public Response listServicesByCategory(@Auth User authUser, @PathParam("category") String category,
		@PathParam("parameter") long parameter) {
	try {
		log.info(" In listServicesByCategory");
		List<MachineAmcServiceHistory> machineAmcServiceHistoryList = null;
		if("technician".equals(category)){ //For Technician
		    User userObj = userDAO.getById(parameter);
		    if (userObj != null) {
			machineAmcServiceHistoryList = machineAmcServiceHistoryDAO.getServicesForTechnician(userObj);
		    }
		} else {
			machineAmcServiceHistoryList = machineAmcServiceHistoryDAO.getServicesByCategory(category, parameter);
		}
		return Response.status(Status.OK).entity(JsonUtils.getJson(machineAmcServiceHistoryList)).build();
	} catch (Exception e) {
		log.severe("Unable to find Machines AMC Service Histories " + e);
		return Response.status(Status.BAD_REQUEST)
				.entity(JsonUtils.getErrorJson("Unable to find Machines AMC Service Histories")).build();
	}
    }

}
