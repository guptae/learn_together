package com.guptae.profiler.agent.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		@SuppressWarnings("resource")
		Socket socket = new Socket("localhost", 3579);
		PrintWriter pw = new PrintWriter(socket.getOutputStream());

		InputStreamReader in = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(in);
		String str = null;
		for(int i=0; i<5; i++)
		{
			System.out.println("Sleeping for 5 seconds");
			Thread.sleep(5000L);
			System.out.println("Getting statistics");
			pw.println("GetStats");
			pw.flush();
			str = br.readLine();
			System.out.println("Server: " + str);
		}
		System.out.println("Clearing statistics");
		pw.println("clearStats");
		pw.flush();
		str = br.readLine();
		System.out.println("Server: " + str);
		
		for(int i=0; i<5; i++)
		{
			System.out.println("Sleeping for 5 seconds");
			Thread.sleep(5000L);
			System.out.println("Getting statistics");
			pw.println("GetStats");
			pw.flush();
			str = br.readLine();
			System.out.println("Server: " + str);
		}
		
	}

}
