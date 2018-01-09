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

import com.sparkonix.dao.UserDAO;
import com.sparkonix.dao.UserRoleDAO;
import com.sparkonix.dao.UserRoleIndexDAO;
import com.sparkonix.entity.User;
import com.sparkonix.entity.dto.ResetPasswordDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	private final UserDAO userDAO;
	private final UserRoleIndexDAO userRoleIndexDAO;
	private final UserRoleDAO userRoleDAO;
	
	private final Logger log = Logger.getLogger(UserResource.class.getName());

	public UserResource(UserDAO userDAO, UserRoleIndexDAO userRoleIndexDAO, UserRoleDAO userRoleDAO) {
		this.userDAO = userDAO;
		this.userRoleIndexDAO = userRoleIndexDAO;
		this.userRoleDAO = userRoleDAO;
	}

	@GET
	@UnitOfWork
	@Path("/{userId}")
	public Response getUserById(@Auth User authUser, @PathParam("userId") long userId) {
		try {
			log.info(" In getUserById");
			return Response.status(Status.OK).entity(JsonUtils.getJson(userDAO.getById(userId))).build();
		} catch (Exception e) {
			log.severe("Unable to find User " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to find User")).build();
		}
	}
	
//	@DELETE
//	@UnitOfWork
//	@Path("/{userId}")
//	public Response deleteUser(@Auth User authUser, @PathParam("userId") long userid) {
//		try {
//			log.info(" In deleteUser");
//			userDAO.delete(userid);
//			return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("User Deleted Successfully")).build();
//		} catch (Exception e) {
//			log.severe("Unable to Delete User " + e);
//			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Delete User")).build();
//		}
//	}

	@PUT
	@UnitOfWork
	@Path("/{userId}")
	public Response updateUserById(@Auth User authUser, User userobj, @PathParam("userId") long userId) {
		try {
			log.info(" In updateUserById");
			/*System.out.println("1-- "+userobj.getId() +"--uid"+userId);
			System.out.println("2-- "+userobj.getAltEmail());
			System.out.println("3-- "+userobj.getName());
			System.out.println("4-- "+userobj.getCompanyDetailsId());
			System.out.println("5-- "+userobj.getReseller_id());
			System.out.println("6-- "+userobj.getRole());
			System.out.println("7-- "+userobj.getToken());
			System.out.println("8-- "+userobj.getEmail());
			System.out.println("9-- "+userobj.getNotificationType());
			System.out.println("10-- "+userobj.getMobile());
			System.out.println("11-- "+userobj.getMetadata());
			System.out.println("12-- "+userobj.getToken());
			System.out.println("13-- "+userobj.getPassword());*/
//			userDAO.save(userobj);
			return Response.status(Status.OK).entity(userDAO.save(userobj)).build();
		} catch (Exception e) {
			log.severe("Unable to Update  " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to Update ")).build();
		}
	}
	
	
	
	

	
	
	@PUT
	@UnitOfWork
	@Path("/resetpassword")
	public Response resetPassword(@Auth User authUser, ResetPasswordDTO resetPasswordDTO) {
		try {
			log.info(" In resetPassword");

			if (authUser == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}

			User dbUser = userDAO.getById(resetPasswordDTO.getUserId());
			if (dbUser == null) {
				// user not found
				log.severe("User not found");
				return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("User not found")).build();

			} else {

				if (dbUser.getPassword().equals(resetPasswordDTO.getOldPassword())) {
					log.info("User found");
					dbUser.setPassword(resetPasswordDTO.getNewPassword());
					userDAO.save(dbUser);

					log.info("Password updated successfully");

					return Response.status(Status.OK).entity(JsonUtils.getSuccessJson("Password updated successfully"))
							.build();
				} else {
					// old pass is not valid
					log.severe("Incorrect old password");
					return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Incorrect old password"))
							.build();
				}

			}
		} catch (Exception e) {
			log.severe("Unable to reset password. Error: " + e);
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Unable to reset password"))
					.build();
		}
	}
}
