package com.gong.white.httpclient.proxy.source;

import java.util.List;

import org.apache.log4j.Logger;

import com.gong.white.httpclient.proxy.pool.ProxyPool;
import com.gong.white.httpclient.proxy.vo.HttpProxy;

public abstract class AbstractProxySource implements Runnable{

	protected String sourceUrl = null;
	
	private static Logger logger = Logger.getLogger(AbstractProxySource.class);
	
	// abstract class
	public  abstract List<HttpProxy> getMoreProxies();
	
	
	/**
	 * run
	 */
	public void run(){
		List<HttpProxy> proxies = this.getMoreProxies();
		ProxyPool.getInstance().push(proxies);
	}

	/**
	 * getters & setters
	 */
	public String getSourceUrl() {
		return sourceUrl;
	}


	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
	
	
}
