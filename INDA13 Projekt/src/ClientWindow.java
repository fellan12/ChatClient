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
		setSize(1000, 800);
		setResizable(true);					//True if screen objects obey resizing.
		
		//Layout
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{40, 940, 20};		//sum = 1000
		gbl_panel.rowHeights = new int[]{75, 640, 75};			//sum = 800
		gbl_panel.columnWeights = new double[]{1.0, 1.0, 0.0};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JTextArea textHistory = new JTextArea();
		GridBagConstraints gbc_textHistory = new GridBagConstraints();
		gbc_textHistory.insets = new Insets(0, 0, 5, 5);
		gbc_textHistory.fill = GridBagConstraints.BOTH;
		gbc_textHistory.gridx = 1;
		gbc_textHistory.gridy = 1;
		panel.add(textHistory, gbc_textHistory);
		
		
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
	}
}

//TODO Smilesar
//TODO Scrollbar
//TODO Logged in activity

