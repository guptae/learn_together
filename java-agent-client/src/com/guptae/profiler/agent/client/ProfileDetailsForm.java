package com.guptae.profiler.agent.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ProfileDetailsForm implements ActionListener {
	JFrame detailsFrame = new JFrame("Profiling Details");
	Container container = detailsFrame.getContentPane();
	JLabel labelHeading, labelExclList, labelCount, labelExecTime, labelFrequency;
	JTextField textExclList, textFrequency;
	String ipAddress, portNum;
	JCheckBox checkBoxCount, checkBoxExecTime;
	JButton buttonStart;
	ProfilingParameters connData;
	SimpleResponse response;

	ProfileDetailsForm() {
		this.connData = new ProfilingParameters();
		initializeFrame();

	}

	private void initializeFrame() {
		detailsFrame.setBounds(150, 50, 750, 500);
		detailsFrame.setVisible(true);
		detailsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		container.setLayout(null);
		container.setBackground(Color.LIGHT_GRAY);

		labelHeading = new JLabel("Profiling Details Form");
		labelHeading.setBounds(150, 10, 400, 35);
		labelHeading.setHorizontalAlignment(SwingConstants.CENTER);
		Font font = new Font("Arial", Font.BOLD, 30);
		labelHeading.setFont(font);
		container.add(labelHeading);

		labelExclList = new JLabel("Package Exclusion List (separated by comma)");
		labelExclList.setBounds(50, 80, 400, 30);
		labelExclList.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		container.add(labelExclList);

		textExclList = new JTextField();
		textExclList.setBounds(420, 82, 280, 30);
		container.add(textExclList);

		labelCount = new JLabel("Profile Invocation Count");
		labelCount.setBounds(50, 130, 360, 30);
		labelCount.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		container.add(labelCount);

		checkBoxCount = new JCheckBox();
		checkBoxCount.setBounds(420, 132, 50, 30);
		checkBoxCount.setBackground(Color.LIGHT_GRAY);
		container.add(checkBoxCount);

		labelExecTime = new JLabel("Profile Execution Time Taken");
		labelExecTime.setBounds(50, 180, 360, 30);
		labelExecTime.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		container.add(labelExecTime);

		checkBoxExecTime = new JCheckBox();
		checkBoxExecTime.setBounds(420, 182, 50, 30);
		checkBoxExecTime.setBackground(Color.LIGHT_GRAY);
		container.add(checkBoxExecTime);

		labelFrequency = new JLabel("Frequency of Data Collection (in seconds)");
		labelFrequency.setBounds(50, 230, 360, 30);
		labelFrequency.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		container.add(labelFrequency);

		textFrequency = new JTextField();
		textFrequency.setBounds(420, 232, 50, 30);
		container.add(textFrequency);

		buttonStart = new JButton("Start Profiling");
		buttonStart.setBounds(420, 300, 150, 30);
		buttonStart.addActionListener(this);
		container.add(buttonStart);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Start Profiling".equals(e.getActionCommand())) {
			connData.setPkgExclList(this.textExclList.getText());
			connData.setCountEnabled(this.checkBoxCount.isSelected());
			connData.setExecTimeEnabled(this.checkBoxExecTime.isSelected());
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		SimpleRequest requestInfo = new ConnectionRequest("Start", connData);
		String jsonRequest;
		Socket socket = null;
		try {
			jsonRequest = mapper.writeValueAsString(requestInfo);
			System.out.println(jsonRequest);
			socket = ServerConnection.getSocket();
			if (null != socket) {
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				char type = 's';
				byte[] dataInBytes = jsonRequest.getBytes(StandardCharsets.UTF_8);

				out.writeChar(type);
				out.writeInt(dataInBytes.length);
				out.write(dataInBytes);
			}
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		detailsFrame.dispose();
		ProfilingTableView table = new ProfilingTableView(textFrequency.getText());
		try{
			DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			char dataType = in.readChar();
			int length = in.readInt();
			System.out.println("Length of response sent by Server: " + length);

			if(dataType == 's')
			{
				byte[] messageByte = new byte[length];
				boolean end = false;
				StringBuilder dataString = new StringBuilder(length);
				int totalBytesRead = 0;
				while(!end)
				{
					int currentBytesRead = in.read(messageByte);
					totalBytesRead = totalBytesRead + currentBytesRead;
					if(totalBytesRead <= length)
					{
						dataString.append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
					} else {
						dataString.append(new String(messageByte, 0, length - totalBytesRead, StandardCharsets.UTF_8));
					}
					if(dataString.length() >= length)
					{
						end = true;
					}
				}
				System.out.println("Server response: " + dataString.toString());
				mapper = new ObjectMapper();
				response = mapper.readValue(dataString.toString(), SimpleResponse.class);
			}
			if("startResponse".equalsIgnoreCase(response.getResponseMsg()))
			{
				System.out.println("Response received");
//				table.getProfilingStatistics();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
