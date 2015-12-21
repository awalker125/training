package uk.co.aw125.training.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import uk.co.aw125.training.bootstrap.Launcher;
import uk.co.aw125.training.config.ConfigManager;
import uk.co.aw125.training.models.Excercise;
import uk.co.aw125.training.models.Heartbeat;
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

	public boolean verifyDatabaseAlive() {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection heartbeats = jongo.getCollection(Heartbeat.class.getSimpleName());

		Heartbeat heartbeat = new Heartbeat();
		heartbeats.insert(heartbeat);

		if (heartbeats.count() < 1) {
			logger.error("Could not write heartbeat message to monogo db");
			return false;
		} else {
			logger.info("Successfully wrote heartbeat message to heartbeat collection with id " + heartbeat.getId());
			heartbeats.drop();
			return true;
		}

	}

	public User saveUser(User user) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection users = jongo.getCollection(User.class.getSimpleName());

		users.insert(user);
		logger.error(user.getUsername() + " has id " + user.getId());

		return user;
	}

	public User updateUser(String username, User user) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection users = jongo.getCollection(User.class.getSimpleName());

		users.update("{username:#}", username).with(user);
		return user;

	}

	public User getUserByUsername(String username) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection users = jongo.getCollection(User.class.getSimpleName());

		MongoCursor<User> foundUsers = users.find("{username:#}", username).as(User.class);

		if (foundUsers.count() == 0) {
			logger.debug("username " + username + " not found");
			return null;
		} else if (foundUsers.count() == 1) {
			User user = foundUsers.next();
			return user;
		} else {
			throw new RuntimeException("found " + foundUsers.count() + " users with the username" + username);
		}

	}

	public boolean removeUserByUsername(String username) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection users = jongo.getCollection(User.class.getSimpleName());

		MongoCursor<User> foundUsers = users.find("{username:#}", username).as(User.class);

		if (foundUsers.count() == 0) {
			logger.debug("username " + username + " not found");
			return false; // User not found
		} else {
			int deleted = 0;
			while (foundUsers.hasNext()) {
				User user = foundUsers.next();
				logger.debug("deleting user with id" + user.getId());
				users.remove(new ObjectId(user.getId()));
				deleted++;
			}
			logger.debug("deleted " + deleted + " user(s) with username " + username);
			return true; // We got rid
		}

	}

	public boolean userExists(String username) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection users = jongo.getCollection(User.class.getSimpleName());

		MongoCursor<User> foundUsers = users.find("{username:#}", username).as(User.class);

		if (foundUsers.count() == 0) {
			logger.debug("username " + username + " not found");
			return false;
		} else {
			logger.debug("found " + foundUsers.count() + " users with username " + username);
			return true;
		}

	}

	public Excercise saveExcercise(Excercise excercise) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

		excercises.insert(excercise);
		logger.error(excercise.getName() + " has id " + excercise.getId());

		return excercise;
	}

	public Excercise updateExcercise(String name, Excercise excercise) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

		excercises.update("{name:#}", name).with(excercise);
		return excercise;

	}

	public Excercise getExcerciseByName(String name) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

		MongoCursor<Excercise> foundExcercises = excercises.find("{name:#}", name).as(Excercise.class);

		if (foundExcercises.count() == 0) {
			logger.debug("name " + name + " not found");
			return null;
		} else if (foundExcercises.count() == 1) {
			Excercise excercise = foundExcercises.next();
			return excercise;
		} else {
			throw new RuntimeException("found " + foundExcercises.count() + " excercises with the name" + name);
		}

	}

	public boolean removeExcerciseByName(String name) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

		MongoCursor<Excercise> foundExcercises = excercises.find("{name:#}", name).as(Excercise.class);

		if (foundExcercises.count() == 0) {
			logger.debug("name " + name + " not found");
			return false; // Excercise not found
		} else {
			int deleted = 0;
			while (foundExcercises.hasNext()) {
				Excercise excercise = foundExcercises.next();
				logger.debug("deleting excercise with id" + excercise.getId());
				excercises.remove(excercise.getId());
				deleted++;
			}
			logger.debug("deleted " + deleted + " excercise(s) with name " + name);
			return true; // We got rid
		}

	}

	public boolean excerciseExists(String name) {

		Jongo jongo = new Jongo(mongoDatabase);
		MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

		MongoCursor<Excercise> foundExcercises = excercises.find("{name:#}", name).as(Excercise.class);

		if (foundExcercises.count() == 0) {
			logger.debug("name " + name + " not found");
			return false;
		} else {
			logger.debug("found " + foundExcercises.count() + " excercises with name " + name);
			return true;
		}

	}
}
