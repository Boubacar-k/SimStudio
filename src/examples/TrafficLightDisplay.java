package examples;

import exception.DEVS_Exception;
import model.*;
import simulator.*;
import types.*;

/**
 * Class that handles the display of the traffic light model. This is also an
 * atomic model.
 * 
 * @author Xav & Sirius
 * 
 */
public class TrafficLightDisplay extends AtomicModel {

	/**
	 * Constructor : This model takes a state of a traffic light in input. There
	 * is no output. There are states, one is transitional and allows to
	 * generate an internal transition just after an external transition.
	 * 
	 * @param name
	 *            name of the model.
	 * @param desc
	 *            description of the model.
	 */
	public TrafficLightDisplay(String name, String desc) {
		super(name, desc);

		// X
		String[] colors = { "green", "orange", "red", "off" };
		addInput(new DEVS_Enum(colors), "color", "color of the traffic light");

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

		System.out.println(((Simulator) sim_).getTime() + " : "
				+ getInput("color"));
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
