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
			inet_ip = InetAddress.getByName(ip);
			socket = new Socket(inet_ip, port);
			System.out.println(socket.isConnected());
			running = true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean isConnectionOpen(){
		return socket.isConnected();
	}
	
	public boolean verifyNameAndSpace(String name){
		
		send(name);				//Send name to verify
		
		ObjectInputStream inFromServer = null;
		String verifyMessage = null;
		boolean verify = false;
		try {
			inFromServer = new ObjectInputStream(socket.getInputStream());	//Wait for response
			verifyMessage = (String) inFromServer.readObject();			//Put message from stream to string
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(verifyMessage.equals("true")){
			verify = false;
		}else if(verifyMessage.equals("false")){
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
		recieveThread = new Thread("Receive-Thread"){
			public void run(){
				try {
					ObjectInputStream inFromServer = null;
					while(running){
						System.out.println(socket.isConnected());
						System.out.println(socket.getPort());
						inFromServer = new ObjectInputStream(socket.getInputStream());
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
		recieveThread.start();
	}

	/**
	 * Send messages to he server.
	 * @throws IOException 
	 */
	public void send(final String message){
		sendThread = new Thread("Send-Thread"){
			public void run(){
				try {
					System.out.println(socket.isConnected());
					ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
					outToServer.writeObject(message);
					outToServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		};
		sendThread.start();
	}

}
