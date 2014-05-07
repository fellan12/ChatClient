package Client;
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

/**
 * ClientWindow class that handles the Screen
 * that all the messages print
 * 
 * @author Felix De Silva
 */
public class ClientWindow extends JFrame implements ClientWindowInterface {

	private JPanel panel;
	private String name, ip;
	private int port;
	private JMenuBar menuBar;
	private JTextField txtMessage;
	private JTextArea textHistory;

	private Client client;

	//UDP - components
	private DatagramSocket socket;
	private InetAddress inet_ip;

	private Thread sendThread;

	/**
	 * Constructor 1 for ClientWindow
	 */
	public ClientWindow(){}

	/**
	 * Constructor 2 for ClientWindow
	 */
	public ClientWindow(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;

		client = new Client(ip, port);										//Create a Client Object

		boolean connect = client.isConnectionOpen();					//Connect to server
		if(!connect){
			System.err.println("Connection failed!");
		}else{
			define();
			client.receive();
		}
	}

	/**
	 * Defines the content in ClientWindow
	 * 
	 * All its content is defines and creates here
	 */
	public void define(){
		//Panel
		panel = new JPanel();
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);

		//Frame Settings
		setTitle("InstaChat");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 800);
		setResizable(true);	
		setLocationRelativeTo(null);

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
	 * Print the message to the screen
	 * 
	 * @param message
	 */
	private void printToScreen(String message){
		textHistory.append(message + "\n");
		textHistory.setCaretPosition(textHistory.getDocument().getLength());					//Sets the caret at the botton
	}

	/**
	 * send to the server
	 * 
	 * @param message
	 */
	public void sendMessage(String message){
		if(message.length() > 0 ){
			String text = name + ": " + message;
			client.send(text);
			txtMessage.setText("");
		}
	}

	/**
	 * Recieve a message from server
	 * @param output
	 */
	public void receive(String output){
		printToScreen(output);
	}
}

//TODO Smilesar
//TODO Logged in activity

