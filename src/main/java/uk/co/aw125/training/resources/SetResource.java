package uk.co.aw125.training.resources;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.co.aw125.training.auth.AuthenticationCache;
import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.exceptions.CustomBadRequestException;
import uk.co.aw125.training.exceptions.CustomNotAuthorizedException;
import uk.co.aw125.training.models.core.Set;


@Path("/set")
@Api(value = "/set", description = "Operations about set", tags = {"Set"})
@Produces({"application/json", "application/xml"})
public class SetResource {

  private static final Logger logger = LogManager.getLogger(SetResource.class);


  private void authenticate(HttpHeaders headers) {
    try {
      String username = headers.getRequestHeader("username").get(0);
      String api_key = headers.getRequestHeader("api_key").get(0);
      if (username == null || username.isEmpty()) {
        throw new CustomNotAuthorizedException("username not set");
      }

      if (api_key == null || api_key.isEmpty()) {
        throw new CustomNotAuthorizedException("api key not set");
      }

      AuthenticationCache authenticationCache = AuthenticationCache.getAuthenticationCache();

      if (authenticationCache.isAuthenticated(username, api_key)) {
        logger.info(username + " is authorized with key " + api_key);
      } else {
        if (!authenticationCache.authenticate(username, api_key)) {
          throw new CustomNotAuthorizedException("Not authourized");
        }
      }

    } catch (NullPointerException npe) {
      logger.error("unexpected null pointer during authentication", npe);
      throw new CustomNotAuthorizedException("unable to retrieve authentication data");

    }
  }

  @POST
  @ApiOperation(value = "Create set", notes = "This can only be done by the logged in set.")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid set supplied"), @ApiResponse(code = 400, message = "Set already exists")})
  public Response createSet(@ApiParam(value = "Created set object", required = true) @Valid Set set, @Context HttpHeaders headers) {

    //This will throw a 403 if auth fails
    authenticate(headers);

    DataManager dataManager = DataManager.getDataManager();

    Set inserted = dataManager.saveSet(set);
    return Response.ok().entity(inserted).build();

  }

  // @PUT
  // @Path("/{id}")
  // @ApiOperation(value = "Updated set", notes = "This can only be done by the logged in set.")
  // @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid set supplied"),
  // @ApiResponse(code = 404, message = "Set not found"), @ApiResponse(code = 400, message = "id
  // specified in path does not match id in the set object. id is immutable. Please create a new set
  // instead") })
  // public Response updateSet(@ApiParam(value = "id that need to be updated", required = true)
  // @PathParam("id") String id, @ApiParam(value = "Updated set object", required = true) Set set) {

  // if (id.equals(set.getId())) {

  // DataManager dataManager = DataManager.getDataManager();

  // Set updated = dataManager.updateSet(id, set);
  // if (updated != null) {
  // return Response.ok().entity("").build();
  // } else {
  // throw new NotFoundException("Set not found");
  // }
  // } else {
  // throw new CustomBadRequestException("id specified in path does not match id in the set object.
  // id is immutable. Please create a new set instead");
  // }
  // }

  @DELETE
  @Path("/{id}")
  @ApiOperation(value = "Delete set", notes = "This can only be done by the logged in set.")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid id supplied"), @ApiResponse(code = 404, message = "Set not found")})
  public Response deleteSet(@ApiParam(value = "The id that needs to be deleted", required = true) @PathParam("id") String id) {

    if (DataManager.getDataManager().removeSetById(id)) {
      return Response.ok().entity("").build();
    } else {
      throw new NotFoundException("Set not found");
    }
  }

  @GET
  @Path("/{id}")
  @ApiOperation(value = "Get set by set id", response = Set.class)
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid name supplied"), @ApiResponse(code = 404, message = "Set not found")})
  public Response getSetByName(
      @ApiParam(value = "The id that needs to be fetched. Use set1 for testing. ", required = true) @PathParam("id") String id) {
    Set set = DataManager.getDataManager().getSetById(id);
    if (null != set) {
      return Response.ok().entity(set).build();
    } else {
      throw new NotFoundException("Set not found");
    }
  }

}
