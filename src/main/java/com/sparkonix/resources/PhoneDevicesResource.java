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

import com.google.gson.JsonObject;
import com.sparkonix.auth.JwtToken;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.dao.UnregisterOperatorDAO;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.UnregisterOperator;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.PhoneDeviceOtpDTO;

import com.sparkonix.entity.jsonb.CompanyLocationAddress;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.Otp;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/phonedevices")
@Produces(MediaType.APPLICATION_JSON)
public class PhoneDevicesResource {

	private final PhoneDeviceDAO phoneDeviceDAO;
	private final CompanyLocationDAO companyLocationDAO;

	
	private final Logger log = Logger.getLogger(PhoneDevicesResource.class.getName());
	private UnregisterOperatorDAO unregisterOperatorDAO;

	public PhoneDevicesResource(PhoneDeviceDAO phoneDeviceDAO, CompanyLocationDAO companyLocationDAO, UnregisterOperatorDAO unregisterOperatorDAO) {
		this.phoneDeviceDAO = phoneDeviceDAO;
		this.companyLocationDAO = companyLocationDAO;
		this.unregisterOperatorDAO = unregisterOperatorDAO;
	}

	/**
	 * This method is used to register the authorized mobile number for
	 * Operator.
	 * 
	 * @param authUser
	 * @param phoneDevice
	 *            Its authorized phone device (aka Operator)
	 * @return The object {@link PhoneDevice}
	 */
	@POST
	@UnitOfWork
	public Response createPhoneDevice(@Auth User authUser, PhoneDevice phoneDevice) throws Exception {
		try {

			PhoneDevice dbPhoneDevice = phoneDeviceDAO.getOperatorByPhoneNumber(phoneDevice.getPhoneNumber());
			if (dbPhoneDevice != null) {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("The Operator mobile number is already registered.")).build();
			}
			
			//---here We Append '+' to the phone number
			if (!phoneDevice.getPhoneNumber().contains("+")) {
				
				String pho = "+" + phoneDevice.getPhoneNumber();
				System.out.println("-------------------" + pho);
				phoneDevice.setPhoneNumber(pho);
			}
			
			PhoneDevice phoneDevice2 = phoneDeviceDAO.save(phoneDevice);
			if (phoneDevice2 != null) {
				// send sms
				JsonObject jsonObj = new JsonObject();
				jsonObj.addProperty("toMobileNumber",phoneDevice2.getPhoneNumber().replaceAll("\\+",""));
				jsonObj.addProperty("smsMessage",
						"You have been registered to use the Attendme application. "
						+ "Click here https://goo.gl/0glP8q to download.");

				// send SMS(using textlocal)
				new SendSMS(jsonObj).run(); //**********************************SMS SEND OPERATIONs
				log.info("Registraion success sms has sent to operator");
			}
			return Response.status(Status.OK).entity(phoneDevice2).build();
		} catch (Exception e) {
			log.severe("Failed to create Operator. Error:" + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to create Operator."))
					.build();
		}
	}

