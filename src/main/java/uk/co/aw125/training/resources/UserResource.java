package uk.co.aw125.training.resources;

import io.swagger.annotations.*;
import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.exceptions.InternalErrorException;
import uk.co.aw125.training.exceptions.AlreadyExistsException;
import uk.co.aw125.training.exceptions.ImmutableException;
import uk.co.aw125.training.exceptions.NotFoundException;
import uk.co.aw125.training.models.User;

import javax.ws.rs.core.Response;

import java.util.UUID;

import javax.ws.rs.*;

@Path("/user")
@Api(value = "/user", description = "Operations about user")
@Produces({ "application/json", "application/xml" })
public class UserResource {

	@POST
	@ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid user supplied"), @ApiResponse(code = 400, message = "User already exists") })
	public Response createUser(@ApiParam(value = "Created user object", required = true) User user) {

		DataManager dataManager = DataManager.getDataManager();

		if (dataManager.userExists(user.getUsername())) {

			throw new AlreadyExistsException(user.getUsername() + " already exists");
		}
		User inserted = dataManager.saveUser(user);
		return Response.ok().entity(inserted).build();

	}

	@PUT
	@Path("/{username}")
	@ApiOperation(value = "Updated user", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid user supplied"), @ApiResponse(code = 404, message = "User not found"), @ApiResponse(code = 400, message = "username specified in path does not match username in the user object. username is immutable. Please create a new user instead") })
	public Response updateUser(@ApiParam(value = "name that need to be deleted", required = true) @PathParam("username") String username, @ApiParam(value = "Updated user object", required = true) User user) {

		if (username.equals(user.getUsername())) {

			DataManager dataManager = DataManager.getDataManager();

			User updated = dataManager.updateUser(username, user);
			if (updated != null) {

				return Response.ok().entity("").build();
			} else {
				throw new NotFoundException("User not found");
			}
		} else {
			throw new ImmutableException("username specified in path does not match username in the user object. username is immutable. Please create a new user instead");
		}
	}

	@DELETE
	@Path("/{username}")
	@ApiOperation(value = "Delete user", notes = "This can only be done by the logged in user.")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username supplied"), @ApiResponse(code = 404, message = "User not found") })
	public Response deleteUser(@ApiParam(value = "The name that needs to be deleted", required = true) @PathParam("username") String username) {

		if (DataManager.getDataManager().removeUserByUsername(username)) {
			return Response.ok().entity("").build();
		} else {
			throw new NotFoundException("User not found");
		}
	}

	@GET
	@Path("/{username}")
	@ApiOperation(value = "Get user by user name", response = User.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username supplied"), @ApiResponse(code = 404, message = "User not found") })
	public Response getUserByName(@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ", required = true) @PathParam("username") String username) {
		User user = DataManager.getDataManager().getUserByUsername(username);
		if (null != user) {
			return Response.ok().entity(user).build();
		} else {
			throw new NotFoundException("User not found");
		}
	}

	@GET
	@Path("/login")
	@ApiOperation(value = "Logs user into the system", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username/password supplied") })
	public Response loginUser(@ApiParam(value = "The user name for login", required = true) @QueryParam("username") String username, @ApiParam(value = "The password for login in clear text", required = true) @QueryParam("password") String password) {
		return Response.ok().entity("logged in user session:" + System.currentTimeMillis()).build();
	}

	@GET
	@Path("/logout")
	@ApiOperation(value = "Logs out current logged in user session")
	public Response logoutUser() {
		return Response.ok().entity("").build();
	}
}
