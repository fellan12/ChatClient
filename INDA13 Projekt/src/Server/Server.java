package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	private ServerSocket servSock;
	private static ArrayList<Socket> clients; // The sockets of this server's client-server connections.
	private static ArrayList<String> users; // The screen name of the users connected to the server.
	private static final int LIMIT = 20; // The maximum number of connected clients. TODO: Change.
	private static boolean serverFull; // If users.size() == LIMIT.
	
	/**
	 * Set up a server. Listen for connection requests to the server on the port,
	 * specified by the user who is trying to set up the server. If the port can 
	 * not be used, notify the user.
	 * 
	 * Accept all connection requests as long as the server limit has not been reached.
	 * For all clients connected to the server, create a new thread that takes care
	 * of the server-client communication. 
	 * 
	 * @param args Ignore. TODO: Maybe pass in port through args?
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO: Start GUI. Get port from user.
		
		// TODO: Error handling. Try/catch. Handle exceptions?

		while (true) {
			if (!serverFull) {
				// Listen for and accept any incoming connection request.
				Socket sock = servSock.accept(); // The socket over which to communicate.  

				// TODO: Only one instance of a socket? Add only if it doesn't already exist in list??
				clients.add(sock); // Add the connection socket to clients.  

				// If the server limit has been reached, the server is full. 
				if (users.size() == LIMIT) {
					serverFull = true;
				}
				
				System.out.println("Client connected from: " + sock.getLocalAddress().getHostName()); // TODO: Remove!
				
				// Communicate with client in a new thread.
				ChatService chat = new ChatService(sock); 
				Thread communicate = new Thread(chat);
				communicate.start();
			} else {
				// TODO: Do not accept request. Make sure the "requester" is notified somehow. 
			}
		}
		// TODO: Close the ServerSocket. Where?
	}
	
	// TODO: Create a server constructor, and set up the server in that instead.
	// Remember to remove static declarations.
	
	/**
	 * Creates a new server
	 * 
	 * @param port 
	 */
	public Server(int port) {
		// Create a server socket that listens for connection requests on the port specified by the user.
		try {
			servSock = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		 * Get the name from the client. HOW? Depends on the client input!
		 * Add the name to users.
		 * Echo the users to all clients. (send the users list to the clients).
		 */
			
		// So many ways to read/write from/to sockets! Which one to use?!
	}
	
	/**
	 * Echos the users list to all clients connected to the server.
	 * The method should be executed whenever users are connected
	 * or disconnected to the server; whenever users are added or 
	 * removed from the users list. This is to insure that all 
	 * clients know which users are connected to the chat room.
	 */
	private void updateUsers() {
		/* TODO
		 * update boolean
		 */
		
	}

	/**
	 * The ChatService class provides the service of handling the
	 * communication between a server and its clients as part of 
	 * a server-client chat system. The socket from which the input
	 * is read is passed in as a parameter in the constructor of 
	 * this class (as a new ChatService instance is initiated).
	 * 
	 * @author Richard Sjöberg
	 * @version 2014-05-05
	 */
	public class ChatService implements Runnable {
		
		Socket sock; // The socket from which to read input.
		
		/**
		 * Creates a new ChatService that handles the communication
		 * between a server and its clients.
		 * 
		 * @param A given socket from which to read input.
		 */
		public ChatService(Socket sock) {
			this.sock = sock;
		}
		
		/**
		 * Receives messages from the socket, sock, and echoes the messages 
		 * to all clients connected to the server TODO: Which server?.  
		 */
		public void run() {
			while (true) {
				// If socket has been closed and disconnected from the server.
				if (sock.isClosed()) {
					removeSock(sock); 
					break;
				}
				
				try {
					// Read the client input from the socket to ObjectInputStream.
					ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
					// OBS! The client can only send string messages to server! This may be changed for future versions.
					String message = (String) input.readObject(); // The message read from socket.
					echoMessage(message); // Echoes the message to all clients connected to the server.
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		
		/**
		 * Removes the given socket from the clients list.
		 * 
		 * @param sock The socket to be removed.
		 */
		private void removeSock(Socket sock) {
			for (int i = 0; i < Server.clients.size(); i++) {
				if (Server.clients.get(i) == sock) {
					Server.clients.remove(i);
				}
			}
			// TODO: Update users. Echo the users list to all connected clients.
		}
		
		/**
		 * Sends a given message (of type string) to all clients connected
		 * to the server.
		 * 
		 * @param message The message to be echoed.
		 */
		private void echoMessage(String message) {
			for (Socket client : clients) {
				// Send message on client.
				try {
					ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
					output.writeObject(message);
					output.close();					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
