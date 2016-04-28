package uk.co.aw125.training.auth;

import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import uk.co.aw125.training.bootstrap.Launcher;
import uk.co.aw125.training.config.ConfigManager;
import uk.co.aw125.training.exceptions.CustomNotAuthorizedException;
import uk.co.aw125.training.models.core.Auth;

public class AuthenticationManager {

  private static final Logger logger = LogManager.getLogger(AuthenticationManager.class);

  private static AuthenticationManager authenticationCache;

  private long maxEntries = 10000;
  private int maxAgeMinutes = 10;
  private HashMap<String, Auth> cache;
  ConfigManager configManager;

  String configName;
  String configPrefix;


  private AuthenticationManager() {
    logger.debug("Authentication cache initialising");

    configName = Launcher.getConfigName();
    configPrefix = Launcher.getConfigPrefix();

    logger.trace("configName: " + configName);
    logger.trace("configPrefix: " + configPrefix);

    // load properties for mongodb
    configManager = ConfigManager.getConfigManager(configName);

    maxEntries = configManager.getProperty(configPrefix + "." + AuthenticationManager.class.getSimpleName() + ".maxEntries", 10000);
    maxAgeMinutes = configManager.getProperty(configPrefix + "." + AuthenticationManager.class.getSimpleName() + ".maxAgeMinutes", 10);

    cache = new HashMap<>();

    logger.debug("Authentication cache intialised");
  }

  public static AuthenticationManager getAuthenticationCache() {
    if (authenticationCache == null) {
      authenticationCache = new AuthenticationManager();
    }
    return authenticationCache;
  }


  public Auth authenticate(HttpHeaders headers) {

    String username;
    String api_key;
    try {
      username = headers.getRequestHeader("username").get(0);
      api_key = headers.getRequestHeader("api_key").get(0);

      if (username == null || username.isEmpty()) {
        logger.error("username not set");
        throw new CustomNotAuthorizedException("Not Authorized. Missing username header");
      }

      if (api_key == null || api_key.isEmpty()) {
        logger.error("username not set");
        throw new CustomNotAuthorizedException("Not Authorized. Missing api_key header");
      }

    } catch (NullPointerException npe) {
      logger.error("unexpected null pointer during authentication", npe);
      throw new CustomNotAuthorizedException("Not Authorized. Unable to retrieve authentication data");

    }


    Auth auth = cache.get(api_key);

    if (auth == null) {
      logger.trace(api_key + " not found in cache. Must validated");
      auth = externalAuthentication(username, api_key);
      return auth;
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
        auth = externalAuthentication(username, api_key);
        return auth;

      } else {
        logger.trace(api_key + " is still valid");
        auth.updateSeenEqualsNow();
        return auth;
      }

    }


  }

  private Auth externalAuthentication(String username, String api_key) {
    if (username.equals("clarkkent") && api_key.equals("ueh32ueb23be32e73e723e2e7h2e7h23eibhvhv")) {
      Auth auth = cache.get(api_key);
      if (auth == null) {
        auth = new Auth(username, api_key);
        cache.put(api_key, auth);
      }
      auth.updateSeenEqualsNow();
      auth.updateValidatedEqualsNow();
      return auth;
    } else {
      logger.error(username + " with api_key " + api_key + " not valid");
      throw new CustomNotAuthorizedException("Not authoruized. The supplied api_key is invalid for the supplied username");
    }

  }


}
