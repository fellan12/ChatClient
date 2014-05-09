package Client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import Server.Identifier;

/**
 * Client class that handles the logics for the ClinetWindow
 * 
 * @author Felix De Silva
 */
public class Client {
	private Socket socket;
	private InetAddress inet_ip;
	private int port;
	private String ip;

	private Thread sendThread, recieveThread;

	private ObjectInputStream inFromServer = null;
	private ObjectOutputStream outToServer = null;

	private boolean running;

	private String name;

	private ClientWindow window;
	private boolean sendAllowed;


	public Client(String name, String ip, int port){
		this.ip = ip;
		this.port = port;
		this.name = name;
		openConnection(ip, port);														//Open connection
	}

	/**
	 * Try to connect to the server
	 * 
	 * @param ip - Ip-address to the server
	 * @param port - Port to the server
	 * @return true/false - if the connection worked
	 */
	public boolean openConnection(String ip, int port){
		try {
			inet_ip = InetAddress.getByName(ip);										//Make String ip to Inet-address ip
			socket = new Socket(inet_ip, port);										//Make a socket connection to ip and port
			InputStream input = socket.getInputStream();
			inFromServer = new ObjectInputStream(input);							//Create a inputstream
			outToServer = new ObjectOutputStream(socket.getOutputStream());			//Creates a OutputStream
			running = true;
			sendAllowed = true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * connect to server
	 * 
	 * Tries to open connection to server by open sockets and streams
	 * and sends name to verify its not in use. 
	 * 
	 * @param name
	 * @param ip
	 * @param port
	 * @return	true/false - if it worked or not.
	 */
	public boolean reconnectToServer(String name, String ip, int port){
		if(openConnection(ip, port)){												//Open connection
			verifyConnection(name);													//Verify name is not in use
			receive();																//Start receive messages
			return true;
		}
		return false;
	}

	/**
	 * Get the name of the user
	 * 
	 * @return name - the name of the user
	 */
	public String getName(){
		return name;
	}

	/**
	 * Checks if the socked is connected to the server
	 * 
	 * @return socket.isConnected() - true/false if there is a connection
	 */
	public boolean isConnectionOpen(){
		return socket.isConnected();
	}

	/**
	 * Send the name to the server and recieves a true/false message
	 * if you are allowed to connect.
	 * 
	 * during the verifying, if it is true create a CilentWindow
	 * 
	 * @param name
	 * @return
	 */
	public boolean verifyConnection(String userName){
		send(userName);																	//Send name to server for verify

		boolean verify = false;
		try {
			verify = (boolean) inFromServer.readObject();								//wait to put message from stream to boolean
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(verify == true){
			window = new ClientWindow(this); 												//Create a ClientWIndow
		}
		return verify;
	}

	/**
	 * receives messeges from the server.
	 * 
	 * @return message - the recevied message.
	 * @throws IOException 
	 */
	public void receive() {
		recieveThread = new Thread("Receive-Thread"){									//Thread
			public void run(){
				try {
					while(running){
						Object message = inFromServer.readObject();					//wait to put message from stream to string
						System.out.println("Recieve from server: " + message);

						if((message.equals(Identifier.MESSAGE))){
							String text = (String) inFromServer.readObject();
							window.receive(text);									//Send message to ClientWindow
						}else if(message.equals(Identifier.USER)){
							Object user = inFromServer.readObject();				//Receive name of user online
							ArrayList<String> users = new ArrayList<>();
							while(!user.equals(Identifier.NO_MORE_USERS)){
								users.add((String) user);							//add user to users.list
								user = inFromServer.readObject();					//Receive name of another user, if there are more
							}
							updateOnlinelist(users);								//Send users-list for updating
						}

					}
				} catch (IOException | ClassNotFoundException e) {
					e.getStackTrace();
				}

			}

			/**
			 * Uppdate online user list
			 * @param users
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
	 * Send messages to he server.
	 * @throws IOException 
	 */
	public void send(final String message){
		if(sendAllowed){
			sendThread = new Thread("Send-Thread"){
				public void run(){
					try {
						outToServer.writeObject(message);									//Send message through the stream
						System.out.println("Write to server: " + message);
						outToServer.flush();												//Flushes the stream
					} catch (IOException e) {
						sendAllowed = false;
						window.printToScreen("Instachat: Lost connection to server... Trying to reconnect to server!");
						disconnect();														//Disconnect sockets and streams
						boolean connected = false;
						while(!connected){													//Try to reconnect	
							connected = reconnectToServer(name, ip, port);
						}
						window.printToScreen("Instachat: You are reconnected to the server");

						e.printStackTrace();
					}
				}
			};
			sendThread.start();
		}
	}

	/**
	 * Disconnect from server
	 * 
	 * when you use the Exit button in the file-menubar
	 */
	public void disconnect(){
		try {
			inFromServer.close();														//Close input-stream
			outToServer.close();														//Close output-stream
			socket.close();																//Close socket
			running = false;
			sendAllowed = false;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}	
