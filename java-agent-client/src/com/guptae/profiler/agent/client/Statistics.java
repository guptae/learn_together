package com.guptae.profiler.agent.client;

public class Statistics {

	private long methodTimeTaken;
	private int methodInvocationCount;
	
	public Statistics(int count, long timeTaken)
	{
		this.methodInvocationCount = count;
		this.methodTimeTaken = timeTaken;
	}
	/**
	 * @return the methodTimeTaken
	 */
	public long getMethodTimeTaken() {
		return methodTimeTaken;
	}
	/**
	 * @param methodTimeTaken the methodTimeTaken to set
	 */
	public void setMethodTimeTaken(long methodTimeTaken) {
		this.methodTimeTaken = methodTimeTaken;
	}
	/**
	 * @return the methodInvocationCount
	 */
	public int getMethodInvocationCount() {
		return methodInvocationCount;
	}
	/**
	 * @param methodInvocationCount the methodInvocationCount to set
	 */
	public void setMethodInvocationCount(int methodInvocationCount) {
		this.methodInvocationCount = methodInvocationCount;
	}
	
	
}
