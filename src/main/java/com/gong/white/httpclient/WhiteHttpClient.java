package com.gong.white.httpclient;

import java.util.Map;

import org.apache.log4j.Logger;

import com.gong.white.httpclient.config.Configs;
import com.gong.white.httpclient.proxy.pool.ProxyPool;
import com.gong.white.httpclient.proxy.scheduler.ProxyScheduler;
import com.gong.white.httpclient.proxy.util.HttpUtil;
import com.gong.white.httpclient.proxy.vo.HttpProxy;

public class WhiteHttpClient {

	private static Logger logger = Logger.getLogger(WhiteHttpClient.class);  
	
	protected HttpProxy curProxy = null;

	/**
	 * init proxies
	 */
	public void init(){
		
		logger.info("start init proxy pool ...");
		ProxyScheduler.getInstance().start();
	
	}
	
	/**
	 * do get
	 * @param url
	 * @param headers
	 * @return
	 */
	public String doGet(String url, Map<String,String> headers) {
		
		logger.info("doGet: " + url);
		
		if( this.curProxy == null ){
			this.curProxy = ProxyPool.getInstance().pop();
		}
		
		logger.info("proxy: " + this.curProxy.getIP() + ":" + this.curProxy.getPort());
		
		try{
			String page = HttpUtil.sendGet(url, headers, this.curProxy);
			return page;
		}catch(Exception e){
			logger.error(e.getMessage());
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
		
		logger.info("doPost: " + url);
		
		if( this.curProxy == null ){
			this.curProxy = ProxyPool.getInstance().pop();
		}
		
		logger.info("proxy: " + this.curProxy.getIP() + ":" + this.curProxy.getPort());
		
		try{
			String page = HttpUtil.sendPost(url, headers, postBody, this.curProxy);
			return page;
		}catch(Exception e){
			logger.error(e.getMessage());
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
		
		logger.info("change proxy to: " + this.curProxy.getIP() + ":" + this.curProxy.getPort());
		
	}
	
	/**
	 * add external source class
	 */
	public boolean addExternalSourceClass(String className){
		
		logger.info("add external proxy source : " + className);
		ProxyScheduler.getInstance().addExternalSource(className);
		
		return true;
	}
	
	public boolean skipInternalSources(boolean isSkip){
		
		logger.info("set to skip internal proxy sources");
		ProxyScheduler.getInstance().setSkipInternalSource(isSkip);
		return true;
	}
	
	/**
	 * set global configs
	 * @param key
	 * @param value
	 */
	public void setConfig(String key, Object value){
		
		try{
			if( "MAX_PROXYPOOL_SIZE".equals(key) )
				Configs.MAX_PROXYPOOL_SIZE = (Integer)value;
			else if( "UPDATE_PROXY_TIMEINTERVAL".equals(key) )
				Configs.UPDATE_PROXY_TIMEINTERVAL = (Integer)value;
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * sample : how to use WhiteHttpClient
	 * @param args
	 */
	public static void main(String[] args) {
		
		WhiteHttpClient c = new WhiteHttpClient();
		c.init();
		int count = 0;
		while( true ){
			System.out.println(count++);
			String page = c.doGet("http://www.baidu.com", null);
			if( page == null || "".equals(page) ){
				c.nextProxy(-1);
				continue;
			} else{
				System.out.println(page);
				System.out.println("good hit");
				//break;
			}
		}
	}
	
	
}
