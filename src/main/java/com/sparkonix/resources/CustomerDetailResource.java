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

import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.CustomerDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Customer;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.CustomerDetailDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/*
 * Resource class for the customer detail save and update the customer information 
 * 
 */

@Path("/customerdetail")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerDetailResource {
	private final CustomerDAO customerDAO;
	private final UserDAO userDAO;
	private final MachineDAO machineDAO;
	private final CompanyLocationDAO companyLocationDAO;
	
	private final Logger log = Logger.getLogger(CustomerDetailResource.class.getName());

	public CustomerDetailResource(CustomerDAO customerDAO, UserDAO userDAO, MachineDAO machineDAO,
			CompanyLocationDAO companyLocationDAO) {
		this.customerDAO = customerDAO;
		this.userDAO = userDAO;
		this.machineDAO = machineDAO;
		this.companyLocationDAO = companyLocationDAO;
	}
	
	@GET
	@UnitOfWork
	@Path("/{customerDetailId}")
	public Response getCompanyDetailById(@Auth User authUser, @PathParam("customerDetailId") long customerDetailId) {
		CustomerDetailDTO customerDetailDTO = new CustomerDetailDTO();
		
		try {
			customerDetailDTO.setCustomerDetail(customerDAO.getById(customerDetailId));
			
			return Response.status(Status.OK).entity(JsonUtils.getJson(customerDetailDTO))
					.build();
		} catch (Exception e) {
			log.severe("Unable to find Customer detail " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Customer detail")).build();
		}
		
	}
	
	@PUT
	@UnitOfWork
	@Path("/{customerDetailId}")
	public Response updateCompanyDetail(@Auth User authUser, @PathParam("customerDetailId") long customerDetailId,
			CustomerDetailDTO customerDetailDTO) {
		if (authUser == null) {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
					.build();
		}
		
		try {
			Customer dbCustomer = customerDAO.getById(customerDetailId);
			dbCustomer.setCompany_name(customerDetailDTO.getCustomerDetail().getCompany_name());
			dbCustomer.setPan(customerDetailDTO.getCustomerDetail().getPan());
			dbCustomer.setCust_support_name(customerDetailDTO.getCustomerDetail().getCust_support_name());
			dbCustomer.setCust_support_email(customerDetailDTO.getCustomerDetail().getCust_support_email());
			dbCustomer.setCust_support_phone(customerDetailDTO.getCustomerDetail().getCust_support_phone());
			
			customerDAO.save(dbCustomer);
			
			log.info("Customer Detail updated");
			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson("Customer detail updated successfully")).build();
		} catch (Exception e) {
			log.severe("Unable to Update customer detail" + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Update Customer Detail")).build();
		}
	}
}
