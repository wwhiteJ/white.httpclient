package com.gong.white.httpclient.proxy.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gong.white.httpclient.proxy.source.AbstractProxySource;
import com.gong.white.httpclient.proxy.util.FileUtil;

public class ProxyScheduler {

	protected ExecutorService proxySourceThreadPool = Executors.newCachedThreadPool();
	
	protected Integer timeInterval = 60; // seconds  
	
	protected List<String> proxySourceClassNames = new ArrayList<String>();
	
	protected boolean isRunning = false;
	
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
		
		// load proxysource class names from config file
		initClassNamesFromConfig();
		
		// timer thread
		new Thread(){
		
			public void run(){
				
				isRunning = true;
				while( true ){
					
					try{
						runProxySources();
						Thread.sleep(timeInterval * 1000);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			}
		}.start();
		
	}
	
	protected void runProxySources(){
		
		if( this.proxySourceClassNames == null )
			return;
		
		for( int i=0; i<this.proxySourceClassNames.size(); ++i ){
			try{
				Class c=Class.forName(this.proxySourceClassNames.get(i));
				AbstractProxySource curPorxySource = (AbstractProxySource)c.newInstance();
				this.proxySourceThreadPool.execute(curPorxySource);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	protected void initClassNamesFromConfig(){
		
		this.proxySourceClassNames = FileUtil.readStringListFromFile("proxysource.conf");
	}
}
