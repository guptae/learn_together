package com.guptae.profiler.agent.client;

public class ConnectionRequest extends SimpleRequest{

	private ProfilingParameters params;
	
	@SuppressWarnings("unused")
	private ConnectionRequest()
	{
		
	}

	public ConnectionRequest(String msgType, ProfilingParameters params)
	{
		super(msgType);
		this.params = params;
	}

	/**
	 * @return the params
	 */
	public ProfilingParameters getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(ProfilingParameters params) {
		this.params = params;
	}

}
