package message;

import model.*;

/**
 * Message class. Standard class, used to define common specificities of the
 * messages.
 * 
 * @author Xav & Sirius
 * 
 */
public class Message {

	/**
	 * Date of the message.
	 */
	protected int t_;

	/**
	 * Port of the message.
	 */
	protected Port port_;

	/**
	 * Constructor
	 * 
	 */
	public Message() {

	}

	/**
	 * Constructor II
	 * 
	 * @param p
	 *            port.
	 * @param t
	 *            date of the message.
	 */
	public Message(Port p, int t) {
		t_ = t;
		port_ = p;
	}

	/**
	 * Gets the time of the message.
	 * 
	 * @return the date t.
	 */
	public int getTime() {
		return t_;
	}

	/**
	 * Sets the time of the message.
	 * 
	 * @param t
	 *            the date t.
	 */
	public void setTime(int t) {
		t_ = t;
	}

	/**
	 * Gets the port of the message.
	 * 
	 * @return the port port.
	 */
	public Port getPort() {
		return port_;
	}
}
