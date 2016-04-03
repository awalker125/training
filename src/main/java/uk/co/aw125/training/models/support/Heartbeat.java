package uk.co.aw125.training.models.support;

import java.util.Date;


import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import io.swagger.annotations.ApiModelProperty;

public class Heartbeat {

	@MongoObjectId
	private String id;

	@ApiModelProperty(hidden = true, notes = "set to object constructor time")
	Date date;

	public Heartbeat() {
		// TODO Auto-generated constructor stub
		date = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
