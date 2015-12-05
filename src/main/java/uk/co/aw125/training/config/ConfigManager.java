package uk.co.aw125.training.config;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import uk.co.aw125.training.helpers.Utils;

/**
 * The config file to load will be as follows: 1. The file given by the system property "config." + <configName>" e.g config.appname 2. The file called <configName>.properties in the <root>/config directory e.g <root>/config/appname.properties Note: <root> is determined by the value of the config.root system property or if not set the value of the user.dir system property
 * 
 * @author Andrew
 */
public class ConfigManager implements Serializable {

	// <editor-fold defaultstate="collapsed" desc="Singleton">
	private static final Logger logger = LogManager.getLogger(ConfigManager.class);
	private static Random random;
	private final String nestedPropertyRegex = "\\$\\{(.+?)\\}";
	private final String xorPropertyRegex = "\\$XOR\\{(.+?)\\}";
	private static HashMap<String, ConfigManager> configManagerMap;
	// private static ConfigManager configManager;
	private String configName = null;
	protected String targetPropertiesFile;
	Properties properties;
	long lastLoad;
	final Object lock = new Object();
	int recursionLimit = 1000;
	int recursionDepth = 0;
	int reloadInterval = 5;

	static {
		configManagerMap = new HashMap<String, ConfigManager>();
		random = new Random();
	}

	public synchronized static ConfigManager getConfigManager(String configName) {
		if (configManagerMap.containsKey(configName)) {
			return configManagerMap.get(configName);
		} else {
			ConfigManager configManager = new ConfigManager(configName);
			configManagerMap.put(configName, configManager);
			return configManager;
		}
	}

