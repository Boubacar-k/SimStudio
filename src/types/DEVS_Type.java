package types;

import java.io.Serializable;

/**
 * Type class. This class is designed to guide the user in the definition of the
 * state variables, output and input types. This is a abstract class that will
 * be extended by concrete types as reals or integers.
 * 
 * @author Xav & Sirius
 * 
 */
public abstract class DEVS_Type implements Serializable {
	/**
	 * Gets the value of the variable.
	 * 
	 * @return the value of the object.
	 */
	abstract public Object getValue();

	/**
	 * Sets the value of the object.
	 * 
	 * @param v
	 *            the new value for the variable.
	 */
	abstract public void setValue(Object v);

	/**
	 * Serialization method.
	 */
	abstract public String toString();

	/**
	 * Allows to compare two objects of the same type.
	 * 
	 * @param val
	 *            other Object that has to be compared with this.
	 * @return true if the two objects are equals, false else.
	 */
	abstract public boolean equals(DEVS_Type val);
}
