import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class ClientWindow extends JFrame {

	private JPanel panel;
	private String name;
	private String ip;
	private int port;
	private JMenuBar menuBar;

	/**
	 * Constructor for ClientWindow
	 */
	public ClientWindow(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		define();

	}

	/**
	 * Defines the content in ClientWindow
	 * 
	 * All its content is defines and creates here
	 */
	public void define(){
		panel = new JPanel();
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);
		setTitle("Chatroom");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 800);
		setResizable(true);					//True if screen objects obey resizing.


		//Menubar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//Menu - File
		JMenu fileMenu = new JMenu("File");
		fileMenu.setIconTextGap(8);
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
						panel.setLayout(null);
				
						//Message - TextField
						JTextField textField = new JTextField();
						textField.setBounds(12, 684, 840, 30);
						textField.setMinimumSize(new Dimension(3, 22));
						textField.setFont(new Font("Arial", Font.PLAIN, 18));
						panel.add(textField);
						textField.setColumns(10);
						
								//Send - Button
								JButton sendButton = new JButton("Send");
								sendButton.setBounds(864, 685, 106, 30);
								sendButton.setMargin(new Insets(2, 20, 2, 20));
								sendButton.addActionListener(new ActionListener() {		//ActionListener for Clicking the button
									public void actionPerformed(ActionEvent arg0) {
										System.out.println("Sent");
									}
								});
								panel.add(sendButton);
								
								JPanel panel_1 = new JPanel();
								panel_1.setBackground(Color.WHITE);
								panel_1.setForeground(Color.BLACK);
								panel_1.setBounds(12, 13, 958, 658);
								panel.add(panel_1);
								
								JScrollBar scrollBar = new JScrollBar();
								scrollBar.setBounds(961, 13, 21, 659);
								panel.add(scrollBar);
	}
}

//TODO Smilesar
//TODO Scrollbar
//TODO Logged in activity

