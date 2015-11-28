package uk.co.aw125.training.resources;

import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jaxb.internal.XmlCollectionJaxbProvider.App;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import uk.co.aw125.training.resources.MyResource;

public class MyResourceTest extends JerseyTest {

	@Override
	protected ResourceConfig configure() {
	    enable(TestProperties.LOG_TRAFFIC);
        return new ResourceConfig(MyResource.class);
	}

	@Test
	public void testgetItPlain() {
		final Client client = client();
		final WebTarget webTarget = client.target(getBaseUri()).path("myresource");

		Response response;

		response = webTarget.request("text/plain").get();
		assertEquals(200, response.getStatus());
		assertEquals("Got it!", response.readEntity(String.class));
		

		//response = webTarget.request("text/plain").put(Entity.text("Hello"));
		//assertEquals(204, response.getStatus());
		//assertEquals("Hello", webTarget.request("text/plain").get(String.class));

		//response = webTarget.request("text/plain").post(Entity.text(" World!"));
		//assertEquals(200, response.getStatus());
		//assertEquals("Hello World!", webTarget.request("text/plain").get(String.class));

		//response = webTarget.request("text/plain").delete();
		//assertEquals(204, response.getStatus());
		//assertEquals(204, webTarget.request("text/plain").get().getStatus());
	}
	

	@Test
	public void testgetItHTML() {
		final Client client = client();
		final WebTarget webTarget = client.target(getBaseUri()).path("myresource");

		Response response;

		
		response = webTarget.request("text/html").get();
		assertEquals(200, response.getStatus());
		assertEquals("<h1>Got it!</h1>", response.readEntity(String.class));

		//response = webTarget.request("text/plain").put(Entity.text("Hello"));
		//assertEquals(204, response.getStatus());
		//assertEquals("Hello", webTarget.request("text/plain").get(String.class));

		//response = webTarget.request("text/plain").post(Entity.text(" World!"));
		//assertEquals(200, response.getStatus());
		//assertEquals("Hello World!", webTarget.request("text/plain").get(String.class));

		//response = webTarget.request("text/plain").delete();
		//assertEquals(204, response.getStatus());
		//assertEquals(204, webTarget.request("text/plain").get().getStatus());
	}


}
