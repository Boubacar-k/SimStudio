package examples;

import java.util.ArrayList;

import types.DEVS_Enum;
import model.* ;

/**
 * Class that represents a coupled model of a traffic ligth engine and its display.
 * @author Xav & Sirius
 *
 */
public class CompleteTrafficLight extends CoupledModel 
{
	/**
	 * Constructor : builds the coupled model. It instanciates the atomic models that compose this coupled model.
	 * @param name name of the model.
	 * @param desc description of the model.
	 */
	public CompleteTrafficLight(String name, String desc) {
		super(name, desc);
		
		TrafficLight trafficLight = new TrafficLight("traffic light model", "basic traffic light model, input = {start,stop}, output = color of the light") ;

		TrafficLightDisplay display = new TrafficLightDisplay("diplay", "displays the color <<received>> as input") ;
		
		addSubModel(trafficLight) ;
		addSubModel(display) ;
		
		addIC(trafficLight.output("color"), display.input("color")) ;
		
		String[] orders = {"start","stop"} ;
		addInput( new DEVS_Enum( orders ),
				  "order",
				  "turning on/off the traffic light" );
		
		addEIC( this.input("order"), trafficLight.input("order")) ;
	}


	@Override
	public Model select(ArrayList<Model> possibleModels) {
		// TODO Auto-generated method stub
		for ( Model m : possibleModels )
		{
			if ( m instanceof TrafficLight )
				return m ;
		}
		
		return possibleModels.get(0);
	}
}
