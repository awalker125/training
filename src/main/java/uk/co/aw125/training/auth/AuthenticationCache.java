package uk.co.aw125.training.auth;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.aw125.training.bootstrap.Launcher;
import uk.co.aw125.training.config.ConfigManager;
import uk.co.aw125.training.models.core.Auth;

public class AuthenticationCache {

  private static final Logger logger = LogManager.getLogger(AuthenticationCache.class);

  private static AuthenticationCache authenticationCache;

  private long maxEntries = 10000;
  private int maxAgeMinutes = 10;
  private HashMap<String, Auth> cache;
  ConfigManager configManager;

  String configName;
  String configPrefix;


  private AuthenticationCache() {
    logger.debug("Authentication cache initialising");

    configName = Launcher.getConfigName();
    configPrefix = Launcher.getConfigPrefix();

    logger.trace("configName: " + configName);
    logger.trace("configPrefix: " + configPrefix);

    // load properties for mongodb
    configManager = ConfigManager.getConfigManager(configName);

    maxEntries = configManager.getProperty(configPrefix + "." + AuthenticationCache.class.getSimpleName() + ".maxEntries", 10000);
    maxAgeMinutes = configManager.getProperty(configPrefix + "." + AuthenticationCache.class.getSimpleName() + ".maxAgeMinutes", 10);

    cache = new HashMap<>();

    logger.debug("Authentication cache intialised");
  }

  public static AuthenticationCache getAuthenticationCache() {
    if (authenticationCache == null) {
      authenticationCache = new AuthenticationCache();
    }
    return authenticationCache;
  }


  public boolean isAuthenticated(String username, String api_key) {

    Auth auth = cache.get(api_key);

    if (auth == null) {
      logger.trace(api_key + " not found in cache. Must validated");
      return false;
    } else {

      
      
      return true;
    }

  }
}