	public synchronized static ConfigManager getConfigManager() {

		String defaultconfigName = "config";

		if (configManagerMap.containsKey(defaultconfigName)) {
			return configManagerMap.get(defaultconfigName);
		} else {
			ConfigManager configManager = new ConfigManager(defaultconfigName);
			configManagerMap.put(defaultconfigName, configManager);
			return configManager;
		}
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public int getReloadInterval() {
		return reloadInterval;
	}

	/**
	 * Sets the reload interval. Default is 5 seconds. Use a value 0 to indicate no reload
	 *
	 * @param reloadInterval
	 * @return
	 */
	public void setReloadInterval(int reloadInterval) {
		this.reloadInterval = reloadInterval;
	}

	// </editor-fold>
	public String getRoot() {
		logger.trace("Getting cwd");
		String cwd = System.getProperty("user.dir");
		logger.trace("cwd is " + cwd);
		logger.trace("getting root");
		String root = System.getProperty("config.root", cwd);
		logger.trace("Root is " + root);
		return root;
	}

	private ConfigManager(String configName) {
		this.configName = configName;

		logger.debug("Creating ConfigManager singleton for app " + configName);
		this.targetPropertiesFile = System.getProperty("config." + configName, getRoot() + "/config/" + configName + ".properties");
		logger.trace("ConfigManager singleton created");

	}

	private void reloadProperties() {
		synchronized (lock) {
			logger.trace("Reloading properties");
			if (properties == null) {
				logger.trace("Properties are null calling loadProperties");
				loadProperties();
			} else {
				if (this.reloadInterval > 0) {
					logger.trace("Properties are already loaded. Checking staleness");
					Date now = new Date();
					int reloadMillseconds = this.reloadInterval * 1000;
					if ((now.getTime() - this.lastLoad) > reloadMillseconds) {
						logger.debug("Properties have not been loaded within the last " + this.reloadInterval + " secs and will be reloaded");
						loadProperties();
					} else {
						logger.debug("Properties have been loaded within the last " + this.reloadInterval + " secs and will not be reloaded");
					}
				} else {
					logger.trace("reloadInterval is not greater than 0. Will not reload dynamically");
				}
			}
		}
	}

	private void loadProperties() {

		logger.trace("Loading properties");
		try {

			this.properties = new Properties();
			FileInputStream fis = new FileInputStream(targetPropertiesFile);
			this.properties.load(fis);
			fis.close();
			if (this.properties == null) {
				logger.fatal("Properties are null after loading properties from " + targetPropertiesFile);
			} else {
				logger.trace("Properties loaded from " + targetPropertiesFile);
				Date now = new Date();
				this.lastLoad = now.getTime();
			}
		} catch (Exception e) {
			logger.error("Could not load properties from " + targetPropertiesFile, e);
		}
	}

	private String resolveNestedPropertyValue(String name, String defaultValue) {

		recursionDepth++;

		try {

			logger.trace("recursionDepth " + recursionDepth);

			if (recursionDepth > recursionLimit) {
				logger.fatal("Recursion limit breached");
				throw new Exception("Recursion limit breached");
			}

			// trim the default just in case
			defaultValue = trim(defaultValue);

			reloadProperties();
			logger.trace("Looking up property " + name);
			String startingValue = this.properties.getProperty(name, defaultValue);

			if (startingValue == null) {
				logger.warn("Could not find property " + name);
				recursionDepth = 0;
				return startingValue;

			}

			logger.trace("Using " + nestedPropertyRegex + " as regex for nested properties");
			Pattern pattern = Pattern.compile(nestedPropertyRegex);

			Matcher matcher = pattern.matcher(startingValue);
			List<String> nestedProperties = new LinkedList<String>();
			while (matcher.find()) {
				logger.debug("found nested property " + matcher.group());
				nestedProperties.add(matcher.group(1));
			}

			if (nestedProperties.size() > 0) {

				for (Iterator<String> it = nestedProperties.iterator(); it.hasNext();) {
					String nestedProperty = it.next();
					logger.debug("nestedProperty> " + nestedProperty);

					if (nestedProperty.equalsIgnoreCase(name)) {
						logger.fatal("Unresolvable circular reference in key/value " + name + "/" + startingValue);
						throw new Exception("Unresolvable circular reference in key/value " + name + "/" + startingValue);
					}

					StringTokenizer stringTokenizer = new StringTokenizer(nestedProperty, ":");

					logger.trace("stringTokenizer.countTokens() " + stringTokenizer.countTokens());

					if (stringTokenizer.hasMoreElements()) {
					} else {
						logger.fatal("Nested property name is null after split. Cannot continue");
						throw new Exception("Nested property name is null after split. Cannot continue");
					}

					String nestedName = stringTokenizer.nextToken();

					String nestedDefault = null;

					if (stringTokenizer.hasMoreElements()) {
						nestedDefault = stringTokenizer.nextToken();
					} else {
						logger.warn("No default supplied for " + nestedProperty);
					}
					while (stringTokenizer.hasMoreElements()) {
						logger.warn("Addtional element " + stringTokenizer.nextToken() + " in split on : will be ignored");
					}
					logger.trace("nestedName " + nestedName);
					logger.trace("nestedDefault " + nestedDefault);

					String nestedValue = resolveNestedPropertyValue(nestedName, nestedDefault);
					if (nestedValue == null) {
						logger.error("Failed to find nested property " + nestedProperty + ". No replacement will take place");
						recursionDepth = 0;
						return null;
					} else {
						logger.trace("Before replace> " + startingValue);
						startingValue = startingValue.replaceAll("\\$\\{" + nestedProperty + "\\}", nestedValue);
						logger.trace("After replace> " + startingValue);
					}

				}
				logger.debug("Finished replacing all nested properties. New value is: " + startingValue);
				startingValue = decrypt(startingValue);
				recursionDepth = 0;
				return startingValue;
			} else {
				logger.debug("No nested properties found. Returning original " + startingValue);
				startingValue = decrypt(startingValue);
				recursionDepth = 0;
				return startingValue;
			}

		} catch (Exception e) {
			logger.error("Could not load property " + name + " returning default value " + defaultValue, e);
			recursionDepth = 0;
			return defaultValue;

		}

	}

	private String trim(String string) {
		if (string == null) {
			return string;
		}
		logger.trace("before trim " + string);
		string = string.trim();
		logger.trace("after trim " + string);
		return string;
	}

	private byte[] randomBytes(int length) {
		byte[] out = new byte[length];
		random.nextBytes(out);
		return out;
	}

	public String encrypt(String string) {

		logger.trace("Encrypting: " + string);
		try {
			for (int i = 0; i < 2; i++) {
				string = xorEncrypt(string);
			}
			return string;
		} catch (Exception e) {
			logger.fatal("Failed to encrypt string: " + string, e);
			return null;
		}
	}

	public String decrypt(String string) {

		logger.trace("Using " + xorPropertyRegex + " as regex for nested properties");
		Pattern pattern = Pattern.compile(xorPropertyRegex);

		Matcher matcher = pattern.matcher(string);

		while (matcher.find()) {
			logger.debug("found xor encypted value " + matcher.group());
			String xor = matcher.group(1);
			logger.trace("Decrypting: " + xor);
			try {
				for (int i = 0; i < 2; i++) {
					xor = xorDecrypt(xor);
				}
				return xor;
			} catch (Exception e) {
				logger.fatal("Failed to decrypt string: " + string, e);
				return null;
			}
		}
		// If we are here we did not find an encrypted string
		logger.debug("No encryption found");
		return string;

	}

	private String xorEncrypt(String string) throws Exception {

		logger.trace("String to xor encrypt is: " + string);
		byte[] stringBytes = string.getBytes();
		byte[] randomBytes = randomBytes(stringBytes.length);

		if (stringBytes.length != randomBytes.length) {
			throw new RuntimeException("Random byte length does not match string byte length");
		}

		byte[] xor = new byte[stringBytes.length];
		for (int i = 0; i < xor.length; i++) {
			xor[i] = (byte) (stringBytes[i] ^ randomBytes[i]);
		}

		byte[] encrypted = new byte[xor.length * 2];
		System.arraycopy(xor, 0, encrypted, 0, xor.length);
		System.arraycopy(randomBytes, 0, encrypted, xor.length, randomBytes.length);

		BASE64Encoder encoder = new BASE64Encoder();
		String base64Encoded = encoder.encode(encrypted);
		logger.trace("base64 " + base64Encoded);
		return base64Encoded;

	}

	private String xorDecrypt(String base64) throws Exception {

		logger.trace("String to xor decrypt is: " + base64);

		BASE64Decoder decoder = new BASE64Decoder();
		byte[] encrypted = decoder.decodeBuffer(base64);

		if (Utils.isOdd(encrypted.length)) {
			throw new RuntimeException("The base64 string decoded to an uneven byte array length");
		}

		byte[] xorBytes = new byte[encrypted.length / 2];
		byte[] randomBytes = new byte[encrypted.length / 2];

		System.arraycopy(encrypted, 0, xorBytes, 0, encrypted.length / 2);
		System.arraycopy(encrypted, encrypted.length / 2, randomBytes, 0, encrypted.length / 2);

		byte[] stringBytes = new byte[xorBytes.length];

		for (int i = 0; i < xorBytes.length; i++) {
			stringBytes[i] = (byte) (randomBytes[i] ^ xorBytes[i]);
		}

		String string = new String(stringBytes);
		logger.trace("string " + string);
		return string;

	}

	// get properties

	public HashMap<String, String> getProperties(String prefix) {

		HashMap<String, String> pMap = new HashMap<String, String>();
		try {
			reloadProperties();
			logger.trace("Looking up properties with prefix " + prefix);
			Enumeration<?> e = this.properties.propertyNames();

			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if (key.startsWith(prefix)) {
					String suffix = key.replaceFirst(prefix, "");
					if (suffix.startsWith(".")) {
						suffix = suffix.substring(1, suffix.length());
					}

					logger.trace("Matched " + prefix + " for key " + key);
					// pMap.put(suffix, this.properties.getProperty(key));
					pMap.put(suffix, resolveNestedPropertyValue(key, null));
				} else {
					logger.trace("Could not match " + prefix + " for key " + key);
				}
			}
		} catch (Exception e) {
			logger.error("Could not load properties with prefix " + prefix, e);
		}
		return pMap;

	}

