package Client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client class that handles the logics for the ClinetWindow
 * 
 * @author Felix De Silva
 */
public class Client {
	private Socket socket;
	private InetAddress inet_ip;

	private Thread sendThread, recieveThread;
	private int port;
	
	private boolean running;

	public Client(String ip, int port){
		this.port = port;

			openConnection(ip, port);
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
			inet_ip = InetAddress.getByName(ip);							//Make String ip to Inet-address ip
			socket = new Socket(inet_ip, port);								//Make a socket connection to ip and port
			running = true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
		
		send(userName);															//Send name to server for verify
		
		ObjectInputStream inFromServer = null;
		String verifyMessage = null;
		boolean verify = false;
		try {
			inFromServer = new ObjectInputStream(socket.getInputStream());	//Wait for response
			verifyMessage = (String) inFromServer.readObject();				//Put message from stream to string
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(verifyMessage.equals("true")){									//if the message was true then the name is taken
			verify = false;
		}else if(verifyMessage.equals("false")){							//if the message was false then the name is not taken
			verify = true;
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
		final ClientWindow window = new ClientWindow(); 
		recieveThread = new Thread("Receive-Thread"){									//Thread
			public void run(){
				try {
					ObjectInputStream inFromServer = null;
					while(running){
						inFromServer = new ObjectInputStream(socket.getInputStream());	//make a inputStream
						String message = (String) inFromServer.readObject();			//Put message from stream to string

						if(!message.equals("")){
							window.receive(message);									//Send message to ClientWindow
						}
					}
					inFromServer.close();											//Close input Stream
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
		};	
		recieveThread.start();														//Start the thread
	}

	/**
	 * Send messages to he server.
	 * @throws IOException 
	 */
	public void send(final String message){
		sendThread = new Thread("Send-Thread"){										//Thread
			public void run(){
				try {
					ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());		//Make a OutputStream
					outToServer.writeObject(message);								//send message through the stream
					outToServer.close();											//Close the stream
				} catch (IOException e) {		
					e.printStackTrace();
				}
				
			}
		};
		sendThread.start();															//Start the thread
	}

}
