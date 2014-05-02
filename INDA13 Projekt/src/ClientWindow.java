import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class ClientWindow extends JFrame {

	private JPanel panel;
	private JTextField textField;
	private JButton sendButton;

	/**
	 * Create the frame.
	 */
	public ClientWindow() {
		define();
		//asdaskdnalksjdlkajsdlkjasd
	}

	public void define(){
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(null);
		setTitle("Chatroom");
		setContentPane(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 800);
		setResizable(false);								//True if screen objects obey resizing.
		
		//Message - TextField
		textField = new JTextField();
		textField.setFont(new Font("Arial", Font.PLAIN, 15));
		textField.setBounds(7, 728, 870, 30);
		panel.add(textField);
		textField.setColumns(10);
		
		//Send - Button
				JButton sendButton = new JButton("Send");
				sendButton.addActionListener(new ActionListener() {		//ActionListener for Clicking the button
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Sent");
					}
				});
				sendButton.setBounds(885, 728, 100, 29);
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
