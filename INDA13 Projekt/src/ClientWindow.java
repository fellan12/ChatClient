import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class ClientWindow extends JFrame {

	private JPanel panel;

	/**
	 * Constructor for ClientWindow
	 */
	public ClientWindow() {
		define();
	}
	
	/**
	 * Defines the content in ClientWindow
	 * 
	 * All its content is defines and creates here
	 */
	public void define(){
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(null);
		setTitle("Chatroom");
		
		//Menubar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//Menu - File
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		//MenuItem - Open
		JMenuItem mt_Open = new JMenuItem("Open");
		fileMenu.add(mt_Open);
		
		//MenuItem - Exit
		JMenuItem mt_Exit = new JMenuItem("Exit");
		mt_Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(mt_Exit);
	
		setContentPane(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 800);
		setResizable(true);								//True if screen objects obey resizing.
		
		//Message - TextField
		JTextField textField = new JTextField();
		textField.setFont(new Font("Arial", Font.PLAIN, 15));
		textField.setBounds(7, 691, 859, 30);
		panel.add(textField);
		textField.setColumns(10);
		
		//Send - Button
				JButton sendButton = new JButton("Send");
				sendButton.addActionListener(new ActionListener() {		//ActionListener for Clicking the button
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Sent");
					}
				});
				sendButton.setBounds(873, 691, 100, 30);
				panel.add(sendButton);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientWindow frame = new ClientWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

//TODO Smilesar
//TODO Scrollbar
//TODO Logged in activity

