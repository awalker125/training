package uk.co.aw125.training.models.core;

import java.util.Date;

import org.joda.time.DateTime;

public class Auth {

  String username;
  String api_key;
  DateTime validated;
  DateTime seen;

  public Auth(String username, String api_key) {
    super();
    this.username = username;
    this.api_key = api_key;
    Date epoch = new Date(0);
    this.validated = new DateTime(epoch);
  }

  public void updateSeenEqualsNow() {
    this.seen = DateTime.now();
  }

  public void updateValidatedEqualsNow() {
    this.validated = DateTime.now();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getApi_key() {
    return api_key;
  }

  public void setApi_key(String api_key) {
    this.api_key = api_key;
  }

  public DateTime getValidated() {
    return validated;
  }

  public void setValidated(DateTime validated) {
    this.validated = validated;
  }

  public DateTime getSeen() {
    return seen;
  }

  public void setSeen(DateTime seen) {
    this.seen = seen;
  }



}
