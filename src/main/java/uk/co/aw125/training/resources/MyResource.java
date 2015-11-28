package uk.co.aw125.training.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;

/**
 * Root resource (exposed at "myresource" path)
 */
@Api(value = "/myresource")
@Path("/myresource")
@Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
public class MyResource {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getItPlain() {
		return "Got it!";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getItHTML() {
		return "<h1>Got it!</h1>";
	}
}
