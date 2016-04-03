package uk.co.aw125.training.models.core;

import java.util.Date;

public class Auth {

  String username;
  String api_key;
  Date validated;
  Date seen;
    
  public Auth(String username, String api_key) {
    super();
    this.username = username;
    this.api_key = api_key;
    
    this.validated = new Date(0);
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


  public Date getValidated() {
    return validated;
  }


  public void setValidated(Date validated) {
    this.validated = validated;
  }


  public Date getSeen() {
    return seen;
  }


  public void setSeen(Date seen) {
    this.seen = seen;
  }


  
}
