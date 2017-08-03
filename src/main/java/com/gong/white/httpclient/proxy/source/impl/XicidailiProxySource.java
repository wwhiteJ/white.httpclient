package com.gong.white.httpclient.proxy.source.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

import com.gong.white.httpclient.proxy.source.AbstractProxySource;
import com.gong.white.httpclient.proxy.vo.HttpProxy;
import com.gong.white.httpclient.proxy.vo.HttpUtil;


/**
 * ref: http://www.xicidaili.com/nn/1
 * @author sparrow
 *
 */
public class XicidailiProxySource extends AbstractProxySource{

	@Override
	public List<HttpProxy> getMoreProxies() {
		
		List<HttpProxy> res = new ArrayList<HttpProxy>();
		
		String [] urls = {
				"http://www.xicidaili.com/nn/1","http://www.xicidaili.com/nn/2","http://www.xicidaili.com/nn/3",
				"http://www.xicidaili.com/nt/1","http://www.xicidaili.com/nt/2","http://www.xicidaili.com/nt/3",
				"http://www.xicidaili.com/wt/1","http://www.xicidaili.com/wt/2","http://www.xicidaili.com/wt/3"
		};
		
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36");
		
		
		for( String url : urls ){
			try{
				String page = HttpUtil.sendGet(url, headers, null);
				if( page == null )
					continue;
				
				List<HttpProxy> proxies = this.extract(page);
				if( proxies != null )
					res.addAll(proxies);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return res;
	}

	
	protected List<HttpProxy> extract(String page){
		
		List<HttpProxy> res = new ArrayList<HttpProxy>();
		// parse
		try{
			Parser parser = new Parser(page);
			
		   HasAttributeFilter filter = new HasAttributeFilter("id","ip_list");
           NodeList nodes = parser.parse(filter); 
	
           TableTag tableNode = null;
           if( nodes != null && nodes.size() > 0 )
        	   tableNode = (TableTag)nodes.elementAt(0);
           
           TableRow[] rows = tableNode.getRows();
           
           for( int i=1; i<rows.length; ++i ){
        	   try{
        		   TableRow tr = rows[i];
        		   TableColumn[] td = tr.getColumns();
        		   String curIp = td[1].toPlainTextString();
					String curPort = td[2].toPlainTextString();
					if( curIp != null && curPort!=null ){
						HttpProxy newProxy = new HttpProxy();
						newProxy.setIP(curIp);
						newProxy.setPort(Integer.valueOf(curPort));
						res.add(newProxy);
					}
        	   }catch(Exception e){
        		   e.printStackTrace();
        	   }
           }
           
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return res;
	}
	
	
	public static void main(String[] args) {
		
		XicidailiProxySource test = new XicidailiProxySource();
		test.getMoreProxies();
	} 
}
