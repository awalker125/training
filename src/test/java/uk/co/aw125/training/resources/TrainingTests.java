package uk.co.aw125.training.resources;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import uk.co.aw125.training.models.core.User;

public class TrainingTests extends JerseyTest {

	@Override
	protected ResourceConfig configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(User.class);
	}

	@Test
	public void createUserTest() {
		final Client client = client();
		final WebTarget webTarget = client.target(getBaseUri()).path("user");

		Response response;

		User user = new User();
		user.setEmail("andrew@email.com");
		user.setUsername("andrew");

		response = webTarget.request().post(Entity.json(user));
		assertEquals(200, response.getStatus());


		response = webTarget.request().post(Entity.json(user));
		assertEquals(400, response.getStatus());
		assertEquals("andrew already exists", response.readEntity(String.class));

	}

}
