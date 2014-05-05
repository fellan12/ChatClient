package Server;
/**
 * Interface for a Server
 * 
 * @author Felix De Silva
 */
public interface ServerInterface {
		/**
		 * runs the server
		 */
		public void run();

		/**
		 * Manage the clients, checks that them are there
		 */
		public void manageClients();

		/**
		 * Receives messages from clients
		 */
		public void receive();

}
