package Client;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;


/**
 * ClientWindow class that handles the Screen
 * that all the messages print
 * 
 * @author Felix De Silva
 */
@SuppressWarnings("serial")
public class ClientWindow extends JFrame implements ClientWindowInterface {

	private JPanel panel;
	private String name;
	private JMenuBar menuBar;
	private JTextField txtMessage;
	private JTextArea textConversation;

	private Client client;

	/**
	 * Constructor for ClientWindow
	 */
	public ClientWindow(Client client){
		this.client = client;
		this.name = client.getName();
		define();
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

		//On Screen Conversation - TextArea/JScrollPane
		textConversation = new JTextArea(20, 30);
		textConversation.setFont(new Font("Arial", Font.PLAIN, 18));
		textConversation.setEditable(false);
		JScrollPane scroll = new JScrollPane(textConversation);
		GridBagConstraints scrollConstrains = new GridBagConstraints();
		scrollConstrains.insets = new Insets(0, 12, 5, 100);
		scrollConstrains.fill = GridBagConstraints.BOTH;
		scrollConstrains.gridx = 0;
		scrollConstrains.gridy = 0;
		scrollConstrains.gridwidth = 2;
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
		textConversation.append(message + "\n");
		textConversation.setCaretPosition(textConversation.getDocument().getLength());					//Sets the caret at the botton
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
	 * Receive a message from server
	 * @param output
	 */
	public void receive(String fromServer){
		printToScreen(fromServer);
	}
}

//TODO Smilesar
//TODO Logged in activity

