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

import com.sparkonix.dao.ManufacturerDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.CompanyPanDTO;
import com.sparkonix.entity.dto.ManufacturerResellerDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/manufacturerdetail")
@Produces(MediaType.APPLICATION_JSON)
public class ManufacturerDetailResource {
	private final UserDAO userDAO;
	private final ManufacturerDAO manufacturerDAO;
	ResellerDAO resellerDAO;

	private final Logger log = Logger.getLogger(ManufacturerDetailResource.class.getName());

	public ManufacturerDetailResource(UserDAO userDAO, ManufacturerDAO manufacturerDAO, ResellerDAO resellerDAO) {
		this.userDAO = userDAO;
		this.manufacturerDAO = manufacturerDAO;
		this.resellerDAO = resellerDAO;
	}

	// This method gives a manufacturer name and its details to client
	@GET
	@UnitOfWork
	@Path("/info/{manId}")
	public Response getCompanyManufacturerId(@Auth User authUser, @PathParam("manId") long manId) {

		ManufacturerResellerDTO manufacturerdto = new ManufacturerResellerDTO();
		try {
			System.out.println("Manufacturer id - " + manId);
			manufacturerdto.setManufacturer(manufacturerDAO.getById(manId));
			return Response.status(Status.OK).entity(JsonUtils.getJson(manufacturerdto)).build();
		} catch (Exception e) {
			log.severe("Unable to find manufacturer detail " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Manufacturer detail")).build();
		}
	}
	
	@PUT
	@UnitOfWork
	@Path("/info/{manId}")
	public Response updateCompanyManufactrerId(@Auth User authUser,@PathParam("manId") long manId, ManufacturerResellerDTO manufacturerdto){
		log.info(" In updateManufacturerDetail");
		if (authUser == null) {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
					.build();
		}
		
		try {
			Manufacturer manuDeatilUpdate = manufacturerDAO.getById(manId);
			
			manuDeatilUpdate.setCompany_name(manufacturerdto.getManufacturer().getCompany_name());
			manuDeatilUpdate.setPan(manufacturerdto.getManufacturer().getPan());
			manuDeatilUpdate.setCust_support_name(manufacturerdto.getManufacturer().getCust_support_name());
			manuDeatilUpdate.setCust_support_phone(manufacturerdto.getManufacturer().getCust_support_phone());
			manuDeatilUpdate.setCust_support_email(manufacturerdto.getManufacturer().getCust_support_email());
			manuDeatilUpdate.setCur_subscription_type(manufacturerdto.getManufacturer().getCur_subscription_type());
			manuDeatilUpdate
					.setCur_subscription_status(manufacturerdto.getManufacturer().getCur_subscription_status());
			manuDeatilUpdate
					.setCur_subscription_startdate(manufacturerdto.getManufacturer().getCur_subscription_startdate());
			manuDeatilUpdate
					.setCur_subscription_enddate(manufacturerdto.getManufacturer().getCur_subscription_enddate());

			manufacturerDAO.save(manuDeatilUpdate);
			
			log.info("Manufacturer Detail updated");
			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson("Manufacturer detail updated successfully")).build();
		} catch (Exception e) {
			log.severe("Unable to Update  " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Update ")).build();
		}
	}
	
	@GET
	@UnitOfWork
	@Path("/check/{companyType}/{companyPan}")
	public Response checkByCompanyTypeAndCompanyPan(@Auth User authUser, @PathParam("companyType") String companyType,
			@PathParam("companyPan") String companyPan) {
		try {
			log.info(" In checkByCompanyTypeAndCompanyPan");
			// Check Manufacturer's pan number
			CompanyPanDTO companyPanDTO = new CompanyPanDTO();
			if(companyType.equals("MANUFACTURER")){  
				Manufacturer manufacturer = manufacturerDAO.findmanufacturerDetailByPan(companyPan);
				
				if(manufacturer == null){
					companyPanDTO.setCompanyPanExist(false);
				}else{
					companyPanDTO.setCompanyPanExist(true);
				}
				
			}else if(companyType.equals("RESELLER")){// Check Reseller's pan number
				Reseller reseller = resellerDAO.findResellerDetailByPan(companyPan);
				
				if (reseller == null) {
					companyPanDTO.setCompanyPanExist(false);
				} else {
					companyPanDTO.setCompanyPanExist(true);
				}
				
			}
			return Response.status(Status.OK).entity(JsonUtils.getJson(companyPanDTO)).build();
		} catch (Exception e) {
			log.severe("Unable to find Company detail by PAN" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find company detail by PAN")).build();
		}
	}
	
	@GET
	@UnitOfWork
	@Path("/editmanufacturer/{manufacturerDetailId}/{companyType}")
	public Response getManufacturerDTO(@Auth User authUser, @PathParam("manufacturerDetailId") long manufacturerDetailId,
			@PathParam("companyType") String companyType) {

		System.out.println(companyType);
		log.info("In getManResDTO");
		ManufacturerResellerDTO manufacturerDTO = new ManufacturerResellerDTO();
				try {
				Manufacturer dbmanufacturerDetail = manufacturerDAO.getById(manufacturerDetailId);
				System.out.println("1IN MANUFACTURER--->");
				if (dbmanufacturerDetail != null) {
					manufacturerDTO.setManufacturer(dbmanufacturerDetail);
				} else {
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Unable to find company detail.")).build();
				}

//				String role = role = User.ROLE_TYPE.MANUFACTURERADMIN.toString();
				/*
				 * if (dbCompanyDetail.getCompanyType().equals(CompanyDetail.
				 * COMPANY_TYPE.MANUFACTURER.toString())) { role =
				 * User.ROLE_TYPE.MANUFACTURERADMIN.toString(); } else if
				 * (dbCompanyDetail.getCompanyType().equals(CompanyDetail.
				 * COMPANY_TYPE.RESELLER.toString())) { role =
				 * User.ROLE_TYPE.RESELLERADMIN.toString(); }
				 */

				User dbUser = userDAO.findByManufacturerIdRoleID(manufacturerDetailId, 3);//Manufacturer admin role = 3
				if (dbUser != null) {
					manufacturerDTO.setWebAdminUser(dbUser);
				} else {
					/*
					 * return Response.status(Status.BAD_REQUEST)
					 * .entity(JsonUtils.getErrorJson(
					 * "Unable to find company detail.")).build();
					 */
					manufacturerDTO.setWebAdminUser(null);
				}
				System.out.println(
						"END OF MANU IN GET--" + manufacturerDTO.getCompanyType() + "-" + manufacturerDTO.getManufacturer());
				return Response.status(Status.OK).entity(manufacturerDTO).build();

			} catch (Exception e) {
				log.severe("Unable to find company detail by id. Reason: " + e.getMessage());
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find company detail.")).build();
			}
		}

	@PUT
	@UnitOfWork
	@Path("/editmanufacturer/{companyDetailId}/{companyType}")
	public Response updateManufacturerDTO(@Auth User authUser, @PathParam("manufacturerDetailId") long manufacturerDetailId,
			ManufacturerResellerDTO manufacturerDTO, @PathParam("companyType") String companyType) {
		log.info("In updateManResDTO");
		Manufacturer manDetail = manufacturerDTO.getManufacturer();
		User manResWebAdmin = manufacturerDTO.getWebAdminUser();
		Reseller resellerAdmin = manufacturerDTO.getReseller();
		try {

			if (!companyType.equals("RESELLER")) {

				Manufacturer dbCompanyDetail = manufacturerDAO.getById(manufacturerDetailId);

				// update company details
				dbCompanyDetail.setCompany_name(manDetail.getCompany_name());

				dbCompanyDetail.setCust_support_name(manDetail.getCust_support_name());
				dbCompanyDetail.setCust_support_phone(manDetail.getCust_support_phone());
				dbCompanyDetail.setCust_support_email(manDetail.getCust_support_email());

				dbCompanyDetail.setCur_subscription_type(manDetail.getCur_subscription_type());
				dbCompanyDetail.setCur_subscription_startdate(manDetail.getCur_subscription_startdate());
				dbCompanyDetail.setCur_subscription_enddate(manDetail.getCur_subscription_enddate());
				dbCompanyDetail.setCur_subscription_status(manDetail.getCur_subscription_status());

				manufacturerDAO.save(dbCompanyDetail);

				// update web admin user
				if (manResWebAdmin != null) {
					User dbUser = userDAO.getById(manResWebAdmin.getId());

					dbUser.setName(manResWebAdmin.getName());
					dbUser.setMobile(manResWebAdmin.getMobile());
					dbUser.setEmail(manResWebAdmin.getEmail());

					userDAO.save(dbUser);
				}

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
