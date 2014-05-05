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
	 * Print the message to the screen
	 * 
	 * @param message
	 */
	public void printToScreen(String message);

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
