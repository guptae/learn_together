package com.guptae.profiler.agent.client;

public class SimpleRequest {

	String messageType;

	public SimpleRequest() {

	}

	public SimpleRequest(String messageType) {
		super();
		this.messageType = messageType;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
}
