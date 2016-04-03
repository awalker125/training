package uk.co.aw125.training.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CustomNotAuthorizedException extends WebApplicationException {

	
	/**
	 * Create a HTTP 400 (Bad Request) exception.
	 * 
	 * @param message
	 *            the String that is the entity of the 404 response.
	 */
	public CustomNotAuthorizedException(String message) {
		super(Response.status(Response.Status.UNAUTHORIZED).entity(message).type("text/plain").build());
	}
}
