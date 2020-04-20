package com.guptae.profiler.agent.client;

public class GetStatsResponse extends SimpleResponse {

	private String methodName;
	private String invocationCount;
	private String executionTime;

	public GetStatsResponse(String responseMsg, String result) {
		super(responseMsg, result);
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the invocationCount
	 */
	public String getInvocationCount() {
		return invocationCount;
	}

	/**
	 * @param invocationCount the invocationCount to set
	 */
	public void setInvocationCount(String invocationCount) {
		this.invocationCount = invocationCount;
	}

	/**
	 * @return the executionTime
	 */
	public String getExecutionTime() {
		return executionTime;
	}

	/**
	 * @param executionTime the executionTime to set
	 */
	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	
}
