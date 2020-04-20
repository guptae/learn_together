package com.guptae.profiler.agent.statistics;

public class Statistics {

	private long methodTimeTaken;
	private int methodInvocationCount;

	public Statistics() {

	}

	public Statistics(int methodInvocationCount) {
		super();
		this.methodInvocationCount = methodInvocationCount;
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
