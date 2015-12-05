package uk.co.aw125.training.data;

import javax.jws.soap.SOAPBinding.Use;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.co.aw125.training.bootstrap.Launcher;
import uk.co.aw125.training.config.ConfigManager;
import uk.co.aw125.training.exceptions.BadRequestException;
import uk.co.aw125.training.models.User;

public class DataManager {

	private static final Logger logger = LogManager.getLogger(DataManager.class);

	// Singleton
	private static DataManager dataManager;

	// fields

	// We have one Mongo Client per data type/collection
	MongoClient mongoClient;
	// this is deprecated in mongo client 3.x but mongo jack 2.5.1 still uses it
	DB mongoDatabase;
	ConfigManager configManager;
	String mongoConnectionString;
	String mongoDbName;
	String configName;
	String configPrefix;

	@SuppressWarnings("deprecation")
	private DataManager() {
		// Get some bootstrap info from the web.xml
		configName = Launcher.getConfigName();
		configPrefix = Launcher.getConfigPrefix();

		logger.trace("configName: " + configName);
		logger.trace("configPrefix: " + configPrefix);

		// load properties for mongodb
		configManager = ConfigManager.getConfigManager(configName);

		mongoConnectionString = configManager.getRequiredProperty(configPrefix + "." + DataManager.class.getSimpleName() + ".mongoConnectionString");
		mongoDbName = configManager.getRequiredProperty(configPrefix + "." + DataManager.class.getSimpleName() + ".mongoDbName");

		logger.trace("mongoConnectionString: " + mongoConnectionString);
		logger.trace("mongoDbName: " + mongoDbName);

		// Connect to mongodb
		logger.debug("Creaing MongoClient with connection string: " + mongoConnectionString);

		MongoClientURI mongoClientURI = new MongoClientURI(mongoConnectionString);
		mongoClient = new MongoClient(mongoClientURI);

		// Get the database
		logger.debug("Gettin database: " + mongoDbName);
		// this is deprecated in mongo client 3.x but mongo jack 2.5.1 still //
		// uses it
		mongoDatabase = mongoClient.getDB(mongoDbName);

	}

	// singleton
	public static synchronized DataManager getDataManager() {
		if (dataManager == null) {
			dataManager = new DataManager();
		}
		return dataManager;
	}

	public void saveUser(User user) {

		DBCollection dbCollection = mongoDatabase.getCollection(User.class.getSimpleName());
		JacksonDBCollection<User, String> collection = JacksonDBCollection.wrap(dbCollection, User.class, String.class);

		WriteResult<User, String> result = collection.insert(user);
		// We probably dont need this but it provides a means of catching if
		// something has gone wrong.
		String id = result.getSavedId();
		User savedUser = result.getSavedObject();
	}

	public User getUserByUsername(String username) throws BadRequestException {

		DBCollection dbCollection = mongoDatabase.getCollection(User.class.getSimpleName());
		JacksonDBCollection<User, String> collection = JacksonDBCollection.wrap(dbCollection, User.class, String.class);

		DBCursor<User> cursor = collection.find().is("username", username);
		if (cursor.count() == 0) {
			logger.debug("username " + username + " not found");
			return null;
		} else if (cursor.count() == 1) {
			User user = cursor.next();
			return user;
		} else {
			throw new BadRequestException(400, "found " + cursor.count() + " users with the username" + username);
		}
	}

	public boolean userExists(String username) {

		DBCollection dbCollection = mongoDatabase.getCollection(User.class.getSimpleName());
		JacksonDBCollection<User, String> collection = JacksonDBCollection.wrap(dbCollection, User.class, String.class);

		DBCursor<User> cursor = collection.find().is("username", username);
		if (cursor.count() == 0) {
			logger.debug("username " + username + " not found");
			return false;
		} else {
			logger.debug("found " + cursor.count() + " users with username " + username);
			return true;
		}
	}
}
