package message;

import model.Port;

/**
 * External transition message.
 * 
 * @author Xav & Sirius
 * 
 */
public class X_Message extends Message {
	/**
	 * Constructor : just calls the constructor of the super class.
	 * 
	 * @param t
	 *            date of the message.
	 * @param p
	 *            port of the message.
	 */
	public X_Message(Port p, int t) {
		super(p, t);
	}

}
