package types;

@SuppressWarnings("serial")
/**
 * Class that represents a real.
 * 
 * @author Xav & Sirius
 */
public class DEVS_Real extends DEVS_Type {
	/**
	 * + infinity
	 */
	public final static double POSITIVE_INTINITY = Double.POSITIVE_INFINITY;

	/**
	 * - infinity
	 */
	public final static double NEGATIVE_INTINITY = Double.NEGATIVE_INFINITY;

	/**
	 * zero
	 */
	public final static double ZERO = Double.MIN_VALUE;

	/**
	 * Current value
	 */
	double value_ = 0;

	/**
	 * void constructor
	 * 
	 */
	public DEVS_Real() {

	}

	/**
	 * Constructor II
	 * 
	 * @param d
	 *            new value of the object
	 */
	public DEVS_Real(double d) {
		value_ = d;
	}

	/**
	 * returns the value of the real.
	 */
	public Object getValue() {
		return value_;
	}

	/**
	 * Returns the value of the real
	 * 
	 * @return value_
	 */
	public double getDouble() {
		return value_;
	}

	/**
	 * Sets the value of the real.
	 */
	public void setValue(Object v) {
		value_ = (Double) v;
	}

	/**
	 * Serialization method
	 */
	public String toString() {
		return new String("" + value_);
	}

	/**
	 * Comparison method
	 */
	public boolean equals(DEVS_Type other) {
		return value_ == ((DEVS_Real) other).value_;
	}

}
