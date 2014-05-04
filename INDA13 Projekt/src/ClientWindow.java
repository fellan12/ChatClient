import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientWindow extends JFrame {

	private JPanel panel;
	private String name;
	private String ip;
	private int port;
	private JMenuBar menuBar;
	private JTextField txtMessage;
	private JTextArea textHistory;

	//UDP - components
	private DatagramSocket socket;
	private InetAddress inet_ip;

	private Thread sendThread;
	/**
	 * Constructor for ClientWindow
	 */
	public ClientWindow(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		boolean connect = openConnection(ip, port);
		if(!connect){
			System.err.println("Connection failed!");
		}else{
			define();
			printToScreen(name + " is connected on " + ip + ":" + port);
		}
	}

	/**
	 * Try to connect to the server
	 * 
	 * @param ip - Ip-address to the server
	 * @param port - Port to the server
	 * @return true/false - if the connection worked
	 */
	private boolean openConnection(String ip, int port){
		try {
			socket = new DatagramSocket(port);
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
			socket.receive(packet);								//Acts like a while-loop
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String message = new String(packet.getData());
		return message;
	}
	
	/**
	 * Send messages to the server.
	 */
	private void send(final byte[] data){					//final because of anonymous class.
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

	/**
	 * Defines the content in ClientWindow
	 * 
	 * All its content is defines and creates here
	 */
	private void define(){
		//Panel
		panel = new JPanel();
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);

		//Frame Settings
		setTitle("Chatroom");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 800);
		setResizable(true);	

		//Layout -GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]{40, 910, 40, 10};						//sum = 1000
		layout.rowHeights = new int[]{75, 660, 55};								//sum = 800
		layout.columnWeights = new double[]{1.0, 1.0, 0.0};
		layout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(layout);

		//On Screen History - TextArea/JScrollPane
		textHistory = new JTextArea();
		textHistory.setFont(new Font("Arial", Font.PLAIN, 18));
		textHistory.setEditable(false);
		JScrollPane scroll = new JScrollPane(textHistory);
		GridBagConstraints scrollConstrains = new GridBagConstraints();
		scrollConstrains.insets = new Insets(0, 12, 5, 5);
		scrollConstrains.fill = GridBagConstraints.BOTH;
		scrollConstrains.gridx = 0;
		scrollConstrains.gridy = 0;
		scrollConstrains.gridwidth = 3;
		scrollConstrains.gridheight = 2;
		panel.add(scroll, scrollConstrains);

		//Message Field - TextField
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {										//KeyListener for clicking ENTER
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					sendMessage(txtMessage.getText());
				}
			}
		});
		GridBagConstraints txtMessageConstrains = new GridBagConstraints();
		txtMessageConstrains.fill = GridBagConstraints.HORIZONTAL;
		txtMessageConstrains.insets = new Insets(0, 0, 0, 5);
		txtMessageConstrains.gridx = 0;
		txtMessageConstrains.gridy = 2;
		txtMessageConstrains.gridwidth = 2;
		panel.add(txtMessage, txtMessageConstrains);
		txtMessage.setColumns(10);
		txtMessage.requestFocusInWindow();												//Sets focus on the message input

		//Send - Button
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {								//ActionListener for clicking button
				sendMessage(txtMessage.getText());
				txtMessage.requestFocusInWindow();
			}
		});
		GridBagConstraints sendButtonConstrains = new GridBagConstraints();
		sendButtonConstrains.insets = new Insets(0, 0, 0, 5);
		sendButtonConstrains.gridx = 2;
		sendButtonConstrains.gridy = 2;
		panel.add(sendButton, sendButtonConstrains);

		//Menubar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//Menu - File
		JMenu fileMenu = new JMenu("File");
		fileMenu.setIconTextGap(8);
		menuBar.add(fileMenu);

		//MenuItem - Open
		JMenuItem menuItemOpen = new JMenuItem("Open");
		fileMenu.add(menuItemOpen);

		//MenuItem - Exit
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(menuItemExit);
	}

	/**
	 * Print the message to the text history
	 * 
	 * @param message
	 */
	public void printToScreen(String message){
		textHistory.append(message + "\n");
		textHistory.setCaretPosition(textHistory.getDocument().getLength());					//Sets the caret at the botton
	}

	/**
	 * Send message
	 * @param message
	 */
	public void sendMessage(String message){
		if(message.length() > 0 ){
			printToScreen(name + ": " + message);
			txtMessage.setText("");
		}
	}
}

//TODO Smilesar
//TODO Scrollbar
//TODO Logged in activity

