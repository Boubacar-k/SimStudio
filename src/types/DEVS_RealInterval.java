package types;

@SuppressWarnings("serial")
/**
 * Class that represents an interval of reals, and a value in the interval.
 * 
 * @author Xav & Sirius
 */
public class DEVS_RealInterval extends DEVS_Type {
	/**
	 * Lower bound.
	 */
	double min_ = 0;

	/**
	 * Higher bound.
	 */
	double max_ = 0;

	/**
	 * Value
	 */
	double value_ = 0;

	/**
	 * Void constructor. Do not use.
	 * 
	 */
	public DEVS_RealInterval() {

	}

	/**
	 * Constructor II : sets the bounds and places the value in the middle of
	 * the interval.
	 * 
	 * @param min
	 *            lower bound
	 * @param max
	 *            higher bound
	 */
	public DEVS_RealInterval(double min, double max) {
		min_ = min;
		max_ = max;
		value_ = (min_ + max_) / 2.;
	}

	/**
	 * Returns the current value.
	 */
	public Object getValue() {
		return value_;
	}

	/**
	 * Returns the current value.
	 * 
	 * @return value_
	 */
	public double getDouble() {
		return value_;
	}

	/**
	 * Sets the current value, if it is in the interval
	 */
	public void setValue(Object v) {
		if (contains((Double) v) == true)
			value_ = (Double) v;
	}

	/**
	 * Checks if the value is in the interval
	 * 
	 * @param d
	 *            value to check.
	 * @return true if the d is in the interval.
	 */
	public boolean contains(double d) {
		return (d <= max_) && (d >= min_);
	}

	/**
	 * Serialization method.
	 */
	public String toString() {
		return new String("" + value_);
	}

	/**
	 * Comparison method
	 */
	public boolean equals(DEVS_Type other) {
		return value_ == ((DEVS_RealInterval) other).value_;
	}
}
