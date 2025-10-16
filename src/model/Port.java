package model;

import types.*;

public class Port {
	/**
	 * Model of the port
	 */
	protected Model model_ = null;

	/**
	 * Value of the port
	 */
	protected Object value_ = null;

	/**
	 * Name of the port
	 */
	protected String name_ = "";

	/**
	 * Description of the port
	 */
	protected String desc_ = "";

	/**
	 * Constructor
	 * 
	 * @param value
	 *            value of the port
	 * @param name
	 *            name of the port
	 * @param desc
	 *            description of the port
	 * @param model
	 *            model of the port
	 */
	public Port(DEVS_Type value, String name, String desc, Model model) {
		model_ = model;
		name_ = name;
		value_ = value;
		desc_ = desc;
	}

	/**
	 * Sets the name of the port
	 * 
	 * @param name
	 *            name of the port
	 */
	public void setName(String name) {
		name_ = name;
	}

	/**
	 * Gets the name of the port
	 * 
	 * @return the name of the port
	 */
	public String getName() {
		return name_;
	}

	/**
	 * Sets the description of the port
	 * 
	 * @param desc
	 *            the description of the port
	 */
	public void setDesc(String desc) {
		name_ = desc;
	}

	/**
	 * Gets the description of the port
	 * 
	 * @return the descrition of the port
	 */
	public String getDesc() {
		return desc_;
	}

	/**
	 * Gets the value of the port
	 * 
	 * @return the value of the port
	 */
	public Object getValue() {
		return value_;
	}

	/**
	 * Sets the value of the port
	 * 
	 * @param inValue
	 *            the new value of the port
	 */
	public void setValue(Object inValue) {
		value_ = inValue;
	}

	/**
	 * gets the model of the port
	 * 
	 * @return the model of the port
	 */
	public Model getModel() {
		return model_;
	}

	/**
	 * Sets the model of the port
	 * 
	 * @param inModel
	 *            the new model of the port
	 */
	public void setModel(Model inModel) {
		model_ = inModel;
	}

	/**
	 * Comparison method
	 * @param p port to compare with this
	 * @return true if the two ports are equals, false else
	 */
	public boolean equals(Port p) {
		return name_.equals(p.name_);
	}
}