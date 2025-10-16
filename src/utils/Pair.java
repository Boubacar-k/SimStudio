package utils;

/**
 * Pair class.
 * 
 * @author Xav & Sirius
 * 
 */
public class Pair {

	/**
	 * First object of the pair.
	 */
	protected Object first_;

	/**
	 * Second object of the pair.
	 */
	protected Object second_;

	/**
	 * Constructor
	 * 
	 * @param first
	 *            first object of the pair.
	 * @param second
	 *            second object of the pair.
	 */
	public Pair(Object first, Object second) {
		first_ = first;
		second_ = second;
	}

	/**
	 * Gets the first object of the pair.
	 * 
	 * @return the first object of the pair.
	 */
	public Object first() {
		return first_;
	}

	/**
	 * Gets the second object of the pair.
	 * 
	 * @return the second object of the pair.
	 */
	public Object second() {
		return second_;
	}
}