	public String getProperty(String name, String defaultValue) {

		try {
			reloadProperties();
			String value = resolveNestedPropertyValue(name, defaultValue);
			return value;
		} catch (Exception e) {
			logger.error("Could not load property " + name + " returning default value " + defaultValue, e);
			return defaultValue;

		}

	}

	public String getRequiredProperty(String name, String defaultValue) {

		String value = getProperty(name, defaultValue);
		if (Utils.nullOrEmpty(value)) {
			throw new RuntimeException("Failed to get a value for property: " + name);
		}
		return value;
	}

	public String getRequiredProperty(String name) {

		String value = getProperty(name, null);
		if (Utils.nullOrEmpty(value)) {
			throw new RuntimeException("Failed to get a value for property: " + name);
		}
		return value;
	}

	public boolean getProperty(String name, boolean defaultValue) {
		// synchronized (lock) {
		try {
			reloadProperties();
			logger.trace("Looking up property " + name);
			String defaultValueString = String.valueOf(defaultValue);
			// String s = this.properties.getProperty(name, defaultValueString);
			// logger.trace("Value of property " + name + " is " + s);

			String value = resolveNestedPropertyValue(name, defaultValueString);

			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			logger.error("Could not load property " + name + " returning default value " + defaultValue, e);
			return defaultValue;
		}
		// }
	}

	public int getProperty(String name, int defaultValue) {

		// synchronized (lock) {
		try {
			reloadProperties();
			String defaultString = String.valueOf(defaultValue);

			String value = resolveNestedPropertyValue(name, defaultString);
			Integer i = Integer.parseInt(value);

			logger.trace("Value of property " + name + " is " + i.toString());
			return i.intValue();

		} catch (Exception e) {
			logger.error("Could not load property " + name + " returning default value " + defaultValue, e);
			return defaultValue;

		}
		// }
	}

	public long getProperty(String name, long defaultValue) {
		// synchronized (lock) {
		try {
			reloadProperties();

			String defaultString = String.valueOf(defaultValue);

			String value = resolveNestedPropertyValue(name, defaultString);
			Long l = Long.parseLong(value);
			logger.trace("Value of property " + name + " is " + l.toString());
			return l.intValue();
		} catch (Exception e) {
			logger.error("Could not load property " + name + " returning default value " + defaultValue, e);
			return defaultValue;

		}
		// }
	}

}
