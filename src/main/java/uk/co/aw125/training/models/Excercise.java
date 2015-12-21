package uk.co.aw125.training.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Excercise")
public class Excercise {

	@ApiModelProperty(hidden = true, notes = "generated by mongodb")
	@MongoId
	@MongoObjectId
	private String id;
	String name;
	String category;

	public Excercise() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "category")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
