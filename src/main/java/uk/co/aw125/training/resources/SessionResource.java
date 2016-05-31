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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
import uk.co.aw125.training.models.core.Session;


@Path("/session")
@Api(value = "/session", description = "Operations about session", tags = {"Session"})
@Produces({"application/json", "application/xml"})
public class SessionResource {

  private static final Logger logger = LogManager.getLogger(SessionResource.class);


  @PUT
  @ApiOperation(value = "Create session", notes = "This can only be done by the logged in session.")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid session supplied"), @ApiResponse(code = 400, message = "Session already exists")})
  public Response updateSessions(@Context HttpHeaders headers, @ApiParam(
      value = "If not set will default to current time minus 1 hour. Default format is yyyy-MM-dd HH:mm e.g Use 2014-07-02 00:00 for testing.") @QueryParam("fromTime") String fromTimeString,
  @ApiParam(
      value = "If not set will default to current time. Default format is yyyy-MM-dd HH:mm e.g 1982-12-05 15:59. Use 2014-07-03 00:00 for testing.") @QueryParam("toTime") String toTimeString)  {

    // This will throw a 403 if auth fails
    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);

    
    logger.debug("fromTimeString " + fromTimeString);
    logger.debug("toTimeString " + toTimeString);


    String dateFormat = "yyyy-MM-dd HH:mm";

    DateTime toTime = new DateTime();
    DateTime fromTime = toTime.minusHours(1);

    DateTimeFormatter fmt = DateTimeFormat.forPattern(dateFormat);


    // DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

    if (fromTimeString != null) {
      try {
        fromTime = fmt.parseDateTime(fromTimeString);
      } catch (Exception e) {
        logger.error("supplied fromTime is not parsable", e);
        throw new CustomBadRequestException("supplied fromTime is not parsable. Must be in " + dateFormat);
      }
    } else {
      logger.debug("Using default fromTime " + fromTime);
    }

    if (toTimeString != null) {
      try {
        toTime = fmt.parseDateTime(toTimeString);
      } catch (Exception e) {
        logger.error("supplied toTime is not parsable", e);
        throw new CustomBadRequestException("supplied toTime is not parsable. Must be in " + dateFormat);
      }
    } else {
      logger.debug("Using default toTime " + toTime);
    }

    logger.debug("toTime " + toTime);
    logger.debug("fromTime " + fromTime);
    
    
    if(toTime.isBefore(fromTime.getMillis()))
    {
      throw new CustomBadRequestException("toTimeString cannot be before fromTimeString");
    }
    //session.setUsername(auth.getUsername());
    //if(session.getDate() == null)
    //{
    //  logger.trace("No date passed. Adding today");
    //  session.setDate(new Date());
   // }
    //session.addDateIfNeeded();

    DataManager dataManager = DataManager.getDataManager();

    Session[] sessions = dataManager.updateSessions(fromTime,toTime, auth.getUsername());
    return Response.ok().entity(sessions).build();

  }

//  @DELETE
//  @Path("/{id}")
//  @ApiOperation(value = "Delete session", notes = "This can only be done by the logged in session.")
//  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid id supplied"), @ApiResponse(code = 404, message = "Session not found")})
//  public Response deleteSession(@ApiParam(value = "The id that needs to be deleted", required = true) @PathParam("id") String id,
//      @Context HttpHeaders headers) {
//
//    // This will throw a 403 if auth fails
//    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);
//
//
//    if (DataManager.getDataManager().removeSessionById(id, auth.getUsername())) {
//      return Response.ok().entity("").build();
//    } else {
//      throw new NotFoundException("Session not found");
//    }
//  }

//  @GET
//  @Path("/{id}")
//  @ApiOperation(value = "Get session by session id", response = Session.class)
//  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid name supplied"), @ApiResponse(code = 404, message = "Session not found")})
//  public Response getSessionByName(
//      @ApiParam(value = "The id that needs to be fetched. Use session1 for testing. ", required = true) @PathParam("id") String id,
//      @Context HttpHeaders headers) {
//
//    Auth auth = AuthenticationManager.getAuthenticationCache().authenticate(headers);
//
//
//
//    Session session = DataManager.getDataManager().getSessionById(id, auth.getUsername());
//    if (null != session) {
//      return Response.ok().entity(session).build();
//    } else {
//      throw new NotFoundException("Session not found");
//    }
//  }

}
