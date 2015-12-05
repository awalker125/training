package uk.co.aw125.training.bootstrap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launcher implements ServletContextListener {

	private static final Logger logger = LogManager.getLogger(Launcher.class.getName());

	static String configName;
	static String configPrefix;

	public static String getConfigName() {
		return configName;
	}

	public static void setConfigName(String configName) {
		Launcher.configName = configName;
	}

	public static String getConfigPrefix() {
		return configPrefix;
	}

	public static void setConfigPrefix(String configPrefix) {
		Launcher.configPrefix = configPrefix;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

		logger.warn("<<< contextDestroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		try {
			logger.info(">>> contextInitialized");

			configName = servletContextEvent.getServletContext().getInitParameter("configName");
			if (configName == null || configName.isEmpty()) {
				configName = "config";
			}
			configPrefix = servletContextEvent.getServletContext().getInitParameter("configPrefix");
			if (configPrefix == null || configPrefix.isEmpty()) {
				configPrefix = "uk.co.aw125.training";
			}

			System.out.println(">>>> configName: " + configName + " <<<<");
			System.out.println(">>>> configPrefix: " + configPrefix + " <<<<");

		} catch (Exception e) {
			logger.error("An unexpected context initialization exception occured", e);
		}

	}

	public Launcher() {

	}

	// static {
	//
	//
	// }

}
