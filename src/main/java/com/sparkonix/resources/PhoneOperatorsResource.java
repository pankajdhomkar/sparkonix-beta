package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;
import com.sparkonix.dao.PhoneOperatorDAO;
import com.sparkonix.entity.PhoneOperator;
import com.sparkonix.entity.dto.PhoneOperatorDTO;
import com.sparkonix.utils.JsonUtils;
import com.sparkonix.utils.Otp;
import com.sparkonix.utils.SendSMS;

import io.dropwizard.hibernate.UnitOfWork;


/**
 * @author Pankaj Dhomkar
 *
 */

@Path("/phoneoperators")
@Produces(MediaType.APPLICATION_JSON)
public class PhoneOperatorsResource {
    private final PhoneOperatorDAO phoneOperatorDAO;
    
    private final Logger log = Logger.getLogger(PhoneOperatorsResource.class.getName());

    public PhoneOperatorsResource(PhoneOperatorDAO phoneOperatorDAO) {
	this.phoneOperatorDAO = phoneOperatorDAO;
    }
    
    @SuppressWarnings("null")
    @POST
	@UnitOfWork
	@Path("/otp")///{phoneNumber}/*@PathParam("phoneNumber") String phoneNumber2*/
    public Response getOtp(PhoneOperatorDTO dtoObject) {
	try {
	    //remove this line after UI fixes done in apk(to accept 10digit number with country code)
	    String phoneNumber="+"+dtoObject.getPhoneNumber();		
	    System.out.println(phoneNumber);
	    PhoneOperator operatorObj = phoneOperatorDAO.getOperatorByPhoneNumber(phoneNumber);


	    /*This if number is not present then it will add a number into a unrecognized table in database and 
	    after it will stored in phoneDevice table*/
	    if (operatorObj == null) {
	        //			System.out.println("no not in the phonedevice database----------"+operatorObj);
	        log.severe("inserting a phone operator");
	        //			System.out.println("1here upto--------------------"+phoneNumber);
	        String otp = Otp.generate();
	        System.out.println("2here upto--------------------"+otp);
	        operatorObj.setFld_mobile_number(phoneNumber);
	        operatorObj.setFld_name(dtoObject.getName());
	        operatorObj.setFld_otp(otp);
	        phoneOperatorDAO.save(operatorObj);

	        JsonObject jsonObjSms = new JsonObject();
	        jsonObjSms.addProperty("toMobileNumber", phoneNumber.replaceAll("\\+",""));
	        jsonObjSms.addProperty("smsMessage", "OTP for the AttendMe application is " + otp);

	        /* send SMS(using TEXTLOCAL) */
	        new SendSMS(jsonObjSms).run();
	        log.info("OTP sms sent to mobile number  - PhoneDevicesResource Class- getOtp method in 1st else");

	        return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("OTP has been sent")).build();				

	    } else {
	        String otp = Otp.generate();
	        // update OTP in db
	        operatorObj.setFld_otp(otp);

	        JsonObject jsonObjSms = new JsonObject();
	        jsonObjSms.addProperty("toMobileNumber", phoneNumber.replaceAll("\\+",""));
	        jsonObjSms.addProperty("smsMessage", "OTP for the AttendMe application is " + otp);

	        /* send SMS(using TEXTLOCAL) */
	        new SendSMS(jsonObjSms).run();
	        log.info("OTP sms sent to mobile number  - PhoneDevicesResource Class- getOtp method in 1st else");

	        return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("OTP has been sent")).build();
	    }
	} catch (Exception e) {
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Something went wrong")).build();
	}
    }
    
    @POST
    @UnitOfWork
    @Path("/otp/validate")
    public Response validateOtp(PhoneOperatorDTO phoneOperatorOtpDTO) {
	phoneOperatorOtpDTO.setPhoneNumber("+"+phoneOperatorOtpDTO.getPhoneNumber());
	try {
	    if (phoneOperatorOtpDTO.getPhoneNumber().equals("") || phoneOperatorOtpDTO.getOtp().equals("")) {
		log.severe("Mobile number or OTP should not be empty.");
		return Response.status(Status.BAD_REQUEST)
			.entity(JsonUtils.getErrorJson("Mobile number or OTP should not be empty")).build();
	    }
	    PhoneOperator operatorObj = phoneOperatorDAO.getOperatorByPhoneNumber(phoneOperatorOtpDTO.getPhoneNumber());
	    if (operatorObj != null) {
		System.out.println("----ob-" + operatorObj.getFld_otp());
		System.out.println("------" + operatorObj.getFld_otp().equals(phoneOperatorOtpDTO.getOtp()));
		if (operatorObj.getFld_otp().equals(phoneOperatorOtpDTO.getOtp())) {
		    log.info("OTP is valid.");
		   /* // generate token for @Auth
		    User userObj = new User();
		    userObj.setRole("OPERATOR");
		    userObj.setEmail(phoneDeviceOtpDTO.getPhoneNumber());

		    // generate token
		    String token = JwtToken.generateToken(phoneDeviceOtpDTO.getPhoneNumber(), userObj);
		    log.info("JWT token generated.");
		    userObj.setToken(token);

		    JsonObject jsonObj = new JsonObject();
		    jsonObj.addProperty("success", "OTP is valid");
		    jsonObj.addProperty("token", token);*/

		    // set OTP to null
		    JsonObject jsonObj = new JsonObject();
		    jsonObj.addProperty("success", "OTP is valid");
		    
		    operatorObj.setFld_otp(null);
		    log.info("OTP value updated as null.");

		    // updating fcm token for push notification
		    if (phoneOperatorOtpDTO.getFcmToken() == null || phoneOperatorOtpDTO.getFcmToken().trim().isEmpty()) {
			log.severe("Fcm token is empty.");
		    } else {
			operatorObj.setFld_fcm_token(phoneOperatorOtpDTO.getFcmToken());
			log.info("Updated fcm token value.");
		    }
		    return Response.status(Status.OK).entity(jsonObj.toString()).build();
		    
		} else {
		    log.severe("OTP is not valid.");
		    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("OTP is not valid"))
			    .build();
		} 
	    }else{
		return Response.status(Status.BAD_REQUEST)
			.entity(JsonUtils.getErrorJson("Mobile number or OTP should not be empty")).build();
	    }
	} catch (Exception e) {
	    log.severe("Unable to validate otp. Error: " + e.getMessage());
	    return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to validate otp")).build();
	}
    }

}
