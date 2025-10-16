package simulator;

import message.*;
import exception.*;
import model.*;
import types.*;

/**
 * Abstract simulator. Abstract class, that only contains the basis of
 * simulators and coordinators.
 * 
 * @author Xav & Sirius
 * 
 */
public abstract class AbstractSimulator {
	/**
	 * date of the next event.
	 */
	protected int tn_ = DEVS_Integer.POSITIVE_INTINITY;

	/**
	 * date of the last event
	 */
	protected int tl_ = DEVS_Integer.NEGATIVE_INTINITY;

	/**
	 * Elapsed time since the last event.
	 */
	protected int e_ = 0;

	/**
	 * Parent simulator.
	 */
	protected AbstractSimulator parent_ = null;

	/**
	 * void constructor
	 * 
	 */
	public AbstractSimulator() {
	}

	/**
	 * Constructor II : set the parent.
	 * 
	 * @param s
	 *            new parent
	 */
	public AbstractSimulator(AbstractSimulator s) {
		parent_ = s;
	}

	/**
	 * Returns the simulated model.
	 * 
	 * @return the simulated model
	 */
	abstract public Model getModel();

	/**
	 * Gets the parent of the simulator
	 * 
	 * @return parent_
	 */
	public AbstractSimulator getParent() {
		return parent_;
	}

	/**
	 * Sets the parent of the simulator
	 * 
	 * @param s
	 *            new parent
	 */
	public void setParent(AbstractSimulator s) {
		parent_ = s;
	}

	/**
	 * Gets the time of the last event
	 * 
	 * @return tl_
	 */
	public int getTime() {
		return tl_;
	}

	/**
	 * Gets the time of the next event.
	 * 
	 * @return tn_
	 */
	public int getTN() {
		return tn_;
	}

	/**
	 * Initialisation of the model
	 * 
	 * @param t
	 *            time
	 * @throws DEVS_Exception
	 */
	abstract public void init(int t) throws DEVS_Exception;

	/**
	 * Simulates an internal transition
	 * 
	 * @param msg
	 *            message of the transition
	 * @param t
	 *            date of the transition
	 * @throws DEVS_Exception
	 */
	abstract public void internalTransition(S_Message msg, int t)
			throws DEVS_Exception;

	/**
	 * Simulates an external transition
	 * 
	 * @param msg
	 *            message of the transition
	 * @param t
	 *            date of the transition
	 * @throws DEVS_Exception
	 */
	abstract public void externalTransition(X_Message msg, int t)
			throws DEVS_Exception;

	/**
	 * Simulates a tranfert.
	 * 
	 * @param msg
	 *            message
	 * @param t
	 *            date of the transfert
	 * @throws ConceptionErrorException
	 * @throws DEVS_Exception
	 */
	abstract public void transfert(Y_Message msg, int t)
			throws ConceptionErrorException, DEVS_Exception;

	/**
	 * Handles the incoming message.
	 * 
	 * @param msg
	 *            message that as to be treated.
	 * @throws DEVS_Exception
	 */
	public void handleMessage(Message msg) throws DEVS_Exception {
		int t = msg.getTime();

		if (msg instanceof I_Message) {
			/*
			 * System.out.println("********************");
			 * System.out.println(getModel().toString());
			 * System.out.println("handling I_Message ");
			 * System.out.println("********************");
			 */
			init(t);
		} else if (msg instanceof S_Message) {
			/*
			 * System.out.println("********************");
			 * System.out.println(getModel().toString());
			 * System.out.println("handling S_Message ");
			 * System.out.println("********************");
			 */
			if (t != tn_)
				throw new SynchroException();

			internalTransition((S_Message) msg, t);
		} else if (msg instanceof X_Message) {
			/*
			 * System.out.println("********************");
			 * System.out.println(getModel().toString());
			 * System.out.println("handling X_Message " +
			 * msg.getPort().getValue().toString());
			 * System.out.println("********************");
			 */
			if ((t < tl_) || (t > tn_)) {
				// System.out.println( t + " --> " + getModel().getName() + " tl
				// = " + tl_ + " and tn = " + tn_ );

				throw new SynchroException();
			}

			externalTransition((X_Message) msg, t);
		} else if (msg instanceof Y_Message) {
			/*
			 * System.out.println("********************");
			 * System.out.println(getModel().toString());
			 * System.out.println("handling Y_Message " +
			 * msg.getPort().toString());
			 * System.out.println("********************");
			 */
			transfert((Y_Message) msg, t);
		} else
			throw new ProgrammingException();

	}

}
