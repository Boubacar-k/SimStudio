package examples;

import java.util.ArrayList;

import types.DEVS_Enum;
import types.*;
import exception.DEVS_Exception;
import model.AtomicModel;

/**
 * Class that represents an atomic model of a fifo stack. The constructor
 * defines the outputs, inputs and states of the model. The other methods are
 * redefinitions of the methods of the class AtiomicModel.
 * 
 * @author Xav & Sirius
 * 
 */
public class FIFO extends AtomicModel {

	/**
	 * fifo stack. It contains the objects of the stack.
	 */
	protected ArrayList<Object> fifo_ = new ArrayList<Object>();

	/**
	 * Constructor. The constructor defines the inputs, outputs and states of
	 * the model.
	 * 
	 * @param name
	 *            name of the atomic model.
	 * @param desc
	 *            description of the atomic model.
	 */
	public FIFO(String name, String desc) {
		super(name, desc);

		// X
		addInput(new DEVS_Integer(), "arrival", "something get in the queue");

		// Y
		addOutput(new DEVS_Integer(), "departure",
				"something get out of the queue");

		// S
		String[] s = { "occupied", "free" };
		addStateVariable(new DEVS_Enum(s), "state", "FIFO's state");

		addStateVariable(new DEVS_Integer(), "w",
				"current treatment's duration");

		// init state
		fifo_.clear();
		setVar("state", "free");
		setVar("w", DEVS_Integer.POSITIVE_INTINITY);
	}

	@Override
	public void deltaExt(int e) throws DEVS_Exception {
		String state = getVar("state").toString();

		fifo_.add(getInput("arrival"));

		if (state.equals("free") == true)
			setVar("w", 10);
		else
			setVar("w", ((DEVS_Integer) getVar("w")).getInteger() - e);

		setVar("state", "occupied");
	}

	@Override
	public void deltaInt() {
		String state = getVar("state").toString();

		if (state.equals("occupied") == true) {
			setVar("w", 10);

			fifo_.remove(0);

			if (fifo_.size() == 0)
				setVar("state", "free");
		}
	}

	@Override
	public void lambda() throws DEVS_Exception {
		setOutput("departure", fifo_.get(0));
	}

	@Override
	public int ta() {
		int t = 0;

		String state = getVar("state").toString();

		if (state.equals("free") == true) {
			t = DEVS_Integer.POSITIVE_INTINITY;
		} else if (state.equals("occupied") == true)
			t = ((DEVS_Integer) getVar("w")).getInteger();

		return t;
	}

}
