import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Window.Type;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;


public class LoginWindow2 extends JFrame {

	private JPanel panel;
	private JTextField textIp;
	private JTextField textName;
	private JTextField textPort;
	private boolean error = false;
	
	/**
	 * Create the application.
	 */
	public LoginWindow2() {
		setResizable(false);	
		setTitle("Login");		//Windom name
		define();
	}

	/**
	 * Defines the contents of the frame.
	 */
	private void define(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 300, 355, 530);				//TODO set center though resolution
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5,5,5,5));
		setContentPane(panel);
		panel.setLayout(null);
		
		//IP-Adress  - TextField
		textIp = new JTextField();
		textIp.setBounds(84, 145, 180, 32);
		panel.add(textIp);
		textIp.setColumns(10);
		
		//Screen Name  - TextField
		textName = new JTextField();
		textName.setColumns(10);
		textName.setBounds(84, 70, 180, 32);
		panel.add(textName);
		
		//Port - TextField
		textPort = new JTextField();
		textPort.setColumns(10);
		textPort.setBounds(84, 220, 180, 32);
		panel.add(textPort);
		
		//Name - Label
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(155, 50, 38, 16);
		panel.add(lblName);
		
		//IP-Adress - Label
		JLabel lblIpadress = new JLabel("IP-Adress:");
		lblIpadress.setBounds(144, 125, 61, 16);
		panel.add(lblIpadress);
		
		//Port - Label
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(155, 200, 38, 16);
		panel.add(lblPort);
		
		if(error == true){
			//Error - Label
			JLabel lblError = new JLabel("Check that alla the inpus are correct");
			lblError.setBounds(124, 400, 38, 16);
			panel.add(lblError);
		}
		
		//Login - Button
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {		//ActionListener for Clicking the button
			public void actionPerformed(ActionEvent arg0) {
				if(!textName.getText().isEmpty() && !textIp.getText().isEmpty() && !textPort.getText().isEmpty()){
					System.out.println("Name: " + textName.getText());
					System.out.println("IP: " + textIp.getText());
					System.out.println("Port: " + textPort.getText());
					String name = textName.getText();
					String ip = textIp.getText();
					int port = Integer.parseInt(textPort.getText());
					login(name, ip, port);
					System.out.println("Logged in");
				}else{
					error = true;
					System.out.println("Error");
					System.out.println("Name: " + textName.getText());
					System.out.println("IP: " + textIp.getText());
					System.out.println("Port: " + textPort.getText());
				}
			}
		});
		btnNewButton.setBounds(124, 347, 100, 30);
		panel.add(btnNewButton);
		
	}
	
	/**
	 * Login sequence
	 */
	public void login(String name, String ip, int port){
		//TODO
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow window = new LoginWindow();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
