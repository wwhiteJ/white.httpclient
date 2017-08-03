package com.gong.white.httpclient;

import java.util.Map;

import com.gong.white.httpclient.proxy.pool.ProxyPool;
import com.gong.white.httpclient.proxy.scheduler.ProxyScheduler;
import com.gong.white.httpclient.proxy.vo.HttpProxy;
import com.gong.white.httpclient.proxy.vo.HttpUtil;

public class WhiteHttpClient {

	protected HttpProxy curProxy = null;

	/**
	 * init proxies
	 */
	public void init(){
		
		System.out.println("start init proxy pool ...");
		
		ProxyScheduler.getInstance().start();
	
	}
	
	/**
	 * do get
	 * @param url
	 * @param headers
	 * @return
	 */
	public String doGet(String url, Map<String,String> headers) {
		
		if( this.curProxy == null ){
			this.curProxy = ProxyPool.getInstance().pop();
		}
		
		try{
			String page = HttpUtil.sendGet(url, headers, this.curProxy);
			return page;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * do post
	 * @param url
	 * @param headers
	 * @param postBody
	 * @return
	 */
	public String doPost(String url, Map<String,String> headers, String postBody) {
		
		if( this.curProxy == null ){
			this.curProxy = ProxyPool.getInstance().pop();
		}
		
		try{
			String page = HttpUtil.sendPost(url, headers, postBody, this.curProxy);
			return page;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * update proxy, if timeInterval < 0, the last proxy will be discarded directly. otherwise, it will be added to the tail
	 * @param timeInterval
	 */
	public void nextProxy(int timeInterval){
		
		if( timeInterval > 0 ){
			this.curProxy.setNextActiveTimeInterval(timeInterval);
			ProxyPool.getInstance().push(this.curProxy);
		}
		
		this.curProxy = ProxyPool.getInstance().pop();
		
	}
}
