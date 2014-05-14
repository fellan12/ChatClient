package Server;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * This class provides a GUI for setting up and running
 * a server as part of a client-server chat system.
 * 
 * @author Richard Sjöberg
 * @version 2014-05-13
 */
@SuppressWarnings("serial")
public class ServerWindow extends JFrame {
	JPanel panel; // The contents of this window.
	JTextField port; // The text field in which the user enters the port she wishes to start the server on.
	JLabel running; // Shows whether the server is running in the panel.
	
	private static Server server; // The server for which the GUI is made.
	private static CountDownLatch setUp;
		
	/**
	 * Creates and shows a new GUI for a server.
	 */
	public ServerWindow() {
		setUp = new CountDownLatch(1);
		define();
		setVisible(true);
	}
	
	/**
	 * Defines the content of the server window.
	 */
	private void define() {
		// TODO: What happens if this is removed?
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(750, 300, getWidth(), getHeight());
		setResizable(false);
		setTitle("ChatServer"); // TODO: Change?
		setSize(366, 531);
		
		// Panel
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5,5,5,5));
		panel.setLayout(null);
		setContentPane(panel);
		
		// Port - TextField
		port = new JTextField("");
		port.setBounds(90, 145, 180, 32); 
		panel.add(port);
		port.setColumns(10);
		
		// Port - Label
		JLabel lblIpadress = new JLabel("Port:"); 
		lblIpadress.setBounds(165, 125, 61, 16);  
		panel.add(lblIpadress);
		
		// Running - Label
		running = new JLabel("Server not running.");
		running.setBounds(133, 175, 200, 50);
		panel.add(running);
		
		// Start server - Button
		JButton start = new JButton("Start server"); 
		start.setBounds(130, 250, 100, 30); 
		start.addActionListener(
				// ActionListener for clicking the button.
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						// Only one server can run at a time.
						if (server != null) { // Server already running.
							JOptionPane.showMessageDialog(null, "You have already started a server. "
									+ "In order to start a new server you must first stop the existing one.");	
						} else { 
							startServer(port.getText());	
						}
					}
				});
		panel.add(start);
		
		// Shut down server - Button
		JButton stop = new JButton("Shut down"); 
		stop.setBounds(130, 300, 100, 30); 
		stop.addActionListener(
				// ActionListener for clicking the button.
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if (server != null) {
							running.setText("Server not running.");
							running.setBounds(133, 175, 200, 50);
							setUp = new CountDownLatch(1);
							server.shutDown();
							server = null;
						} else {
							JOptionPane.showMessageDialog(null, "There is no server running.");
						}
					}
				});
		panel.add(stop);
	}
	
	/**
	 * Try to set up a server on the given port. Check the port number 
	 * to see if it's valid and available. If it is, set up a new server 
	 * on the port. If it isn't, notify the user. 
	 * 
	 * @param port The input entered as a string in the port text field by the user.
	 */
	private void startServer(String port) {
		int portNumber;
		try {
			portNumber = Integer.parseInt(port);
			
			if (portNumber > 0 && portNumber <= 65535) { // Valid port numbers.
				server = new Server(portNumber); 
				if (server != null) {
					setUp.countDown(); // Server is set up.
					running.setText("Server running on port " + portNumber);
					running.setBounds(115, 175, 200, 50);
				}
			} else { // Invalid port number.
				JOptionPane.showMessageDialog(null, "Please enter a valid port number.");
			}
		} catch (NumberFormatException e) {
			// String could not be converted to integer.
			JOptionPane.showMessageDialog(null, "Please enter a valid port number.");
		} catch (IOException e) { 
			// Attempt to set up server failed. Port already in use.
			JOptionPane.showMessageDialog(null, "Port unavailable.");			
		}
	}
	
	/**
	 * Creates a new server window. 
	 * 
	 * Whenever the user enters a valid port number, that's not already in use,
	 * a new server is set up. While the server is running it listens for and
	 * accepts connection requests from clients on the given port. The clients
	 * are then connected through a chat room, where they can communicate with
	 * each other. 
	 * 
	 * @param args Ignore.
	 */
	public static void main(String[] args) {
		new ServerWindow();
		
		while (true) {
			// Wait until the server has been set up.
			try {
				setUp.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (server != null) { // Server was successfully set up.
				server.acceptRequest(); 
			}
			
			System.out.println("not running");
		}
	}
}

