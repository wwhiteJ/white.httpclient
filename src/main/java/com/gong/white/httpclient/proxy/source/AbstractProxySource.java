package com.gong.white.httpclient.proxy.source;

import java.util.List;

import com.gong.white.httpclient.proxy.pool.ProxyPool;
import com.gong.white.httpclient.proxy.vo.HttpProxy;

public abstract class AbstractProxySource implements Runnable{

	// abstract class
	public abstract List<HttpProxy> getMoreProxies();
	
	
	/**
	 * run
	 */
	public void run(){
		List<HttpProxy> proxies = this.getMoreProxies();
		ProxyPool.getInstance().push(proxies);
	}
}
