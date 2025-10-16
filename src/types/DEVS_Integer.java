package types;

@SuppressWarnings("serial")
/*******************************************************************************
 * Class that represents an integer.
 * 
 * @author Xav & Sirius
 * 
 */
public class DEVS_Integer extends DEVS_Type {
	/**
	 * + infinity
	 */
	public final static int POSITIVE_INTINITY = 10000000; // Integer.MAX_VALUE

	// ;

	/**
	 * - infinity
	 */
	public final static int NEGATIVE_INTINITY = -10000000; // -1 *

	// Integer.MAX_VALUE
	// ;

	/**
	 * zero
	 */
	public final static int ZERO = 0;

	/**
	 * current value.
	 */
	int value_ = 0;

	/**
	 * Void constructor
	 * 
	 */
	public DEVS_Integer() {

	}

	/**
	 * Constructor II
	 * 
	 * @param i
	 *            the value of the integer.
	 */
	public DEVS_Integer(int i) {
		value_ = i;
	}

	/**
	 * Returns the value of the integer.
	 */
	public Object getValue() {
		return value_;
	}

	/**
	 * returns the value of the integer.
	 * 
	 * @return value_
	 */
	public int getInteger() {
		return value_;
	}

	/**
	 * Sets the value of the integer.
	 */
	public void setValue(Object v) {
		value_ = (Integer) v;
	}

	/**
	 * Serialization method.
	 */
	public String toString() {
		return new String("" + value_);
	}

	/**
	 * Comparison method.
	 */
	public boolean equals(DEVS_Type other) {
		return value_ == ((DEVS_Integer) other).value_;
	}

}
