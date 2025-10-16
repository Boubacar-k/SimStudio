package examples;

import exception.DEVS_Exception;
import model.*;
import types.*;

/**
 * Class that represents an atomic model of a traffic light.
 * 
 * @author Xav & Sirius
 * 
 */
public class TrafficLight extends AtomicModel {

	/**
	 * Constructor of the traffic light model. This model can receive two orders
	 * in input. Its output is a color. It has states : green, orange, red, off.
	 * 
	 * @param name
	 *            name of the model.
	 * @param desc
	 *            description of the model.
	 */
	public TrafficLight(String name, String desc) {
		super(name, desc);

		// X
		String[] orders = { "start", "stop" };
		addInput(new DEVS_Enum(orders), "order",
				"turning on/off the traffic light");

		// Y
		String[] colors = { "green", "orange", "red", "off" };
		addOutput(new DEVS_Enum(colors), "color", "color of the light");

		// S
		String[] s = { "s1", "s2", "s3", "s4" };
		addStateVariable(new DEVS_Enum(s), "state",
				"state of the traffic light");

		// init state
		setVar("state", "s4");
	}

	@Override
	public void lambda() throws DEVS_Exception {
		String state = getVar("state").toString();

		if (state.equals("s1"))
			setOutput("color", "orange");
		else if (state.equals("s2"))
			setOutput("color", "red");
		else if (state.equals("s3"))
			setOutput("color", "green");
		else if (state.equals("s4"))
			setOutput("color", "green");
	}

	@Override
	public void deltaInt() {
		String state = getVar("state").toString();

		if (state.equals("s1") == true)
			setVar("state", "s2");
		else if (state.equals("s2") == true)
			setVar("state", "s3");
		else if (state.equals("s3") == true)
			setVar("state", "s1");
	}

	@Override
	public void deltaExt(int e) throws DEVS_Exception {
		String order = getInput("order").toString();
		String state = getVar("state").toString();

		if (order.equals("stop") == true) {
			setVar("state", "s4");
			setOutput("color", "off"); // to keep display up to date
		} else if ((order.equals("start") == true)
				&& (state.equals("s4") == true)) {
			setVar("state", "s1");
			setOutput("color", "green"); // to keep display up to date
		}
	}

	@Override
	public int ta() {
		int t = 0;

		String state = getVar("state").toString();

		if (state.equals("s1") == true)
			t = 5;
		else if (state.equals("s2") == true)
			t = 1;
		else if (state.equals("s3") == true)
			t = 4;
		else if (state.equals("s4") == true)
			t = DEVS_Integer.POSITIVE_INTINITY;

		return t;
	}
}
