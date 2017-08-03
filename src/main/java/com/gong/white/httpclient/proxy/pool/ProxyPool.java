package com.gong.white.httpclient.proxy.pool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.gong.white.httpclient.config.Configs;
import com.gong.white.httpclient.proxy.vo.HttpProxy;


public class ProxyPool {

	private static Logger logger = Logger.getLogger(ProxyPool.class); 
	
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
		
		while( true ){
			try {
				HttpProxy res = this.proxies.take();
				
				if( res == null ){
					Thread.sleep(1000);
					continue;
				}
				
				if( res.isActive() ){
					String resIp = res.getIP() + ":" + res.getPort();
					if( resIp != null )
						this.ipset.remove(resIp);
					return res;
				} else {
					this.proxies.offer(res);
				}
			} catch (InterruptedException e) {
				
				logger.error(e.getMessage());
			}
		}

	}
	
	/**
	 * push a proxy to the tail
	 * @param proxy
	 */
	public void push(HttpProxy proxy){
		
		try{
			
			if( this.proxies.size() >= Configs.MAX_PROXYPOOL_SIZE ){
				logger.info("pushing proxy refused! proxy pool reaches max size : " + Configs.MAX_PROXYPOOL_SIZE);
				return;
			}
			
			if( proxy != null && proxy.getIP() != null && proxy.getPort() != null ){
				
				String pushIp = proxy.getIP()+":"+proxy.getPort();
				if( this.ipset.contains(pushIp) ){
					// already exists
				} else{
					this.proxies.offer(proxy);
					this.ipset.add(pushIp);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
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
			
			if( this.proxies.size() >= Configs.MAX_PROXYPOOL_SIZE ){
				logger.info("pushing proxy batch refused! proxy pool reaches max size : " + Configs.MAX_PROXYPOOL_SIZE);
				break;
			}
		}
	}

	public int curPoolSize(){
		
		return this.proxies.size();
	}
}
