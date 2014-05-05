package Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	private PrintWriter outputStream;
	private Scanner inputStream;

	private Thread sendThread, recieveThread;
	private int port;
	
	public Client(int port){
		this.port = port;
		
		try {
			outputStream = new PrintWriter(socket.getOutputStream());
			inputStream = new Scanner(socket.getInputStream());
			outputStream.flush();
			receive();

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Try to connect to the server
	 * 
	 * @param ip - Ip-address to the server
	 * @param port - Port to the server
	 * @return true/false - if the connection worked
	 */
	public boolean openConnection(String ip){
		try {
			inet_ip = InetAddress.getByName(ip);
			socket = new Socket(inet_ip, port);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * receives messeges from the server.
	 * 
	 * @return message - the recevied message.
	 * @throws IOException 
	 */
	public void receive() throws IOException{
		final ClientWindow window = new ClientWindow(); 
		recieveThread = new Thread("Receive Thread"){
			public void run(){
				while(inputStream.hasNext()){
					String message = inputStream.nextLine();
					
					if(!message.equals("")){
						window.receive(message);
					}
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
		sendThread = new Thread("Send Thread"){
			public void run(){
				outputStream.println(message);
				outputStream.flush();
			}
		};
		sendThread.start();
	}

}
