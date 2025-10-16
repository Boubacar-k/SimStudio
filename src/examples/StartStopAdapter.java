package examples;

import exception.DEVS_Exception;
import model.*;
import types.*;

/**
 * Class that represents an adapter for the traffic light. It simply allows to
 * convert a signal from the random generator to a signal the traffic light
 * model will understand.
 * 
 * @author Xav & Sirius
 * 
 */
public class StartStopAdapter extends AtomicModel {

	/**
	 * Constructor. This model has one input, that should come from a random
	 * generator model, and two outputs, that are orders for the traffic light.
	 * There are two states, one being transitional and allowing to generate a
	 * internal transition when an external transition has just occurred.
	 * 
	 * @param name
	 *            name of the model.
	 * @param desc
	 *            description of the model.
	 */
	public StartStopAdapter(String name, String desc) {
		super(name, desc);

		// X
		addInput(new DEVS_Integer(), "signal", "received signal");

		// Y
		String[] orders = { "start", "stop" };
		addOutput(new DEVS_Enum(orders), "order", "start/stop order");

		// S
		String[] s = { "waiting", "rendering" };
		addStateVariable(new DEVS_Enum(s), "state", "state of the adapter");

		addStateVariable(new DEVS_Enum(orders), "lastOrder",
				"last order emitted");

		// init state
		setVar("state", "waiting");
		setVar("lastOrder", "stop");
	}

	@Override
	public void lambda() throws DEVS_Exception {
		// TODO Auto-generated method stub

		String lastOrder = getVar("lastOrder").toString();
		String state = getVar("state").toString();

		if (state.equals("rendering") == true) {
			if (lastOrder.equals("start") == true) {
				setVar("lastOrder", "stop");
				setOutput("order", "stop");
			} else {
				setVar("lastOrder", "start");
				setOutput("order", "start");
			}
		}
	}

	@Override
	public void deltaInt() {
		// TODO Auto-generated method stub

		String state = getVar("state").toString();

		if (state.equals("waiting") == true)
			setVar("state", "rendering");
		else if (state.equals("rendering") == true)
			setVar("state", "waiting");
	}

	@Override
	public void deltaExt(int e) {
		// TODO Auto-generated method stub

		setVar("state", "rendering");
	}

	@Override
	public int ta() {
		// TODO Auto-generated method stub

		int t = 0;

		String state = getVar("state").toString();

		if (state.equals("waiting") == true)
			t = DEVS_Integer.POSITIVE_INTINITY;
		else if (state.equals("rendering") == true)
			t = 0;

		return t;
	}
}
