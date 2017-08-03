package com.gong.white.httpclient.proxy.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.gong.white.httpclient.config.Configs;
import com.gong.white.httpclient.proxy.source.AbstractProxySource;
import com.gong.white.httpclient.proxy.util.FileUtil;

public class ProxyScheduler {

	private static Logger logger = Logger.getLogger(ProxyScheduler.class);  
	
	protected ExecutorService proxySourceThreadPool = Executors.newCachedThreadPool();
	
	protected List<String> proxySourceClassNames = new ArrayList<String>();
	
	protected List<AbstractProxySource> proxySourcesObject = new ArrayList<AbstractProxySource>();
	
	protected boolean isRunning = false;
	
	protected boolean isSkipInternalSource = false;
	
	public static ProxyScheduler me = null;
	
	public static ProxyScheduler getInstance(){
		if( ProxyScheduler.me == null )
			ProxyScheduler.me = new ProxyScheduler();
		
		return ProxyScheduler.me;
	}
	
	/**
	 * start the timer
	 */
	public void start(){
		
		if( this.isRunning )
			return;
		
		logger.info("launch proxy source scheduler ... ");
		
		// load proxysource class names from config file
		addInternalProxySources();
		
		// timer thread
		new Thread(){
		
			public void run(){
				
				isRunning = true;
				while( true ){
					
					logger.info("start fetching proxies ...");
					try{
						runProxySources();
						Thread.sleep(Configs.UPDATE_PROXY_TIMEINTERVAL * 1000);
					}catch(Exception e){
						logger.error(e.getMessage());
					}
				}
				
			}
		}.start();
		
	}
	
	protected void runProxySources(){
		
		if( this.proxySourcesObject == null )
			this.loadProxySources();
		
		if( this.proxySourcesObject == null )
			return;
		
		for( int i=0; i<this.proxySourcesObject.size(); ++i ){
			try{
				logger.info("start fetching proxies from " + this.proxySourcesObject.get(i).getClass().getName());
				this.proxySourceThreadPool.execute(this.proxySourcesObject.get(i));
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}
		
	}
	
	protected void loadProxySources(){
		
		logger.info("start loading proxy source objects ... ");
		if( this.proxySourceClassNames == null )
			return;
		
		for( int i=0; i<this.proxySourceClassNames.size(); ++i ){
			try{
				logger.info("loading proxy source : " + this.proxySourceClassNames.get(i));
				Class c=Class.forName(this.proxySourceClassNames.get(i));
				AbstractProxySource curPorxySource = (AbstractProxySource)c.newInstance();
				this.proxySourcesObject.add(curPorxySource);
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}
	}
	
	protected void addInternalProxySources(){
		
		try{
			if( !this.isSkipInternalSource ){
				String path = Thread.currentThread().getContextClassLoader().getResource("proxysource.conf").getPath();
				this.proxySourceClassNames.addAll(FileUtil.readStringListFromFile(path));
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	public void addExternalSource(String className){
		
		if( className != null ){
			this.proxySourceClassNames.add(className);
		}
	}

	public boolean isSkipInternalSource() {
		return isSkipInternalSource;
	}

	public void setSkipInternalSource(boolean isSkipInternalSource) {
		this.isSkipInternalSource = isSkipInternalSource;
	}
	
	
}
