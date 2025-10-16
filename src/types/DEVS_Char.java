package types;

@SuppressWarnings("serial")
/**
 * Class that represent a character.
 * 
 * @author Xav & Sirius
 */
public class DEVS_Char extends DEVS_Type {
	/**
	 * Value of the character.
	 */
	char value_ = 0;

	/**
	 * Void constructor.
	 * 
	 */
	public DEVS_Char() {

	}

	/**
	 * Constructor II : initialises the value.
	 * 
	 * @param c
	 *            new value.
	 */
	public DEVS_Char(char c) {
		value_ = c;
	}

	/**
	 * Returns the value of the character.
	 */
	public Object getValue() {
		return value_;
	}

	/**
	 * Returns the value of the character
	 * 
	 * @return value_
	 */
	public char getChar() {
		return value_;
	}

	/**
	 * Sets the value of the character
	 */
	public void setValue(Object v) {
		value_ = (Character) v;
	}

	/**
	 * Serialization method.
	 */
	public String toString() {
		return new String("" + value_);
	}

	/**
	 * Compares two characters
	 */
	public boolean equals(DEVS_Type other) {
		return value_ == ((DEVS_Char) other).value_;
	}

}
