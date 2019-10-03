package com.guptae.thread;

import java.util.List;

public class ThreadInfo {
	
	private String threadName;
	private String threadId;
	private boolean isDaemonThread;
	private int threadPriority;
	private int osThreadPriority;
	private String threadAddress;
	private String osThreadId;
	private String threadStatus;
	private String lastStackPointer;
	private String threadState;
	private List<String> stackTrace;

	public ThreadInfo(String threadInfoStr) {
		super();
		this.threadName = threadInfoStr.substring(1, threadInfoStr.lastIndexOf("\""));

		String str = null, subStr = null;
		int beginIndex = 0;

		beginIndex = (threadInfoStr.contains("#")==true) ? threadInfoStr.indexOf("#") : 0;
		if(beginIndex > 0) {
			subStr = threadInfoStr.substring(beginIndex);
			this.threadId = subStr.substring(0, subStr.indexOf(" "));
		}

		this.isDaemonThread = (threadInfoStr.contains("daemon")==true)? true: false;

		str = getPropertyValue(threadInfoStr, " prio=");
		if (str != null) {
			this.threadPriority = Integer.parseInt(str);
		}

		str = getPropertyValue(threadInfoStr, "os_prio=");
		if(str != null) {
			this.osThreadPriority = Integer.parseInt(str);
		}

		str = getPropertyValue(threadInfoStr, "tid=");
		this.threadAddress = str;

		str = getPropertyValue(threadInfoStr, "nid=");
		this.osThreadId = str;

		beginIndex = (threadInfoStr.contains("nid=")==true)? threadInfoStr.indexOf("nid="): 0;
		if(beginIndex > 0)
		{
			subStr = threadInfoStr.substring(beginIndex);
			if(subStr.contains("["))
				str = subStr.substring(subStr.indexOf(" ")+1, subStr.indexOf('['));
			else
				str = subStr.substring(subStr.indexOf(" ")+1);
			this.threadStatus = str;
		}
		beginIndex = (threadInfoStr.contains("[")==true)? threadInfoStr.indexOf('['): 0;
		if(beginIndex > 0)
			this.lastStackPointer = threadInfoStr.substring(beginIndex+1, threadInfoStr.indexOf(']'));

	}

	/**
	 * 
	 * @param threadInfoStr
	 * @param property
	 * @return
	 */
	private String getPropertyValue(String threadInfoStr, String property)
	{
		String str = null;
		int beginIndex = (threadInfoStr.contains(property)==true)? threadInfoStr.indexOf(property): 0;
		if(beginIndex > 0)
		{
			String subStr = threadInfoStr.substring(beginIndex);
			str = subStr.trim().substring(property.trim().length(), subStr.trim().indexOf(" "));
		}
		return str;
	}
	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}
	/**
	 * @param threadName the threadName to set
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	/**
	 * @return the threadId
	 */
	public String getThreadId() {
		return threadId;
	}
	/**
	 * @param threadId the threadId to set
	 */
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	/**
	 * @return the isDaemonThread
	 */
	public boolean isDaemonThread() {
		return isDaemonThread;
	}
	/**
	 * @param isDaemonThread the isDaemonThread to set
	 */
	public void setDaemonThread(boolean isDaemonThread) {
		this.isDaemonThread = isDaemonThread;
	}
	/**
	 * @return the threadPriority
	 */
	public int getThreadPriority() {
		return threadPriority;
	}
	/**
	 * @param threadPriority the threadPriority to set
	 */
	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}
	/**
	 * @return the osThreadPriority
	 */
	public int getOsThreadPriority() {
		return osThreadPriority;
	}
	/**
	 * @param osThreadPriority the osThreadPriority to set
	 */
	public void setOsThreadPriority(int osThreadPriority) {
		this.osThreadPriority = osThreadPriority;
	}
	/**
	 * @return the threadAddress
	 */
	public String getThreadAddress() {
		return threadAddress;
	}
	/**
	 * @param threadAddress the threadAddress to set
	 */
	public void setThreadAddress(String threadAddress) {
		this.threadAddress = threadAddress;
	}
	/**
	 * @return the osThreadId
	 */
	public String getOsThreadId() {
		return osThreadId;
	}
	/**
	 * @param osThreadId the osThreadId to set
	 */
	public void setOsThreadId(String osThreadId) {
		this.osThreadId = osThreadId;
	}
	/**
	 * @return the threadStatus
	 */
	public String getThreadStatus() {
		return threadStatus;
	}
	/**
	 * @param threadStatus the threadStatus to set
	 */
	public void setThreadStatus(String threadStatus) {
		this.threadStatus = threadStatus;
	}
	/**
	 * @return the lastStackPointer
	 */
	public String getLastStackPointer() {
		return lastStackPointer;
	}
	/**
	 * @param lastStackPointer the lastStackPointer to set
	 */
	public void setLastStackPointer(String lastStackPointer) {
		this.lastStackPointer = lastStackPointer;
	}
	/**
	 * @return the threadState
	 */
	public String getThreadState() {
		return threadState;
	}
	/**
	 * @param threadState the threadState to set
	 */
	public void setThreadState(String threadStateString) {
		String stateClass = "java.lang.Thread.State: ";
		if(threadStateString.contains(stateClass))
			this.threadState = threadStateString.trim().substring(stateClass.length()).trim();
	}
	/**
	 * @return the stackTrace
	 */
	public List<String> getStackTrace() {
		return stackTrace;
	}
	/**
	 * @param stackTrace the stackTrace to set
	 */
	public void setStackTrace(List<String> stackTrace) {
		this.stackTrace = stackTrace;
	}

}