	@GET
	@UnitOfWork
	public Response listPhoneDevices(@Auth User authUser) {
		try {
			log.info(" In listPhoneDevices");
			return Response.status(Status.OK).entity(JsonUtils.getJson(phoneDeviceDAO.findAll())).build();
		} catch (Exception e) {
			log.severe("Unable to find Phone devices" + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find Phone devices"))
					.build();
		}
	}

	
	@POST
	@UnitOfWork
	@Path("/otp")///{phoneNumber}/*@PathParam("phoneNumber") String phoneNumber2*/
	public Response getOtp(PhoneDeviceOtpDTO dtoObject) {
		//remove this line after UI fixes done in apk(to accept 10digit number with country code)
		String phoneNumber="+"+dtoObject.getPhoneNumber();		
		System.out.println(phoneNumber);
		PhoneDevice operatorObj = phoneDeviceDAO.getOperatorByPhoneNumber(phoneNumber);
		/*This if number is not present then it will add a number into a unrecognized table in database and 
		after it will stored in phoneDevice table*/
		if (operatorObj == null) {
//			System.out.println("no not in the phonedevice database----------"+operatorObj);
			log.severe("inserting a unregister operator");
//			System.out.println("1here upto--------------------"+phoneNumber);
			String otp = Otp.generate();
			System.out.println("2here upto--------------------"+otp);
			UnregisterOperator unregisterOperatorObj = unregisterOperatorDAO.getOperatorByPhoneNumber1(phoneNumber);
//			System.out.println("3here upto--------------------"+unregisterOperatorObj);
			if(unregisterOperatorObj == null){
//				System.out.println("4here upto--------------------"+phoneNumber);
				unregisterOperatorObj = new UnregisterOperator();
				unregisterOperatorObj.setFld_mobile_number(phoneNumber);
				unregisterOperatorObj.setFldOperatorName_unregister(dtoObject.getName());
				unregisterOperatorObj.setFldOtp_unregister(otp);
				unregisterOperatorDAO.save(unregisterOperatorObj);				
				
//				System.out.println("5here upto--------------------"+phoneNumber);
			}else{
//				System.out.println("6here upto--------------------"+phoneNumber);
				unregisterOperatorObj.setFldOtp_unregister(otp);
				
			}
//			System.out.println("8here upto--------------------"+phoneNumber);
			//insert into a tbl_unregisterOperator
			log.info("OTP generated - For new operator");
			JsonObject jsonObjSms = new JsonObject();
			jsonObjSms.addProperty("toMobileNumber", phoneNumber.replaceAll("\\+",""));
			jsonObjSms.addProperty("smsMessage", "OTP for the AttendMe application is " + otp);

			/* send SMS(using TEXTLOCAL) */
			new SendSMS(jsonObjSms).run();
			log.info("OTP sms sent to mobile number  - PhoneDevicesResource Class- getOtp method in 1st else");
			
			
			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("OTP has been sent")).build();				
			
		} else {
			String otp = Otp.generate();
			log.info("OTP generated - PhoneDevicesResource Class- getOtp method 1st else");

			// update OTP in db
			operatorObj.setOtp(otp);
			JsonObject jsonObjSms = new JsonObject();
			jsonObjSms.addProperty("toMobileNumber", phoneNumber.replaceAll("\\+",""));
			jsonObjSms.addProperty("smsMessage", "OTP for the AttendMe application is " + otp);

			/* send SMS(using TEXTLOCAL) */
			new SendSMS(jsonObjSms).run();
			log.info("OTP sms sent to mobile number  - PhoneDevicesResource Class- getOtp method in 1st else");

			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("OTP has been sent")).build();
		}
	}
	
	

	
	@POST
	@UnitOfWork
	@Path("/otp/validate")
	public Response validateOtp(PhoneDeviceOtpDTO phoneDeviceOtpDTO) {
		phoneDeviceOtpDTO.setPhoneNumber("+"+phoneDeviceOtpDTO.getPhoneNumber());
		System.out.print("In validate method---"+phoneDeviceDAO.getOperatorByPhoneNumber(phoneDeviceOtpDTO.getPhoneNumber()));
		try {
			if (phoneDeviceOtpDTO.getPhoneNumber().equals("") || phoneDeviceOtpDTO.getOtp().equals("")) {
				log.severe("Mobile number or OTP should not be empty.");
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Mobile number or OTP should not be empty")).build();
			}
			PhoneDevice operatorObj = phoneDeviceDAO.getOperatorByPhoneNumber(phoneDeviceOtpDTO.getPhoneNumber());
			if (operatorObj != null) {
				System.out.println("----ob-" + operatorObj.getOtp());
				System.out.println("------" + operatorObj.getOtp().equals(phoneDeviceOtpDTO.getOtp()));
				if (operatorObj.getOtp().equals(phoneDeviceOtpDTO.getOtp())) {
					log.info("OTP is valid.");
					// generate token for @Auth
					User userObj = new User();
					userObj.setRole("OPERATOR");
					userObj.setEmail(phoneDeviceOtpDTO.getPhoneNumber());

					// generate token
					String token = JwtToken.generateToken(phoneDeviceOtpDTO.getPhoneNumber(), userObj);
					log.info("JWT token generated.");
					userObj.setToken(token);

					JsonObject jsonObj = new JsonObject();
					jsonObj.addProperty("success", "OTP is valid");
					jsonObj.addProperty("token", token);

					// set OTP to null
					operatorObj.setOtp(null);
					log.info("OTP value updated as null.");

					// updating fcm token for push notification
					if (phoneDeviceOtpDTO.getFcmToken() == null || phoneDeviceOtpDTO.getFcmToken().trim().isEmpty()) {
						log.severe("Fcm token is empty.");
					} else {
						operatorObj.setFcmToken(phoneDeviceOtpDTO.getFcmToken());
						log.info("Updated fcm token value.");
					}

					return Response.status(Status.OK).entity(jsonObj.toString()).build();

				} else {
					log.severe("OTP is not valid.");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("OTP is not valid"))
							.build();
				} 
			}else{
				UnregisterOperator unregisterOperatorObj = unregisterOperatorDAO.getOperatorByPhoneNumber1(phoneDeviceOtpDTO.getPhoneNumber());
				if(unregisterOperatorObj.getFldOtp_unregister().equals(phoneDeviceOtpDTO.getOtp())){
					log.info("OTP is valid.");
					// generate token for @Auth
					User userObj = new User();
					userObj.setRole("OPERATOR");
					userObj.setEmail(phoneDeviceOtpDTO.getPhoneNumber());

					// generate token
					String token = JwtToken.generateToken(phoneDeviceOtpDTO.getPhoneNumber(), userObj);
					log.info("JWT token generated.");
					userObj.setToken(token);

					JsonObject jsonObj = new JsonObject();
					jsonObj.addProperty("success", "OTP is valid");
					jsonObj.addProperty("token", token);

					//setting unregister operator name 
					unregisterOperatorObj.setFldOperatorName_unregister(phoneDeviceOtpDTO.getName());
					
					// set OTP to null
					unregisterOperatorObj.setFldOtp_unregister(null);
					log.info("OTP value updated as null.");

					// updating fcm token for push notification
					if (phoneDeviceOtpDTO.getFcmToken() == null || phoneDeviceOtpDTO.getFcmToken().trim().isEmpty()) {
						log.severe("Fcm token is empty.");
					} else {
						unregisterOperatorObj.setFldFcmToken_unregister(phoneDeviceOtpDTO.getFcmToken());
						log.info("Updated fcm token value.");
					}

					return Response.status(Status.OK).entity(jsonObj.toString()).build();
				}else{
					log.severe("OTP is not valid.");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("OTP is not valid"))
							.build();
				}
			}
		} catch (Exception e) {
			log.severe("Unable to validate otp. Error: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to validate otp")).build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{customerId}/{onBoardedById}")
	public Response listPhoneDevicesByCustIdAndOnBoardedId(@Auth User authUser,
			@PathParam("customerId") long customerId, @PathParam("onBoardedById") long onBoardedById) {
		try {
			log.info(" In listPhoneDevicesByCustIdAndOnBoardedId");

			List<PhoneDevice> phoneDevicesList = phoneDeviceDAO.findAllByCustomerIdAndOnBoardedId(customerId,
					onBoardedById);

			List<PhoneDevice> devicesList = new ArrayList<>();

			for (int i = 0; i < phoneDevicesList.size(); i++) {
				PhoneDevice phoneDevice = new PhoneDevice();

				phoneDevice.setId(phoneDevicesList.get(i).getId());
				phoneDevice.setOperatorName(phoneDevicesList.get(i).getOperatorName());
				phoneDevice.setPhoneNumber(phoneDevicesList.get(i).getPhoneNumber());
				phoneDevice.setCustomerId(phoneDevicesList.get(i).getCustomerId());

				CompanyLocation locationObj = companyLocationDAO.getById(phoneDevicesList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				phoneDevice.setCompanyLocation(locationObj);

				phoneDevice.setOnBoardedBy(phoneDevicesList.get(i).getOnBoardedBy());

				devicesList.add(phoneDevice);
			}
			return Response.status(Status.OK).entity(devicesList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine list " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine list"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("/{customerId}")
	public Response listPhoneDevicesByCustomerId(@Auth User authUser, @PathParam("customerId") long customerId) {
		try {
			log.info(" In listPhoneDevicesByCustomerId");

			List<PhoneDevice> phoneDevicesList = phoneDeviceDAO.findAllByCustomerId(customerId);

			List<PhoneDevice> devicesList = new ArrayList<>();

			for (int i = 0; i < phoneDevicesList.size(); i++) {
				PhoneDevice phoneDevice = new PhoneDevice();

				phoneDevice.setId(phoneDevicesList.get(i).getId());
				phoneDevice.setOperatorName(phoneDevicesList.get(i).getOperatorName());
				phoneDevice.setPhoneNumber(phoneDevicesList.get(i).getPhoneNumber());
				phoneDevice.setCustomerId(phoneDevicesList.get(i).getCustomerId());

				CompanyLocation locationObj = companyLocationDAO.getById(phoneDevicesList.get(i).getLocationId());
				String address = locationObj.getAddress();
				locationObj.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
				phoneDevice.setCompanyLocation(locationObj);

				phoneDevice.setOnBoardedBy(phoneDevicesList.get(i).getOnBoardedBy());

				devicesList.add(phoneDevice);
			}
			return Response.status(Status.OK).entity(devicesList).build();
		} catch (Exception e) {
			log.severe("Unable to find machine list " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find machine list"))
					.build();
		}
	}

}
