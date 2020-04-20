package com.guptae.profiler.agent.client;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class StatsCollector {

	private String methodName;
	private Statistics statistics;
	public static final Map<String, Statistics> methodStatsMap = new HashMap<>();
	static {
		methodStatsMap.put("method 1", new Statistics(1, 3));
		methodStatsMap.put("method 2", new Statistics(2, 4));
		methodStatsMap.put("method 3", new Statistics(1, 2));
	}

	public static void main(String[] args)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		String jsonRequest;
		try {
			jsonRequest = mapper.writeValueAsString(methodStatsMap);
			System.out.println(jsonRequest);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
}
