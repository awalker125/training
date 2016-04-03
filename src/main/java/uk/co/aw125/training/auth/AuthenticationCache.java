package uk.co.aw125.training.auth;

import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import uk.co.aw125.training.bootstrap.Launcher;
import uk.co.aw125.training.config.ConfigManager;
import uk.co.aw125.training.exceptions.CustomNotAuthorizedException;
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


  public boolean authenticate(String username, String api_key) {
    if (username.equals("clarkkent") && api_key.equals("ueh32ueb23be32e73e723e2e7h2e7h23eibhvhv")) {
      Auth auth = cache.get(api_key);
      if (auth == null) {
        auth = new Auth(username, api_key);
        cache.put(api_key, auth);
      }
      auth.updateSeenEqualsNow();
      auth.updateValidatedEqualsNow();
      return true;
    } else {
      logger.error(username + " with api_key " + api_key + " not valid");
      return false;
    }
  }

  public boolean isAuthenticated(String username, String api_key) {

    Auth auth = cache.get(api_key);

    if (auth == null) {
      logger.trace(api_key + " not found in cache. Must validated");
      return false;
    } else {

      if (auth.getUsername().equals(username)) {
        logger.trace("username matches cache");
      } else {
        throw new CustomNotAuthorizedException("username in cache does not match api_key provided");
      }


      DateTime now = DateTime.now();
      DateTime maxAgeDateTime = now.minusMinutes(maxAgeMinutes);

      logger.trace("now: " + now);
      logger.trace("maxAgeDateTime: " + maxAgeDateTime);

      if (auth.getValidated().isBefore(maxAgeDateTime)) {
        logger.warn(api_key + " has been seen but was last validated " + auth.getValidated() + " and must be revalidated");
        auth.updateSeenEqualsNow();
        return false;
      } else {
        logger.trace(api_key + " is still valid");
        auth.updateSeenEqualsNow();
        return true;
      }

    }

  }
}
