import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

/*Alternatively
 * import java.io*;
 * import java.set.*;
 */

/**
 * This is a server that communicates with clients, as part 
 * of a client-server chat system.
 * 
 * @author Richard Sjöberg
 * @version 2014-05-05
 */
public class Server {

	private static ArrayList<Socket> clients; // The sockets of this server's client-server connections.
	private static ArrayList<String> users; // The screen name of the users connected to the server.
	private static final int LIMIT = 20; // The maximum number of connected clients. TODO: Change
	private static boolean serverFull;
	
	/**
	 * Set up a server. Listen for connection requests to the server on the port,
	 * specified by the user who is trying to set up the server. If the port
	 * can not be used, notify the user.
	 * 
	 * Accept all connection requests as long as the server limit is not exceeded.
	 * For all clients connected to the server, create a new thread that takes care
	 * of the server-client communication. 
	 * 
	 * @param args Ignore. TODO: Maybe pass in port through args?
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//TODO: Start GUI. Get port from user.

		// Create a server socket that listens on the port specified by the user.
		ServerSocket servSock = new ServerSocket(123);    

		while (true) {
			if (!serverFull) {
				// Accept any incoming connection request.
				Socket sock = servSock.accept(); // TODO: ??? Socket ??? 
				clients.add(sock); // Add the connection socket to clients.
				
				// TODO: Client connected!
				System.out.println("Client connected from: " + sock.getLocalAddress().getHostName());
				
				
				// TODO: Start communication with client in a new thread.
				
				
				
			} else {
				// TODO: Do not accept request. Make sure the "requester" is notified somehow. 
			}
		}
		
		servSock.close();
	}
	
	// TODO: Create a server constructor, and set up the server in that instead.
	// Remember to remove static declarations.
	
	/**
	 * 
	 * Communicates over a ServerSocket with any client that
	 * sends a connection request to the server, as long as the 
	 * server limit hasn't been reached. 
	 *  
	 * @throws Exception Any exception encountered is thrown.
	 */
	public void run() throws Exception {
		Socket sock = servSock.accept(); // Socket connected to client. TODO: ???
		
		Thread thread = new Thread() 		
		communicate(sock);	
		
		
		servSock.close();
	}
	
	/**
	 * Reads input from the connected clients and sends the messages to
	 * each client connected to the server.
	 * 
	 * @param sock The socket over which to communicate.
	 */
	private void communicate(Socket sock) throws Exception {
		InputStreamReader IR = new InputStreamReader(sock.getInputStream());
		BufferedReader BR = new BufferedReader(IR);
		
		String message = BR.readLine(); // Message received from client.
		System.out.println(message); // TODO: Remove

		// Send message to all clients connected to the server.
		for (Socket s : clients) {
			PrintStream PS = new PrintStream(sock.getOutputStream());			
		}
	}
	
	
	/**
	 * Gets the screen name of the (user of the) client connected to the given socket,
	 * and adds it to the users list.
	 * 
	 * @param sock A given socket (server-client connection).
	 */
	private void addUser(Socket sock) {
		/* TODO:
		 * Get the name from the client.
		 * Add the name to users.
		 * Echo the users to all clients. (send the users list to the clients).
		 */
	}
}
