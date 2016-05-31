package uk.co.aw125.training.resources;

import java.util.Date;

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
import uk.co.aw125.training.auth.AuthenticationManager;
import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.exceptions.CustomBadRequestException;
import uk.co.aw125.training.exceptions.CustomNotAuthorizedException;
import uk.co.aw125.training.models.core.Auth;
import uk.co.aw125.training.models.core.Set;


@Path("/set")
@Api(value = "/set", description = "Operations about set", tags = {"Set"})
@Produces({"application/json", "application/xml"})
public class SetResource {

  private static final Logger logger = LogManager.getLogger(SetResource.class);


  @POST
  @ApiOperation(value = "Create set", notes = "This can only be done by the logged in set.")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid set supplied"), @ApiResponse(code = 400, message = "Set already exists")})
  public Response createSet(@ApiParam(value = "Created set object", required = true) @Valid Set set, @Context HttpHeaders headers) {

    // This will throw a 403 if auth fails
    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);

    set.setUsername(auth.getUsername());
    if(set.getDate() == null)
    {
      logger.trace("No date passed. Adding today");
      set.setDate(new Date());
    }
    //set.addDateIfNeeded();

    DataManager dataManager = DataManager.getDataManager();

    Set inserted = dataManager.saveSet(set, auth.getUsername());
    return Response.ok().entity(inserted).build();

  }

  @DELETE
  @Path("/{id}")
  @ApiOperation(value = "Delete set", notes = "This can only be done by the logged in set.")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid id supplied"), @ApiResponse(code = 404, message = "Set not found")})
  public Response deleteSet(@ApiParam(value = "The id that needs to be deleted", required = true) @PathParam("id") String id,
      @Context HttpHeaders headers) {

    // This will throw a 403 if auth fails
    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);


    if (DataManager.getDataManager().removeSetById(id, auth.getUsername())) {
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
      @ApiParam(value = "The id that needs to be fetched. Use set1 for testing. ", required = true) @PathParam("id") String id,
      @Context HttpHeaders headers) {

    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);



    Set set = DataManager.getDataManager().getSetById(id, auth.getUsername());
    if (null != set) {
      return Response.ok().entity(set).build();
    } else {
      throw new NotFoundException("Set not found");
    }
  }

}
