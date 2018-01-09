package com.sparkonix.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sparkonix.ApplicationContext;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.CustomerDAO;
import com.sparkonix.dao.CustomerManufacturerIndexDAO;
import com.sparkonix.dao.CustomerResellerIndexDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.ManufacturerDAO;
import com.sparkonix.dao.PhoneOperatorDAO;
import com.sparkonix.dao.ResellerDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.Customer;
import com.sparkonix.entity.CustomerResellerIndex;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.CustomerDetailDTO;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.MailUtils;
import com.sparkonix.utils.SendMail;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/*
 * It will fetch a customer details and create a new customer
 * 
 */

@Path("/customerdetails")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerDetailsResource {
	private final CustomerDAO customerDAO;
	private final UserDAO userDAO;
	private final CompanyLocationDAO companyLocationDAO;
	private final PhoneOperatorDAO phoneOperatorDAO;
	private final ManufacturerDAO manufacturerDAO;
	private final ResellerDAO resellerDAO;
	private final MachineDAO machineDAO;
	private final CustomerResellerIndexDAO customerResellerIndexDAO;
	private final CustomerManufacturerIndexDAO customerManufacturerIndexDAO;

	private final Logger log = Logger.getLogger(CustomerDetailsResource.class.getName());

	public CustomerDetailsResource(CustomerDAO customerDAO, UserDAO userDAO, CompanyLocationDAO companyLocationDAO,
			PhoneOperatorDAO phoneOperatorDAO, ManufacturerDAO manufacturerDAO, ResellerDAO resellerDAO, MachineDAO machineDAO,
			CustomerResellerIndexDAO customerResellerIndexDAO, CustomerManufacturerIndexDAO customerManufacturerIndexDAO) {
		this.customerDAO = customerDAO;
		this.userDAO = userDAO;
		this.companyLocationDAO = companyLocationDAO;
		this.phoneOperatorDAO = phoneOperatorDAO;
		this.manufacturerDAO = manufacturerDAO;
		this.resellerDAO = resellerDAO;
		this.machineDAO = machineDAO;
		this.customerResellerIndexDAO = customerResellerIndexDAO;
		this.customerManufacturerIndexDAO = customerManufacturerIndexDAO;
	}

	@POST
	@UnitOfWork
	public Response createCustomer(@Auth User authUser, Customer customer){
		System.out.println("Company Pan Number------"+customer.getPan());
		// Here we check weather the pan is enterd or not
		try{
			if (customer.getPan().length() != 10) {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Enter 10 digit in company PAN.")).build();
			}
			//Here it may be change afterwards
			Customer dbCustomer = customerDAO.findCustomerByPan(customer.getPan());
			if (dbCustomer != null) {
				// exist
				Gson gson = new Gson();
				JsonObject json = new JsonObject();
				json.addProperty("message", "A customer already exist with this PAN, Please add factory location.");
				json.addProperty("entity", gson.toJson(dbCustomer));

				return Response.status(Status.OK).entity(json.toString()).build();
			} else {
				// not exist
				Gson gson = new Gson();
				JsonObject json = new JsonObject();
				json.addProperty("message", "New customer created successfully.");
				json.addProperty("entity", gson.toJson(customerDAO.save(customer)));
				if(authUser.getUser_role_id() == 3){
					
				}
				
				// send email to super admin
				String email = ApplicationContext.getInstance().getConfig().getSuperadminEmail();
				System.out.println("Email of Super admin--->"+email);
				
				JsonObject jsonObj = MailUtils.getAddCompanyMail(customer, authUser, email);
				new SendMail(jsonObj).run();
				Thread t1 = new Thread(new SendMail(jsonObj));
				t1.start();
				
				// send email to his reseller or manufacturer who added customer
				email = authUser.getEmail();
				JsonObject jsonObj2 = MailUtils.getAddCompanyMail(customer, authUser, email);
				new SendMail(jsonObj2).run();
				Thread t2 = new Thread(new SendMail(jsonObj2));
				t2.start();
				//log.info("Email sent to super admin");
				
				return Response.status(Status.OK).entity(json.toString()).build();
				
			}
		}catch(Exception e){
			log.severe("Failed to create customer. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to create customer"))
					.build();
		}
		
	}
	
	// Get the customer detail list
	@GET
	@UnitOfWork
	@Path("/{userId}/{userRoleId}")
	public Response listCustomerDetails(@Auth User authUser, @PathParam("userId") long userId,
			@PathParam("userRoleId") long userRoleId){
		//This arraylist will store the whole information
		List<Customer> listCustomerDetails = new ArrayList<>();
		if(authUser.getUser_role_id() == 1){ //Super admin login
			listCustomerDetails = customerDAO.findAll(); // Get the all customers
			//This if checks the user is manufacturer or reseller
		}else if(authUser.getUser_role_id() == 3){
			listCustomerDetails = customerDAO.findCustomerByManufacturerID(authUser.getManufacturer_id());
		}else if(authUser.getUser_role_id() == 4){
			listCustomerDetails = customerDAO.findCustomerByResellerID(authUser.getReseller_id());
		}
		
		List<CustomerDetailDTO> listCustDTO = new ArrayList<>();
		for(int i = 0; i < listCustomerDetails.size(); i++){
			CustomerDetailDTO customerDetailDTO = new CustomerDetailDTO();
			long customerId = listCustomerDetails.get(i).getId();
			//It will store a count of location
			long companyLocationCount = companyLocationDAO.getCountLocationByCustomerId(customerId);
			long machinesCount = machineDAO.getMachineCountByCustomerId(customerId);
//			long phoneDeviceCount =  phoneOperatorDAO.
			customerDetailDTO.setCustomerDetail(listCustomerDetails.get(i));
			customerDetailDTO.setFactoryLocationsCount(companyLocationCount);
			customerDetailDTO.setMachinesCount(machinesCount);
			
			listCustDTO.add(customerDetailDTO);
		}
		
		return Response.status(Status.OK).entity(JsonUtils.getJson(listCustDTO)).build();	
	}
	
	/*It will get the customer information according to a reseller id
	 * Here we first check the index table of customer and resellers
	 * Then from index table get the customer detail information
	 * */
	@GET
	@UnitOfWork
	@Path("/searchCustomer/{resellerId}")
	public Response listCompanyDetailsOnBoardedById(@Auth User authUser, @PathParam("resellerId") long resellerId) {
		try {
			log.info("In listCompanyDetailsOnBoardedById");
			// Get the resellers customer id  from the index table 
				List<CustomerResellerIndex> listCustomerId = new ArrayList<>();
				listCustomerId = customerResellerIndexDAO.findAllCustomerIdByResellerId(resellerId);
				
//				Creating a arraylist for stored the information in it.
				List<CustomerDetailDTO> listCustomerDetailsDTO = new ArrayList<>();
//				For loop on resellers customers
				for (int i = 0; i < listCustomerId.size(); i++) {
					System.out.println("Arraylist length---"+listCustomerId.size());
					long customerId = Long.parseLong(""+listCustomerId.get(i).getCust_id());
					System.out.println("Id of customer------>"+customerId+",I =="+i);
					CustomerDetailDTO customerDetailsDTO = new CustomerDetailDTO();
					/*long customerId = Long.parseLong(listCompanyDetails.get(i).toString());
					System.out.println("customer id---"+customerId);*/
					// CompanyLocationCount give the count of location.
					long companyLocationCount = companyLocationDAO.getCountLocationByCustomerId(customerId);
					long machinesCount = machineDAO.getMachineCountByCustomerId(customerId);
					

					customerDetailsDTO.setCustomerDetail(customerDAO.getById(customerId));
					customerDetailsDTO.setFactoryLocationsCount(companyLocationCount);
					customerDetailsDTO.setMachinesCount(machinesCount);

					listCustomerDetailsDTO.add(customerDetailsDTO);
				}
				return Response.status(Status.OK).entity(JsonUtils.getJson(listCustomerDetailsDTO)).build();
		} catch (Exception e) {
			log.severe("Unable get customer id" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable get customer id")).build();
		}
	}

}
