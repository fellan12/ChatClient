import java.net.DatagramSocket;
import java.net.SocketException;


public class Server implements Runnable {
	
	private int port;
	
	private DatagramSocket socket;
	
	private boolean running = false;;
	
	private Thread run, manage, send, recieve;
	
	public Server(int port) {
		this.port = port;
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		run = new Thread(this, "Server Thread");
		
	}

	/**
	 * runs the server
	 */
	public void run() {
		running = true;
		manageClients();
		receive();
	}
	
	/**
	 * Manage the clients, checks that them are there
	 */
	private void manageClients(){
		manage = new Thread("Manage"){
			public void run(){
				while(running){
					//Managing
				}
			}
		};
		manage.start();
	}
	
	/**
	 * Receives messages from clients
	 */
	private void receive(){
		recieve = new Thread("Receive Thread"){
			public void run(){
				while(running){
					//Receiving
				}
			}
		};
	}
	
}
