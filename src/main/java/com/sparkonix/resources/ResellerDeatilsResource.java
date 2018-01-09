package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.ManufacturerResellerDTO;
import com.sparkonix.utils.JsonUtils;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/*
 * All the reseller related work done here multiple handling a data 
 */
@Path("/resellerdetails")
@Produces(MediaType.APPLICATION_JSON)
public class ResellerDeatilsResource {
	
	private final UserDAO userDAO;
	private final ResellerDAO resellerDAO; 
	
	private final Logger log = Logger.getLogger(ResellerDeatilsResource.class.getName());

	public ResellerDeatilsResource(UserDAO userDAO, ResellerDAO resellerDAO) {
		this.userDAO = userDAO;
		this.resellerDAO = resellerDAO;
	}
	
	// It will return the all information of reseller by manufacturer id
	@GET
	@Path("/info/{manufacturerId}")
	@UnitOfWork
	public Response listresellerDetailsByResellersOfManufacturerId(@Auth User authUser, @PathParam("manufacturerId") long manufacturerId){
		try {
			log.info("(Resellers resource) 1 Manufacturer id -->" + manufacturerId);
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(resellerDAO.findAllResellerByManufacturerId(manufacturerId))).build();
		}catch (Exception e) {
			// TODO: handle exception
			log.severe("Unable to find Resellers" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Resellers")).build();
		}
	}
	
	@POST
	@UnitOfWork
	@Path("addreseller")
	public Response createCompanyDetailForManRes(@Auth User authUser, ManufacturerResellerDTO manResDTO) {
		
		
		User manResWebAdmin = manResDTO.getWebAdminUser();
		Reseller resDetail = manResDTO.getReseller();
		
		User dbUser = null;
	
		Reseller dbResellerDetail = resellerDAO.findResellerDetailByPan(resDetail.getPan());
		if (manResWebAdmin != null) {
			dbUser = userDAO.findByEmail(manResWebAdmin.getEmail());
		}
		if(dbResellerDetail != null){
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson(
							"A " + resDetail.getCompany_name().toLowerCase() + " already exists with this PAN."))
					.build();
		}else if (dbUser != null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("A web admin already exists with this email.")).build();
		}
		try {
			// save CompanyDetail
			// It will set a all data to a reseller object
				resDetail.setCompany_name(resDetail.getCompany_name()); // Company Name
				resDetail.setPan(resDetail.getPan()); //Pan
				resDetail.setCust_support_name(resDetail.getCust_support_name()); //Contact Person
				resDetail.setCust_support_phone(resDetail.getCust_support_phone());//fld_contact_person_number
				resDetail.setCust_support_email(resDetail.getCust_support_email());//fld_contact_person_email
				resDetail.setCur_subscription_type(resDetail.getCur_subscription_type());//fld_subscription_type
				resDetail.setCur_subscription_status(resDetail.getCur_subscription_status());//fld_subscription_status
				resDetail.setCur_subscription_startdate(resDetail.getCur_subscription_startdate());//fld_subscription_start_date
				resDetail.setCur_subscription_enddate(resDetail.getCur_subscription_enddate());//fld_subscription_end_date
				
				Reseller newReseller = resellerDAO.save(resDetail);
				if (manResWebAdmin != null) {
					manResWebAdmin.setUser_role_id(2);
					manResWebAdmin.setReseller_id(newReseller.getId());
					// save User
					userDAO.save(manResWebAdmin);
				}
				// send email to super admin
				System.out.println("Kattapaaa"+newReseller.getCompany_name()+" USER NAME- "+authUser);
//				JsonObject jsonObj = MailUtils.getAddCompanyMailReseller(newReseller, authUser);
//				new SendMail(jsonObj).run();
//				Thread t2 = new Thread(new SendMail(jsonObj));
//				t2.start();
//				log.info("Email sent to super admin");
			

			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson(
							"A reseller has been created successfully."))
					.build();
		} catch (Exception e) {
			log.severe("Failed to create company detail. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Failed to create company detail.")).build();
		}
		
	}
	
}
