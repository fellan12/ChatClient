import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Client class that handles the logics for the ClinetWindow
 * 
 * @author Felix De Silva
 */
public class Client {
	private DatagramSocket socket;
	private InetAddress inet_ip;
	
	private Thread sendThread;
	private int port;
	
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
			socket = new DatagramSocket();
			inet_ip = InetAddress.getByName(ip);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * receives messeges from the server.
	 * 
	 * @return message - the recevied message.
	 */
	private String receive(){
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);			//Receiving packet

		try {
			socket.receive(packet);							//Acts like a while-loop
		} catch (IOException e) {
			e.printStackTrace();
		}

		String message = new String(packet.getData());
		return message;
	}

	/**
	 * Send messages to the server.
	 */
	public void send(final byte[] data){					//final because of anonymous class.
		sendThread = new Thread("Send Thread"){
			public void run(){
				DatagramPacket packet = new DatagramPacket(data, data.length, inet_ip, port);			//Sending packet
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}

}
