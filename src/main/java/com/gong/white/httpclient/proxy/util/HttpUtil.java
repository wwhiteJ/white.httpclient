package com.gong.white.httpclient.proxy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.gong.white.httpclient.proxy.vo.HttpProxy;

/*
 * ref: http://www.cnblogs.com/zhuawang/archive/2012/12/08/2809380.html
 */
public class HttpUtil {

	/**
	 * get request
	 * @param url
	 * @param headers
	 * @param myProxy
	 * @return
	 */
	public static String sendGet(String url, Map<String,String> headers, HttpProxy myProxy) {
		
		String result = "";
		
		BufferedReader in = null;
		
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			
			SslUtil.ignoreSsl();
			
			URLConnection connection = null;
			
			// proxy
			if( myProxy != null && myProxy.getIP() != null && myProxy.getPort() != null ){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(myProxy.getIP(), myProxy.getPort()));
				connection = realUrl.openConnection(proxy);
			} else
				connection = realUrl.openConnection();
			
			// headers
			if( headers != null ){
				for( String key : headers.keySet() ){
					connection.setRequestProperty(key, headers.get(key));
				}
			} else{
				connection.setRequestProperty("Connection", "keep-alive");  
				connection.setRequestProperty("Cache-Control", "no-cache");
			}
			
			// connect
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			
		} catch (Exception e) {
			
			result = HttpUtil.errorMsg(e);			
		}
		
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * post request
	 * @param url
	 * @param headers
	 * @param postData
	 * @param myProxy
	 * @return
	 */
	public static String sendPost(String url, Map<String,String> headers, String postData, HttpProxy myProxy) {
		
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			
			SslUtil.ignoreSsl();
			
			URLConnection connection = null;
			
			// proxy
			if( myProxy != null && myProxy.getIP() != null && myProxy.getPort() != null ){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(myProxy.getIP(), myProxy.getPort()));
				connection = realUrl.openConnection(proxy);
			} else
				connection = realUrl.openConnection();
			
			// headers
			if( headers != null ){
				for( String key : headers.keySet() ){
					connection.setRequestProperty(key, headers.get(key));
				}
			} else{
				connection.setRequestProperty("Connection", "keep-alive");  
				connection.setRequestProperty("Cache-Control", "no-cache");
			}
			
			// post data
			connection.setDoOutput(true);
			connection.setDoInput(true);
			out = new PrintWriter(connection.getOutputStream());
			out.print(postData);
			out.flush();
			
			// get out put
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			
			
		} catch (Exception e) {
			result = HttpUtil.errorMsg(e);
		}
		
		
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * error
	 * @param e
	 * @return
	 */
	public static String ERROR_UNKNOWN = "unknown erro";
	public static String ERROR_CONNECTIONREFUSED = "request refused";
	public static String ERROR_TIMEOUT = "request timeout";
	
	protected static String errorMsg(Exception e){
		
		if( e == null )
			return null;
		
		String msg = e.getMessage();
		String res = ERROR_UNKNOWN;
		if( msg.contains("Connection refused"))
			res = HttpUtil.ERROR_CONNECTIONREFUSED;
		else if( msg.contains("Timeout") )
			res = HttpUtil.ERROR_TIMEOUT;
		
		return res;
	}
}
