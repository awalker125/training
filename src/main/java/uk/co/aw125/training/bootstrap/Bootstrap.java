package uk.co.aw125.training.bootstrap;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;

/**
 * Servlet implementation class Bootstrap
 */
public class Bootstrap extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.0");
		beanConfig.setSchemes(new String[] { "http" });
		beanConfig.setHost("localhost:8080");
		beanConfig.setBasePath("/training/webapi");
		beanConfig.setResourcePackage("uk.co.aw125.training.resources");
		beanConfig.setScan(true);
		beanConfig.setLicenseUrl("https://github.com/awalker125/training/blob/master/LICENSE");
		beanConfig.setLicense("Apache 2.0");
		beanConfig.setDescription(
				"This application allows the tracking of training related data on excercises like snatch, clean and jerk, bench. It's been written for Olympic/Powerlifting style training but may be useful for crossfit and other type of barbell training");
		beanConfig.setTitle("Training");

	}

}
