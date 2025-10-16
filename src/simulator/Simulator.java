package simulator;

import model.*;
import exception.*;
import message.*;

/**
 * Simulator : drives an atomic model.
 * 
 * @author Sirius
 * 
 */
public class Simulator extends AbstractSimulator {
	protected AtomicModel model_ = null;

	/**
	 * Constructor : sets the atomic model.
	 * 
	 * @param m
	 *            atomic model.
	 */
	public Simulator(AtomicModel m) {
		model_ = m;
	}

	/**
	 * Initialises the simulation
	 */
	public void init(int t) {
		tl_ = t;
		tn_ = t + model_.ta();

		// System.out.println( "model " + model_.getName() + " initialized with
		// tl = " + tl_ + " and tn = " + tn_ ) ;
	}

	/**
	 * Returns the driven atomic model
	 */
	public Model getModel() {
		return model_;
	}

	/**
	 * Handles an internal transition
	 * 
	 * @param msg
	 *            message of the transition
	 * @param t
	 *            date of the transition
	 */
	public void internalTransition(S_Message msg, int t) throws DEVS_Exception {
		tl_ = t;

		model_.lambda();

		model_.deltaInt();

		tn_ = t + model_.ta();
	}

	/**
	 * Handles an external transition
	 * 
	 * @param msg
	 *            message of the transition
	 * @param t
	 *            date of the transition
	 */
	public void externalTransition(X_Message msg, int t) throws DEVS_Exception {
		e_ = t - tl_;

		tl_ = t;

		model_.deltaExt(e_);

		tn_ = tl_ + model_.ta();

		// System.out.println("model " + getModel().toString() + " ; tl = " +
		// tl_ + " and tn = " + tn_ );
	}

	/**
	 * Handles a transfert : impossible for a simulator / atomic model.
	 */
	public void transfert(Y_Message msg, int t) throws ConceptionErrorException {
		throw new ConceptionErrorException();
	}

}
