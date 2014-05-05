package Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client class that handles the logics for the ClinetWindow
 * 
 * @author Felix De Silva
 */
public class Client {
	private Socket socket;
	private InetAddress inet_ip;

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;	

	private Thread sendThread, recieveThread;
	private int port;
	
	private boolean running = false;

	public Client(int port){
		this.port = port;
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
		running = true;
		return true;
	}

	/**
	 * receives messeges from the server.
	 * 
	 * @return message - the recevied message.
	 * @throws IOException 
	 */
	public void receive() throws IOException{
		inputStream = new ObjectInputStream(socket.getInputStream());
		final ClientWindow window = new ClientWindow(); 
		recieveThread = new Thread("Receive Thread"){
			public void run(){
				while(running){
					try {
						window.receive((String)inputStream.readObject());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
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
	public void send(final byte[] data) throws IOException{					//final because of anonymous class.
		sendThread = new Thread("Send Thread"){
			public void run(){
				//DO STUFF
			}
		};
		sendThread.start();
	}

}
