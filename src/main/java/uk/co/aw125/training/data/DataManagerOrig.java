package uk.co.aw125.training.data;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import uk.co.aw125.training.bootstrap.Launcher;
import uk.co.aw125.training.config.ConfigManager;
import uk.co.aw125.training.helpers.Utils;
import uk.co.aw125.training.models.core.Set;
import uk.co.aw125.training.models.core.Excercise;
import uk.co.aw125.training.models.core.Mood;
import uk.co.aw125.training.models.core.Session;
import uk.co.aw125.training.models.core.User;
import uk.co.aw125.training.models.support.Heartbeat;

public class DataManagerOrig {

  private static final Logger logger = LogManager.getLogger(DataManagerOrig.class);

  // Singleton
  private static DataManagerOrig dataManager;

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
  private DataManagerOrig() {
    // Get some bootstrap info from the web.xml
    configName = Launcher.getConfigName();
    configPrefix = Launcher.getConfigPrefix();

    logger.trace("configName: " + configName);
    logger.trace("configPrefix: " + configPrefix);

    // load properties for mongodb
    configManager = ConfigManager.getConfigManager(configName);

    mongoConnectionString = configManager.getRequiredProperty(configPrefix + "." + DataManagerOrig.class.getSimpleName() + ".mongoConnectionString");
    mongoDbName = configManager.getRequiredProperty(configPrefix + "." + DataManagerOrig.class.getSimpleName() + ".mongoDbName");

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
  public static synchronized DataManagerOrig getDataManager() {
    if (dataManager == null) {
      dataManager = new DataManagerOrig();
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

  public User insertUser(User user) {

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

  public User saveUser(User user) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection users = jongo.getCollection(User.class.getSimpleName());

    users.save(user);
    return user;

  }

  public void removeUser(User user) {
    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection users = jongo.getCollection(User.class.getSimpleName());
    users.remove(user.getId());
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

  public Excercise insertExcercise(Excercise excercise) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    excercises.insert(excercise);
    logger.error(excercise.getName() + " has id " + excercise.get_id());

    return excercise;
  }

  public Excercise updateExcercise(String name, Excercise excercise) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    excercises.update("{name:#}", name).with(excercise);
    return excercise;

  }

  public Excercise saveExcercise(Excercise excercise) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    excercises.save(excercise);
    return excercise;

  }

  public void removeExcercise(Excercise excercise) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    excercises.remove(excercise.get_id());

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
        logger.debug("deleting excercise with id" + excercise.get_id());
        excercises.remove(new ObjectId(excercise.get_id()));
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



  private String getSetCollectionName(String username) {
    if (Utils.unlessNullOrEmpty(username)) {
      StringBuilder builder = new StringBuilder();
      builder.append(Set.class.getSimpleName());
      builder.append("_");
      builder.append(username);

      String noWhiteSpace = Utils.removeWhiteSpace(builder.toString());
      String noSpecials = Utils.removeSpecialCharacters(noWhiteSpace, "_");
      return noSpecials;


    } else {
      throw new RuntimeException("username is null of empty");
    }



  }

  private String getSessionCollectionName(String username) {
    if (Utils.unlessNullOrEmpty(username)) {
      StringBuilder builder = new StringBuilder();
      builder.append(Session.class.getSimpleName());
      builder.append("_");
      builder.append(username);

      String noWhiteSpace = Utils.removeWhiteSpace(builder.toString());
      String noSpecials = Utils.removeSpecialCharacters(noWhiteSpace, "_");
      return noSpecials;


    } else {
      throw new RuntimeException("username is null of empty");
    }



  }

  public Set saveSet(Set set, String username) {

    String collectionName = getSetCollectionName(username);

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection sets = jongo.getCollection(collectionName);
    sets.save(set);
    return set;
  }

  public Set getSetById(String id, String username) {

    String collectionName = getSetCollectionName(username);

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection sets = jongo.getCollection(collectionName);

    MongoCursor<Set> foundSets = sets.find("{id:#}", id).as(Set.class);

    if (foundSets.count() == 0) {
      logger.debug("id " + id + " not found");
      return null;
    } else if (foundSets.count() == 1) {
      Set set = foundSets.next();
      return set;
    } else {
      throw new RuntimeException("found " + foundSets.count() + " sets with the id" + id);
    }

  }

  public boolean removeSetById(String id, String username) {

    String collectionName = getSetCollectionName(username);

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection sets = jongo.getCollection(collectionName);

    MongoCursor<Set> foundSets = sets.find("{id:#}", id).as(Set.class);

    if (foundSets.count() == 0) {
      logger.debug("id " + id + " not found");
      return false; // Set not found
    } else {
      int deleted = 0;
      while (foundSets.hasNext()) {
        Set set = foundSets.next();
        logger.debug("deleting set with id" + set.get_id());
        sets.remove(set.get_id());
        deleted++;
      }
      logger.debug("deleted " + deleted + " set(s) with id " + id);
      return true; // We got rid
    }

  }


  public Set getMinBodyweight12Months(String username) {

    String collectionName = getSetCollectionName(username);
    logger.trace("collectionName: " + collectionName);
    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection collection = jongo.getCollection(collectionName);

    DateTime toTime = new DateTime();

    DateTime fromTime = toTime.minusMonths(12);

    String query = "{date: {$gte: #, $lte: # }}";
    String sort = "{date: -1}";
    logger.info("query: " + query);
    logger.info("sort: " + sort);
    logger.info("fromTime: " + fromTime);
    logger.info("toTime: " + toTime);
    MongoCursor<Set> found = collection.find(query, fromTime.toDate(), toTime.toDate()).sort(sort).as(Set.class);

    List<Set> foundList = new LinkedList<>();

    while (found.hasNext()) {
      Set set = found.next();
      foundList.add(set);
    }

    if (foundList.isEmpty()) {
      logger.warn("no data found");
      return null;
    } else {
      Set minBodyweight = null;

      for (Set set : foundList) {
        if (minBodyweight == null) {
          minBodyweight = set;
        } else {
          if (set.getBodyWeight() < minBodyweight.getBodyWeight()) {
            minBodyweight = set;
          }
        }
      }
      return minBodyweight;
    }

  }

  public Set getMaxBodyweight12Months(String username) {

    String collectionName = getSetCollectionName(username);
    logger.trace("collectionName: " + collectionName);
    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection collection = jongo.getCollection(collectionName);

    DateTime toTime = new DateTime();

    DateTime fromTime = toTime.minusMonths(12);

    String query = "{date: {$gte: #, $lte: # }}";
    String sort = "{date: -1}";
    logger.info("query: " + query);
    logger.info("sort: " + sort);
    logger.info("fromTime: " + fromTime);
    logger.info("toTime: " + toTime);
    MongoCursor<Set> found = collection.find(query, fromTime.toDate(), toTime.toDate()).sort(sort).as(Set.class);

    List<Set> foundList = new LinkedList<>();

    while (found.hasNext()) {
      Set set = found.next();
      foundList.add(set);
    }

    if (foundList.isEmpty()) {
      logger.warn("no data found");
      return null;
    } else {
      Set maxBodyweight = null;

      for (Set set : foundList) {
        if (maxBodyweight == null) {
          maxBodyweight = set;
        } else {
          if (set.getBodyWeight() > maxBodyweight.getBodyWeight()) {
            maxBodyweight = set;
          }
        }
      }
      return maxBodyweight;
    }

  }

  public Set[] getSets(DateTime fromTime, DateTime toTime, String username) {

    String collectionName = getSetCollectionName(username);
    logger.trace("collectionName: " + collectionName);
    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection collection = jongo.getCollection(collectionName);

    // DateTime toTime = new DateTime();

    // DateTime fromTime = toTime.minusMonths(12);

    String query = "{date: {$gte: #, $lte: # }}";
    String sort = "{date: -1}";
    logger.info("query: " + query);
    logger.info("sort: " + sort);
    logger.info("fromTime: " + fromTime);
    logger.info("toTime: " + toTime);
    MongoCursor<Set> found = collection.find(query, fromTime.toDate(), toTime.toDate()).sort(sort).as(Set.class);

    List<Set> foundList = new LinkedList<>();

    while (found.hasNext()) {
      Set set = found.next();
      foundList.add(set);
    }

    if (foundList.isEmpty()) {
      logger.warn("no data found");
      return new Set[0];
    } else {
      return foundList.toArray(new Set[0]);
    }
  }

  public Session[] updateSessions(DateTime fromTime, DateTime toTime, String username) {

    String collectionName = getSessionCollectionName(username);
    logger.trace("collectionName: " + collectionName);
    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection collection = jongo.getCollection(collectionName);


    fromTime = fromTime.withTimeAtStartOfDay();
    toTime = toTime.plusDays(1);
    toTime = toTime.withTimeAtStartOfDay();
    logger.info("fromTime: " + fromTime);
    logger.info("toTime: " + toTime);

    DateTime targetTime = fromTime;

    List<Session> updatedSessions = new LinkedList<>();

    while (targetTime.isBefore(toTime.getMillis())) {

      DateTime targetTimeMorning = targetTime.withTimeAtStartOfDay();
      DateTime targetTimeMorningPlusOneDay = targetTimeMorning.plusDays(1);
      Set[] sets = getSets(targetTimeMorning, targetTimeMorningPlusOneDay, username);
      List<Set> setsList = Arrays.asList(sets);

      if (setsList.isEmpty()) {
        logger.warn("No sets found for: " + targetTime);
      } else {

        Session targetSession = new Session();

       // boolean first = true;
        for (Iterator<Set> iterator = setsList.iterator(); iterator.hasNext();) {
          Set set = (Set) iterator.next();
         // if (first) {

            String query = "{date: #, excercise: #}";
            logger.info("query: " + query);
            logger.info("date: " + targetTime);
            logger.info("excercise: " + set.getExcercise());
            MongoCursor<Session> foundSessions = collection.find(query, targetTime, set.getExcercise()).as(Session.class);

            List<Session> foundList = new LinkedList<>();
            while (foundSessions.hasNext()) {
              Session session = (Session) foundSessions.next();
              foundList.add(session);
            }

            if (foundList.size() == 1) {
              targetSession = foundList.get(0);
            } else if (foundList.size() > 1) {
              throw new RuntimeException("Multiple sessions found for targetTime: " + targetTime + " and excercise");
            }

            targetSession.setExcercise(set.getExcercise());
            targetSession.setDate(targetTimeMorning.toDate());
            targetSession.setUsername(username);
            targetSession.setBodyWeight(set.getBodyWeight());
            targetSession.setMood(set.getMood());
            targetSession.setCompetition(set.isCompetition());
            targetSession.setCreatine(set.isCreatine());
            targetSession.setInjured(set.isInjured());
            targetSession.addSet();
            targetSession.addTrainingLoad(set.getWeight(), set.getActualReps());
            targetSession.addVolume(set.getActualReps());
            targetSession.setTrainingMax(set.getTrainingMax());
            targetSession.updatePredictedMax(set.getPredictedMax());

        //    first = false;
//          } else {
//
//            if (targetSession.getBodyWeight() != set.getBodyWeight()) {
//              logger.warn("bodyweight missmatch");
//            }
//
//            if (targetSession.getMood() != set.getMood()) {
//              logger.warn("mood missmatch");
//            }
//
//            if (targetSession.isCompetition() != set.isCompetition()) {
//              logger.warn("competition missmatch");
//            }
//
//            if (targetSession.isCreatine() != set.isCreatine()) {
//              logger.warn("creatine missmatch");
//            }
//
//
//            if (targetSession.isCreatine() != set.isCreatine()) {
//              logger.warn("creatine missmatch");
//            }
//
//
//            if (targetSession.isInjured() != set.isInjured()) {
//              logger.warn("injured missmatch");
//            }
//
//            targetSession.addSet();
//            targetSession.addTrainingLoad(set.getWeight(), set.getActualReps());
//
//          }

        }

        // save the session here
        collection.save(targetSession);
        updatedSessions.add(targetSession);
      }


      targetTime = targetTime.plusDays(1);
    }

    return updatedSessions.toArray(new Session[0]);
  }

  public Mood saveMood(Mood mood) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection moods = jongo.getCollection(Mood.class.getSimpleName());

    moods.save(mood);
    return mood;

  }

