package Client;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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
	private JTextArea textConveration;

	private Client client;
	private JTextArea onlineList;

	/**
	 * Constructor for ClientWindow
	 */
	public ClientWindow(final Client client){
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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				client.disconnect();
			}
		});
		setSize(1000, 800);
		setResizable(true);	
		setLocationRelativeTo(null);

		//Layout -GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]{45, 805, 150};						//sum = 1000
		layout.rowHeights = new int[]{75, 660, 55};								//sum = 800
		panel.setLayout(layout);

		//On Screen Conversation - TextArea/JScrollPane
		textConveration = new JTextArea();
		textConveration.setFont(new Font("Arial", Font.PLAIN, 18));
		textConveration.setEditable(false);
		JScrollPane scroll = new JScrollPane(textConveration);
		GridBagConstraints scrollConstrains = new GridBagConstraints();
		scrollConstrains.insets = new Insets(0, 0, 5, 5);
		scrollConstrains.fill = GridBagConstraints.BOTH;
		scrollConstrains.gridx = 0;
		scrollConstrains.gridy = 0;
		scrollConstrains.gridwidth = 2;
		scrollConstrains.gridheight = 2;
		scrollConstrains.weightx = 1;
		scrollConstrains.weighty = 1;
		panel.add(scroll, scrollConstrains);

		//On Screen OnlineList - TextArea/JScrollPane
		onlineList = new JTextArea();
		onlineList.setFont(new Font("Arial", Font.PLAIN, 18));
		onlineList.setEditable(false);
		JScrollPane scrollOnlie = new JScrollPane(onlineList);
		GridBagConstraints scrollOnlineConstrains = new GridBagConstraints();
		scrollOnlineConstrains.insets = new Insets(0, 0, 5, 0);
		scrollOnlineConstrains.fill = GridBagConstraints.BOTH;
		scrollOnlineConstrains.gridx = 2;
		scrollOnlineConstrains.gridy = 0;
		scrollOnlineConstrains.gridwidth = 2;
		scrollOnlineConstrains.gridheight = 2;
		scrollOnlineConstrains.weightx = 0;
		scrollOnlineConstrains.weighty = 0;
		panel.add(scrollOnlie, scrollOnlineConstrains);

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
		txtMessageConstrains.fill = GridBagConstraints.BOTH;
		txtMessageConstrains.insets = new Insets(0, 0, 0, 5);
		txtMessageConstrains.gridx = 0;
		txtMessageConstrains.gridy = 2;
		txtMessageConstrains.gridwidth = 2;
		txtMessageConstrains.weightx = 1;
		txtMessageConstrains.weighty = 0;
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
		sendButtonConstrains.fill = GridBagConstraints.BOTH;
		sendButtonConstrains.gridx = 2;
		sendButtonConstrains.gridy = 2;
		sendButtonConstrains.gridwidth = 2;
		sendButtonConstrains.weightx = 0;
		sendButtonConstrains.weighty = 0;
		panel.add(sendButton, sendButtonConstrains);

		//Menubar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//Menu - File
		JMenu fileMenu = new JMenu("File");
		fileMenu.setIconTextGap(8);
		menuBar.add(fileMenu);

		//MenuItem - Open
		JMenuItem menuItemOpen = new JMenuItem("Save");
		menuItemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		fileMenu.add(menuItemOpen);

		//MenuItem - Exit
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.disconnect();
				dispose();
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
		textConveration.append(message + "\n");
		textConveration.setCaretPosition(textConveration.getDocument().getLength());					//Sets the caret at the botton
	}

	/**
	 * Update OnlineUserList
	 */
	public void updateOnlineUserList(ArrayList<String> users){
		StringBuilder list = new StringBuilder();
		for(String name : users){
				list.append(name + "\n");
		}
		
		onlineList.setText(list.toString());
	}


	/**
	 * send to the server
	 * 
	 * @param message
	 */
	public void sendMessage(String message){
		if(message.length() > 0){
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

	/**
	 * Saves the current textConversation to a txt-file on the same location as the application.
	 */
	public void save(){
		try {
			DateFormat dateformat = new SimpleDateFormat("HH-mm-ss_dd-MM-yy");
			Date currentDate = new Date();

			BufferedWriter saveFile = new BufferedWriter(new FileWriter("Saved_Conversation_ " + dateformat.format(currentDate) + ".txt"));
			saveFile.write(textConveration.getText());
			saveFile.close();
			JOptionPane.showMessageDialog(null, "Saved Conversation");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

//TODO Smilesar
//TODO Logged in activity

