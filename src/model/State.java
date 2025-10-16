package model;

import java.util.ArrayList;

import types.*;

/**
 * Class that represent a state, defined by a set of state variables.
 * 
 * @author Xav & Sirius
 * 
 */
public class State {

	/**
	 * The set of state variables.
	 */
	protected ArrayList<StateVariable> vars_ = new ArrayList<StateVariable>();

	/**
	 * Void constructor
	 * 
	 */
	public State() {
	}

	/**
	 * Gets the state variable identified by "name"
	 * 
	 * @param name
	 *            the name of the state variable
	 * @return the state variable "name"
	 */
	public DEVS_Type getVar(String name) {
		for (StateVariable v : vars_)
			if (v.getName().equals(name) == true)
				return v.getValue();

		return null;
	}

	/**
	 * Sets the value of a state variable
	 * 
	 * @param name
	 *            the name of the state variable to set.
	 * @param val
	 *            the new value of the state variable
	 */
	public void setVar(String name, Object val) {
		for (StateVariable v : vars_)
			if (v.getName().equals(name) == true) {
				v.setValue(val);
				return;
			}

		return;
	}

	/**
	 * Serialization method
	 */
	public String toString() {
		String ret = "";

		for (StateVariable v : vars_)
			ret += v.toString() + " ; ";

		return ret;
	}

	/**
	 * Adds a state variable to the state.
	 * 
	 * @param var
	 *            new state variable
	 */
	public void addStateVariable(StateVariable var) {
		vars_.add(var);
	}

}
