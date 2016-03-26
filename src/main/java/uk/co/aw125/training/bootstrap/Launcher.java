package uk.co.aw125.training.bootstrap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.aw125.training.data.DataManager;
import uk.co.aw125.training.models.Excercise;
import uk.co.aw125.training.models.Tag;

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



      Tag olympicTag = new Tag();
      olympicTag.setName("Type");
      olympicTag.setValue("Olympic");

      Tag powerTag = new Tag();
      powerTag.setName("Type");
      powerTag.setValue("Power");

      // setup core data
      List<Excercise> coreExcercises = new LinkedList<>();


      /// snatch
      Excercise snatchExcercise = new Excercise();
      snatchExcercise.setName("Snatch");
      snatchExcercise.getTags().add(olympicTag);

      coreExcercises.add(snatchExcercise);


      /// C & J
      Excercise cleanAndJerkExcercise = new Excercise();
      cleanAndJerkExcercise.setName("Clean & Jerk");
      cleanAndJerkExcercise.getTags().add(olympicTag);

      coreExcercises.add(cleanAndJerkExcercise);


      /// Clean
      Excercise cleanExcercise = new Excercise();
      cleanExcercise.setName("Clean");
      cleanExcercise.getTags().add(olympicTag);

      coreExcercises.add(cleanAndJerkExcercise);


      /// Clean
      Excercise jerkExcercise = new Excercise();
      jerkExcercise.setName("Jerk");
      jerkExcercise.getTags().add(olympicTag);

      coreExcercises.add(jerkExcercise);


      /// Squat
      Excercise squatExcercise = new Excercise();
      squatExcercise.setName("Squat");
      squatExcercise.getTags().add(powerTag);

      coreExcercises.add(squatExcercise);


      /// Bench
      Excercise benchExcercise = new Excercise();
      benchExcercise.setName("Bench");
      benchExcercise.getTags().add(powerTag);

      coreExcercises.add(benchExcercise);


      /// Bench
      Excercise deadliftExcercise = new Excercise();
      deadliftExcercise.setName("Deadlift");
      deadliftExcercise.getTags().add(powerTag);

      coreExcercises.add(deadliftExcercise);


      for (Iterator iterator = coreExcercises.iterator(); iterator.hasNext();) {
        Excercise excercise = (Excercise) iterator.next();

        Excercise alreadySavedExcercise = DataManager.getDataManager().getExcerciseByName(excercise.getName());
        if (alreadySavedExcercise == null) {
          DataManager.getDataManager().saveExcercise(excercise);
          logger.info(excercise + " saved");
        } else {
          logger.info(excercise + " is already saved");
        }
      }


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
