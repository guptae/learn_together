package com.guptae.profiler.agent.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class StatisticsTableView implements ActionListener {
	JFrame connFrame = new JFrame("Statistics");
	Container container = connFrame.getContentPane();
	JLabel labelHeading;
	JTable table;

	// headers for the table
	String[] columns;

	private static final String SNO_HEADER = "S.No.";
	private static final String METHOD_NAME_HEADER = "Method Name";
	private static final String INVOCATION_COUNT_HEADER = "Invocation Count";
	private static final String EXECUTION_TIME_HEADER = "Execution Time Taken";

	JButton buttonReset;
	ProfilingParameters connData;

	StatisticsTableView(ProfilingParameters connectionData) {
		this.connData = connectionData;
		// connectAgent(connectionData);
		initializeFrame();

	}

	private void initializeFrame() {
		connFrame.setBounds(150, 50, 850, 800);
		connFrame.setVisible(true);
		// connFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		container.setLayout(new BorderLayout());
		container.setBackground(Color.LIGHT_GRAY);

		labelHeading = new JLabel("Collected Statistics");
		labelHeading.setBounds(150, 10, 400, 35);
		labelHeading.setHorizontalAlignment(SwingConstants.CENTER);
		Font font = new Font("Arial", Font.BOLD, 30);
		labelHeading.setFont(font);
		container.add(labelHeading);

		if (connData.isCountEnabled() && connData.isExecTimeEnabled()) {
			columns = new String[] { SNO_HEADER, METHOD_NAME_HEADER, INVOCATION_COUNT_HEADER, EXECUTION_TIME_HEADER };
		} else if (connData.isCountEnabled() && !connData.isExecTimeEnabled()) {
			columns = new String[] { SNO_HEADER, METHOD_NAME_HEADER, INVOCATION_COUNT_HEADER };
		} else if (!connData.isCountEnabled() && connData.isExecTimeEnabled()) {
			columns = new String[] { SNO_HEADER, METHOD_NAME_HEADER, EXECUTION_TIME_HEADER };
		} else {
			
		}
		if(null != columns)
		{
			table = new JTable(6, columns.length);

			table.setGridColor(Color.BLACK);
			table.setShowGrid(true);
			table.setVisible(true);
			connFrame.add(table, BorderLayout.CENTER);
			connFrame.setVisible(true);
//			Object[][] data = new Object()[][];
		}

		buttonReset = new JButton("Clear Statistics");
		buttonReset.setBounds(150, 500, 200, 30);
		buttonReset.addActionListener(this);
		container.add(buttonReset);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("Reset" == action) {
			System.out.println("Button clicked: " + action);
			/*textIP.setText(null);
			textPort.setText(null);*/
		}

	}
}