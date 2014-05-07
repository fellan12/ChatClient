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
 * TODO: Error handling. Try/catch. Handle exceptions?
 * 
 * @author Richard Sjöberg
 * @version 2014-05-06
 */
public class Server {

	private ServerSocket servSock; // TODO: 
	private static ArrayList<Socket> clients; // The sockets of this server's client-server connections. TODO: Change to streams?
	private static ArrayList<ObjectOutputStream> outStreams; // The output streams of this server's client-server connections.
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
	 * @param args Ignore.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int port = 1234; // TODO: Start GUI. Get port from user. 
		
		// Create a server that listens for connection requests on port.
		Server server = new Server(port);
		
		while (true) {
			Object[] streams = server.acceptRequest(); // The socket from which to read client input.
			if (streams != null) { // Client is connected to server.
				ObjectInputStream input = (ObjectInputStream) streams[1];
				server.communicate(input); // Communicate with clients.
			}
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
		outStreams = new ArrayList<ObjectOutputStream>();
		
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
	 * (screen name is sent on the socket). Returns the streams over which 
	 * to communicate.
	 * 
	 * If the client is allowed to connect to the chat, the client is added to
	 * the clients list and the entered user name is added to the users list.
	 * The connection status is sent on the socket, which tells the client 
	 * whether its connect request was successful.
	 *  
	 * @return The object streams of the stored in an array, null if request failed.
	 */
	private Object[] acceptRequest() {
		Socket sock = null; // The socket over which to communicate.
		Object[] streams; // The streams of the socket.
		
		try {
			sock = servSock.accept();
			servSock.close();
			streams = getStreams(sock); 
			
			if (streams != null) {
				ObjectOutputStream output = (ObjectOutputStream) streams[0];
				ObjectInputStream input = (ObjectInputStream) streams[1];
				
				if (!serverFull && !nameInUse(input)) {
					clients.add(sock); // Add the connection socket to clients.
					outStreams.add(output); // Add the output stream of the socket to outStreams.
					
					// If the server limit has been reached, the server is full. 
					if (users.size() == LIMIT) 
						serverFull = true;
					sendConnectionStatus(true, output);
					System.out.println("Good to go!"); // TODO: Remove.
				} else {
					// Client is not connected to server.
					sendConnectionStatus(false, output);
					sock = null;
					System.out.println("Can't connect."); // TODO: Remove.
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			streams = null;
		}		
		return streams;
	}
	
	/**
	 * Returns the object streams stored in an array. The first element
	 * is the output stream, the second element is the input stream.
	 * 
	 * @return The object streams stored in an array, null if streams couldn't be fetched.  
	 */
	private Object[] getStreams(Socket sock) {
		Object[] streams = new Object[2]; // The array in which the streams will be stored. 
		
		ObjectOutputStream output;
		ObjectInputStream input;
		try {
			output = new ObjectOutputStream(sock.getOutputStream()); // Get the output stream.
			input = new ObjectInputStream(sock.getInputStream()); // Get the input stream.
			
			// Add the streams to the array.
			streams[0] = output;
			streams[1] = input;
			
			return streams;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Checks that a user with the screen name received from the given input
	 * stream isn't already connected to the server, that it isn't already 
	 * in the users list. If the name is not in use add the name to the users 
	 * list and return true.
	 * 
	 * @param inputStream The given input stream from which to read.
	 * @return True if the name from inputStream is already in use, false otherwise.
	 * 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private boolean nameInUse(ObjectInputStream inputStream) {
		ObjectInputStream input = inputStream; 
		boolean nameInUse = false; // Name already in use.
		
		String name;
		try {
			name = (String) input.readObject();
			System.out.println("Name request from client: " + name); // TODO: Remove.
			
			if (users.contains(name)) {
				nameInUse = true;
				System.out.println("Already in use. Can't connect."); // TODO: Remove.
			} else {
				nameInUse = false;
				users.add(name);
				System.out.println("Not in use. Good to connect!"); // TODO: Remove.
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // The name received from input.
		return nameInUse;
	}

	/**
	 * Send the given boolean value to the given OutputStream, in order to tell the client
	 * whether it has been connected to the server.
	 * 
	 * @param connected true if client on sock has been connected to the server, false otherwise.
	 * @param outputStream The OutputStream on which to write.
	 */
	private void sendConnectionStatus(boolean connected, ObjectOutputStream outputStream) {
		try {
			ObjectOutputStream output = outputStream;
			output.writeObject(connected);
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates an instance of ChatService, which handles he communication
	 * between the server and the clients, in a new thread.
	 * 
	 * @param The streams over which to communicate.
	 */
	private void communicate(ObjectInputStream input) {
		try {
			// Communicate with client in a new thread.
			ChatService chat = new ChatService(input); 
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
	 * TODO: REMAKE AND EXECUTE SOMEWHERE!
	 */
	private void updateUsers() {
		for (Socket client : clients) {
			// Send the users list on client
			try {
				ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
				output.writeObject(users);
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * The ChatService class provides the service of handling the
	 * communication between a server and its clients as part of 
	 * a server-client chat system. The stream from which the input
	 * is read is passed in as a parameter in the constructor of 
	 * this class (as a new ChatService instance is initiated).
	 * 
	 * @author Richard Sjöberg
	 * @version 2014-05-06
	 */
	private class ChatService implements Runnable {
		
		ObjectInputStream input; // The input stream from which to read input.
		
		/**
		 * Creates a new ChatService that handles the communication
		 * between a server and its clients.
		 * 
		 * @param A given input stream from which to read input.
		 */
		public ChatService(ObjectInputStream input) {
			this.input = input;
		}
		
		/**
		 * Receives messages from the input stream and echoes the messages 
		 * to all clients connected to the server.  
		 */
		public void run() {
			while (true) {
				/* TODO:
				 * Check if socket has been closed and disconnected from the server.
				 */
							
				try {
					// OBS! The client can only send string messages to server! This may be changed for future versions.
					String message = (String) input.readObject(); // The message read from socket.
					System.out.println("Received message: " + message);
					echoMessage(message); // Echoes the message to all clients connected to the server.`
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
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i) == sock) {
					clients.remove(i);
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
			for (ObjectOutputStream output : outStreams) {
				// Send message on client.
				try {
					output.writeObject(message);
					System.out.println("Sent message: " + message);
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}