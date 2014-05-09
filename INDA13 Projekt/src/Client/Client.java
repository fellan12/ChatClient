package Client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

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
		openConnection(ip, port);
		window = new ClientWindow(this); 
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

	public boolean connectToServer(String name, String ip, int port){
		if(openConnection(ip, port)){
			verifyConnection(name);
			receive();
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
						Object message = inFromServer.readObject();			//wait to put message from stream to string
						System.out.println("Recieve from server: " + message);

						if((message.equals(Identifier.MESSAGE))){
							String text = (String) inFromServer.readObject();
							window.receive(text);									//Send message to ClientWindow
						}else if(message.equals(Identifier.USER)){
							Object user = inFromServer.readObject();
							ArrayList<String> users = new ArrayList<>();
							while(!user.equals(Identifier.NO_MORE_USERS)){
								users.add((String) user);
								user = inFromServer.readObject();
							}
							updateOnlinelist(users);
						}

					}
				} catch (IOException | ClassNotFoundException e) {
					e.getStackTrace();
				}

			}

			private void updateOnlinelist(ArrayList<String> users) {
				if(users.size() > 0){
					window.updateOnlineUserList(users);
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
						outToServer.writeObject(message);									//send message through the stream
						System.out.println("Write to server: " + message);
						outToServer.flush();
					} catch (IOException e) {
						sendAllowed = false;
						window.printToScreen("Instachat: Lost connection to server... Trying to reconnect to server!");
						disconnect();
						boolean connected = false;
						while(!connected){
							connected = connectToServer(name, ip, port);
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
			System.out.println("closing");
			inFromServer.close();
			outToServer.close();
			socket.close();
			running = false;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}	
