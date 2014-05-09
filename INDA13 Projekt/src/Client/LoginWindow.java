package Client;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class LoginWindow extends JFrame {
	private JPanel panel;
	private JTextField textIp;
	private JTextField textName;
	private JTextField textPort;

	private Client client;

	/**
	 * Constructor for LoginWindow
	 */
	public LoginWindow() {
		define();
	}

	/**
	 * Defines the contents in the LoginWindow.
	 * 
	 * All its content is defines and creates here
	 */
	private void define(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(750, 300, getWidth(), getHeight());
		setResizable(false);
		setTitle("InstaChat");
		setSize(366, 531);

		//Panel
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5,5,5,5));
		panel.setLayout(null);
		setContentPane(panel);

		//IP-Address  - TextField
		textIp = new JTextField();
		textIp.setBounds(90, 145, 180, 32);
		panel.add(textIp);
		textIp.setColumns(10);

		//Screen Name  - TextField
		textName = new JTextField();
		textName.setBounds(90, 70, 180, 32);
		textName.setColumns(10);
		panel.add(textName);

		//Port - TextField
		textPort = new JTextField();
		textPort.setBounds(90, 220, 180, 32);
		textPort.setColumns(10);
		panel.add(textPort);

		//Name - Label
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(161, 50, 38, 16);
		panel.add(lblName);

		//IP-Adress - Label
		JLabel lblIpadress = new JLabel("IP-Adress:");
		lblIpadress.setBounds(150, 125, 61, 16);
		panel.add(lblIpadress);

		//Port - Label
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(166, 200, 28, 16);
		panel.add(lblPort);

		//Login - Button
		JButton btnNewButton = new JButton("Login");
		btnNewButton.setBounds(130, 347, 100, 30);
		btnNewButton.addActionListener(new ActionListener() {		//ActionListener for Clicking the button
			public void actionPerformed(ActionEvent arg0) {
				client = new Client(textName.getText(), textIp.getText(), Integer.parseInt(textPort.getText()));
				if(LoginCheck(client)){
					client.receive();
				}else{
					client = null;
				}
			}
		});
		panel.add(btnNewButton);
	}

	/**
	 * Login Procedure
	 * 
	 * Checks that all the inputs are correct
	 */
	public boolean LoginCheck(Client client){
		boolean connect = false;
		if(!textName.getText().equals("") && !textIp.getText().equals("") && !textPort.getText().equals("")){
			if(client.verifyConnection(textName.getText())){
				if(client.isConnectionOpen()){
					connect = true;
				}
			}else{
				JOptionPane.showMessageDialog(null, "Username is unavailable");
			}
		}
		return connect;
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
