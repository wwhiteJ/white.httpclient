package com.gong.white.httpclient.proxy.pool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.gong.white.httpclient.proxy.vo.HttpProxy;


public class ProxyPool {

	protected BlockingQueue<HttpProxy> proxies = new LinkedBlockingQueue<HttpProxy>();
	protected Set<String> ipset = new HashSet<String>();
	
	/**
	 * make it singleton
	 */
	protected static ProxyPool me = null;
	
	private ProxyPool(){
	}
	
	public static ProxyPool getInstance(){
		if( ProxyPool.me == null )
			ProxyPool.me = new ProxyPool();
		return ProxyPool.me;
	}
	
	/**
	 * pop a proxy from head, if it's not active, push it back to the tail and try next one
	 * @return
	 */
	public HttpProxy pop(){
		
		while( proxies.isEmpty() == false ){
			try {
				HttpProxy res = this.proxies.take();
				
				if( res == null )
					continue;
				
				if( res.isActive() ){
					String resIp = res.getIP() + ":" + res.getPort();
					if( resIp != null )
						this.ipset.remove(resIp);
					return res;
				} else {
					this.proxies.offer(res);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * push a proxy to the tail
	 * @param proxy
	 */
	public void push(HttpProxy proxy){
		
		if( proxy != null && proxy.getIP() != null && proxy.getPort() != null ){
			
			String pushIp = proxy.getIP()+":"+proxy.getPort();
			if( this.ipset.contains(pushIp) ){
				// already exists
			} else{
				this.proxies.offer(proxy);
				this.ipset.add(pushIp);
			}
		}
	}
	
	/**
	 * push batch to the tail
	 * @param proxyList
	 */
	public void push(List<HttpProxy> proxyList){
		
		if( proxyList == null )
			return;
		
		for( HttpProxy p : proxyList ){
			this.push(p);
		}
	}

	public int curPoolSize(){
		
		return this.proxies.size();
	}
}
