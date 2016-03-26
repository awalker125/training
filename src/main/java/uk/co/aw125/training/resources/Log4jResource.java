package uk.co.aw125.training.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.co.aw125.training.exceptions.CustomBadRequestException;
import uk.co.aw125.training.models.LoggerConfigModel;

@Path("/log4j")
@Api(value = "/log4j", tags = {"Log4j"})
@Produces({"application/json"})
public class Log4jResource {

  /** The Constant logger. */
  private static final Logger logger = LogManager.getLogger(Log4jResource.class);

  @GET
  @ApiOperation(value = "Get log4j logger levels")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "no log4j loggers found")})
  public Response getLoggerConfigs() {

    LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
    Map<String, LoggerConfig> map = logContext.getConfiguration().getLoggers();

    List<LoggerConfigModel> LoggerConfigModelsList = new LinkedList<>();

    for (LoggerConfig loggerConfig : map.values()) {

      logger.debug(loggerConfig.toString() + " -> " + loggerConfig.getLevel().toString());
      LoggerConfigModel loggerConfigModel = new LoggerConfigModel();
      loggerConfigModel.setName(loggerConfig.toString());
      loggerConfigModel.setLevel((loggerConfig.getLevel().toString()));
      LoggerConfigModelsList.add(loggerConfigModel);

    }

    if (LoggerConfigModelsList.isEmpty()) {
      logger.error("Could not find any loggers");
      throw new NotFoundException();
    }

    LoggerConfigModel[] foundLoggerConfigs = LoggerConfigModelsList.toArray(new LoggerConfigModel[0]);
    return Response.ok().entity(foundLoggerConfigs).build();

  }

  @GET
  @Path("/{logger}")
  @ApiOperation(value = "Get log4j by name", response = LoggerConfigModel.class)
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid name supplied"), @ApiResponse(code = 404, message = "logger not found")})
  public Response getLoggerConfigName(@ApiParam(value = "The name that needs to be fetched.", required = true) @PathParam("logger") String name) {

    LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
    Map<String, LoggerConfig> map = logContext.getConfiguration().getLoggers();

    for (LoggerConfig loggerConfig : map.values()) {

      logger.debug(loggerConfig.toString() + " -> " + loggerConfig.getLevel().toString());

      if (loggerConfig.toString().equals(name)) {
        LoggerConfigModel loggerConfigModel = new LoggerConfigModel();
        loggerConfigModel.setName(loggerConfig.toString());
        loggerConfigModel.setLevel((loggerConfig.getLevel().toString()));
        return Response.ok().entity(loggerConfigModel).build();
      }

    }

    logger.error("LoggerConfig not found with name " + name);
    throw new NotFoundException();

  }

  @PUT
  @Path("/{logger}")
  @ApiOperation(value = "Update log4j", response = LoggerConfigModel.class)
  @ApiResponses(value = {@ApiResponse(code = 404, message = "logger not found")})
  public Response updateLogger(@ApiParam(value = "The name that needs to be fetched.", required = true) @PathParam("logger") String name,
      @ApiParam(value = "log4j level e.g OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE,ALL", required = true) @QueryParam("level") String level,
      @Context HttpServletRequest httpServletRequest, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders) {

    if (level == null || level.length() == 0) {
      throw new CustomBadRequestException("level is null or empty");
      // throw new BadRequestException("level is null or empty");
    }

    String upperCaseLevel = level.toUpperCase();
    Level targetLevel = Level.getLevel(upperCaseLevel);

    if (targetLevel == null) {
      logger.error("Could not convert " + level + " to a log4j level");
      throw new BadRequestException("level" + level + " is invalid. Try one of FATAL,ERROR,WARN,INFO,DEBUG,TRACE");
    }

    LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
    Map<String, LoggerConfig> map = logContext.getConfiguration().getLoggers();

    for (LoggerConfig loggerConfig : map.values()) {

      logger.debug(loggerConfig.toString() + " -> " + loggerConfig.getLevel().toString());

      if (loggerConfig.toString().equals(name)) {

        // Set the level
        loggerConfig.setLevel(targetLevel);

        LoggerConfigModel loggerConfigModel = new LoggerConfigModel();
        loggerConfigModel.setName(loggerConfig.toString());
        loggerConfigModel.setLevel((loggerConfig.getLevel().toString()));


        return Response.ok().entity(loggerConfigModel).build();
      }

    }

    logger.error("LoggerConfig not found with name " + name);
    throw new uk.co.aw125.training.exceptions.CustomNotFoundException("Logger not found");

  }

}
