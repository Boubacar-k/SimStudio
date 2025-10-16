package model;

import java.util.ArrayList;

import simulator.*;

import exception.*;

import utils.Pair;

/**
 * Coupled model
 * 
 * @author Xav & Sirius
 * 
 */
abstract public class CoupledModel extends Model {
	/**
	 * list of the submodels.
	 */
	protected ArrayList<Model> subModels_ = new ArrayList<Model>();

	/**
	 * List of External Input Coupling.
	 */
	protected ArrayList<Pair> EIC_ = new ArrayList<Pair>();

	/**
	 * List of External Output Coupling.
	 */
	protected ArrayList<Pair> EOC_ = new ArrayList<Pair>();

	/**
	 * List of Internal Coupling.
	 */
	protected ArrayList<Pair> IC_ = new ArrayList<Pair>();

	/**
	 * Select method: has to be defined by the user when implementing a coupled
	 * model. Elects a model among a list of given models.
	 * 
	 * @param models
	 *            list of models
	 * @return the selected model among the list
	 */
	abstract public Model select(ArrayList<Model> models);

	public CoupledModel(String name, String desc) {
		super(name, desc);

		sim_ = new Coordinator(this);
	}

	/**
	 * Adds a submodel to the coupled model
	 * 
	 * @param m
	 *            the submodel to add.
	 */
	public void addSubModel(Model m) {
		subModels_.add(m);
		((Coordinator) sim_).addSubject(m.getSimulator());

		m.getSimulator().setParent(sim_);
	}

	/**
	 * Adds an External Input Coupling.
	 * 
	 * @param port1
	 *            port of the coupled model
	 * @param port2
	 *            port of one of the submodel
	 */
	public void addEIC(Input port1, Input port2) {
		EIC_.add(new Pair(port1, port2));
	}

	/**
	 * Adds an External Output Coupling.
	 * 
	 * @param port1
	 *            port of the coupled model
	 * @param port2
	 *            port of one of the submodel
	 */
	public void addEOC(Output port1, Output port2) {
		EOC_.add(new Pair(port1, port2));
	}

	/**
	 * Adds an Internal Coupling.
	 * 
	 * @param port1
	 *            port of the first submodel
	 * @param port2
	 *            port of the second submodel
	 */
	public void addIC(Output port1, Input port2) {
		IC_.add(new Pair(port1, port2));
	}

	/**
	 * Gets the linked outputs
	 * 
	 * @param port
	 *            port of the coupled model
	 * @return the list of outputs
	 * @throws ConceptionErrorException
	 */
	public ArrayList<Output> getLinkedOutput(Port port)
			throws ConceptionErrorException {
		ArrayList<Output> linkedPorts = new ArrayList<Output>();

		for (Object o : EOC_) {
			Pair pair = (Pair) o;

			if ((pair.first() instanceof Output)
					&& (pair.second() instanceof Output)) {
				Output f = (Output) pair.first();
				Output s = (Output) pair.second();

				if (f.equals(port))
					linkedPorts.add(s);
			} else
				throw new ConceptionErrorException();
		}

		return linkedPorts;
	}

	/**
	 * Gets the linked inputs
	 * 
	 * @param port
	 *            port of the coupled model
	 * @return the list of inputs
	 * @throws ConceptionErrorException
	 */
	public ArrayList<Input> getLinkedInput(Port port)
			throws ConceptionErrorException {
		ArrayList<Input> linkedPorts = new ArrayList<Input>();

		for (Object o : EIC_) {
			Pair pair = (Pair) o;

			if ((pair.first() instanceof Input)
					&& (pair.second() instanceof Input)) {
				Input f = (Input) pair.first();
				Input s = (Input) pair.second();

				if (f.equals(port))
					linkedPorts.add(s);
			} else
				throw new ConceptionErrorException();

		}

		return linkedPorts;
	}

	/**
	 * Gets the internal linked port for a given port
	 * 
	 * @param port
	 *            port of the coupled model
	 * @return the lists of linked ports
	 * @throws ConceptionErrorException
	 */
	public ArrayList<Port> getLinkedInternalPort(Port port)
			throws ConceptionErrorException {
		ArrayList<Port> linkedPorts = new ArrayList<Port>();

		for (Object o : IC_) {
			Pair pair = (Pair) o;

			// System.out.println ( pair.first() + " ;" + pair.second() ) ;

			if ((pair.first() instanceof Port)
					&& (pair.second() instanceof Port)) {
				Port f = (Port) pair.first();
				Port s = (Port) pair.second();

				if (f.equals(port))
					linkedPorts.add(s);
			} else
				throw new ConceptionErrorException();

		}

		return linkedPorts;
	}

	/**
	 * Gets all the linked ports for a given port
	 * 
	 * @param port
	 *            linked port
	 * @return the list of linked ports
	 * @throws ConceptionErrorException
	 */
	public ArrayList<Port> getLinkedPort(Port port)
			throws ConceptionErrorException {
		ArrayList<Port> linkedPorts = new ArrayList<Port>();

		linkedPorts.addAll(getLinkedOutput(port));
		linkedPorts.addAll(getLinkedInput(port));
		linkedPorts.addAll(getLinkedInternalPort(port));

		return linkedPorts;
	}

	/**
	 * Serialization method
	 */
	public String toString(int level) {
		String ret = new String(name_ + " : " + desc_ + "\n");

		String offset = "";
		for (int i = 0; i < level; ++i)
			offset += "|   ";

		for (Model m : subModels_)
			ret += offset + "|---" + m.toString(level + 1);

		return ret;
	}
}
