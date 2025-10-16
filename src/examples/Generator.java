package examples;

//import java.io.IOException;
import java.util.Random;

import exception.DEVS_Exception;
import model.*;
import types.*;

/**
 * Class that represents a generator, seen as an atomic model.
 * 
 * @author Xav & Sirius
 * 
 */
public class Generator extends AtomicModel {

	/**
	 * Constructor. The constructor defines the inputs, outputs and states of
	 * the model. For the generator, we do not have any input, but an output
	 * signal generated at random interval of time. There are two states :
	 * waiting and rendering. The state rendering is transitional. It allows to
	 * cause a internal transition to generate an output just after an external
	 * transition.
	 * 
	 * @param name
	 *            name of the model.
	 * @param desc
	 *            description of the model.
	 */
	public Generator(String name, String desc) {
		super(name, desc);

		// X

		// Y
		addOutput(new DEVS_Integer(), "signal",
				"signal generated every random() s");

		// S
		String[] s = { "waiting", "rendering" };
		addStateVariable(new DEVS_Enum(s), "state", "state of the generator");

		// init state
		setVar("state", "waiting");
	}

	@Override
	public void deltaExt(int e) {
		// TODO Auto-generated method stub

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
	public void lambda() throws DEVS_Exception {
		// TODO Auto-generated method stub

		String state = getVar("state").toString();

		if (state.equals("waiting") == true) {
			setOutput("signal", 1);
		}
	}

	@Override
	public int ta() {
		// TODO Auto-generated method stub

		int t = 0;

		String state = getVar("state").toString();

		if (state.equals("waiting") == true) {
			t = new Random().nextInt() % 20;
			if (t < 0)
				t *= -1;
		} else if (state.equals("rendering") == true)
			t = 0;

		return t;

	}

}
