package types;

@SuppressWarnings("serial")
/**
 * Class that represents an interval of integers, and a value in this interval.
 * 
 * @author Xav & Sirius
 */
public class DEVS_IntInterval extends DEVS_Type {
	/**
	 * lower bound.
	 */
	int min_ = 0;

	/**
	 * higher bound.
	 */
	int max_ = 0;

	/**
	 * current value.
	 */
	int value_ = 0;

	/**
	 * Void constructor.
	 * 
	 */
	public DEVS_IntInterval() {

	}

	/**
	 * Constructor II : sets the interval and the current value at the middle of
	 * the interval.
	 * 
	 * @param min
	 *            lower bound.
	 * @param max
	 *            higher bound.
	 */
	public DEVS_IntInterval(int min, int max) {
		min_ = min;
		max_ = max;
		value_ = (min_ + max_) / 2;
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
	public int getInteger() {
		return value_;
	}

	/**
	 * Sets the value
	 */
	public void setValue(Object v) {
		if (contains((Integer) v) == true)
			value_ = (Integer) v;
	}

	/**
	 * Checks if the given value belongs to the interval.
	 * 
	 * @param i
	 *            value to check
	 * @return true if the i belongs to the interval [min, max]
	 */
	public boolean contains(int i) {
		return (i <= max_) && (i >= min_);
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
		return value_ == ((DEVS_IntInterval) other).value_;
	}
}
