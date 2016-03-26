package uk.co.aw125.training.models;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LoggerConfig")
public class LoggerConfigModel {

	@NotNull
	String name;
	@NotNull
	String level;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
