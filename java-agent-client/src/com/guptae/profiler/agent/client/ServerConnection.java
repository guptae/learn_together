package com.guptae.profiler.agent.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

class ConnectionForm implements ActionListener {
	protected JFrame connFrame = new JFrame("Server Connection");
	protected Container container = connFrame.getContentPane();
	protected JLabel labelHeading, labelIP, labelPort, labelError;
	protected JTextField textIP, textPort;
	protected JButton buttonConnect, buttonReset;

	ConnectionForm()
	{
		initializeFrame();
		
	}

	private void initializeFrame() {
		connFrame.setBounds(150, 50, 750, 500);
		connFrame.setVisible(true);
		connFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		container.setLayout(null);
		container.setBackground(Color.LIGHT_GRAY);
		
		labelHeading = new JLabel("Server Connection Form");
		labelHeading.setBounds(150, 10, 400, 35);
		labelHeading.setHorizontalAlignment(SwingConstants.CENTER);
		Font font = new Font("Arial", Font.BOLD, 30);
		labelHeading.setFont(font);
		container.add(labelHeading);

		labelIP = new JLabel("Server IP Address");
		labelIP.setBounds(50, 80, 160, 30);
		labelIP.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		container.add(labelIP);
		
		textIP = new JTextField();
		textIP.setBounds(220, 82, 180, 30);
		container.add(textIP);
		
		labelPort = new JLabel("Server Port");
		labelPort.setBounds(50, 130, 160, 30);
		labelPort.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		container.add(labelPort);
		
		textPort = new JTextField();
		textPort.setBounds(220, 132, 180, 30);
		container.add(textPort);

		labelError = new JLabel("Could not connect to the specified server and port! Please check the details again.");
		labelError.setBounds(50, 180, 500, 30);
		labelError.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		labelError.setForeground(Color.RED);
		labelError.setVisible(false);
		container.add(labelError);
		
		buttonReset = new JButton("Reset");
		buttonReset.setBounds(150, 220, 100, 30);
		buttonReset.addActionListener(this);
		container.add(buttonReset);

		buttonConnect = new JButton("Connect");
		buttonConnect.setBounds(270, 220, 100, 30);
		buttonConnect.addActionListener(this);
		container.add(buttonConnect);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		System.out.println("Button pressed: " + action);
		if("Connect".equals(action))
		{
			System.out.println("IP: " + textIP.getText() + "\n" + "Port: " + textPort.getText());
//			if ((null != textIP.getText() || textIP.getText().isEmpty()) && (null != textPort.getText() ||textPort.getText().isEmpty()))
//			{
			boolean isConnected = ServerConnection.connectServer(textIP.getText(), textPort.getText());
			if(isConnected) {
			connFrame.dispose();
				/*ProfileDetailsForm detailsForm =*/ new ProfileDetailsForm();
//			} else {
//				JLabel warningLabel = new JLabel("Please enter server IP and port number");
//				warningLabel.setBounds(150, 180, 150, 30);
//				container.add(warningLabel);
//			}
			} else{
				labelError.setVisible(true);
			}
		} else if("Reset" == action)
		{
			textIP.setText(null);
			textPort.setText(null);
		}

	}
}
public class ServerConnection {

	private static Socket socket;

	/**
	 * @return the socket
	 */
	public static Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket the socket to set
	 */
	public static void setSocket(Socket socket) {
		ServerConnection.socket = socket;
	}

	public static void main(String[] args) {
		new ConnectionForm();
	}

	protected static boolean connectServer(String server, String port) {
		if(server.isEmpty() || port.isEmpty())
		{
			System.out.println("Either server or port detail is missing!");
			return false;
		}
		try {
			socket = new Socket(server, new Integer(port).intValue());
			return true;
		} catch (NumberFormatException | IOException e) {
			System.out.println("Could not connect to the specified server and port");
			e.printStackTrace();
			return false;
		}
		
	}
}
