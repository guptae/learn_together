package com.guptae.profiler.agent.client;

public interface AgentClientListener {

	void connectionError();
	void updateStatistics(Object[][] tableData);
}
