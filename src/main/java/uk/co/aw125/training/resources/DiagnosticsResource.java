package uk.co.aw125.training.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hpe.devops.odam.bootstrap.Launcher;
import com.hpe.devops.odam.config.ConfigManager;
import com.hpe.devops.odam.data.DataManager;
import com.hpe.devops.odam.model.client.RuntimeStats;
import com.hpe.devops.odam.model.client.Stats;
import com.hpe.devops.odam.version.Version;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

// TODO: Auto-generated Javadoc
/**
 * The Class StatusResource.
 */
@Path("/diagnostics")
@Api(value = "/diagnostics", tags = {"Diagnostics"})
public class DiagnosticsResource {
  // static UserData userData = new UserData();

  /** The Constant logger. */
  private static final Logger logger = LogManager.getLogger(DiagnosticsResource.class);

  /**
   * Gets the status mongo.
   *
   * @return the status mongo
   */
  @GET
  @Path("/mongo")
  @ApiOperation(value = "Get's the status of the mongo")
  @Produces({"text/plain"})
  public Response getStatusMongo() {

    logger.trace("checking mongo db");
    try {
      boolean canWrite = DataManager.getDataManager().testMongo();
      if (canWrite) {
        return Response.ok("OK").build();
      } else {
        return Response.serverError().build();
      }
    } catch (Exception e) {
      logger.error("Mongo is not writeable", e);
      return Response.serverError().build();
    }

  }

  /**
   * Gets the status config.
   *
   * @return the status config
   */
  @GET
  @Path("/config")
  @ApiOperation(value = "Get's the status of the config file")
  @Produces({"text/plain"})
  public Response getStatusConfig() {

    logger.trace("checking config setup");
    try {
      String check = ConfigManager.getConfigManager(Launcher.getConfigName()).getRequiredProperty(Launcher.getConfigPrefix() + ".config.check");

      if (check == null || check.isEmpty()) {
        return Response.serverError().build();
      } else {
        return Response.ok("OK").build();
      }
    } catch (Exception e) {
      logger.error("config file is not setup correctly", e);
      return Response.serverError().build();
    }

  }

  /**
   * Gets the JVM status.
   *
   * @return the JVM status.
   */
  @GET
  @Path("/test")
  @ApiOperation(value = "A keepalive page for loadbalancer(s)")
  @Produces({"text/plain"})
  public Response getStatusBasic() {
    return Response.ok("OK").build();
  }


  @GET
  @Path("/runtime")
  @ApiOperation(value = "Get runtime stats for the JVM", response = RuntimeStats.class)
  public Response getRuntimeStats() {
    RuntimeStats runtimeStats = new RuntimeStats();
    return Response.ok(runtimeStats).build();
  }



  @GET
  @Path("/version")
  @ApiOperation(value = "The version of this instance of the application")
  @Produces({"text/plain"})
  public Response getVersion() {
    return Response.ok(Version.VERSION).build();
  }


}
