package simulator;

import java.util.ArrayList;

import exception.*;
import model.*;
import message.*;

/**
 * Coordinator class. Drives a coupled model. A coordinator is a specific
 * simulator.
 * 
 * @author Xav & Sirius
 * 
 */
public class Coordinator extends AbstractSimulator {
	/**
	 * driven coupled model
	 */
	protected CoupledModel model_;

	/**
	 * List of the sons (simulators)
	 */
	protected ArrayList<AbstractSimulator> subjects_ = new ArrayList<AbstractSimulator>();

	/**
	 * Constructor : builds the coordinator for a given model.
	 * 
	 * @param cm
	 */
	public Coordinator(CoupledModel cm) {
		model_ = cm;
	}

	/**
	 * Returns the model of the coordinator
	 */
	public Model getModel() {
		return model_;
	}

	/**
	 * Adds a son to the simulator.
	 * 
	 * @param sim
	 */
	public void addSubject(AbstractSimulator sim) {
		subjects_.add(sim);
	}

	/**
	 * Initialisation of the coordinator. Sends init messages to all the sons
	 */
	public void init(int t) throws DEVS_Exception {
		for (Object o : subjects_) {
			if (o instanceof AbstractSimulator) {
				AbstractSimulator sim = (AbstractSimulator) o;

				I_Message msg = new I_Message(t);

				sim.handleMessage((Message) msg);

				if (sim.tl_ > tl_)
					tl_ = sim.tl_;

				if (sim.tn_ < tn_)
					tn_ = sim.tn_;
			} else
				throw new ProgrammingException();
		}
	}

	/**
	 * Handles an internal transition/
	 */
	public void internalTransition(S_Message msg, int t) throws DEVS_Exception {
		ArrayList<Model> models = new ArrayList<Model>();
		AbstractSimulator simToActivate = null;

		for (Object o : subjects_) {
			if (o instanceof AbstractSimulator) {
				AbstractSimulator sim = (AbstractSimulator) o;

				if (sim.tn_ == tn_)
					models.add(sim.getModel());
			} else
				throw new ProgrammingException();
		}

		if (models.size() == 1)
			simToActivate = (AbstractSimulator) models.get(0).getSimulator();
		else if (models.size() > 1)
			simToActivate = model_.select(models).getSimulator();
		else if (models.size() < 1)
			throw new ProgrammingException();

		simToActivate.handleMessage(msg);

		updateTn();

	}

	/**
	 * Handles an external transition
	 */
	public void externalTransition(X_Message msg, int t) throws DEVS_Exception {
		// envoyer msg a tous les sujets conformement a EIC
		for (Object o : model_.getLinkedInput(msg.getPort())) {
			Port p = (Port) o;
			p.setValue(msg.getPort().getValue());
			p.getModel().getSimulator().handleMessage(msg);
		}

		tl_ = t;

		updateTn();
	}

	/**
	 * Handles a transfert.
	 */
	public void transfert(Y_Message msg, int t) throws DEVS_Exception {
		// envoyer msg a tous les sujets conformement a EOC
		for (Object o : model_.getLinkedOutput(msg.getPort())) {
			Port p = (Port) o;
			p.setValue(msg.getPort().getValue());
			p.getModel().getSimulator().handleMessage(msg);
		}

		// envoyer X-msg a tous les sujets conformement a IC
		for (Object o : model_.getLinkedInternalPort(msg.getPort())) {
			Port p = (Port) o;
			p.setValue(msg.getPort().getValue());
			p.getModel().getSimulator().handleMessage(
					new X_Message(msg.getPort(), msg.getTime()));
		}

	}

	/**
	 * Calculates the date of the next event.
	 * 
	 * @throws DEVS_Exception
	 */
	protected void updateTn() throws DEVS_Exception {
		if (subjects_.size() < 1)
			throw new ConceptionErrorException();

		tn_ = ((AbstractSimulator) subjects_.get(0)).tn_;

		for (Object o : subjects_) {
			if (o instanceof AbstractSimulator) {
				AbstractSimulator sim = (AbstractSimulator) o;

				if (sim.tn_ < tn_)
					tn_ = sim.tn_;
			} else
				throw new ProgrammingException();
		}
	}
}
