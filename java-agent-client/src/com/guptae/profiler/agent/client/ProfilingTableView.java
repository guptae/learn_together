package com.guptae.profiler.agent.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ProfilingTableView implements ActionListener, AgentClientListener {

	JFrame frame = new JFrame("Profiling Statistics");
	JScrollPane sp;
	private JTable table;
	JLabel labelHeading, labelError;
	protected JButton buttonReset, buttonHome;
	StatisticsCollector collector;
	protected DefaultTableModel model = new DefaultTableModel();

	public ProfilingTableView(String frequency) {
		initializeFrame();
		collector = new StatisticsCollector(this, ServerConnection.getSocket(), Integer.parseInt(frequency));
		System.out.println("Calling getStatistics !");
		collector.getStatistics();
	}

	private void initializeFrame() {
		frame.setBounds(150, 50, 950, 400);
		frame.setVisible(true);
		BorderLayout layout = new BorderLayout();
		layout.setHgap(10);
		layout.setVgap(10);

		frame.setLayout(layout);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		labelHeading = new JLabel("Collected Statistics");
		labelHeading.setBounds(150, 10, 400, 35);
		labelHeading.setHorizontalAlignment(SwingConstants.CENTER);
		Font font = new Font("Arial", Font.BOLD, 30);
		labelHeading.setFont(font);
		frame.add(labelHeading, BorderLayout.NORTH);

		labelError = new JLabel("Error while connecting to server. Please click on Home button to reconnect.");
		labelError.setBounds(150, 60, 400, 35);
		labelError.setHorizontalAlignment(SwingConstants.CENTER);
		font = new Font("Arial", Font.PLAIN, 18);
		labelError.setFont(font);
		labelError.setForeground(Color.RED);
		labelError.setVisible(false);

		table = new JTable(model);
		model.addColumn("Method Name");
		model.addColumn("Invocation count");
		model.addColumn("Execution Time taken(in milliseconds)");

		table.setBounds(180, 100, 400, 250);
		sp = new JScrollPane(table);
		sp.setAlignmentX(10);
		sp.setAlignmentY(10);
		sp.setBounds(160, 120, 450, 300);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		frame.add(sp, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		FlowLayout buttonLayout = new FlowLayout();
		buttonLayout.setAlignment(FlowLayout.TRAILING);
		panel.setLayout(buttonLayout);
		panel.add(labelError);
		buttonHome = new JButton("Home");
		buttonHome.setBounds(150, 425, 150, 40);
		buttonHome.setVisible(false);
		panel.add(buttonHome);

		buttonReset = new JButton("Clear Stats");
		buttonReset.setBounds(300, 425, 150, 40);
		buttonReset.addActionListener(this);
		panel.add(buttonReset);

		frame.add(panel, BorderLayout.SOUTH);
	}

	private static void sortTableData(Object[][] tableData)
	{
		Arrays.sort(tableData, new Comparator<Object[]>() {

			@Override
			public int compare(Object[] o1, Object[] o2) {
				Long obj1 = Long.parseLong((String) o1[2]);
				Long obj2 = Long.parseLong((String) o2[2]);
				return obj2.compareTo(obj1);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String action = e.getActionCommand();
		System.out.println("Button pressed: " + action);
		if ("Clear Stats" == action) {
			collector.clearStatistics();
			System.out.println("Sending clear stats event");
		} else if("Home" == action)
		{
			frame.dispose();
			new ConnectionForm();
		}
	}

	@Override
	public void connectionError() {
		labelError.setVisible(true);
		buttonHome.setVisible(true);
		buttonHome.addActionListener(this);
		buttonReset.setEnabled(false);
	}

	@Override
	public void updateStatistics(Object[][] tableData) {
		sortTableData(tableData);
		model.setRowCount(0);
		for(int i=0; i<tableData.length; i++)
		{
			Object[] rowData = tableData[i];
			model.addRow(rowData);
		}		
	}
}
