package com.sparkonix.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.annotations.Any;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.CustomerDetailsDTO;
import com.sparkonix.entity.dto.ManResDTO;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendMail;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import net.sourceforge.argparse4j.annotation.Arg;

@Path("/companydetails")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyDetailsResource {

	private final CompanyDetailDAO companyDetailDAO;
	private final CompanyLocationDAO companyLocationDAO;
	private final MachineDAO machineDAO;
	private final PhoneDeviceDAO phoneDeviceDAO;
	private final UserDAO userDAO;
	private final ResellerDAO resellerDAO; 
	
	
	private final Logger log = Logger.getLogger(CompanyDetailsResource.class.getName());

	public CompanyDetailsResource(CompanyDetailDAO companyDetailDAO, CompanyLocationDAO companyLocationDAO,
			MachineDAO machineDAO, PhoneDeviceDAO phoneDeviceDAO, UserDAO userDAO, ResellerDAO resellerDAO) {
		this.companyDetailDAO = companyDetailDAO;
		this.companyLocationDAO = companyLocationDAO;
		this.machineDAO = machineDAO;
		this.phoneDeviceDAO = phoneDeviceDAO;
		this.userDAO = userDAO;
		this.resellerDAO = resellerDAO;
	}

	@POST
	@UnitOfWork
	public Response createCompanyDetail(@Auth User authUser, CompanyDetail companyDetail) {
		try {
			//synchronized(CompanyDetail.class){
			System.out.println("Company Type---------"+companyDetail.getCompanyType());
			if (companyDetail.getCompanyType().equals("CUSTOMER")) {
				System.out.println("Company Pan Number------"+companyDetail.getPan());
				if (companyDetail.getPan().length() != 10) {
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Enter 10 digit in company PAN.")).build();
				}

				CompanyDetail dbCompanyDetail = companyDetailDAO
						.findCompanyDetailByPanAndCompanyType(companyDetail.getPan(), companyDetail.getCompanyType());

				if (dbCompanyDetail != null) {
					// exist
					Gson gson = new Gson();
					JsonObject json = new JsonObject();
					json.addProperty("message", "A customer already exist with this PAN, Please add factory location.");
					json.addProperty("entity", gson.toJson(dbCompanyDetail));

					return Response.status(Status.OK).entity(json.toString()).build();
				} else {
					// not exist
					Gson gson = new Gson();
					JsonObject json = new JsonObject();
					json.addProperty("message", "New customer created successfully.");
					json.addProperty("entity", gson.toJson(companyDetailDAO.save(companyDetail)));

					// send email to super admin
					JsonObject jsonObj = MailUtils.getAddCompanyMail(companyDetail, authUser);
					new SendMail(jsonObj).run();
					Thread t1 = new Thread(new SendMail(jsonObj));
					t1.start();
					
					//log.info("Email sent to super admin");

					return Response.status(Status.OK).entity(json.toString()).build();
					// return
					// Response.status(Status.OK).entity(companyDetailDAO.save(companyDetail)).build();
				}
			}
			//end of synch
			//}
			
		} catch (Exception e) {
			log.severe("Failed to create customer. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to create customer"))
					.build();
		}
		return null;

		/*try {
			if (companyDetail.getCompanyType().equals("MANUFACTURER")
					|| companyDetail.getCompanyType().equals("RESELLER")) {

				CompanyDetail dbCompanyDetail = companyDetailDAO
						.findCompanyDetailByPanAndCompanyType(companyDetail.getPan(), companyDetail.getCompanyType());

				if (dbCompanyDetail != null) {
					// exist
					return Response.status(Status.BAD_REQUEST)
							.entity(JsonUtils.getErrorJson("Company Detail with given PAN is already exist.")).build();
				} else {
					// not exist
					return Response.status(Status.OK).entity(companyDetailDAO.save(companyDetail)).build();
				}

			}
		} catch (Exception e) {
			log.severe("Failed to create Company detail. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Failed to create Company detail.")).build();
		}
		return null;*/

	}

	@GET
	@UnitOfWork
	public Response listCompanyDetails(@Auth User authUser) {
		try {
			log.info("In listCompanyDetails");
			return Response.status(Status.OK).entity(JsonUtils.getJson(companyDetailDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Compnay Details " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Company Details"))
					.build();
		}
	}
	

	@GET
	@Path("/{companyType}")
	@UnitOfWork
	public Response listCompanyDetailsByCompanyType(@Auth User authUser, @PathParam("companyType") String companyType) {
		try {
			if (companyType.equalsIgnoreCase("MANUFACTURER")) {
				log.info("In listCompanyDetailsByCompanyType");
				System.out.println("---" + JsonUtils.getJson(companyDetailDAO.findAllByCompanyType(companyType)));
				return Response.status(Status.OK)
						.entity(JsonUtils.getJson(companyDetailDAO.findAllByCompanyType(companyType))).build();
			}else{
				log.info("In listReseller");
				
				return Response.status(Status.OK)
						.entity(JsonUtils.getJson(resellerDAO.findAll())).build();
			}
		} catch (Exception e) {
			log.severe("Unable to find Company Details by Company Type" + e);
			System.out.println("-----------------------------------------------"+e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Company Details by Company Type")).build();
		}
	}
	
	//Added new for the getting a manufacturer's reseller 
	@GET
	@Path("/reseller/{manufacturerId}")
	@UnitOfWork
	public Response listCompanyDetailsByResellersOfManufacturer(@Auth User authUser, @PathParam("manufacturerId") long manufacturerId){
		try {
			System.out.println("---------------------------------------------------------------");
			System.out.println("ID---" + manufacturerId);
			log.info("In listReseller");
			return Response.status(Status.OK)
					.entity(JsonUtils.getJson(resellerDAO.findAllById(manufacturerId))).build();
		}catch (Exception e) {
			// TODO: handle exception
			log.severe("Unable to find Resellers" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Resellers")).build();
		}
	}
	


	@GET
	@UnitOfWork
	@Path("/{onBoardedById}/{userRole}/{companyType}")
	public Response listCompanyDetailsOnBoardedById(@Auth User authUser, @PathParam("onBoardedById") long onBoardedById,
			@PathParam("userRole") String userRole, @PathParam("companyType") String companyType) {
		try {
			log.info("In listCompanyDetailsOnBoardedById");
			// used for manage customer view page
			if (companyType.equalsIgnoreCase("CUSTOMER")) {
				// get count of location+machines+operators
				List<CompanyDetail> listCompanyDetails = new ArrayList<>();

				if (userRole.equals("SUPERADMIN")) {
					listCompanyDetails = companyDetailDAO.findAllByCompanyType(companyType);

				} else if (userRole.equals("SALESTEAM") || userRole.equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())
						|| userRole.equals(User.ROLE_TYPE.RESELLERADMIN.toString())) {
					listCompanyDetails = companyDetailDAO.findAllByOnBoardedId(onBoardedById, userRole, companyType);
				}

				List<CustomerDetailsDTO> listCustomerDetailsDTO = new ArrayList<>();

				for (int i = 0; i < listCompanyDetails.size(); i++) {
					CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
					long customerId = listCompanyDetails.get(i).getId();
					long companyLocationCount = companyLocationDAO.getCountByCustomerIdAndOnBoardedBy(customerId,
							onBoardedById, userRole);
					long machinesCount = machineDAO.getCountByCustomerIdAndOnBoardedBy(customerId, onBoardedById,
							userRole);
					long operatorsCount = phoneDeviceDAO.getCountByCustomerIdAndOnBoardedBy(customerId, onBoardedById,
							userRole);

					customerDetailsDTO.setCompanyDetail(listCompanyDetails.get(i));
					customerDetailsDTO.setFactoryLocationsCount(companyLocationCount);
					customerDetailsDTO.setMachinesCount(machinesCount);
					customerDetailsDTO.setOperatorsCount(operatorsCount);

					listCustomerDetailsDTO.add(customerDetailsDTO);
				}
				return Response.status(Status.OK).entity(JsonUtils.getJson(listCustomerDetailsDTO)).build();

			}

			// used for Manufacturers and Resellers view page
			if (companyType.equalsIgnoreCase("MANUFACTURER") /*|| companyType.equalsIgnoreCase("RESELLER")*/) {

				// show all to super admin
				if (userRole.equals("SUPERADMIN")) {
					return Response.status(Status.OK)
							.entity(JsonUtils.getJson(companyDetailDAO.findAllByCompanyType(companyType))).build();

				} else if (userRole.equals("SALESTEAM")
						|| userRole.equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())) {
					// show only onBoarded by him
					return Response.status(Status.OK)
							.entity(JsonUtils
									.getJson(companyDetailDAO.findAllByOnBoardedAndType(onBoardedById, companyType)))
							.build();
				}
			}else{
				//This for viewing reseller
				// show all to super admin
				if (userRole.equals("SUPERADMIN")) {
					System.out.println("dsfasfasdfasdfr"+resellerDAO.findAll());
					return Response.status(Status.OK)
							.entity(JsonUtils.getJson(resellerDAO.findAll())).build();

				} else if (userRole.equals("SALESTEAM")
						|| userRole.equals(User.ROLE_TYPE.MANUFACTURERADMIN.toString())) {
//					System.out.println("wwwwwwwwwwwwww"+resellerDAO.findAllByOnBoarded(onBoardedById));
					// show only onBoarded by him
					return Response.status(Status.OK)
							.entity(JsonUtils
									.getJson(resellerDAO.findAllByOnBoarded(onBoardedById)))
							.build();
				}
			}
		} catch (Exception e) {
			log.severe("Unable to find Company Details by onBoardedID " + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find Company Details for logged in user")).build();
		}
		return null;
	}

	@GET
	@UnitOfWork
	@Path("/searchCustomer/{resellerId}")
	public Response listCompanyDetailsOnBoardedById(@Auth User authUser, @PathParam("resellerId") long resellerId) {
		try {
			log.info("In listCompanyDetailsOnBoardedById");
//				ListCompanyDetalis object get the resellers customers.
				List<Machine> listCompanyDetails = new ArrayList<>();
				listCompanyDetails = machineDAO.findAllCustomerIdByResellerId(resellerId);
				
//				Creating a arraylist for stored the information in it.
				List<CustomerDetailsDTO> listCustomerDetailsDTO = new ArrayList<>();
//				For loop on resellers customers
				for (int i = 0; i < listCompanyDetails.size(); i++) {
					System.out.println("Arraylist length---"+listCompanyDetails.size());
					long customerId = Long.parseLong(""+listCompanyDetails.get(i));
					System.out.println("Id of customer------>"+customerId+",I =="+i);
					CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
					/*long customerId = Long.parseLong(listCompanyDetails.get(i).toString());
					System.out.println("customer id---"+customerId);*/
					// CompanyLocationCount give the count of location.
					long companyLocationCount = companyLocationDAO.getCountLocationByCustomerId(customerId);
					long machinesCount = machineDAO.getCountMAchineByCustomerId(customerId);
					long operatorsCount = phoneDeviceDAO.getCountOperatorCustomerId(customerId);

					customerDetailsDTO.setCompanyDetail(companyDetailDAO.getCompanyDetailsByID(customerId));
					customerDetailsDTO.setFactoryLocationsCount(companyLocationCount);
					customerDetailsDTO.setMachinesCount(machinesCount);
					customerDetailsDTO.setOperatorsCount(operatorsCount);

					listCustomerDetailsDTO.add(customerDetailsDTO);
				}
				return Response.status(Status.OK).entity(JsonUtils.getJson(listCustomerDetailsDTO)).build();
		} catch (Exception e) {
			log.severe("Unable get customer id" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable get customer id")).build();
		}
	}
	
	/*
	 * used for adding new Manufacturer/Reseller by salesteam/superadmin
	 * 
	 * @param authUser
	 *            user is authenticated by his username and password.
	 * @param manResDTO
	 *            its payload which contain object of {@link CompanyDetail} and
	 *            {@link User}
	 * @return success message if manufacturer/reseller created
	 */
	@POST
	@UnitOfWork
	@Path("addmanres")
	public Response createCompanyDetailForManRes(@Auth User authUser, ManResDTO manResDTO) {
		
		System.out.println(manResDTO.getCompanyType());
		
		CompanyDetail manDetail = manResDTO.getManResDetail();
		User manResWebAdmin = manResDTO.getWebAdminUser();
		Reseller resDetail = manResDTO.getReseller();
		
		System.out.println("------------Company Type------------------"+manResDTO.getCompanyType());
		if (manDetail.getPan().length() != 10) {
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Enter 10 digit in company PAN."))
					.build();
		}
		
		
		CompanyDetail dbCompanyDetail = companyDetailDAO.findCompanyDetailByPanAndCompanyType(manDetail.getPan(),
				manResDTO.getCompanyType());
		User dbUser = null;
		System.out.println("--------"+manDetail.getPan());
				
		Reseller dbResellerDetail = resellerDAO.findResellerDetailByPan(manDetail.getPan());
		if (manResWebAdmin != null) {
			dbUser = userDAO.findByEmail(manResWebAdmin.getEmail());
		}
		if (dbCompanyDetail != null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson(
							"A " + manDetail.getCompanyType().toLowerCase() + " already exists with this PAN."))
					.build();
		} else if(dbResellerDetail != null){
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson(
							"A " + manDetail.getCompanyType().toLowerCase() + " already exists with this PAN."))
					.build();
		}else if (dbUser != null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("A web admin already exists with this email.")).build();
		}
		try {
			// save CompanyDetail
			if(manResDTO.getCompanyType().equalsIgnoreCase("MANUFACTURER")){
				
				CompanyDetail newCompanyDetail = companyDetailDAO.save(manDetail);
				System.out.println("Kattapaaa"+newCompanyDetail.getCompanyName());
				if (manResWebAdmin != null) {
					manResWebAdmin.setRole(User.ROLE_TYPE.MANUFACTURERADMIN.toString());
					manResWebAdmin.setCompanyDetailsId(newCompanyDetail.getId());
					// save User
					userDAO.save(manResWebAdmin);
				}
				// send email to super admin
				JsonObject jsonObj = MailUtils.getAddCompanyMail(newCompanyDetail, authUser);
				new SendMail(jsonObj).run();
				Thread t2 = new Thread(new SendMail(jsonObj));
				t2.start();
				log.info("Email sent to super admin");
			}else{
				
				resDetail.setCompanyName(manDetail.getCompanyName()); // Company Name
				resDetail.setPan(manDetail.getPan()); //Pan
				resDetail.setCustSupportName(manDetail.getCustSupportName()); //Contact Person
				resDetail.setCustSupportPhone(manDetail.getCustSupportPhone());//fld_contact_person_number
				resDetail.setCustSupportEmail(manDetail.getCustSupportEmail());//fld_contact_person_email
				resDetail.setCurSubscriptionType(manDetail.getCurSubscriptionType());//fld_subscription_type
				resDetail.setCurSubscriptionStatus(manDetail.getCurSubscriptionStatus());//fld_subscription_status
				resDetail.setCurSubscriptionStartDate(manDetail.getCurSubscriptionStartDate());//fld_subscription_start_date
				resDetail.setCurSubscriptionEndDate(manDetail.getCurSubscriptionEndDate());//fld_subscription_end_date
				resDetail.setOnBoardedBy(manDetail.getOnBoardedBy());//fld_on_boarded_by
				
				Reseller newReseller = resellerDAO.save(resDetail);
				if (manResWebAdmin != null) {
					manResWebAdmin.setRole(User.ROLE_TYPE.RESELLERADMIN.toString());
					manResWebAdmin.setCompanyDetailsId(newReseller.getId());
					// save User
					userDAO.save(manResWebAdmin);
				}
				// send email to super admin
				JsonObject jsonObj = MailUtils.getAddCompanyMailReseller(newReseller, authUser);
				new SendMail(jsonObj).run();
				Thread t2 = new Thread(new SendMail(jsonObj));
				t2.start();
				log.info("Email sent to super admin");
			}
//			
			/*CompanyDetail newCompanyDetail = companyDetailDAO.save(manDetail);
			if (manResWebAdmin != null) {
				String role = null;
				if (newCompanyDetail.getCompanyType().equals(CompanyDetail.COMPANY_TYPE.MANUFACTURER.toString())) {
					role = User.ROLE_TYPE.MANUFACTURERADMIN.toString();

				} else if (newCompanyDetail.getCompanyType().equals(CompanyDetail.COMPANY_TYPE.RESELLER.toString())) {
					role = User.ROLE_TYPE.RESELLERADMIN.toString();
				}

				manResWebAdmin.setRole(role);
				manResWebAdmin.setCompanyDetailsId(newCompanyDetail.getId());

				// save User
				userDAO.save(manResWebAdmin);
			}
		

			// send email to super admin
			JsonObject jsonObj = MailUtils.getAddCompanyMail(newCompanyDetail, authUser);
			//new SendMail(jsonObj).run();
			Thread t2 = new Thread(new SendMail(jsonObj));
			t2.start();
			log.info("Email sent to super admin");*/

			return Response.status(Status.OK)
					.entity(JsonUtils.getSuccessJson(
							"A " + manDetail.getCompanyType().toLowerCase() + " has been created successfully."))
					.build();
		} catch (Exception e) {
			log.severe("Failed to create company detail. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Failed to create company detail.")).build();
		}
		
	}
}
