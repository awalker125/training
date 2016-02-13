package uk.co.aw125.training.models;

public class Tag {

	/** The name. */
	String name;

	/** The value. */
	String value;

	/**
	 * Instantiates a new tag.
	 */
	public Tag() {
	}

	/**
	 * Instantiates a new tag.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public Tag(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
