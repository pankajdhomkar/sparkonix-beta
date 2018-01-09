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

import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.ManufacturerDAO;
import com.sparkonix.dao.PhoneOperatorDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.dao.UserRoleIndexDAO;
import com.sparkonix.entity.Manufacturer;
import com.sparkonix.entity.Reseller;
import com.sparkonix.entity.User;
import com.sparkonix.entity.UserRoleIndex;
import com.sparkonix.entity.dto.ManufacturerResellerDTO;
import com.sparkonix.utils.JsonUtils;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/manufacturerdetails")
@Produces(MediaType.APPLICATION_JSON)
public class ManufacturerDetailsResource {
    private final ManufacturerDAO manufacturerDAO;
    private final CompanyLocationDAO companyLocationDAO;
    private final MachineDAO machineDAO;
    private final PhoneOperatorDAO phoneOperatorDAO;
    private final UserDAO userDAO;
    private final ResellerDAO resellerDAO;
    private final UserRoleIndexDAO userRoleIndexDAO;

    private final Logger log = Logger.getLogger(ManufacturerDetailsResource.class.getName());

    public ManufacturerDetailsResource(ManufacturerDAO manufacturerDAO, CompanyLocationDAO companyLocationDAO,
	    MachineDAO machineDAO, PhoneOperatorDAO phoneOperatorDAO, UserDAO userDAO, ResellerDAO resellerDAO,
	    UserRoleIndexDAO userRoleIndexDAO) {
	this.manufacturerDAO = manufacturerDAO;
	this.companyLocationDAO = companyLocationDAO;
	this.machineDAO = machineDAO;
	this.phoneOperatorDAO = phoneOperatorDAO;
	this.userDAO = userDAO;
	this.resellerDAO = resellerDAO;
	this.userRoleIndexDAO = userRoleIndexDAO;
    }

