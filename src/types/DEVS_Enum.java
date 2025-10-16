package types;

import java.util.ArrayList;

@SuppressWarnings("serial")
/**
 * Enumeration of Objects.
 * 
 * @TODO The enumeration should store DEVS_Type objects instead of Objects.
 * @author Xav & Sirius
 */
public class DEVS_Enum extends DEVS_Type {
	ArrayList<Object> value_ = new ArrayList<Object>();

	Object current_ = null;

	/**
	 * Void constructor. Do not use.
	 * 
	 */
	public DEVS_Enum() {

	}

	/**
	 * Constructor with a set of values
	 * 
	 * @param values
	 *            possible values of the enumeration
	 */
	public DEVS_Enum(Object[] values) {
		for (Object o : values)
			value_.add(o);
	}

	/**
	 * Returns the current value of the enumeration.
	 */
	public Object getValue() {
		return current_;
	}

	/**
	 * Serialization method.
	 * 
	 * @return the serialized current value.
	 */
	public String getString() {
		return current_.toString();
	}

	/**
	 * Sets the current value of the enumeration.
	 */
	public void setValue(Object v) {
		if (value_.contains(v) == true)
			current_ = v;
	}

	/**
	 * Checks if a given value is part of the enumeration.
	 * 
	 * @param o
	 *            the value that may be part of the enumeration.
	 * @return true if the value o is in the enumeration.
	 */
	public boolean contains(Object o) {
		return value_.contains(o);
	}

	/**
	 * Serialization method.
	 * 
	 * @return the serialized values of the enumeration.
	 */
	public String toString() {
		if (current_ != null)
			return current_.toString();
		else
			return "";
	}

	/**
	 * Method of comparison.
	 * 
	 * @param other
	 *            object that has to be compared with this.
	 * @return true if the object equals this, false else.
	 */
	public boolean equals(DEVS_Type other) {
		return value_.equals(((DEVS_Enum) other).value_);
	}
}
