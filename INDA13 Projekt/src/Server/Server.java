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
 * @author Richard Sjöberg
 * @version 2014-05-10
 */
public class Server {

	private ServerSocket servSock; // Waits for client connection requests.  
	private static final int LIMIT = 20; // The maximum number of connected clients. 
	private static boolean serverFull; // If users.size() == LIMIT.
	private static boolean isRunning; // If server is running.
	
	private static ArrayList<ObjectOutputStream> outStreams; // The output streams of this server's client-server connections.
	private static ArrayList<String> users; // The screen name of the users connected to the server.
	private static ArrayList<ChatService> connections; // The client-server connections of this server.
	private static ArrayList<Socket> sockets; // The socket of the client-server sockets.
	
	/**
	 * Creates a new server that listens for connections requests from clients 
	 * on the given port.
	 * 
	 * @param port A given port.
	 * @throws IOException If port is unavailable.  
	 */
	public Server(int port) throws IOException {
		users = new ArrayList<String>();
		outStreams = new ArrayList<ObjectOutputStream>();
		connections = new ArrayList<ChatService>();
		sockets = new ArrayList<Socket>();
						
		servSock = new ServerSocket(port);
		isRunning = true;
	}
	
	/**
	 * Listens for and accepts any incoming connection request on servSock. 
	 * The client is allowed to connect to the chat if the server is not 
	 * full, and does not contain any user with the requested screen name
	 * (screen name is sent on the socket). 
	 * 
	 * If the client is allowed to connect to the chat, the entered user 
	 * name is added to the users list. The connection status is sent on 
	 * the socket, which tells the client whether its connect request was 
	 * successful.
	 */
	public void acceptRequest() {
		Object[] streams; // The streams of the socket.
		Socket sock;
		
		try {
			sock = servSock.accept();
			sockets.add(sock);
			streams = getStreams(sock); 
			
			if (streams != null) {
				ObjectOutputStream output = (ObjectOutputStream) streams[0];
				ObjectInputStream input = (ObjectInputStream) streams[1];
				String name = getName(input);
				
				if (!serverFull && !nameInUse(name)) {
					//clients.add(sock); // Add the connection socket to clients.
					outStreams.add(output); // Add the output stream of the socket to outStreams.
					
					// If the server limit has been reached, the server is full. 
					if (users.size() == LIMIT) { 
						serverFull = true;
					}
	
					sendConnectionStatus(true, output);
					System.out.println("Good to go!"); // TODO: Remove.
					communicate(streams, sock, name); // Communicate with clients.
					updateUsers();
				} else {
					// Client is not connected to server.
					sendConnectionStatus(false, output);
					System.out.println("Can't connect."); // TODO: Remove.
				}
			}
		} catch (IOException e) {
			//Do nothing.
		} catch (NullPointerException e) {
			// Do nothing.
		}
	}
	
