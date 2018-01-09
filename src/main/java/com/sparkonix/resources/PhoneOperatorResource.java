package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.PhoneOperatorDAO;
import com.sparkonix.entity.PhoneOperator;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.FcmTokenDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * @author Pankaj Dhomkar
 *
 */

@Path("/phoneoperator")
@Produces(MediaType.APPLICATION_JSON)
public class PhoneOperatorResource {
    private final PhoneOperatorDAO phoneOperatorDAO;
    private final Logger log = Logger.getLogger(PhoneOperatorResource.class.getName());

    public PhoneOperatorResource(PhoneOperatorDAO phoneOperatorDAO) {
	this.phoneOperatorDAO = phoneOperatorDAO;
    }

    @GET
    @UnitOfWork
    @Path("/{phoneOperatorId}")
    public Response getPhoneOperatorById(@Auth User authUser, @PathParam("phoneOperatorId") long phoneOperatorId) {
	try {
	    log.info(" In getClosedIssueById");
	    System.out.println("1.Phone Device---"+phoneOperatorId);
	    return Response.status(Status.OK).entity(JsonUtils.getJson(phoneOperatorDAO.getById(phoneOperatorId))).build();
	} catch (Exception e) {
	    log.severe("Unable to find Phone Device " + e);
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Phone Device"))
		    .build();
	}
    }
    
    @PUT
    @UnitOfWork
    @Path("/{phoneOperatorId}")
    public Response updatePhoneOperator(@Auth User authUser, PhoneOperator phoneOperator,
	    @PathParam("phoneDeviceId") long phoneDeviceId) {
	try {
	    log.info(" In updatePhoneDevice");
	    if (authUser == null) {
		return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
			.build();
	    }
	    PhoneOperator phoneDevice2 = phoneOperatorDAO.getById(phoneDeviceId);
	    if (phoneDevice2 != null) {
		phoneDevice2.setFld_name(phoneOperator.getFld_name());
		phoneDevice2.setFld_mobile_number(phoneOperator.getFld_mobile_number());
		phoneOperatorDAO.save(phoneDevice2);
	    }
	    log.info("Company Detail updated");
	    return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Operator updated successfully")).build();
	} catch (Exception e) {
	    log.severe("Unable to Update  " + e);
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update operator "))
		    .build();
	}
    }
    
    @PUT
	@UnitOfWork
	@Path("/fcmtoken")
	public Response updateFcmTokenForPhoneDevice(@Auth User authUser, FcmTokenDTO fcmTokenDTO) {

		PhoneOperator phoneDevice = phoneOperatorDAO.getOperatorByPhoneNumber(authUser.getEmail());

		if (phoneDevice == null) {
			log.severe("This is not authorized operator mobile number.");
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("This is not authorized operator mobile number")).build();
		} else {
			// update fcm token
			phoneDevice.setFld_fcm_token(fcmTokenDTO.getFcmToken());

			try {
				phoneOperatorDAO.save(phoneDevice);
				log.severe("Fcm token updated.");
				return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Fcm token updated")).build();
			} catch (Exception e) {
				log.severe("Fcm token not updated." + e.getMessage());
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to update fcm token"))
						.build();
			}

		}
	}

}
