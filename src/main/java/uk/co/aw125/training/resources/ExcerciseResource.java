package uk.co.aw125.training.resources;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.exceptions.CustomBadRequestException;
import uk.co.aw125.training.exceptions.CustomNotFoundException;
import uk.co.aw125.training.models.core.Excercise;

@Path("/excercise")
@Api(value = "/excercise", description = "Operations about excercise", tags = {"Excercise"})
@Produces({"application/json", "application/xml"})
public class ExcerciseResource {

  @GET
  @ApiOperation(value = "Get excercises")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "No excercises found")})
  public Response getExcercises(@Context HttpHeaders headers) {

//    try {
//      String username = headers.getRequestHeader("username").get(0);
//      String api_key = headers.getRequestHeader("api_key").get(0);
//      if (username == null || username.isEmpty()) {
//        throw new NotAuthorizedException(Response.status(403).entity("").build());
//      }
//
//      if (api_key == null || api_key.isEmpty()) {
//        throw new NotAuthorizedException(Response.status(403).entity("").build());
//      }
//    } catch (NullPointerException npe) {
//      throw new NotAuthorizedException(Response.status(403).entity("").build());
//    }

    Excercise[] excercises = DataManager.getDataManager().getExcercises();
    if (null != excercises && excercises.length > 0) {
      return Response.ok().entity(excercises).build();
    } else {
      throw new CustomNotFoundException("Excercises not found");
    }
  }

  @POST
  @ApiOperation(value = "Create excercise", notes = "This can only be done by the logged in excercise.")
  @ApiResponses(
      value = {@ApiResponse(code = 400, message = "Invalid excercise supplied"), @ApiResponse(code = 400, message = "Excercise already exists")})
  public Response createExcercise(@ApiParam(value = "Created excercise object", required = true) @Valid Excercise excercise) {

    DataManager dataManager = DataManager.getDataManager();

    if (dataManager.excerciseExists(excercise.getName())) {

      throw new CustomBadRequestException("Exercise already exists");
    }
    Excercise inserted = dataManager.saveExcercise(excercise);
    return Response.ok().entity(inserted).build();

  }


  @DELETE
  @Path("/{name}")
  @ApiOperation(value = "Delete excercise", notes = "This can only be done by the logged in excercise.")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid name supplied"), @ApiResponse(code = 404, message = "Excercise not found")})
  public Response deleteExcercise(@ApiParam(value = "The name that needs to be deleted", required = true) @PathParam("name") String name) {

    if (DataManager.getDataManager().removeExcerciseByName(name)) {
      return Response.ok().entity("").build();
    } else {
      throw new CustomNotFoundException("Excercise not found");
    }
  }

  @GET
  @Path("/{name}")
  @ApiOperation(value = "Get excercise by excercise name", response = Excercise.class)
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid name supplied"), @ApiResponse(code = 404, message = "Excercise not found")})
  public Response getExcerciseByName(
      @ApiParam(value = "The name that needs to be fetched. Use excercise1 for testing. ", required = true) @PathParam("name") String name) {
    Excercise excercise = DataManager.getDataManager().getExcerciseByName(name);
    if (null != excercise) {
      return Response.ok().entity(excercise).build();
    } else {
      throw new CustomNotFoundException("Excercise not found");
    }
  }

  @GET
  @Path("/search/name/{name}")
  @ApiOperation(value = "Get excercise by eExcercise name using search")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Excercise not found")})
  public Response searchExcerciseByName(@ApiParam(value = "The name that needs to be fetched.", required = true) @PathParam("name") String name) {

    Excercise[] eExcercises = DataManager.getDataManager().searchExcerciseByName(name);
    if (null != eExcercises && eExcercises.length > 0) {
      return Response.ok().entity(eExcercises).build();
    } else {
      throw new CustomNotFoundException("Excercise not found");
    }
  }

  @GET
  @Path("/search/tag/name/{name}")
  @ApiOperation(value = "Get excercise by tag name using search")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Excercise not found")})
  public Response searchExcerciseByTagName(
      @ApiParam(value = "The tag name that needs to be fetched.", required = true) @PathParam("name") String name) {

    Excercise[] eExcercises = DataManager.getDataManager().searchExcerciseByTagName(name);
    if (null != eExcercises && eExcercises.length > 0) {
      return Response.ok().entity(eExcercises).build();
    } else {
      throw new CustomNotFoundException("Excercise not found");
    }
  }

  @GET
  @Path("/search/tag/value/{value}")
  @ApiOperation(value = "Get excercise by tag value using search")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "Excercise not found")})
  public Response searchExcerciseByTagValue(
      @ApiParam(value = "The tag name that needs to be fetched.", required = true) @PathParam("value") String value) {

    Excercise[] eExcercises = DataManager.getDataManager().searchExcerciseByTagValue(value);
    if (null != eExcercises && eExcercises.length > 0) {
      return Response.ok().entity(eExcercises).build();
    } else {
      throw new CustomNotFoundException("Excercise not found");
    }
  }

}
