package model;

import types.DEVS_Type;

/**
 * Output class : standard port
 * 
 * @author Xav & Sirius
 * 
 */
public class Output extends Port {
	/**
	 * Constructor
	 * 
	 * @param value
	 *            value of the port
	 * @param name
	 *            name of the port
	 * @param desc
	 *            description of the port
	 * @param model
	 *            model of the port
	 */
	public Output(DEVS_Type value, String name, String desc, Model model) {
		super(value, name, desc, model);
	}
}
