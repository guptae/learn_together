package com.guptae.profiler.agent.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class StatisticsCollector {

	protected Socket socket;
	protected int frequency;
	protected BlockingQueue<String> queue;
	protected AgentClientListener clientListener;

	public StatisticsCollector(AgentClientListener cl, Socket s, int frequency) {
		this.clientListener = cl;
		this.socket = s;
		this.frequency = frequency;
		queue = new ArrayBlockingQueue<>(10);
	}

	public void getStatistics() {
		StatisticsCollectorHelper helper = new StatisticsCollectorHelper();
		System.out.println("Calling helper.start()");
		helper.start();
	}

	public void clearStatistics() {
		if (null != queue) {
			try {
				queue.put("clearStats");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected class StatisticsCollectorHelper extends Thread {

		public void run() {
			String msg = null;
			while (true) {
				try {
					msg = (String) queue.poll(frequency, TimeUnit.SECONDS);
					System.out.println("Printing msg from queue: " + msg);
					if (null != msg && "clearStats".equalsIgnoreCase(msg)) {
						sendRequest(msg);
					} else {
						sendRequest("GetStats");
					}
					receiveResponse();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			clientListener.connectionError();
		}

		private void sendRequest(String msg) throws IOException {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			SimpleRequest requestInfo = new SimpleRequest(msg);
			String jsonRequest = mapper.writeValueAsString(requestInfo);
			System.out.println(jsonRequest);
			if (null != socket) {
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				char type = 's';
				byte[] dataInBytes = jsonRequest.getBytes(StandardCharsets.UTF_8);
				out.writeChar(type);
				out.writeInt(dataInBytes.length);
				out.write(dataInBytes);
			}
		}

		private void receiveResponse() throws IOException {
			
				DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				char dataType = in.readChar();
				int length = in.readInt();
				System.out.println("Length of response sent by Server: " + length);

				if (dataType == 's') {
					byte[] messageByte = new byte[length];
					boolean end = false;
					StringBuilder dataString = new StringBuilder(length);
					int totalBytesRead = 0;
					while (!end) {
						int currentBytesRead = in.read(messageByte);
						totalBytesRead = totalBytesRead + currentBytesRead;
						if (totalBytesRead <= length) {
							dataString.append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
						} else {
							dataString.append(
									new String(messageByte, 0, length - totalBytesRead, StandardCharsets.UTF_8));
						}
						if (dataString.length() >= length) {
							end = true;
						}
					}
					System.out.println("Server response: " + dataString.toString());
					if (dataString.toString().contains("getStatsResponse")) {
						JsonNode statsNode = new ObjectMapper().readTree(dataString.toString());
						GetStatsResponse response = new GetStatsResponse("getStatsResponse", "SUCCESS");
						Iterator<String> iter = statsNode.get("methodStatsMap").fieldNames();
						Object[][] objArray = new Object[statsNode.get("methodStatsMap").size()][3];
						int index = 0;
						while (iter.hasNext()) {
							String methodName = (String) iter.next();
							String count = statsNode.get("methodStatsMap").get(methodName).get("methodInvocationCount")
									.toString();
							String timeTaken = statsNode.get("methodStatsMap").get(methodName).get("methodTimeTaken")
									.toString();

							response.setMethodName(methodName);
							response.setInvocationCount(count);
							response.setExecutionTime(timeTaken);

							System.out.println(response.getMethodName());
							System.out.println(response.getInvocationCount());
							System.out.println(response.getExecutionTime());

							objArray[index++] = new Object[] { response.getMethodName(), response.getInvocationCount(),
									response.getExecutionTime() };
						}
						clientListener.updateStatistics(objArray);
					}
				}
		}
	}
}