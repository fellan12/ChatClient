package Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class Server2 implements Runnable, ServerInterface {

	private int port;

	private DatagramSocket socket;

	private boolean running = false;;

	private Thread run, manage, send, recieve;

	public Server2(int port) {
		this.port = port;

		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		run = new Thread(this, "Server Thread");
		run.start();
	}

	/**
	 * runs the server
	 */
	public void run() {
		running = true;
		System.out.println("Servers on " + port);
		manageClients();
		receive();
	}

	/**
	 * Manage the clients, checks that them are there
	 */
	public void manageClients(){
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
	public void receive(){
		recieve = new Thread("Receive Thread"){
			public void run(){
				while(running){
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String message = new String(packet.getData());
					System.out.println(message);
				}
			}
		};
		recieve.start();
	}

}
