package com.guptae.profiler.agent.client;

public class SimpleRequest {

	protected String messageType;

	@SuppressWarnings("unused")
	private SimpleRequest() {
		
	}

	public SimpleRequest(String msgType) {
		super();
		this.messageType = msgType;
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