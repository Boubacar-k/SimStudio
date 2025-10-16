package message;

import model.Port;

/**
 * Output message.
 * 
 * @author Xav & Sirius
 * 
 */
public class Y_Message extends Message {
	/**
	 * Constructor : just calls the constructor of the super class.
	 * 
	 * @param t
	 *            date of the message.
	 * @param p
	 *            port of the message.
	 */
	public Y_Message(Port p, int t) {
		super(p, t);
	}
}
