package model;

import java.lang.String;

import types.*;

/**
 * State variable. This class is used to define a part of a state of the system.
 * It is represented by an object of type DEVS_Type.
 * 
 * @author Xav & Sirius
 * 
 */
public class StateVariable {

	// ------------//
	// attributes //
	// ------------//

	/**
	 * is state active ?
	 */
	protected boolean active_;

	/**
	 * name of the state
	 */
	protected String name_;

	/**
	 * value
	 */
	protected DEVS_Type value_;

	/**
	 * description of the state
	 */
	protected String desc_;

	/**
	 * identificator
	 */
	protected int id_;

	// ---------------------------//
	// constructors & destructor //
	// ---------------------------//

	public StateVariable(DEVS_Type val, String name, String desc, int id,
			boolean active) {
		name_ = name;
		desc_ = desc;
		id_ = id;
		active_ = active;
		value_ = val;
	}

	// ----------//
	// mutators //
	// ----------//

	/**
	 * @param active_
	 *            The active_ to set.
	 */
	public void setActive(boolean active_) {
		this.active_ = active_;
	}

	/**
	 * @return Returns the active_.
	 */
	public boolean isActive() {
		return active_;
	}

	/**
	 * @param id_
	 *            The id_ to set.
	 */
	public void setId(int id_) {
		this.id_ = id_;
	}

	/**
	 * @return Returns the id_.
	 */
	public int getId() {
		return id_;
	}

	/**
	 * @param desc_
	 *            The desc_ to set.
	 */
	public void setDesc(String desc_) {
		this.desc_ = desc_;
	}

	/**
	 * @return Returns the desc_.
	 */
	public String getDesc() {
		return desc_;
	}

	/**
	 * @param name_
	 *            The name_ to set.
	 */
	public void setName(String name_) {
		this.name_ = name_;
	}

	/**
	 * @return Returns the name_.
	 */
	public String getName() {
		return name_;
	}

	/**
	 * @param value_
	 *            The value_ to set.
	 */
	public void setValue(Object v) {
		value_.setValue(v);
	}

	/**
	 * @return Returns the value_.
	 */
	public DEVS_Type getValue() {
		return value_;
	}

	// ---------//
	// methods //
	// ---------//

	/**
	 * Serialization method
	 */
	public String toString() {
		return new String(name_ + " = " + value_.toString());
	}

}
