package uk.co.aw125.training.exceptions;

import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class HeartbeatException extends WebApplicationException {

	/**
	 * Create a HTTP 400 (Bad Request) exception.
	 * 
	 * @param message
	 *            the String that is the entity of the 404 response.
	 */
	public HeartbeatException(String message) {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
	}
}
