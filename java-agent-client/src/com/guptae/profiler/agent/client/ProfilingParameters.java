package com.guptae.profiler.agent.client;

public class ProfilingParameters {

	private String pkgExclList;
	private boolean countEnabled;
	private boolean execTimeEnabled;

	/**
	 * @return the pkgExclList
	 */
	public String getPkgExclList() {
		return pkgExclList;
	}
	/**
	 * @param pkgExclList the pkgExclList to set
	 */
	public void setPkgExclList(String pkgExclList) {
		this.pkgExclList = pkgExclList;
	}
	/**
	 * @return the countEnabled
	 */
	public boolean isCountEnabled() {
		return countEnabled;
	}
	/**
	 * @param countEnabled the countEnabled to set
	 */
	public void setCountEnabled(boolean countEnabled) {
		this.countEnabled = countEnabled;
	}
	/**
	 * @return the execTimeEnabled
	 */
	public boolean isExecTimeEnabled() {
		return execTimeEnabled;
	}
	/**
	 * @param execTimeEnabled the execTimeEnabled to set
	 */
	public void setExecTimeEnabled(boolean execTimeEnabled) {
		this.execTimeEnabled = execTimeEnabled;
	}	
}
