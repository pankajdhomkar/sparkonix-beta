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

import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.ManufacturerResellerDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/resellerdetail")
@Produces(MediaType.APPLICATION_JSON)
public class ResellerDetailResource {
	private final UserDAO userDAO;
	private final ResellerDAO resellerDAO;

	private final Logger log = Logger.getLogger(ResellerDetailResource.class.getName());

	public ResellerDetailResource(UserDAO userDAO, ResellerDAO resellerDAO) {
		this.userDAO = userDAO;
		this.resellerDAO = resellerDAO;
	}

	@GET
	@UnitOfWork
	@Path("/info/{resellerId}")
	public Response getDetailsOfReseller(@Auth User authUser, @PathParam("resellerId") long resellerId) {
		ManufacturerResellerDTO objectManufacReseller = new ManufacturerResellerDTO();
		try {
			objectManufacReseller.setReseller(resellerDAO.getById(resellerId));
			return Response.status(Status.OK).entity(JsonUtils.getJson(objectManufacReseller)).build();
		} catch (Exception e) {
			log.severe("Unable to find Company detail " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Reseller detail"))
					.build();
		}
	}

	@PUT
	@UnitOfWork
	@Path("/info/{resellerId}")
	public Response updateDetailsOfReseller(@Auth User authUser, @PathParam("resellerId") long resellerId,
			ManufacturerResellerDTO resellerDetailDTO) {
		log.info("update Reseller Detail");
		if (authUser == null) {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed")).build();
		}
		try {
			Reseller resellerDetail = resellerDAO.getById(resellerId);
			log.info("1 Reseller Detail Resource");
			if (resellerDetail != null) {
				resellerDetail.setFld_manuid(resellerDetailDTO.getReseller().getFld_manuid());
				resellerDetail.setCompany_name(resellerDetailDTO.getReseller().getCompany_name());
				resellerDetail.setPan(resellerDetailDTO.getReseller().getPan());
				resellerDetail.setCust_support_name(resellerDetailDTO.getReseller().getCust_support_name());
				resellerDetail.setCust_support_phone(resellerDetailDTO.getReseller().getCust_support_phone());
				resellerDetail.setCust_support_email(resellerDetailDTO.getReseller().getCust_support_email());
				resellerDetail.setCur_subscription_type(resellerDetailDTO.getReseller().getCur_subscription_type());
				resellerDetail.setCur_subscription_status(resellerDetailDTO.getReseller().getCur_subscription_status());
				resellerDetail
						.setCur_subscription_startdate(resellerDetailDTO.getReseller().getCur_subscription_startdate());
				resellerDetail
						.setCur_subscription_enddate(resellerDetailDTO.getReseller().getCur_subscription_enddate());

				resellerDAO.save(resellerDetail);
			}
			log.info("Reseller detail updated");
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Reseller detail updated successfully"))
					.build();
		} catch (Exception e) {
			log.severe("Unable to Update Reseller Detail " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to Update Reseller Detail")).build();
		}
	}

	//For view a reseller
	/**
	 * this method is used to get the {@link CompanyDetail} & {@link User} by
	 * using companyDetailId for edit Manufacturer/Reseller by
	 * salesTeam/superAdmin
	 * 
	 * @param authUser
	 *            user is authenticated by his username and password.
	 * @param companyDetailId
	 * @return response as object of {@link ManResDTO}
	 */
	@GET
	@UnitOfWork
	@Path("/editreseller/{resellerId}/{companyType}")
	public Response getManResDTO(@Auth User authUser, @PathParam("resellerId") long resellerId,
			@PathParam("companyType") long companyType) {
		log.info("In getManResDTO - ResellerDetail Resource");
		ManufacturerResellerDTO resellerDTO = new ManufacturerResellerDTO();
		Reseller reseller;
		try {
			reseller = resellerDAO.getById(resellerId);

			if (reseller != null) {
				resellerDTO.setReseller(reseller);
			} else {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find reseller detail.")).build();
			}

			User dbUser = userDAO.findByResellerIdRoleID(resellerId, companyType);

			if (dbUser != null) {
				resellerDTO.setWebAdminUser(dbUser);
			} else {
				resellerDTO.setWebAdminUser(null);
			}
			System.out.println("END OF RESELLER");
			return Response.status(Status.OK).entity(resellerDTO).build();
		} catch (Exception e) {
			log.severe("Unable to find company detail by id. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find reseller detail."))
					.build();
		}
	}

	// Here it will update the reseller when it is edited or added
	@PUT
	@UnitOfWork
	@Path("/editreseller/{resellerId}/{companyType}")
	public Response updateManResDTO(@Auth User authUser, @PathParam("resellerId") long resellerId, ManufacturerResellerDTO resellerDTO,
			@PathParam("companyType") long companyType) {
		log.info("In updateManResDTO");
		
		User manResWebAdmin = resellerDTO.getWebAdminUser();
		Reseller resellerAdmin = resellerDTO.getReseller();
		try {
			if(resellerAdmin !=null){
				Reseller dbReseller = resellerDAO.getById(resellerId);

				// update reseller
				// update company details
				dbReseller.setCompany_name(resellerAdmin.getCompany_name());

				dbReseller.setCust_support_name(resellerAdmin.getCust_support_name());
				dbReseller.setCust_support_phone(resellerAdmin.getCust_support_phone());
				dbReseller.setCust_support_email(resellerAdmin.getCust_support_email());

				dbReseller.setCur_subscription_type(resellerAdmin.getCur_subscription_type());
				dbReseller.setCur_subscription_startdate(resellerAdmin.getCur_subscription_startdate());
				dbReseller.setCur_subscription_enddate(resellerAdmin.getCur_subscription_enddate());
				dbReseller.setCur_subscription_status(resellerAdmin.getCur_subscription_status());

				resellerDAO.save(dbReseller);

			}
			
			// update web admin user
			if (manResWebAdmin != null) {
				User dbUser = userDAO.getById(manResWebAdmin.getId());

				dbUser.setName(manResWebAdmin.getName());
				dbUser.setMobile(manResWebAdmin.getMobile());
				dbUser.setAlt_email(manResWebAdmin.getEmail());

				userDAO.save(dbUser);

			}

			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson(companyType + " has been updated successfully.")).build();

		} catch (Exception e) {
			log.severe("Unable to update company detail. Error: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to update company detail.")).build();
		}

	}

}
