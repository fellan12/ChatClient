package Server;

import java.util.ArrayList;

/**
 * An instance of this class represents a message that is communicated between users 
 * of a client-server chat system. 
 * 
 * @author Richard Sjöberg
 * @version 2014-05-08
 */
public abstract class Message {


	
	
	/**
	 * Creates a new message of type ArrayList.
	 * 
	 * @param message A given message.
	 */
	public Message(ArrayList message) {
		this.message = message;
		type = ArrayList;
	}
	
}
