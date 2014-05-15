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
 * ClientWindow is the class that handles the window for which the user
 * uses to send and recieve messages
 * 
 * ClientWindow itself is a JFrame
 * 
 * @author Felix De Silva
 * @date 15 maj 2014
 */
@SuppressWarnings("serial")
public class ClientWindow extends JFrame{
	//Swing - related
	private JPanel panel;						//Panel for the window
	private JMenuBar menuBar;					//MenuBar for the window
	private JTextField txtMessage;				//TextFiled for the window
	private JTextArea textConversation;			//Conversation area for the window
	private JTextArea onlineList;				//Online user area for the window

	//String
	private String name;						//Name of the user

	//Client
	private Client client;						//Client object

	/**
	 * Constructor for ClientWindow
	 * 
	 * @param client - The backend for this class
	 */
	public ClientWindow(final Client client){
		this.client = client;
		this.name = client.getName();
		define();																		//Create the window
	}

	/**
	 * Defines the content in ClientWindow
	 * 
	 * All its content is defines and creates here
	 * including all the actionlistening is defines here
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
		addWindowListener(new WindowAdapter() {											//Operations for closing the window
			public void windowClosing(WindowEvent arg0) {
				client.disconnect();
			}
		});
		setSize(1000, 800);
		setResizable(true);	
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(270,187));
		addComponentListener(new ComponentAdapter(){									//Set the minimum size for the window
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
		layout.columnWidths = new int[]{45, 805, 148, 2};								//sum = 1000
		layout.rowHeights = new int[]{75, 695, 30};										//sum = 800
		panel.setLayout(layout);


		//On Screen Conversation - TextArea/JScrollPane
		textConversation = new JTextArea();
		textConversation.addMouseListener(new MouseAdapter() {							//MouseListener for clicking the conversation area						
			public void mouseClicked(MouseEvent arg0) {			
				txtMessage.requestFocusInWindow();										//Sets focus on the message input
			}
		});
		textConversation.setFont(new Font("Arial", Font.PLAIN, 18));
		textConversation.setEditable(false);
		textConversation.setLineWrap(true);
		textConversation.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(textConversation);
		GridBagConstraints scrollConstrains = new GridBagConstraints();
		scrollConstrains.insets = new Insets(0, 0, 5, 5);
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
		onlineList.addMouseListener(new MouseAdapter() {								//MouseListener for clicking the online user area
			public void mouseClicked(MouseEvent e) {
				txtMessage.requestFocusInWindow();										//Sets focus on the message input
			}
		});
		onlineList.setFont(new Font("Arial", Font.PLAIN, 18));
		onlineList.setEditable(false);
		onlineList.setMinimumSize(new Dimension(100,30));
		JScrollPane scrollOnlie = new JScrollPane(onlineList);
		GridBagConstraints scrollOnlineConstrains = new GridBagConstraints();
		scrollOnlineConstrains.insets = new Insets(0, 0, 5, 0);
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
		txtMessage.addKeyListener(new KeyAdapter() {									//ActionListener for clicking ENTER
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER && !txtMessage.getText().equals("")){
					sendMessage(txtMessage.getText());									//Send the message
				}
			}
		});
		txtMessage.setFont(new Font("Arial", Font.PLAIN, 18));
		txtMessage.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
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

		//Send - Button
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {								//ActionListener for clicking button
				sendMessage(txtMessage.getText());										//Send the message
				txtMessage.requestFocusInWindow();										//Sets the fokus to the messageFiled
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
		menuItemOpen.addActionListener(new ActionListener() {							//ActionListener for Save
			public void actionPerformed(ActionEvent arg0) {
				save();																	//Saves the conversation
			}
		});
		fileMenu.add(menuItemOpen);

		//MenuItem - Exit
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {							//ActionListener for Exit
			public void actionPerformed(ActionEvent arg0) {	
				client.disconnect();													//Disconnect from the server
				dispose();																//Dispose of the window
				System.exit(0);															//Exit the application
			}
		});
		fileMenu.add(menuItemExit);

		txtMessage.requestFocusInWindow();
	}

	/**
	 * Print the message to the screen
	 * and sets the caret to the bottom of the textConversation
	 * 
	 * @param message
	 */
	public void printToScreen(String message){
		textConversation.append(message + "\n");	 									//Print the message to the screen
		textConversation.setCaretPosition(textConversation.getDocument().getLength());	//Sets the caret at the botton
	}

	/**
	 * Update OnlineUserList
	 * 
	 * Builds a string with alla the users online and
	 * put it on the onlineList
	 * 
	 * @param users - The list of online users
	 */
	public void updateOnlineUserList(ArrayList<String> users){
		StringBuilder list = new StringBuilder();
		for(String name : users){														//Iterates though the user-list
			list.append(name + "\n");											 		//Append it to a StringBuilder
		}
		onlineList.setText(list.toString());											//Set onlineList to StringBuilder-list

	}


	/**
	 * send to the server
	 * 
	 * @param message - the message the user wants to send
	 */
	public void sendMessage(String message){
		if(!message.equals("")){														//Checks that the message isnt empty
			String text = name + ": " + message;										//Adds the name of the send to the message
			client.send(text);															//Send the message though the client
			txtMessage.setText("");														//Set message textfield to empty-string.
		}
	}

	/**
	 * Receive a message from client and print it to the screen
	 * @param fromServer - the message from the server
	 */
	public void receive(String fromServer){
		printToScreen(fromServer);														//Print to screen
	}

	/**
	 * Saves the current textConversation to a txt-file on the same location as the application.
	 * Adds the date to the file name
	 */
	public void save(){
		try {
			if(!textConversation.getText().equals("")){
				DateFormat dateformat = new SimpleDateFormat("HH-mm-ss_dd-MM-yy");		//Dataformat
				Date currentDate = new Date();											//Get the current date

				BufferedWriter saveFile = new BufferedWriter(new FileWriter("Saved_Conversation_ " + dateformat.format(currentDate) + ".txt")); //Create a savefile
				saveFile.write(textConversation.getText());								//Write textConvesation to file
				saveFile.close();														//Close the file
				JOptionPane.showMessageDialog(null, "Saved Conversation");
			}else{
				JOptionPane.showMessageDialog(null, "Nothing to save");
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}
}
