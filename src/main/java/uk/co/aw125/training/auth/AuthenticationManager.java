package uk.co.aw125.training.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.codec.binary.*;
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

	public static String encode(String access_token, String app_secret) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(app_secret.getBytes("UTF-8"), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		//byte[] hmacData = sha256_HMAC.doFinal(access_token.getBytes("UTF-8"));
		//return new String(Base64.encodeBase64URLSafe(hmacData), "UTF-8");
		 return Hex.encodeHexString(sha256_HMAC.doFinal(access_token.getBytes("UTF-8")));
	}


	
	private Auth externalAuthentication(String username, String access_token) {
		try {
			String app_secret = configManager.getRequiredProperty("uk.co.aw125.training.facebook.app_secret");

			String appsecret_proof = encode(access_token, app_secret);

			logger.info("appsecret_proof " + appsecret_proof);

			SSLContext sslContext = SSLContext.getDefault();

			Client client = ClientBuilder.newBuilder().sslContext(sslContext).build();

			WebTarget graphFacebookComWebTarget = client.target("https://graph.facebook.com");
			WebTarget versionWebTarget = graphFacebookComWebTarget.path("v2.7");
			WebTarget meWebTarget = versionWebTarget.path("me");

			WebTarget meWebTargetWithQueryParams = meWebTarget.queryParam("access_token", access_token).queryParam("appsecret_proof", appsecret_proof).queryParam("fields", "email,name").queryParam("debug", "all");

			Response response = meWebTargetWithQueryParams.request().get();

			String responseString = response.readEntity(String.class);

			if (response.getStatus() == 200) {

				logger.info(responseString);
				Auth auth = cache.get(access_token);
				if (auth == null) {
					auth = new Auth(username, access_token);
					cache.put(access_token, auth);
				}
				auth.updateSeenEqualsNow();
				auth.updateValidatedEqualsNow();
				return auth;
			} else {
				logger.error(responseString);
				logger.error(username + " with access_token " + access_token + " not valid");
				throw new CustomNotAuthorizedException("Not authorized. The supplied api_key is invalid for the supplied username");
			}

		} catch (Exception e) {
			logger.error(e);
			throw new CustomNotAuthorizedException("A problem occured authenticating access_token");
		}

	}

}
