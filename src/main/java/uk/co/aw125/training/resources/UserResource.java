package uk.co.aw125.training.resources;

import io.swagger.annotations.*;
import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.exceptions.ApiException;
import uk.co.aw125.training.exceptions.BadRequestException;
import uk.co.aw125.training.exceptions.UserAlreadyExistsException;
import uk.co.aw125.training.models.User;
import uk.co.aw125.training.stubs.UserData;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/user")
@Api(value = "/user", description = "Operations about user")
@Produces({ "application/json", "application/xml" })
public class UserResource {
	static UserData userData = new UserData();

	@POST
	@ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.", position = 1)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid user supplied"), @ApiResponse(code = 400, message = "User already exists") })
	public Response createUser(@ApiParam(value = "Created user object", required = true) User user) {

		DataManager dataManager = DataManager.getDataManager();

		if (dataManager.userExists(user.getUsername())) {

			throw new UserAlreadyExistsException(user.getUsername() + " already exists");
		}
		dataManager.saveUser(user);
		return Response.ok().entity("").build();
	}


	@PUT
	@Path("/{username}")
	@ApiOperation(value = "Updated user", notes = "This can only be done by the logged in user.", position = 4)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid user supplied"), @ApiResponse(code = 404, message = "User not found") })
	public Response updateUser(@ApiParam(value = "name that need to be deleted", required = true) @PathParam("username") String username, @ApiParam(value = "Updated user object", required = true) User user) {
		userData.addUser(user);
		return Response.ok().entity("").build();
	}

	@DELETE
	@Path("/{username}")
	@ApiOperation(value = "Delete user", notes = "This can only be done by the logged in user.", position = 5)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username supplied"), @ApiResponse(code = 404, message = "User not found") })
	public Response deleteUser(@ApiParam(value = "The name that needs to be deleted", required = true) @PathParam("username") String username) {
		if (userData.removeUser(username)) {
			return Response.ok().entity("").build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/{username}")
	@ApiOperation(value = "Get user by user name", response = User.class, position = 0)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username supplied"), @ApiResponse(code = 404, message = "User not found") })
	public Response getUserByName(@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ", required = true) @PathParam("username") String username) throws ApiException {
		User user = userData.findUserByName(username);
		if (null != user) {
			return Response.ok().entity(user).build();
		} else {
			throw new uk.co.aw125.training.exceptions.NotFoundException(404, "User not found");
		}
	}

	@GET
	@Path("/login")
	@ApiOperation(value = "Logs user into the system", response = String.class, position = 6)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username/password supplied") })
	public Response loginUser(@ApiParam(value = "The user name for login", required = true) @QueryParam("username") String username, @ApiParam(value = "The password for login in clear text", required = true) @QueryParam("password") String password) {
		return Response.ok().entity("logged in user session:" + System.currentTimeMillis()).build();
	}

	@GET
	@Path("/logout")
	@ApiOperation(value = "Logs out current logged in user session", position = 7)
	public Response logoutUser() {
		return Response.ok().entity("").build();
	}
}