  public void removeMood(Mood mood) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection moods = jongo.getCollection(Mood.class.getSimpleName());

    moods.remove(mood.get_id());

  }

  public Mood getMoodByName(String name) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection moods = jongo.getCollection(Mood.class.getSimpleName());

    MongoCursor<Mood> foundMoods = moods.find("{name:#}", name).as(Mood.class);

    if (foundMoods.count() == 0) {
      logger.debug("name " + name + " not found");
      return null;
    } else if (foundMoods.count() == 1) {
      Mood mood = foundMoods.next();
      return mood;
    } else {
      throw new RuntimeException("found " + foundMoods.count() + " moods with the name" + name);
    }

  }

  public boolean removeMoodByName(String name) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection moods = jongo.getCollection(Mood.class.getSimpleName());

    MongoCursor<Mood> foundMoods = moods.find("{name:#}", name).as(Mood.class);

    if (foundMoods.count() == 0) {
      logger.debug("name " + name + " not found");
      return false; // Mood not found
    } else {
      int deleted = 0;
      while (foundMoods.hasNext()) {
        Mood mood = foundMoods.next();
        logger.debug("deleting mood with id" + mood.get_id());
        moods.remove(mood.get_id());
        deleted++;
      }
      logger.debug("deleted " + deleted + " mood(s) with name " + name);
      return true; // We got rid
    }

  }

  public boolean moodExists(String name) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection moods = jongo.getCollection(Mood.class.getSimpleName());

    MongoCursor<Mood> foundMoods = moods.find("{name:#}", name).as(Mood.class);

    if (foundMoods.count() == 0) {
      logger.debug("name " + name + " not found");
      return false;
    } else {
      logger.debug("found " + foundMoods.count() + " moods with name " + name);
      return true;
    }

  }

  public Excercise[] getExcercises() {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    MongoCursor<Excercise> foundExcercises = excercises.find().as(Excercise.class);

    List<Excercise> foundExcercisesList = new LinkedList<>();

    while (foundExcercises.hasNext()) {
      Excercise excercise = foundExcercises.next();
      foundExcercisesList.add(excercise);
    }
    return foundExcercisesList.toArray(new Excercise[0]);
  }

  public Excercise[] searchExcerciseByName(String search) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    // MongoCursor<Excercise> foundExcercises = excercises.find("{name:#}", Pattern.compile(search +
    // ".*")).limit(10).as(Excercise.class);

    // Basically here if it contains the string we will return it. (Case insensitive)
    MongoCursor<Excercise> foundExcercises =
        excercises.find("{name:#}", Pattern.compile(search, Pattern.CASE_INSENSITIVE)).limit(10).as(Excercise.class);

    List<Excercise> foundExcercisesList = new LinkedList<>();

    while (foundExcercises.hasNext()) {
      Excercise excercise = foundExcercises.next();
      foundExcercisesList.add(excercise);
    }
    return foundExcercisesList.toArray(new Excercise[0]);
  }

  public Excercise[] searchExcerciseByTagName(String search) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    // MongoCursor<Excercise> foundExcercises = excercises.find("tags: [{name: #}]",
    // Pattern.compile(search, Pattern.CASE_INSENSITIVE)).limit(10).as(Excercise.class);
    MongoCursor<Excercise> foundExcercises =
        excercises.find("{tags: {$elemMatch: {name: #}}}", Pattern.compile(search, Pattern.CASE_INSENSITIVE)).limit(10).as(Excercise.class);

    List<Excercise> foundExcercisesList = new LinkedList<>();

    while (foundExcercises.hasNext()) {
      Excercise excercise = foundExcercises.next();
      foundExcercisesList.add(excercise);
    }
    return foundExcercisesList.toArray(new Excercise[0]);
  }

  public Excercise[] searchExcerciseByTagValue(String search) {

    Jongo jongo = new Jongo(mongoDatabase);
    MongoCollection excercises = jongo.getCollection(Excercise.class.getSimpleName());

    MongoCursor<Excercise> foundExcercises =
        excercises.find("{tags: {$elemMatch: {value: #}}}", Pattern.compile(search, Pattern.CASE_INSENSITIVE)).limit(10).as(Excercise.class);

    List<Excercise> foundExcercisesList = new LinkedList<>();

    while (foundExcercises.hasNext()) {
      Excercise excercise = foundExcercises.next();
      foundExcercisesList.add(excercise);
    }
    return foundExcercisesList.toArray(new Excercise[0]);
  }

}
