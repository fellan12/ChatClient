package Client;
/**
 * Interface for a Client to a chat service
 * 
 * @author Felix De Silva
 */
public interface ClientWindowInterface {
	/**
	 * Defines the content in ClientWindow
	 * 
	 * All its content is defines and creates here
	 */
	public void define();

	/**
	 * Send message
	 * 
	 * Prints the message to the screen and
	 * send it to the server
	 * 
	 * @param message
	 */
	public void sendMessage(String message);
	
}
