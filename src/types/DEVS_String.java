package types;

@SuppressWarnings("serial")
/**
 * Class that represents a string.
 * 
 * @author Xav & Sirius
 */
public class DEVS_String extends DEVS_Type {
	/**
	 * Value of the string.
	 */
	String value_ = "";

	/**
	 * void constructor.
	 * 
	 */
	public DEVS_String() {

	}

	/**
	 * Constructor II : sets the value of the string.
	 * 
	 * @param s
	 *            new value of the string
	 */
	public DEVS_String(String s) {
		value_ = s;
	}

	/**
	 * Gets the value of the string.
	 */
	public Object getValue() {
		return value_;
	}

	/**
	 * Gets the value of the string
	 * 
	 * @return value_
	 */
	public String getString() {
		return value_;
	}

	/**
	 * Sets the value of the string
	 */
	public void setValue(Object v) {
		value_ = (String) v;
	}

	/**
	 * Serialization method
	 */
	public String toString() {
		return value_;
	}

	/**
	 * Comparison method
	 */
	public boolean equals(DEVS_Type other) {
		return value_.equals(((DEVS_String) other).value_);
	}
}
