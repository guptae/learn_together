package com.guptae.profiler.agent;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.guptae.profiler.agent.client.ClientHandler;
import com.guptae.profiler.agent.client.ConnectionRequest;
import com.guptae.profiler.agent.client.SimpleResponse;

public class InstrumentationAgent {
	
	private static ConnectionRequest connData = null;
	private static String[] excludes;
	private static boolean isInvocationCountEnabled;
	private static boolean isTimeTakenEnabled;
	private static Socket clientSocket;

	@SuppressWarnings("resource")
	public static void premain(String agentArgs, Instrumentation inst) throws IOException
	{
		System.out.println("[Agent] in premain method");
		System.out.println("Is RedefineClasses Supported: " + inst.isRedefineClassesSupported());
		System.out.println("Is RetransformClasses Supported: " + inst.isRetransformClassesSupported());
		ServerSocket ss = new ServerSocket(3579);
		clientSocket = ss.accept();
		System.out.println("Connection accepted");
		
		DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
		char dataType = in.readChar();
		int length = in.readInt();
		System.out.println("Length of data sent by Client: " + length);

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
			connData = mapper.readValue(dataString.toString(), ConnectionRequest.class);
		}
		if(null != connData && "start".equalsIgnoreCase(connData.getMessageType()))
		{
			excludes = connData.getParams().getPkgExclList().replace(" ", "").split(",");
			isInvocationCountEnabled = connData.getParams().isCountEnabled();
			isTimeTakenEnabled = connData.getParams().isExecTimeEnabled();
			System.out.println("Parameters received");
			sendResponse("SUCCESS"); //TODO: create a method to send response - responseMsg = "Profiling Started", result = "SUCCESS"
		}
		ClientHandler ch = new ClientHandler(clientSocket);
		ch.start();
		
		inst.addTransformer(new ClassTransformer(excludes, isInvocationCountEnabled, isTimeTakenEnabled), true);
	}

	private static void sendResponse(String result) {
		if("SUCCESS".equals(result))
		{
			SimpleResponse response = new SimpleResponse("startResponse", result);
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

	public static void agentmain(String agentArgs, Instrumentation inst)
	{
		System.out.println("[Agent] in agentmain method");
		System.out.println("Is RedefineClasses Supported: " + inst.isRedefineClassesSupported());
		System.out.println("Is RetransformClasses Supported: " + inst.isRetransformClassesSupported());
		inst.addTransformer(new ClassTransformer(), true);
	}
}