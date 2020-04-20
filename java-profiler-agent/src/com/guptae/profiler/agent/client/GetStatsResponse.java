package com.guptae.profiler.agent.client;

import java.util.HashMap;
import java.util.Map;

import com.guptae.profiler.agent.statistics.Statistics;

public class GetStatsResponse extends SimpleResponse {

	private Map<String, Statistics> methodStatsMap = new HashMap<>();

	public GetStatsResponse(String responseMsg, String result) {
		super(responseMsg, result);
	}

	/**
	 * @return the methodStatsMap
	 */
	public Map<String, Statistics> getMethodStatsMap() {
		return methodStatsMap;
	}

	/**
	 * @param methodStatsMap the methodStatsMap to set
	 */
	public void setMethodStatsMap(Map<String, Statistics> methodStatsMap) {
		this.methodStatsMap = methodStatsMap;
	}
	
}
