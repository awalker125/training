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
import uk.co.aw125.training.auth.AuthenticationManager;
import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.exceptions.CustomBadRequestException;
import uk.co.aw125.training.exceptions.CustomNotAuthorizedException;
import uk.co.aw125.training.models.core.Auth;
import uk.co.aw125.training.models.core.Set;


@Path("/stat")
@Api(value = "/stat", description = "Stats", tags = {"Set"})
@Produces({"application/json", "application/xml"})
public class StatResource {

  private static final Logger logger = LogManager.getLogger(StatResource.class);


  @GET
  @Path("/bodyweight/max/12months")
  @ApiOperation(value = "Get max bodyweight in the last 12 months", response = Set.class)
  @ApiResponses(@ApiResponse(code = 404, message = "data not found"))
  public Response getBodyweightMax12mponths(@Context HttpHeaders headers) {

    // This will throw a 403 if auth fails
    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);

    Set set = DataManager.getDataManager().getMaxBodyweight12Months(auth.getUsername());
    if (null != set) {

      return Response.ok().entity(set).build();
    } else {
      throw new NotFoundException("data not found");
    }
  }
  
  
  @GET
  @Path("/bodyweight/min/12months")
  @ApiOperation(value = "Get min body weight in the last 12 months", response = Set.class)
  @ApiResponses(@ApiResponse(code = 404, message = "data not found"))
  public Response getBodyweightMin12mponths(@Context HttpHeaders headers) {

    // This will throw a 403 if auth fails
    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);

    Set set = DataManager.getDataManager().getMinBodyweight12Months(auth.getUsername());
    if (null != set) {

      return Response.ok().entity(set).build();
    } else {
      throw new NotFoundException("data not found");
    }
  }

}
