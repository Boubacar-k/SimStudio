package examples;

import exception.DEVS_Exception;
import model.*;
import simulator.*;
import types.*;

/**
 * Class that represents an atomic model of a system that displays a fifo stack.
 * 
 * @author Xav & Sirius
 * 
 */
public class FIFODisplay extends AtomicModel {

	/**
	 * Constructor. It defines the inputs, outputs and states of the model.
	 * 
	 * @param name
	 *            name of the model.
	 * @param desc
	 *            description of the model.
	 */
	public FIFODisplay(String name, String desc) {
		super(name, desc);

		// X
		addInput(new DEVS_Integer(), "arrival", "sthing get in the queue");

		addInput(new DEVS_Integer(), "departure", "sthing get out of the queue");

		// Y

		// S
		String[] s = { "waiting", "displaying" };
		addStateVariable(new DEVS_Enum(s), "state", "state of the display");

		// init state
		setVar("state", "waiting");
	}

	@Override
	public void lambda() throws DEVS_Exception {
		// TODO Auto-generated method stub

		if (getInput("arrival").toString().equals("0") == false)
			System.out.println(((Simulator) sim_).getTime()
					+ " : sthing get in the queue");

		if (getInput("departure").toString().equals("0") == false)
			System.out.println(((Simulator) sim_).getTime()
					+ " : sthing get out of the queue");

		setInput("arrival", 0);
		setInput("departure", 0);
	}

	@Override
	public void deltaInt() {
		// TODO Auto-generated method stub

		setVar("state", "waiting");
	}

	@Override
	public void deltaExt(int e) {
		// TODO Auto-generated method stub

		setVar("state", "displaying");
	}

	@Override
	public int ta() {
		// TODO Auto-generated method stub

		int t = 0;

		String state = getVar("state").toString();

		if (state.equals("waiting") == true)
			t = DEVS_Integer.POSITIVE_INTINITY;
		else if (state.equals("displaying") == true)
			t = 0;

		return t;
	}
}
