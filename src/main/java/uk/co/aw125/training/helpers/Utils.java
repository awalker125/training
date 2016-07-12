package uk.co.aw125.training.helpers;

import java.security.*;
import java.text.*;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Andrew
 */
public class Utils {

  /**
   *
   */
  protected static Logger logger = LogManager.getLogger(Utils.class.getName());
  /**
   *
   */
  protected static Random random;

  static {
    random = new Random();
  }

  /**
   *
   * @return
   */
  public static String timeStamp() {
    // Display today's date using a default format for the current locale
    DateFormat defaultDate = DateFormat.getDateInstance(DateFormat.SHORT);
    // System.out.println(defaultDate.format(new Date()));

    String DateStamp = defaultDate.format(new Date());
    // Display the current time using a short time format for the current
    // locale
    DateFormat shortTime = DateFormat.getTimeInstance(DateFormat.SHORT);
    // System.out.println(shortTime.format(new Date()));

    String TimeStamp = shortTime.format(new Date());

    return DateStamp + " " + TimeStamp;
  }

  /**
   *
   * @return
   */
  public static String dateStamp() {
    // Display today's date using a default format for the current locale
    DateFormat defaultDate = DateFormat.getDateInstance(DateFormat.SHORT);
    // System.out.println(defaultDate.format(new Date()));

    String DateStamp = defaultDate.format(new Date());
    // Display the current time using a short time format for the current
    // locale
    return DateStamp;
  }

  /**
   *
   * @return
   */
  public static String fileFriendlyTimeStamp() {

    DateFormat df = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
    String s = df.format(new Date());
    return s;

  }

  /**
   *
   * @return
   */
  public static String fileFriendlyDateStamp() {
    DateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
    String s = df.format(new Date());
    return s;
  }

  /**
   *
   * @return
   */
  public static boolean isSolaris() {
    if (System.getProperty("os.name").contains("Solaris") || System.getProperty("os.name").contains("SunOS")) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   * @return
   */
  public static boolean isWindows() {
    if (System.getProperty("os.name").contains("Windows")) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *
   * @param S
   * @return
   */
  public static String generateMD5(String string) {

    logger.trace("String to convert to MD5 is: \"" + string + "\"");

    try {
      byte[] buffer = string.getBytes();
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(buffer);

      byte[] messageDigest = md.digest();
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++) {
        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.error("Could not find MD5 Algorithm", e);
      throw new RuntimeException("Could not find MD5 Algorithm", e);
    }

  }

  /**
   *
   * @param sourceFile
   * @return
   */
  public static String generateFileMD5(String sourceFile) {
    // DOMConfigurator.configureAndWatch(Config.getLog4jLocation());
    File Source = new File(sourceFile);

    logger.trace("Reading source file " + Source.getAbsolutePath());

    try {
      InputStream in = new FileInputStream(Source);

      MessageDigest md = MessageDigest.getInstance("MD5");
      long TotalRead = 0;
      int Len = 0;
      byte[] buf = new byte[1024];
      while ((Len = in.read(buf)) > 0) {
        TotalRead = TotalRead + Len;
        logger.trace(TotalRead + " Bytes read so far.");
        md.update(buf);
      }
      in.close();
      logger.info("Read a total of " + TotalRead + " bytes from " + Source);

      // md.update(buffer);
      byte[] messageDigest = md.digest();
      StringBuffer hexString = new StringBuffer();

      for (int i = 0; i < messageDigest.length; i++) {
        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
      }

      logger.trace("Finished creating digest of " + Source.getAbsolutePath());
      return hexString.toString();
    } catch (IOException e) {
      logger.error("Failed to read file " + Source.getAbsolutePath(), e);
      throw new RuntimeException("Failed to read file " + Source.getAbsolutePath(), e);
    } catch (NoSuchAlgorithmException e) {
      logger.error("Could not find MD5 Algorithm", e);
      throw new RuntimeException("Could not find MD5 Algorithm", e);
    }

  }

  /**
   *
   * @return
   */
  public static String getLineSeparator() {
    return System.getProperty("line.separator");
  }

  /**
   *
   * @param max
   * @return
   */
  public static int getNextRandomInt(int max) {
    return random.nextInt(max);
  }

  /**
   * Removes whitespace with matched by regex \s+
   *
   * @param orig
   * @return the original string with the whitespace stripped
   */
  public static String removeWhiteSpace(String orig) {
    return orig.replaceAll("\\s+", "");
  }

  /**
   * Removes special characters matched by regex \W and replaces with another string
   *
   * @param orig string to have special chars removed from
   * @param sub string to replace special chars with. If null replaced with ""
   * @return the original string with the whitespace stripped
   */
  public static String removeSpecialCharacters(String orig, String sub) {
    if (sub == null) {
      sub = "";
    }
    return orig.replaceAll("\\W", sub);
  }

  /**
   * Splits a string containing commas into an List<String> object
   *
   * @param csv
   * @return a List<String> object containing the individual strings
   */
  public static List<String> splitCSV(String csv) {
    String[] split = csv.split(",");

    LinkedList<String> list = new LinkedList<String>();

    list.addAll(Arrays.asList(split));
    return list;

  }

  /**
   * Checks if the number is even
   *
   * @param i
   * @return true if even
   */
  public static boolean isEven(int i) {
    if (i % 2 == 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks if the number is odd
   *
   * @param i
   * @return true if odd number
   */
  public static boolean isOdd(int i) {
    if (i % 2 == 0) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Checks if the string is null or empty
   *
   * @param string
   * @return true if the string is null or empty
   */
  public static boolean nullOrEmpty(String string) {
    if (string == null || string.length() == 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks if the string is null or empty
   *
   * @param string
   * @return false if the string is null or empty
   */
  public static boolean unlessNullOrEmpty(String string) {

    if (string == null || string.length() == 0) {
      // java 1.5 hangs on isEmpty()
      return false;
    } else {

      return true;
    }
  }

  /**
   * An implementation of the perl unless statement. This reverses a boolean. i.e if true this
   * returns false and vice versa
   *
   * @param b
   * @return
   */
  public static boolean unless(boolean b) {
    if (b) {
      return false;
    } else {
      return true;
    }
  }

  public static DateTime normaliseDate(DateTime dateTime) {
    return dateTime.withTimeAtStartOfDay();
  }
  
  public static DateTime normaliseDate(Date date)
  {
    DateTime dateTime = new DateTime(date.getTime());
    return normaliseDate(dateTime);
  }

}
