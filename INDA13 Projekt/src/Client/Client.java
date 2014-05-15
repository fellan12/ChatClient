package Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import Server.Identifier;

/**
 * Client is the class that handles the backend for the ClinetWindow
 * 
 * @author Felix De Silva
 * @date 15 maj 2014
 */
public class Client {
	//TCP - related
	private Socket socket;							//Socket for connecting to the server
	private InetAddress inet_ip;					//InetAddress for connecting to the server

	//Streams
	private ObjectInputStream inFromServer;			//Stream to receiving objects 
	private ObjectOutputStream outToServer;			//Stream to send objects 

	//Boolean
	private boolean running;						//boolean reflecting if the client is running or not 
	private boolean sendAllowed;					//boolean reflecting if sending is allowed of not
	
	//Integer
	private int port;								//port to the server

	//Threads
	private Thread sendThread, recieveThread;		//Thread for sending and receiving
	
	//ClientWindow
	private ClientWindow window;					//ClientWindow object
	
	//String
	private String ip, name;						//Ip and name from user

	/**
	 * Create a new chat client that communicates with the server
	 * on the given port.
	 * 
	 * @param name The name of the user of this client.
	 * @param ip The IP address of the server 
	 * @param port The port 
	 */
	public Client(String name, String ip, int port){
		this.ip = ip;
		this.port = port;
		this.name = name;
		inFromServer = null;
		outToServer = null;
		openConnection(ip, port);														//Open connection to the server
	}

	/**
	 * Try to connect to the server.
	 * 
	 * @param ip - Ip-address to the server
	 * @param port - Port to the server
	 * @return true/false - if the connection worked
	 */
	public boolean openConnection(String ip, int port){
		try {
			inet_ip = InetAddress.getByName(ip);										//Make String ip to Inet-address ip
			socket = new Socket(inet_ip, port);											//Make a socket connection to ip and port
			inFromServer = new ObjectInputStream(socket.getInputStream());				//Create a inputstream 
			outToServer = new ObjectOutputStream(socket.getOutputStream());				//Creates a OutputStream
			running = true;																//Set running to true
			sendAllowed = true;															//set sendAllowed to true
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Tries to reconnect to the server on the given port. 
	 *  
	 * @param name The name of the client. 
	 * @param ip The IP address of the server host.
	 * @param port The port to which to connect.
	 * @return	true/false - if it worked or not.
	 */
	public boolean reconnectToServer(String name, String ip, int port){
		if(openConnection(ip, port)){													//Open connection
			send(name);																	//Send name to server for verify
			receive();																	//Start receive messages
			return true;
		}
		return false;
	}

	/**
	 * Get the name of the user of this client.
	 * 
	 * @return The name of the user of this client.
	 */
	public String getName(){
		return name;
	}

	/**
	 * Checks if the socket is connected to the server.
	 * 
	 * @return True if the socket is connected to the server, false otherwise. 
	 */
	public boolean isConnectionOpen(){
		return socket.isConnected();
	}

	/**
	 * Send the name of this client to the server. The server returns a
	 * boolean that tells the client whether it is allowed to connect. 
	 * If it is, create a ClientWindow, if it isn't do nothing. Return
	 * the value that is returned from the server.
	 * 
	 * @param name The name of the user of this client.
	 * @return True if the client is allowed to connect to server, false otherwise.
	 */
	public boolean verifyConnection(String userName){
		send(userName);																	//Send name to server for verify

		boolean verify = false;															//Create a verify boolean temp-variable
		try {
			verify = (boolean) inFromServer.readObject();								//wait to put message from stream to boolean
		} catch (IOException | ClassNotFoundException e) {
			// e.printStackTrace();
		}
		if(verify == true){
			window = new ClientWindow(this); 											//Create a ClientWIndow
		}
		return verify;
	}

	/**
	 * Receives messages from the server.
	 */
	public void receive() {
		recieveThread = new Thread("Receive-Thread"){									//Thread
			public void run(){
				try {
					while(running){														//Loop until program not running
						Object message = inFromServer.readObject();						//wait to put message from stream to string

						if((message.equals(Identifier.MESSAGE))){
							String text = (String) inFromServer.readObject();
							window.receive(text);										//Send message to ClientWindow
						}else if(message.equals(Identifier.USER)){	
							Object user = inFromServer.readObject();					//Receive name of user online
							ArrayList<String> users = new ArrayList<>();	
							while(!user.equals(Identifier.NO_MORE_USERS)){				//Loop until Identifier says NO_MORE_USERS
								users.add((String) user);								//add user to users.list
								user = inFromServer.readObject();						//Receive name of another user, if there are more
							}
							updateOnlinelist(users);									//Send users-list for updating
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					sendAllowed = false;												//Set sendAllowed to false
					window.printToScreen("Instachat: Lost connection to server! Trying to reconnect to server...");
					disconnect();														//Disconnect sockets and streams
					boolean connected = false;
					while(!connected){													//Loop for reconnecting
						connected = reconnectToServer(name, ip, port); 					//Try to reconnect
					}
					window.printToScreen("Instachat: You have been reconnected to the server!");
				}
			}

			/**
			 * Update online users list.
			 * 
			 * @param users The array of the users currently connected to the server.
			 */
			private void updateOnlinelist(ArrayList<String> users) {
				if(users.size() > 0){
					window.updateOnlineUserList(users);									//Send users to window for updating					
				}
			}
		};	
		recieveThread.start();															//Start the thread
	}

	/**
	 * Send messages to the server.
	 */
	public void send(final String message){
		if(sendAllowed){
			sendThread = new Thread("Send-Thread"){										//Send Thread
				public void run(){
					try {
						outToServer.writeObject(message);								//Send message through the stream
						outToServer.flush();											//Flushes the stream
					} catch (IOException e) {
						// e.printStackTrace();
					}
				}
			};
			sendThread.start();															//Starts the send thread
		}
	}

	/**
	 * Disconnect from server. Close the streams and the socket.
	 * Update the fields.
	 */
	public void disconnect(){
		try {
			inFromServer.close();														//Close input-stream
			outToServer.close();														//Close output-stream
			socket.close();																//Close socket
			running = false;															//Set running to false
			sendAllowed = false;														//Set sendAllowed to false
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}
}	
