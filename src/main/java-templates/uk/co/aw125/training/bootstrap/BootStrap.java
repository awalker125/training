package uk.co.aw125.training.bootstrap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import io.swagger.jaxrs.config.BeanConfig;

// TODO: Auto-generated Javadoc
/**
 * The Class BootStrap.
 */
@SuppressWarnings("serial")
public class BootStrap extends HttpServlet {

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("${project.version}-${jenkins.build}");

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
