package simulator;

import message.S_Message;
import message.I_Message;
import exception.DEVS_Exception;

/**
 * Root coordinator : drives a simulator, and not a model.
 * 
 * @author Xav & Sirius
 * 
 */
public class RootCoordinator {
	/**
	 * The simulator he leads
	 */
	AbstractSimulator sim_;

	/**
	 * Constructor
	 * 
	 * @param s
	 *            son simulator
	 */
	public RootCoordinator(AbstractSimulator s) {
		sim_ = s;
	}

	/**
	 * Initialisation of the simulator
	 * 
	 * @param t
	 *            time
	 * @throws DEVS_Exception
	 */
	public void init(int t) throws DEVS_Exception {
		sim_.handleMessage(new I_Message(t));
	}

	/**
	 * Launches the simulation
	 * 
	 * @throws DEVS_Exception
	 */
	public void run() throws DEVS_Exception {
		while (true) {
			sim_.handleMessage(new S_Message(sim_.getTN()));
		}
	}

	/**
	 * Launches the simulation, with a limit of time
	 * 
	 * @param limit
	 *            duration of the simulation
	 * @throws DEVS_Exception
	 */
	public void run(int limit) throws DEVS_Exception {
		while (sim_.tl_ < limit) {
			sim_.tl_ = sim_.tn_;
			sim_.handleMessage(new S_Message(sim_.getTN()));
		}
	}

}
