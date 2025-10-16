package model;

import exception.DEVS_Exception;
import simulator.Simulator;
import types.*;

/**
 * Atomic model
 * 
 * @author Xav & Sirius
 * 
 */
abstract public class AtomicModel extends Model {
	/**
	 * Current state of the model
	 */
	protected State state_ = new State();

	protected static int stateIndex_;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the model
	 * @param desc
	 *            description of the model
	 */
	public AtomicModel(String name, String desc) {
		super(name, desc);

		sim_ = new Simulator(this);
	}

	/**
	 * Adds a state variable to the state of the model
	 * 
	 * @param type
	 *            type of the variable
	 * @param name
	 *            name of the variable
	 * @param desc
	 *            description of the variable
	 */
	public void addStateVariable(DEVS_Type type, String name, String desc) {
		state_.addStateVariable(new StateVariable(type, name, desc,
				stateIndex_++, false));
	}

	/**
	 * gets the state variable defined by its name
	 * 
	 * @param name
	 *            name of the state variable
	 * @return
	 */
	public DEVS_Type getVar(String name) {
		return state_.getVar(name);
	}

	/**
	 * Sets the value of the state variable
	 * 
	 * @param name
	 *            name of the state variable
	 * @param val
	 *            value of the state variable
	 */
	public void setVar(String name, Object val) {
		state_.setVar(name, val);
	}

	/**
	 * Serialization method
	 */
	public String toString(int level) {
		String ret = new String(name_ + " : " + desc_ + "\n");

		String offset = "";
		for (int i = 0; i < level; ++i)
			offset += "|   ";

		ret += offset + "--> " + state_.toString() + "\n";

		return ret;
	}

	// ---------//
	// methods //
	// ---------//

	/**
	 * lambda law : generates outputs
	 */
	abstract public void lambda() throws DEVS_Exception;

	/**
	 * deltaInt law : determines the next state of the model when an internal
	 * transition occurres.
	 * 
	 */
	abstract public void deltaInt();

	/**
	 * deltaExt law : determines the next state of the model when an external
	 * transition occurres.
	 * 
	 */
	abstract public void deltaExt(int e) throws DEVS_Exception;

	/**
	 * ta law : determines the duration of the current state
	 * 
	 */
	abstract public int ta();

}
