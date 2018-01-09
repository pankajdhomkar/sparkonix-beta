package com.sparkonix.resources;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sparkonix.dao.UserDAO;
import com.sparkonix.dao.UserRoleDAO;
import com.sparkonix.dao.UserRoleIndexDAO;
import com.sparkonix.entity.User;
import com.sparkonix.entity.UserRoleIndex;
import com.sparkonix.entity.dto.UserRoleIndexDTO;
import com.sparkonix.utils.JsonUtils;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * @author Pankaj Dhomkar
 *
 */
public class UsersResource {
	
	private final UserDAO userDAO;
	private final UserRoleDAO userRoleDAO;
	private final UserRoleIndexDAO userRoleIndexDAO; 
	
	private final Logger log = Logger.getLogger(UsersResource.class.getName());
	
	public UsersResource(UserDAO userDAO, UserRoleDAO userRoleDAO, UserRoleIndexDAO userRoleIndexDAO) {
		this.userDAO = userDAO;
		this.userRoleDAO = userRoleDAO;
		this.userRoleIndexDAO = userRoleIndexDAO;
	}
	
	@POST
	@UnitOfWork
	public Response createUser(@Auth User authUser, User user) throws Exception {
		User dbUser = userDAO.findByEmail(user.getEmail());
		System.out.println("-------------------"+user);
		try {
			if (dbUser == null) {
				userDAO.save(user);
				User userObject = userDAO.getById(user.getId());
				
				UserRoleIndexDTO obj = new UserRoleIndexDTO();
				long role_id = userObject.getUser_role_id();
				long userid = dbUser.getId();
				if(role_id == 1){
					obj.setUser_role_id(1);
				}else if(role_id == 2){
					obj.setUser_role_id(2);
				}else if(role_id == 3){
					obj.setUser_role_id(3);
				}else if(role_id == 4){
					obj.setUser_role_id(4);
				}else if(role_id == 5){
					obj.setUser_role_id(5);
				}else if(role_id == 6){
					obj.setUser_role_id(6);
				}else{
					obj.setUser_role_id(0);
				}
				obj.setUser_id(userid);
				UserRoleIndex userRoleIndex = new UserRoleIndex();
				userRoleIndex.setUser_id(userObject.getId());
				userRoleIndex.setUser_role_id(obj.getUser_role_id());
				userRoleIndexDAO.save(userRoleIndex);
				
				return Response.status(Status.OK).entity(userDAO.save(user)).build();
			} else {
				log.severe("User not created, Username/Email already exist.");
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonUtils.getErrorJson("Username/Email already exist.")).build();
			}
		} catch (Exception e) {
			log.severe("Failed to create user. Reason: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(JsonUtils.getErrorJson("Failed to create User.")).build();
		}

	}
	
	
	@GET
	@Path("/{roleId}")
	@UnitOfWork
	public Response listUsersByRole(@Auth User authUser, @PathParam("roleId") long roleId) {
		try {
			log.info(" In listUsersByRole");
			return Response.status(Status.OK).entity(JsonUtils.getJson(userDAO.findByRoleID(roleId))).build();
		} catch (Exception e) {
			log.severe("Unable to find users by given role" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find users by given role")).build();
		}
	}
	
	/*
	 * Here we get the technician by its role id and its manufacturers/resellers id 
	 */
	@GET
	@Path("/{roleid}/{manufacturer_id}/{resellerID}")
	@UnitOfWork
	public Response listUsersByRoleAndCompanyDetailsId(@Auth User authUser, @PathParam("roleid") long roleid,
			@PathParam("manufacturer_id") long manufacturer_id, @PathParam("resellerID") long resellerID) {
		try {
			log.info(" In listUsersByRoleAndCompanyDetailsId"+roleid);
			if(roleid == 3){ //.equals("MANUFACTURERADMIN")
				System.out.println("Manufacturer--"+manufacturer_id);
				return Response.status(Status.OK)
						.entity(JsonUtils.getJson(userDAO.findAllByRoleManufacturerId(5, manufacturer_id))).build();
			}else{
				System.out.println("Reseller-->"+resellerID);
				return Response.status(Status.OK)
						.entity(JsonUtils.getJson(userDAO.findAllByRoleResellerId(5, resellerID))).build();
			}
			
		} catch (Exception e) {
			log.severe("Unable to find user by given role and comany details id" + e);
			return Response.status(Status.BAD_REQUEST)
					.entity(JsonUtils.getErrorJson("Unable to find users by given role & comany details id")).build();
		}
	}

}
