package com.gong.white.httpclient.proxy.vo;

public class HttpProxy {

	protected String IP = null;
	
	protected Integer port = -1;
	
	private long activeTime = 0;

	/**
	 * set next active time of this proxy
	 * @param s
	 */
	public void setNextActiveTimeInterval(long s){
		this.activeTime = System.currentTimeMillis() + s * 1000;
	}
	
	public boolean isActive(){
		if( System.currentTimeMillis() >= this.activeTime )
			return true;
		else 
			return false;
	}
	
	/**
	 * getters & setters
	 * @return
	 */
	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	
}
