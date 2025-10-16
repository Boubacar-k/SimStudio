package model;

import message.*;
import exception.*;
import types.*;

import java.lang.String;
import java.util.ArrayList;

import simulator.AbstractSimulator;

/**
 * Class model. Abstract class : a model can either be a coupled model or an
 * atomic model.
 * 
 * @author Xav & Sirius
 * 
 */
public abstract class Model {

	// ------------//
	// attributes //
	// ------------//

	/**
	 * Simulator that drives this model
	 */
	protected AbstractSimulator sim_ = null;

	/**
	 * name of the model
	 */
	protected String name_;

	/**
	 * description of the model
	 */
	protected String desc_;

	/**
	 * Identificator of the model
	 */
	protected int id_;

	/**
	 * List of inputs
	 */
	protected ArrayList<Input> X_ = new ArrayList<Input>();

	/**
	 * List of outputs
	 */
	protected ArrayList<Output> Y_ = new ArrayList<Output>();

	protected static int ID_ = 0;

	// ---------------------------//
	// constructors & destructor //
	// ---------------------------//

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the model
	 * @param description
	 *            of the mode
	 */
	public Model(String name, String desc) {
		name_ = name;
		desc_ = desc;
		id_ = ID_++;
	}

	// ----------//
	// mutators //
	// ----------//

	/**
	 * @return Returns the id_.
	 */
	public int getId() {
		return id_;
	}

	/**
	 * @param desc_
	 *            The desc_ to set.
	 */
	public void setDesc(String desc_) {
		this.desc_ = desc_;
	}

	/**
	 * @return Returns the desc_.
	 */
	public String getDesc() {
		return desc_;
	}

	/**
	 * @param name_
	 *            The name_ to set.
	 */
	public void setName(String name_) {
		this.name_ = name_;
	}

	/**
	 * @return Returns the name_.
	 */
	public String getName() {
		return name_;
	}

	/**
	 * Returns the simulator
	 * 
	 * @return sim_
	 */
	public AbstractSimulator getSimulator() {
		return sim_;
	}

	/**
	 * Adds an input to the mode.
	 * 
	 * @param type
	 *            type of the input
	 * @param name
	 *            name of the input
	 * @param desc
	 *            description of the input
	 */
	public void addInput(DEVS_Type type, String name, String desc) {
		X_.add(new Input(type, name, desc, this));
	}

	/**
	 * Adds an output
	 * 
	 * @param type
	 *            type of the output
	 * @param name
	 *            name of the output
	 * @param desc
	 *            description of the output
	 */
	public void addOutput(DEVS_Type type, String name, String desc) {
		Y_.add(new Output(type, name, desc, this));
	}

	/**
	 * gets an input
	 * 
	 * @param name
	 *            name of the input
	 * @return the input of name "name"
	 */
	public Object getInput(String name) {
		for (Input i : X_)
			if (i.getName().equals(name) == true)
				return i.getValue();

		return null;
	}

	/**
	 * gets an input
	 * 
	 * @param name
	 *            name of the input
	 * @return the input of name "name"
	 */
	public Object getOutput(String name) {
		for (Output o : Y_)
			if (o.getName().equals(name) == true)
				return o.getValue();

		return null;
	}

	/**
	 * gets an input
	 * 
	 * @param name
	 *            name of the input
	 * @return the input of name "name"
	 */
	public Input input(String name) {
		for (Input i : X_)
			if (i.getName().equals(name) == true)
				return i;

		return null;
	}

	/**
	 * gets an input
	 * 
	 * @param name
	 *            name of the input
	 * @return the input of name "name"
	 */
	public Output output(String name) {
		for (Output o : Y_)
			if (o.getName().equals(name) == true)
				return o;

		return null;
	}

	/**
	 * Sets the value of an input
	 * 
	 * @param name
	 *            name of the input
	 * @param value
	 *            new value for the input
	 */
	public void setInput(String name, Object value) {
		for (Input i : X_)
			if (i.getName().equals(name) == true) {
				i.setValue(value);
				return;
			}
	}

	/**
	 * Sets the value of an output
	 * 
	 * @param name
	 *            name of the output
	 * @param value
	 *            new value of the output
	 * @throws DEVS_Exception
	 */
	public void setOutput(String name, Object value) throws DEVS_Exception {
		for (Output o : Y_)
			if (o.getName().equals(name) == true) {
				o.setValue(value);

				if (sim_.getParent() != null) {
					sim_.getParent().handleMessage(
							new Y_Message(o, sim_.getTime()));
				}

				return;
			}
	}

	/**
	 * Serialization method
	 */
	public String toString() {
		return toString(0);
	}

	/**
	 * Serialization method
	 */
	abstract public String toString(int level);

}
