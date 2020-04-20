package com.guptae.profiler.agent.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.guptae.profiler.agent.statistics.StatsCollector;


public class ClientHandler extends Thread {

	Socket clientSocket;
	private SimpleRequest request;
	PrintWriter pw;
	
	public ClientHandler(Socket cs)
	{
		this.clientSocket = cs;
		System.out.println("Creating a new thread");
	}

	public void run()
	{
		try{
			DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

			while(true) {
				char dataType = in.readChar();
				int length = in.readInt();
				System.out.println("Length of data sent by Client in ClientHandler: " + length);
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
					System.out.println("Client: " + dataString.toString());
					ObjectMapper mapper = new ObjectMapper();
					request = mapper.readValue(dataString.toString(), SimpleRequest.class);
				}
				if(null != request && "GetStats".equalsIgnoreCase(request.getMessageType()))
				{
					System.out.println("Received request to get statistics");
					sendResponse("GetStats");
				} else if(null != request && "clearStats".equalsIgnoreCase(request.getMessageType()))
				{
					System.out.println("Received request to clear statistics");
					sendResponse("clearStats");
				}
/*				if (str.equals("GetStats")) {
					String response = StatsHolder.getStats();
					System.out.println("Sending response: " + response);
					pw.println(response);
					pw.flush();
				} else if (str.equals("clearStats")) {
					StatsHolder.clearStats();
					System.out.println("Returning success");
					pw.println("Success");
					pw.flush();
				}*/
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void sendResponse(String reqMsg) {
		if("GetStats".equalsIgnoreCase(reqMsg))
		{
			GetStatsResponse response = new GetStatsResponse("getStatsResponse", reqMsg);
			response.setMethodStatsMap(StatsCollector.getStats());
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			String jsonRequest;
			try {
				jsonRequest = mapper.writeValueAsString(response);
				System.out.println(jsonRequest);
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
				char type = 's';
				byte[] dataInBytes = jsonRequest.getBytes(StandardCharsets.UTF_8);

				out.writeChar(type);
				out.writeInt(dataInBytes.length);
				out.write(dataInBytes);
			}
			catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if("clearStats".equalsIgnoreCase(reqMsg))
		{
			SimpleResponse response = new SimpleResponse("clearStatsResponse", reqMsg);
			StatsCollector.clearStats();
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			String jsonRequest;
			try {
				jsonRequest = mapper.writeValueAsString(response);
				System.out.println(jsonRequest);
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
				char type = 's';
				byte[] dataInBytes = jsonRequest.getBytes(StandardCharsets.UTF_8);

				out.writeChar(type);
				out.writeInt(dataInBytes.length);
				out.write(dataInBytes);
			}
			catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
