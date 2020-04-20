package com.guptae.profiler.agent.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StatsHolder {

	public static final Map<String, Integer> methodCountMap = new HashMap<>();
	public static final Map<String, List<Long>> methodTimeMap = new HashMap<>();
	public static final Map<String, Long> methodCumilativeTimeMap = new HashMap<>();

	public static void incrementInvocationCount(String methodIdentifier)
	{
		Integer value = StatsHolder.methodCountMap.get(methodIdentifier);
		if(null == value)
		{
			StatsHolder.methodCountMap.put(methodIdentifier, 1);
		} else {
			StatsHolder.methodCountMap.put(methodIdentifier, value+1);
		}
	}

	public static void addAvgExecutionTime(String methodIdentifier, long timeTaken)
	{
		List<Long> listValue = StatsHolder.methodTimeMap.get(methodIdentifier);
		System.out.println("Method name: " + methodIdentifier + "and time taken: " + timeTaken);
		if(null == listValue)
		{
			listValue = new ArrayList<>();
		}
		listValue.add(timeTaken);
		methodTimeMap.put(methodIdentifier, listValue);
		
	}
	
	public static void addExecutionTime(String methodIdentifier, long timeTaken)
	{
		Long listValue = StatsHolder.methodCumilativeTimeMap.get(methodIdentifier);
		System.out.println("Method name: " + methodIdentifier + "and time taken: " + timeTaken);
		if(null == listValue)
		{
			listValue = 0L;
		}
		listValue = listValue + timeTaken;
		methodCumilativeTimeMap.put(methodIdentifier, listValue);
		
	}
	
	public static void printCurrentStats()
	{
		System.out.println("Printing method and total number of its invocations\n");
		for(Entry<String, Integer> entry : StatsHolder.methodCountMap.entrySet())
        {
        	System.out.println(entry.getKey() + " : " + entry.getValue());
        }
		
		System.out.println("Printing method and average time taken for its execution\n");
		System.out.println("Size of map: " + methodTimeMap.size());
		for(Entry<String, List<Long>> entry : StatsHolder.methodTimeMap.entrySet())
        {
			int size = entry.getValue().size();
			if(size == 1)
				System.out.println(entry.getKey() + " : " + entry.getValue().get(0));
			else if(size > 1)
			{
				long sum = 0;
				for(int i=0; i<size; i++)
				{
					sum = sum + entry.getValue().get(i);
				}
				System.out.println(entry.getKey() + " : " + (sum/size));
			}
        }
	}

	public static String getStats()
	{
		System.out.println("Sending statistics: ");
		StringBuilder response = null;
		for(Entry<String, Integer> entry : StatsHolder.methodCountMap.entrySet())
        {
//        	System.out.println(entry.getKey() + " : " + entry.getValue());
			if(null == response)
			{
				response =new StringBuilder(entry.getKey()).append(",");
			}
			else {
				response.append(";").append(entry.getKey()).append(",");
			}
			response.append(",").append(entry.getValue());
			response.append(",").append(methodCumilativeTimeMap.get(entry.getKey()));
        }
		
		return response==null ? "": response.toString();
	}
	
	public static void clearStats()
	{
		System.out.println("Clearing statistics: ");
		methodCountMap.clear();
		methodCumilativeTimeMap.clear();
		methodTimeMap.clear();
	}
}
