package Client;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
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
public class ClientWindow extends JFrame{

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
		define();																	//Create the window
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
		setTitle(client.getName() + " - InstaChat");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				client.disconnect();
			}
		});
		setSize(1000, 800);
		setResizable(true);	
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(270,187));
		addComponentListener(new ComponentAdapter(){								//Set the minimum size for the window
			public void componentResized(ComponentEvent e){
				Dimension d= getSize();
				Dimension minD=getMinimumSize();
				if(d.width<minD.width)
					d.width=minD.width;
				if(d.height<minD.height)
					d.height=minD.height;
				setSize(d);
			}
		});


		//Layout -GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]{45, 805, 148, 2};							//sum = 1000
		layout.rowHeights = new int[]{75, 695, 30};									//sum = 800
		panel.setLayout(layout);


		//On Screen Conversation - TextArea/JScrollPane
		textConveration = new JTextArea();
		textConveration.setFont(new Font("Arial", Font.PLAIN, 18));
		textConveration.setEditable(false);
		textConveration.setLineWrap(true);
		textConveration.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(textConveration);
		GridBagConstraints scrollConstrains = new GridBagConstraints();
		scrollConstrains.insets = new Insets(0, 5, 5, 5);
		scrollConstrains.fill = GridBagConstraints.BOTH;
		scrollConstrains.gridx = 0;
		scrollConstrains.gridy = 0;
		scrollConstrains.gridwidth = 2;
		scrollConstrains.gridheight = 2;
		scrollConstrains.weightx = 0;
		scrollConstrains.weighty = 1;
		panel.add(scroll, scrollConstrains);

		//On Screen OnlineList - TextArea/JScrollPane
		onlineList = new JTextArea();
		onlineList.setFont(new Font("Arial", Font.PLAIN, 18));
		onlineList.setEditable(false);
		onlineList.setMinimumSize(new Dimension(100,30));
		JScrollPane scrollOnlie = new JScrollPane(onlineList);
		GridBagConstraints scrollOnlineConstrains = new GridBagConstraints();
		scrollOnlineConstrains.insets = new Insets(0, 0, 5, 5);
		scrollOnlineConstrains.fill = GridBagConstraints.BOTH;
		scrollOnlineConstrains.gridx = 2;
		scrollOnlineConstrains.gridy = 0;
		scrollOnlineConstrains.gridwidth = 1;
		scrollOnlineConstrains.gridheight = 2;
		scrollOnlineConstrains.weightx = 0.1;
		scrollOnlineConstrains.weighty = 0;
		panel.add(scrollOnlie, scrollOnlineConstrains);

		//Message Field - TextField
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {									//KeyListener for clicking ENTER
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
		txtMessageConstrains.weightx = 1;
		txtMessageConstrains.weighty = 0;
		panel.add(txtMessage, txtMessageConstrains);
		txtMessage.setColumns(10);
		txtMessage.requestFocusInWindow();											//Sets focus on the message input

		//Send - Button
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {							//ActionListener for clicking button
				sendMessage(txtMessage.getText());
				txtMessage.requestFocusInWindow();
			}
		});
		GridBagConstraints sendButtonConstrains = new GridBagConstraints();
		sendButtonConstrains.fill = GridBagConstraints.BOTH;
		sendButtonConstrains.gridx = 2;
		sendButtonConstrains.gridy = 2;
		sendButtonConstrains.gridwidth = 1;
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

		txtMessage.requestFocusInWindow();
	}

	/**
	 * Print the message to the screen
	 * 
	 * @param message
	 */
	public void printToScreen(String message){
		textConveration.append(message + "\n");
		textConveration.setCaretPosition(textConveration.getDocument().getLength());//Sets the caret at the botton
	}

	/**
	 * Update OnlineUserList
	 * 
	 * Builds a string with alla the users online and
	 * put it on the onlineList
	 */
	public void updateOnlineUserList(ArrayList<String> users){
		StringBuilder list = new StringBuilder();
		for(String name : users){													//Iterates though the user-list
			list.append(name + "\n");										 		//Append it to a StringBuilder
		}

		onlineList.setText(list.toString());										//Set onlineList to StringBuilder-list
	}


	/**
	 * send to the server
	 * 
	 * @param message
	 */
	public void sendMessage(String message){
		if(!message.equals("")){													//Checks that the message isnt empty
			String text = name + ": " + message;									//Adds the name of the send to the message
			client.send(text);														//Send the message though the client
			txtMessage.setText("");													//Set message textfield to empty-string.
		}
	}

	/**
	 * Receive a message from client and print it to the screen
	 * @param output
	 */
	public void receive(String fromServer){
		printToScreen(fromServer);													//Print to screen
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
