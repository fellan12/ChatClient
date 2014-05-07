package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * This is a server that communicates with clients, as part 
 * of a client-server chat system.
 * 
 * // TODO: Error handling. Try/catch. Handle exceptions?
 * 
 * @author Richard Sjöberg
 * @version 2014-05-06
 */
public class Server {

	private ServerSocket servSock; // TODO: 
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
		int port = 1234; // TODO: Start GUI. Get port from user. 
		
		// Create a server that listens for connection requests on port.
		Server server = new Server(port);
		
		while (true) {
			Socket sock = server.acceptRequest(); // The socket from which to read client input.
			if (sock != null) // Client is connected to server.
				server.communicate(sock); // Communicate with clients.
		}
		// TODO: Close the ServerSocket. Where?
	}
	
	/**
	 * Creates a new server that listens on the given port.
	 * 
	 * @param port A given port.
	 */
	public Server(int port) {
		clients = new ArrayList<Socket>();
		users = new ArrayList<String>();
		
		try {
			servSock = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Listens for and accepts any incoming connection request on servSock. 
	 * The client is allowed to connect to the chat if the server is not 
	 * full, and does not contain any user with the requested screen name
	 * (screen name is sent on the socket). Returns the Socket over which 
	 * to communicate.
	 * 
	 * If the client is allowed to connect to the chat, the client is added to
	 * the clients list and the entered user name is added to the users list.
	 * The connection status is sent on the socket, which tells the client 
	 * whether its connect request was successful.
	 *  
	 * @return The socket over which to communicate, or null if client is not allowed connect.
	 */
	private Socket acceptRequest() {
		Socket sock = null; // The socket over which to communicate.
		
		try {
			sock = servSock.accept(); 
			
			if (!serverFull && !nameInUse(sock)) {
				clients.add(sock); // Add the connection socket to clients.

				// If the server limit has been reached, the server is full. 
				if (users.size() == LIMIT)
					serverFull = true;
				sendConnectionStatus(true, sock);
			} else {
				// Client is not connected to server.
				sock = null;
				sendConnectionStatus(false, sock);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sock;
	}
	
	/**
	 * Checks that a user with the screen name received from the given socket 
	 * isn't already connected to the server, that it isn't already in the
	 * users list. If the name is not in use add the name to the users list
	 * and return true.
	 * 
	 * @param sock A given socket from which to receive a screen name.
	 * @return True if the name from socket is already in use, false otherwise.
	 */
	private boolean nameInUse(Socket sock) {
		ObjectInputStream input;
		boolean nameInUse = false; // Name already in use.
		
		try {
			input = new ObjectInputStream(sock.getInputStream());
			String name = (String) input.readObject();
			if (users.contains(name)) {
				nameInUse = true;
			} else {
				nameInUse = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nameInUse;
	}

	/**
	 * Send the given boolean value to the given socket, in order to tell the client
	 * whether it has been connected to the server.
	 * 
	 * @param connected true if client on sock has been connected to the server, false otherwise.
	 * @param sock The socket from which a connection request was received.
	 */
	private void sendConnectionStatus(boolean connected, Socket sock) {
		try {
			ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());
			output.writeObject(connected);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates an instance of ChatService, which handles he communication
	 * between the server and the clients, in a new thread.
	 * 
	 * @param The socket from which to read input from client.
	 */
	private void communicate(Socket sock) {
		try {
			// Communicate with client in a new thread.
			ChatService chat = new ChatService(sock); 
			Thread communicate = new Thread(chat);
			communicate.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Echoes the users list to all clients connected to the server.
	 * The method should be executed whenever users are connected
	 * or disconnected to the server; whenever users are added or 
	 * removed from the users list. This is to insure that all 
	 * clients know which users are connected to the chat room.
	 * 
	 * TODO: Does this work? Make a call to it somewhere!
	 */
	private void updateUsers() {
		for (Socket client : clients) {
			// Send the users list on client
			try {
				ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
				output.writeObject(users);
				output.flush();
				output.close();					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * The ChatService class provides the service of handling the
	 * communication between a server and its clients as part of 
	 * a server-client chat system. The socket from which the input
	 * is read is passed in as a parameter in the constructor of 
	 * this class (as a new ChatService instance is initiated).
	 * 
	 * @author Richard Sjöberg
	 * @version 2014-05-06
	 */
	private class ChatService implements Runnable {
		
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Fail - RunException");
					System.exit(1);
					break;
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