	/**
	 * Returns the object streams of the client-server connection on 
	 * the specified socket, stored in an array. The first element is 
	 * the output stream, the second element is the input stream.
	 * 
	 * @return The object streams stored in an array, null if streams couldn't be fetched.  
	 */
	private Object[] getStreams(Socket sock) {
		Object[] streams = new Object[2]; // The array in which the streams will be stored. 
		
		// Streams
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
	 * Checks that a user with the given screen name isn't already connected 
	 * to the server, that it isn't already in the users list. If the name 
	 * is not in use add the name to the users list and return true.
	 * 
	 * @param name The given name.
	 * @return True if the name from inputStream is already in use, false otherwise.
	 */
	private boolean nameInUse(String name) {
		boolean nameInUse = false; // Name already in use.
		
		if (users.contains(name)) {
			nameInUse = true;
			System.out.println("Already in use. Can't connect."); // TODO: Remove.
		} else {
			nameInUse = false;
			users.add(name);
			System.out.println("Not in use. Good to connect!"); // TODO: Remove.
		}
		return nameInUse;
	}
	
	/**
	 * Returns the requested screen name received from the given input stream.
	 * 
	 * @return The requested screen name if read successful, null if read failed.
	 */
	private String getName(ObjectInputStream input) {
		String name = null;
		try {
			name = (String) input.readObject();
			System.out.println("Name request from client: " + name); // TODO: Remove.
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return name;
	}
	
	/**
	 * Checks whether the server is running.
	 * 
	 * @return True if server is running, false otherwise.
	 */
	public boolean isRunning() {
		return isRunning;
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
	 * Echoes the users list to all clients connected to the server.
	 * The method should be executed whenever users are connected
	 * or disconnected to the server; whenever users are added or 
	 * removed from the users list. This is to insure that all 
	 * clients know which users are connected to the chat room.
	 * 
	 * TODO: EXECUTE SOMEWHERE! In the chatService???
	 */
	private void updateUsers() {
		// Send the users list on client
		try {
			for (ObjectOutputStream output : outStreams) {
				System.out.println(users);
				output.writeObject(Identifier.USER);
				for(String name : users){
					output.writeObject(name);
				}
				output.writeObject(Identifier.NO_MORE_USERS);
				output.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates an instance of ChatService, which handles he communication
	 * between the server and the clients, in a new thread. Adds the 
	 * ChatService to the connections list.
	 * 
	 * @param streams The streams over which to communicate.
	 * @param sock The socket of this client-server connection.
	 * @param name The name of the user of this connection.
	 */
	private void communicate(Object[] streams, Socket sock, String name) {
		try {
			// Communicate with client in a new thread.
			ChatService chat = new ChatService(streams, sock, name);
			Thread communicate = new Thread(chat);
			connections.add(chat);
			communicate.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Shuts down the server. Closes all connections, removes them from
	 * the connections list and changes isRunning to false.
	 */
	public void shutDown() {
		for (ChatService connection : connections) {
			connection.closeConnection();
		}
				
		try {
			for (Socket socket : sockets) {
				socket.close();
				socket = null;
			}
			servSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		servSock = null;
		sockets = null;
		users = null;
		outStreams = null;
		connections = null;
		isRunning = false;
	}
	
	/**
	 * The ChatService class provides the service of handling the
	 * communication between a server and its clients as part of 
	 * a server-client chat system. 
	 * 
	 * @author Richard Sjöberg
	 * @version 2014-05-06
	 */
	private class ChatService implements Runnable {
		Socket sock; // The socket of this client-server connection.
		ObjectInputStream input; // The input stream from which to read input.
		ObjectOutputStream output; // The output stream of this client-server connection's socket.
		String name; // The name of the user of this connection.
		
		
		/**
		 * Creates a new ChatService that handles the communication
		 * between a server and its clients.
		 * 
		 * @param streams The streams over which to communicate.
		 * @param sock The socket of this client-server connection.
		 * @param name The name of the user of this connection.
		 */
		public ChatService(Object[] streams, Socket sock, String name) {
			output = (ObjectOutputStream) streams[0];
			input = (ObjectInputStream) streams[1];
			this.sock = sock;
			this.name = name;
		}
		
		/**
		 * Receives messages from the input stream and echoes the messages 
		 * to all clients connected to the server.
		 * 
		 * If the user has disconnected from the server, remove the client
		 * connection from the server and make sure all connected clients
		 * are notified.
		 */
		public void run() {
			while (true) {
				try {
					// OBS! The client can only send string messages to server! This may be changed for future versions.
					String message = (String) input.readObject(); // The message read from socket.
					System.out.println("Received message: " + message);
					echoMessage(message); // Echoes the message to all clients connected to the server.`
				} catch (Exception e) {
					closeConnection();
					updateUsers();
					echoMessage(name + " has left the chat room.");
					break;
				}
			} 
		}
		
		/**
		 * Closes the client-server connection, by closing its streams and socket.
		 * Removes the user from the users list, the socket from clients and output
		 * from outStreams.
		 * 
		 * Notifies all connected clients.
		 */
		private void closeConnection() {
			try {
				input.close();
				output.close();
				sock.close();

				outStreams.remove(output);
				users.remove(name);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Sends a given message to all clients connected to the server.
		 * 
		 * @param message The message to be echoed.
		 */
		private void echoMessage(String message) {
			for (ObjectOutputStream output : outStreams) {
				// Send message on client.
				try {
					output.writeObject(Identifier.MESSAGE);
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