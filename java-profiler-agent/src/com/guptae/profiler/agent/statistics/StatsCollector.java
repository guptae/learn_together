package com.guptae.profiler.agent.statistics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatsCollector {

	public static final Map<String, Statistics> methodStatsMap = new ConcurrentHashMap<>();

	public static void incrementInvocationCount(String methodIdentifier)
	{
		Statistics stats = StatsCollector.methodStatsMap.get(methodIdentifier);
		if(null == stats)
		{
			stats = new Statistics();
			stats.setMethodInvocationCount(1);
		} else {
			stats.setMethodInvocationCount(stats.getMethodInvocationCount() + 1);
		}
		StatsCollector.methodStatsMap.put(methodIdentifier, stats);
	}
	
	public static void addExecutionTime(String methodIdentifier, long timeTaken)
	{
		Statistics stats = StatsCollector.methodStatsMap.get(methodIdentifier);
		System.out.println("Method name: " + methodIdentifier + "and time taken: " + timeTaken);
		if(null == stats)
		{
			stats = new Statistics(1);
			stats.setMethodTimeTaken(timeTaken);
		} else {
			stats.setMethodTimeTaken(stats.getMethodTimeTaken() + timeTaken);
		}
		methodStatsMap.put(methodIdentifier, stats);
		
	}

	public static Map<String, Statistics> getStats()
	{
		System.out.println("Sending statistics: ");
		/*StringBuilder response = null;
		for(Entry<String, Statistics> entry : StatsCollector.methodStatsMap.entrySet())
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
//			response.append(",").append(methodCumilativeTimeMap.get(entry.getKey()));
        }
		
		return response==null ? "": response.toString();*/
		return methodStatsMap;
	}
	
	public static void clearStats()
	{
		System.out.println("Clearing statistics: ");
		methodStatsMap.clear();
	}
}
