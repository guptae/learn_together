package com.guptae.profiler.agent.client;

public class SimpleResponse {

	String responseMsg;
	String result;
	public SimpleResponse(String responseMsg, String result) {
		super();
		this.responseMsg = responseMsg;
		this.result = result;
	}
	@SuppressWarnings("unused")
	private SimpleResponse()
	{
		
	}
	/**
	 * @return the responseMsg
	 */
	public String getResponseMsg() {
		return responseMsg;
	}
	/**
	 * @param responseMsg the responseMsg to set
	 */
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	
	
}
