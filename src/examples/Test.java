package examples;

import java.io.IOException;
import java.util.ArrayList;

import simulator.RootCoordinator;
import exception.DEVS_Exception;

import model.*;

/**
 * Class that allows to test several models. This class can test 2 different
 * coupled model : the traffic light model or the fifo model.
 * 
 * @author Xav & Sirius
 * 
 */
public class Test extends CoupledModel {

	/**
	 * Constructor : just calls the constructor of the super class. All the job
	 * is done in the methods.
	 * 
	 * @param name
	 *            name of the model.
	 * @param desc
	 *            description of the model.
	 */
	public Test(String name, String desc) {
		super(name, desc);
	}

	@Override
	public Model select(ArrayList<Model> models) {
		return models.get(0);
	}

	/**
	 * Method that plays the role of the constructor and launcher when testing
	 * the traffic light model. The coupled model is built here. The atomic
	 * model which compose the coupled model are instantiated here. The links
	 * between these submodels are also drawn. Then the root coordinator is
	 * created, and the simulation launched
	 * 
	 * @param duration
	 *            duration of the simulation.
	 */
	public static void trafficLight(int duration) {
		Test test = new Test(
				"traffic light's test",
				"model composed by a generator, an adapter and a traffic light with its display");

		CompleteTrafficLight trafficLight = new CompleteTrafficLight(
				"traffic light", "traffic light coupled with its display");
		Generator generator = new Generator("generator",
				"randomly generates a signal");
		StartStopAdapter adapter = new StartStopAdapter("adapter",
				"signal to start/stop adapter");

		test.addSubModel(trafficLight);
		test.addSubModel(generator);
		test.addSubModel(adapter);

		test.addIC(generator.output("signal"), adapter.input("signal"));
		test.addIC(adapter.output("order"), trafficLight.input("order"));

		System.out.println(test.toString());

		System.out.println("...press a key to start simulation...");

		try {
			System.in.read();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		RootCoordinator root = new RootCoordinator(test.getSimulator());

		try {
			root.init(0);

			root.run(duration);

		} catch (DEVS_Exception e) {
			e.printStackTrace();
		}

		System.out.println("simulation is over");
	}

	/**
	 * Method that plays the role of constructor and launcher for the simulation
	 * of the fifo model. All the submodels that compose that coupled model are
	 * instantiated here. They are tied and the simulation is launched.
	 * 
	 * @param duration
	 *            duration of the simulation.
	 */
	public static void fifo(int duration) {
		Test test = new Test("FIFO's test",
				"model composed by a generator, a FIFO and their display");

		FIFO fifo = new FIFO("fifo", "");
		Generator generator = new Generator("generator",
				"randomly generates a signal");
		FIFODisplay display = new FIFODisplay("display",
				"displays arrivals and departures...");

		test.addSubModel(fifo);
		test.addSubModel(generator);
		test.addSubModel(display);

		test.addIC(generator.output("signal"), fifo.input("arrival"));
		test.addIC(generator.output("signal"), display.input("arrival"));
		test.addIC(fifo.output("departure"), display.input("departure"));

		System.out.println(test.toString());

		System.out.println("...press a key to start simulation...");

		try {
			System.in.read();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		RootCoordinator root = new RootCoordinator(test.getSimulator());

		try {
			root.init(0);

			root.run(duration);

		} catch (DEVS_Exception e) {
			e.printStackTrace();
		}

		System.out.println("simulation is over");

	}

	/**
	 * Main : calls a method to simulate the traffic light model or the fifo
	 * model.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String[] args) {
		trafficLight(1000);
		// fifo(100);
	}

}
