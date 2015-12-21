package uk.co.aw125.training.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.exceptions.HeartbeatException;

@Path("/heartbeat")
@Api(value = "/heartbeat", description = "Application Heartbeat")

public class HeartbeatResource {

	@GET
	@Produces(value = { "text/plain" })
	@ApiOperation(value = "Check application is alive", response = String.class)
	public Response isAliveTextPlain() {

		boolean isAlive = DataManager.getDataManager().verifyDatabaseAlive();

		if (isAlive) {
			return Response.ok().entity("alive").build();
		} else {
			throw new HeartbeatException("Could not connect to mongo db");
		}
	}

	@GET
	@Produces(value = { "text/html" })
	@ApiOperation(value = "Check application is alive", response = String.class)
	public Response isAliveTextHTML() {

		boolean isAlive = DataManager.getDataManager().verifyDatabaseAlive();

		if (isAlive) {
			return Response.ok().entity("<h1>alive</h1>").build();
		} else {
			throw new HeartbeatException("Could not connect to mongo db");
		}
	}

}
