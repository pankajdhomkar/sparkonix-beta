package com.sparkonix.resources;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.User.ROLE_TYPE;
import com.sparkonix.entity.dto.CompanyPanDTO;
import com.sparkonix.entity.dto.ManResDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/companydetail")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyDetailResource {

	private final CompanyDetailDAO companyDetailDAO;
	private final UserDAO userDAO;
	private final ResellerDAO resellerDAO;
	private final Logger log = Logger.getLogger(CompanyDetailResource.class.getName());

	public CompanyDetailResource(CompanyDetailDAO companyDetailDAO, UserDAO userDAO, ResellerDAO resellerDAO) {
		this.companyDetailDAO = companyDetailDAO;
		this.userDAO = userDAO;
		this.resellerDAO = resellerDAO;
	}

	@GET
	@UnitOfWork
	@Path("/{compDetailId}/{companyType}")
	public Response getCompanyDetailById(@Auth User authUser, @PathParam("compDetailId") long companyDetailId,
			@PathParam("companyType") String companyType) {
		
		ManResDTO manResDTO = new ManResDTO();
		// Declare a ManResDTO object for putting into a response

		if (companyType.equalsIgnoreCase("MANUFACTURER")) {
			try {
				log.info(" In getCompanyDetailById" + companyDetailId);
				manResDTO.setManResDetail(companyDetailDAO.getById(companyDetailId));
				
				return Response.status(Status.OK).entity(JsonUtils.getJson(manResDTO))
						.build();
			} catch (Exception e) {
				log.severe("Unable to find Company detail " + e);
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find Manufacturer detail")).build();
			}
		} else {
			try {
				System.out.println("Company Type in retriving a reseller data");
				log.info(" In getCompanyDetailById" + companyDetailId);
				manResDTO.setReseller(resellerDAO.getById(companyDetailId));
				return Response.status(Status.OK).entity(JsonUtils.getJson(manResDTO))
						.build();
			} catch (Exception e) {
				log.severe("Unable to find Company detail " + e);
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find Reseller detail")).build();
			}
		}

	}

	@PUT
	@UnitOfWork
	@Path("/{compDetailId}/{companyType}")
	public Response updateCompanyDetail(@Auth User authUser, @PathParam("compDetailId") long companyDetailId,
			 ManResDTO manResDetail, @PathParam("companyType") String companyType) {
		if (companyType.equalsIgnoreCase("MANUFACTURER") || companyType.equalsIgnoreCase("CUSTOMER")) {
			
			try {
				log.info(" In updateCompanyDetail");
				if (authUser == null) {
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
							.build();
				}
				CompanyDetail dbCompanyDetail = companyDetailDAO.getById(companyDetailId);
				if (dbCompanyDetail != null) {
					dbCompanyDetail.setCompanyName(manResDetail.getManResDetail().getCompanyName());
					dbCompanyDetail.setPan(manResDetail.getManResDetail().getPan());
					dbCompanyDetail.setCustSupportName(manResDetail.getManResDetail().getCustSupportName());
					dbCompanyDetail.setCustSupportPhone(manResDetail.getManResDetail().getCustSupportPhone());
					dbCompanyDetail.setCustSupportEmail(manResDetail.getManResDetail().getCustSupportEmail());
					dbCompanyDetail.setCurSubscriptionType(manResDetail.getManResDetail().getCurSubscriptionType());
					dbCompanyDetail
							.setCurSubscriptionStatus(manResDetail.getManResDetail().getCurSubscriptionStatus());
					dbCompanyDetail
							.setCurSubscriptionStartDate(manResDetail.getManResDetail().getCurSubscriptionStartDate());
					dbCompanyDetail
							.setCurSubscriptionEndDate(manResDetail.getManResDetail().getCurSubscriptionEndDate());

					companyDetailDAO.save(dbCompanyDetail);
				}
				log.info("Company Detail updated");
				return Response.status(Status.OK)
						.entity(JsonUtils.getSuccessJson("Company detail updated successfully")).build();
			} catch (Exception e) {
				log.severe("Unable to Update  " + e);
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Update ")).build();
			}
		} else {
			
			try {
				log.info("update Reseller Detail");
				if (authUser == null) {
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Authorization Failed"))
							.build();
				}
				Reseller resellerDetail = resellerDAO.getById(companyDetailId);
				System.out.println("--Reseller name" + resellerDetail.getCompanyName());
				if (resellerDetail != null) {
					resellerDetail.setFld_manufid(manResDetail.getReseller().getFld_manufid());
					resellerDetail.setCompanyName(manResDetail.getReseller().getCompanyName());
					resellerDetail.setPan(manResDetail.getReseller().getPan());
					resellerDetail.setCustSupportName(manResDetail.getReseller().getCustSupportName());
					resellerDetail.setCustSupportPhone(manResDetail.getReseller().getCustSupportPhone());
					resellerDetail.setCustSupportEmail(manResDetail.getReseller().getCustSupportEmail());
					resellerDetail.setCurSubscriptionType(manResDetail.getReseller().getCurSubscriptionType());
					resellerDetail.setCurSubscriptionStatus(manResDetail.getReseller().getCurSubscriptionStatus());
					resellerDetail.setCurSubscriptionStartDate(manResDetail.getReseller().getCurSubscriptionStartDate());
					resellerDetail.setCurSubscriptionEndDate(manResDetail.getReseller().getCurSubscriptionEndDate());

					resellerDAO.save(resellerDetail);
				}
				log.info("Reseller detail updated");
				return Response.status(Status.OK)
						.entity(JsonUtils.getSuccessJson("Company detail updated successfully")).build();
			} catch (Exception e) {
				log.severe("Unable to Update Reseller Detail " + e);
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to Update Reseller Detail")).build();
			}
		}
	}
	
	
	
	// This method gives a manufacturer name and its details to client
	@GET
	@UnitOfWork
	@Path("/{manId}")
	public Response getCompanyManufacturerId(@Auth User authUser, @PathParam("manId") long manId) {
		
		ManResDTO dto = new ManResDTO();
		try {
				System.out.println("Manufacturer id===="+ manId);	
				dto.setManResDetail(companyDetailDAO.getById(manId));
				return Response.status(Status.OK).entity(JsonUtils.getJson(dto))
						.build();
			} catch (Exception e) {
				log.severe("Unable to find Company detail " + e);
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find Manufacturer detail")).build();
			}
		

	}
	
	@GET
	@UnitOfWork
	@Path("/checkName/{resellerId}/{companyType}")
	public Response getDetailsOfResMan(@Auth User authUser, @PathParam("resellerId") long resellerId,
			@PathParam("companyType") String companyType) {

		ManResDTO manResDTO = new ManResDTO();
		// Declare a ManResDTO object for putting into a response

		if (companyType.equalsIgnoreCase("MANUFACTURER")) {
			try {
				log.info(" In getCompanyDetailById" + resellerId);
				manResDTO.setManResDetail(companyDetailDAO.getById(resellerId));
				System.out.println("-----------MANUFACTURER--------------"+resellerId);
				return Response.status(Status.OK).entity(JsonUtils.getJson(manResDTO))
						.build();
			} catch (Exception e) {
				log.severe("Unable to find Company detail " + e);
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find Manufacturer detail")).build();
			}
		} else {
			try {
				System.out.println("Company Type in retriving a reseller data");
				System.out.println("-----------reseller--------------"+resellerId);
				log.info(" In getCompanyDetailById" + resellerId);
				manResDTO.setReseller(resellerDAO.getById(resellerId));
				System.out.println("***********************"+JsonUtils.getJson(manResDTO));
				
				return Response.status(Status.OK).entity(JsonUtils.getJson(manResDTO))
						.build();
				
			} catch (Exception e) {
				System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]"+e);
				log.severe("Unable to find Company detail " + e);
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find Reseller detail")).build();
			}
		}
	}


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
	@Path("/editmanres/{companyDetailId}/{companyType}")
	public Response getManResDTO(@Auth User authUser, @PathParam("companyDetailId") long companyDetailId,
			@PathParam("companyType") String companyType) {

		System.out.println(companyType);
		log.info("In getManResDTO");
		ManResDTO manResDTO = new ManResDTO();
		if (companyType.equalsIgnoreCase("MANUFACTURER")) {
			try {
				CompanyDetail dbCompanyDetail = companyDetailDAO.getById(companyDetailId);
				System.out.println("1IN MANUFACTURER--->");
				if (dbCompanyDetail != null) {
					manResDTO.setManResDetail(dbCompanyDetail);
				} else {
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Unable to find company detail.")).build();
				}

				String role = role = User.ROLE_TYPE.MANUFACTURERADMIN.toString();
				/*
				 * if (dbCompanyDetail.getCompanyType().equals(CompanyDetail.
				 * COMPANY_TYPE.MANUFACTURER.toString())) { role =
				 * User.ROLE_TYPE.MANUFACTURERADMIN.toString(); } else if
				 * (dbCompanyDetail.getCompanyType().equals(CompanyDetail.
				 * COMPANY_TYPE.RESELLER.toString())) { role =
				 * User.ROLE_TYPE.RESELLERADMIN.toString(); }
				 */

				User dbUser = userDAO.getByCompanyDetailsIdAndRole(companyDetailId, role);
				if (dbUser != null) {
					manResDTO.setWebAdminUser(dbUser);
				} else {
					/*
					 * return Response.status(Status.BAD_REQUEST)
					 * .entity(JsonUtils.getErrorJson(
					 * "Unable to find company detail.")).build();
					 */
					manResDTO.setWebAdminUser(null);
				}
				System.out.println(
						"END OF MANU IN GET--" + manResDTO.getCompanyType() + "-" + manResDTO.getManResDetail());
				return Response.status(Status.OK).entity(manResDTO).build();

			} catch (Exception e) {
				log.severe("Unable to find company detail by id. Reason: " + e.getMessage());
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find company detail.")).build();
			}
		} else {
			try {
				Reseller reseller = resellerDAO.getById(companyDetailId);
				if (reseller != null) {
					manResDTO.setReseller(reseller);
				} else {
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Unable to find company detail.")).build();
				}

				String role = User.ROLE_TYPE.RESELLERADMIN.toString();

				User dbUser = userDAO.getByCompanyDetailsIdAndRole(companyDetailId, role);
				if (dbUser != null) {
					manResDTO.setWebAdminUser(dbUser);
				} else {
					/*
					 * return Response.status(Status.BAD_REQUEST)
					 * .entity(JsonUtils.getErrorJson(
					 * "Unable to find company detail.")).build();
					 */
					manResDTO.setWebAdminUser(null);
				}
				System.out.println("END OF MANU IN GET--" + reseller.getFld_manufid() + "-" + manResDTO.getReseller());
				return Response.status(Status.OK).entity(manResDTO).build();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.severe("Unable to find company detail by id. Reason: " + e.getMessage());
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Unable to find company detail.")).build();
			}
		}
	}

	@PUT
	@UnitOfWork
	@Path("/editmanres/{companyDetailId}/{companyType}")
	public Response updateManResDTO(@Auth User authUser, @PathParam("companyDetailId") long companyDetailId,
			ManResDTO manResDTO, @PathParam("companyType") String companyType) {
		log.info("In updateManResDTO");
		CompanyDetail manResDetail = manResDTO.getManResDetail();
		User manResWebAdmin = manResDTO.getWebAdminUser();
		Reseller resellerAdmin = manResDTO.getReseller();
		try {

			if (!companyType.equals("RESELLER")) {

				CompanyDetail dbCompanyDetail = companyDetailDAO.getById(companyDetailId);

				// update company details
				dbCompanyDetail.setCompanyName(manResDetail.getCompanyName());

				dbCompanyDetail.setCustSupportName(manResDetail.getCustSupportName());
				dbCompanyDetail.setCustSupportPhone(manResDetail.getCustSupportPhone());
				dbCompanyDetail.setCustSupportEmail(manResDetail.getCustSupportEmail());

				dbCompanyDetail.setCurSubscriptionType(manResDetail.getCurSubscriptionType());
				dbCompanyDetail.setCurSubscriptionStartDate(manResDetail.getCurSubscriptionStartDate());
				dbCompanyDetail.setCurSubscriptionEndDate(manResDetail.getCurSubscriptionEndDate());
				dbCompanyDetail.setCurSubscriptionStatus(manResDetail.getCurSubscriptionStatus());

				companyDetailDAO.save(dbCompanyDetail);

				// update web admin user
				if (manResWebAdmin != null) {
					User dbUser = userDAO.getById(manResWebAdmin.getId());

					dbUser.setName(manResWebAdmin.getName());
					dbUser.setMobile(manResWebAdmin.getMobile());
					dbUser.setAltEmail(manResWebAdmin.getAltEmail());

					userDAO.save(dbUser);
				}

			} else {

				Reseller dbReseller = resellerDAO.getById(companyDetailId);

				// update reseller
				// update company details
				dbReseller.setCompanyName(resellerAdmin.getCompanyName());

				dbReseller.setCustSupportName(resellerAdmin.getCustSupportName());
				dbReseller.setCustSupportPhone(resellerAdmin.getCustSupportPhone());
				dbReseller.setCustSupportEmail(resellerAdmin.getCustSupportEmail());

				dbReseller.setCurSubscriptionType(resellerAdmin.getCurSubscriptionType());
				dbReseller.setCurSubscriptionStartDate(resellerAdmin.getCurSubscriptionStartDate());
				dbReseller.setCurSubscriptionEndDate(resellerAdmin.getCurSubscriptionEndDate());
				dbReseller.setCurSubscriptionStatus(resellerAdmin.getCurSubscriptionStatus());

				resellerDAO.save(dbReseller);

				// update web admin user
				if (manResWebAdmin != null) {
					User dbUser = userDAO.getById(manResWebAdmin.getId());

					dbUser.setName(manResWebAdmin.getName());
					dbUser.setMobile(manResWebAdmin.getMobile());
					dbUser.setAltEmail(manResWebAdmin.getEmail());

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

	@GET
	@UnitOfWork
	@Path("/check/{companyType}/{companyPan}")
	public Response checkByCompanyTypeAndCompanyPan(@Auth User authUser, @PathParam("companyType") String companyType,
			@PathParam("companyPan") String companyPan) {
		try {
			log.info(" In checkByCompanyTypeAndCompanyPan");
			CompanyDetail companyDetail = companyDetailDAO.findCompanyDetailByPanAndCompanyType(companyPan,
					companyType);
			CompanyPanDTO companyPanDTO = new CompanyPanDTO();

			if (companyDetail == null) {
				companyPanDTO.setCompanyPanExist(false);
			} else {
				companyPanDTO.setCompanyPanExist(true);
			}
			return Response.status(Status.OK).entity(JsonUtils.getJson(companyPanDTO)).build();

		} catch (Exception e) {
			log.severe("Unable to find Company detail by PAN" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find company detail by PAN")).build();
		}
	}

}
