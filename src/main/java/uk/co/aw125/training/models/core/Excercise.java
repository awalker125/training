package uk.co.aw125.training.models.core;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jongo.marshall.jackson.oid.MongoObjectId;

import io.swagger.annotations.ApiModelProperty;


public class Excercise {

  // @ApiModelProperty(hidden = true, notes = "generated by mongodb")


  @XmlTransient
  @ApiModelProperty(hidden = true, notes = "generated by mongodb")
  @MongoObjectId
  private String _id;

  @NotNull
  String name;

  List<Tag> tags;


  public Excercise() {
    tags = new LinkedList<Tag>();
  }

 

  public String get_id() {
    return _id;
  }



  public void set_id(String _id) {
    this._id = _id;
  }



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }


  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return name;
  }

}