    // This macro service will serve a list of manufacturer
    @GET
    @Path("/infomanufacturer")
    @UnitOfWork
    public Response listCompanyDetailsByCompanyType(@Auth User authUser) {
	try {
	    log.info("In list Manufacturer Details");
	    return Response.status(Status.OK).entity(JsonUtils.getJson(manufacturerDAO.findAll())).build();
	} catch (Exception e) {
	    log.severe("Unable to find Company Details by Company Type " + e);
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson("Unable to find Manufacturer Details")).build();
	}
    }

    @GET
    @UnitOfWork
    @Path("/{onBoardedById}/{userRoleId}/{companyType}")
    public Response listCompanyDetailsOnBoardedById(@Auth User authUser, @PathParam("onBoardedById") long onBoardedById,
	    @PathParam("userRoleId") long userRoleId, @PathParam("companyType") String companyType) {
	// used for Manufacturers and Resellers view page
	try {
	    System.out.println("value of id------>"+onBoardedById);
	    if (companyType.equalsIgnoreCase(
		    "MANUFACTURER") 
		      ||
		      companyType.equalsIgnoreCase("RESELLER")
		     ) {

		// show all to super admin
		if (userRoleId == 1) { // SUPERADMIN =1
		    return Response.status(Status.OK).entity(JsonUtils.getJson(manufacturerDAO.findAll())).build();

		} else if (userRoleId == 2 // SALESTEAM = 2
			|| userRoleId == 3) { // MANUFACTURERADMIN = 3
		    // show only onBoarded by him
		    return Response.status(Status.OK)
			    .entity(JsonUtils.getJson(manufacturerDAO.findAllByOnBoardedId(onBoardedById, companyType)))
			    .build();
		}
	    } else {
		// This for viewing reseller
		// show all to super admin
		if (userRoleId == 1) { // SUPERADMIN =1
		    System.out.println("dsfasfasdfasdfr" + resellerDAO.findAll());
		    return Response.status(Status.OK).entity(JsonUtils.getJson(resellerDAO.findAll())).build();

		} else if (userRoleId == 2 // SALESTEAM = 2
			|| userRoleId == 4) { // Reseller = 4
		    // System.out.println("wwwwwwwwwwwwww"+resellerDAO.findAllByOnBoarded(onBoardedById));
		    // show only onBoarded by him
		    return Response.status(Status.OK)
			    .entity(JsonUtils.getJson(resellerDAO.findAllByOnBoarded(onBoardedById))).build();
		}
	    }
	} catch (Exception e) {
	    log.severe("Unable to find Company Details by onBoardedID " + e);
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson("Unable to find Company Details for logged in user")).build();
	}
	return null;
    }

    //get the company details by a company type
    @GET
    @Path("/{companyType}")
    @UnitOfWork
    public Response listCompanyDetailsByCompanyType(@Auth User authUser, @PathParam("companyType") String companyType) {
	System.out.println("In listCompanyDetailsByCompanyType");
	System.out.println("Authenticated user" + authUser);
	try {
	    if (companyType.equalsIgnoreCase("MANUFACTURER")) {
		System.out.println("In listCompanyDetailsByCompanyType");
		System.out.println("Authenticated user" + authUser);
		return Response.status(Status.OK)
			.entity(JsonUtils.getJson(manufacturerDAO.findAll())).build();
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

    @POST
    @UnitOfWork
    @Path("addmanres")
    public Response createCompanyDetailForManRes(@Auth User authUser, ManufacturerResellerDTO manResDTO) {
	log.info("In createCompanyDetailForManRes");
	Manufacturer manDetail = manResDTO.getManufacturer();
	User manResWebAdmin = manResDTO.getWebAdminUser();
	Reseller resDetail = manResDTO.getReseller();

	if (manDetail.getPan().length() != 10) {
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Enter 10 digit in company PAN."))
		    .build();
	}

	Manufacturer dbCompanyDetail = manufacturerDAO.findmanufacturerDetailByPan(manDetail.getPan());
	User dbUser = null;
	UserRoleIndex userRoleIndex = new UserRoleIndex();
	System.out.println("--------"+manDetail.getPan());

	Reseller dbResellerDetail = resellerDAO.findResellerDetailByPan(manDetail.getPan());
	if (manResWebAdmin != null) {
	    dbUser = userDAO.findByEmail(manResWebAdmin.getEmail());
	}
	if (dbCompanyDetail != null) {
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson(
			    "A " + manDetail.getCompany_name() + " already exists with this PAN."))
		    .build();
	} else if(dbResellerDetail != null){
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson(
			    "A " + resDetail.getCompany_name() + " already exists with this PAN."))
		    .build();
	}else if (dbUser != null) {
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson("A web admin already exists with this email.")).build();
	}
	try {
	    // save CompanyDetail
	    if(manResDTO.getCompanyType().equalsIgnoreCase("MANUFACTURER")){

		Manufacturer newCompanyDetail = manufacturerDAO.save(manDetail);
		System.out.println("IN THE IF---->"+newCompanyDetail.getCompany_name());
		if (manResWebAdmin != null) {
		    // It will set the role id in the user table here a 3 is Manufacturer
		    manResWebAdmin.setUser_role_id(3); 
		    //manResWebAdmin.setRole(User.ROLE_TYPE.MANUFACTURERADMIN.toString());
		    manResWebAdmin.setManufacturer_id(newCompanyDetail.getId());

		    // save User
		    userDAO.save(manResWebAdmin);
		    // save the user table role 
		    userRoleIndex.setUser_id(manResWebAdmin.getId());
		    userRoleIndex.setUser_role_id(3);//Here manufacturer = 3
		    userRoleIndexDAO.save(userRoleIndex);

		}
		// send email to super admin
		System.out.println("Kattapaaa"+newCompanyDetail.getCompany_name()+" USER NAME- "+authUser);
		//				JsonObject jsonObj = MailUtils.getAddCompanyMailManufacturer(newCompanyDetail, authUser);
		//				new SendMail(jsonObj).run();
		//				Thread t2 = new Thread(new SendMail(jsonObj));
		//				t2.start();
		log.info("Email sent to super admin");
	    }else{
		// It will set a all data to a reseller object
		//				resDetail.setCompany_name(manDetail.getCompany_name()); // Company Name
		//				resDetail.setPan(manDetail.getPan()); //Pan
		//				resDetail.setCustSupportName(resDetail.getCustSupportName()); //Contact Person
		//				resDetail.setCustSupportPhone(manDetail.getCustSupportPhone());//fld_contact_person_number
		//				resDetail.setCustSupportEmail(manDetail.getCustSupportEmail());//fld_contact_person_email
		//				resDetail.setCurSubscriptionType(manDetail.getCurSubscriptionType());//fld_subscription_type
		//				resDetail.setCurSubscriptionStatus(manDetail.getCurSubscriptionStatus());//fld_subscription_status
		//				resDetail.setCurSubscriptionStartDate(manDetail.getCurSubscriptionStartDate());//fld_subscription_start_date
		//				resDetail.setCurSubscriptionEndDate(manDetail.getCurSubscriptionEndDate());//fld_subscription_end_date
		//				resDetail.setOnBoardedBy(manDetail.getOnBoardedBy());//fld_on_boarded_by

		Reseller newReseller = resellerDAO.save(resDetail);
		if (manResWebAdmin != null) {
		    //					manResWebAdmin.setRole(User.ROLE_TYPE.RESELLERADMIN.toString());
		    // It will set the role id in the user table here a 3 is Manufacturer
		    manResWebAdmin.setUser_role_id(3); 
		    manResWebAdmin.setReseller_id(newReseller.getId());
		    // save User
		    userDAO.save(manResWebAdmin);
		    // save the user table role 
		    userRoleIndex.setUser_id(manResWebAdmin.getId());
		    userRoleIndex.setUser_role_id(3);//Here manufacturer = 3
		    userRoleIndexDAO.save(userRoleIndex);
		}
		// send email to super admin
		System.out.println("Kattapaaa"+newReseller.getCompany_name()+" USER NAME- "+authUser);
//		JsonObject jsonObj = MailUtils.getAddCompanyMailReseller(newReseller, authUser);
		//				new SendMail(jsonObj).run();
		//				Thread t2 = new Thread(new SendMail(jsonObj));
		//				t2.start();
		log.info("Email sent to super admin");
	    }
	    String test = "A " + manResDTO.getCompanyType().toLowerCase() + " has been created successfully.";
	    return Response.status(Status.OK).entity(JsonUtils.getSuccessJson(test)).build();
	} catch (Exception e) {
	    log.severe("Failed to create company detail. Reason: " + e.getMessage());
	    return Response.status(Status.BAD_REQUEST)
		    .entity(JsonUtils.getErrorJson("Failed to create company detail.")).build();
	}
    }

}