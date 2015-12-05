package uk.co.aw125.training.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UserAlreadyExistsException extends WebApplicationException {

	/**
	 * Create a HTTP 400 (Bad Request) exception.
	 * 
	 * @param message
	 *            the String that is the entity of the 404 response.
	 */
	public UserAlreadyExistsException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST).entity(message).type("text/plain").build());
	}
}
