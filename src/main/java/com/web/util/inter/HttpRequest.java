package com.web.util.inter;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.web.common.Constants;

public class HttpRequest {

	private static DefaultHttpClient httpclient;  

	private static final String ENCODING = "UTF-8";

	private static Logger log =LoggerFactory.getLogger(HttpRequest.class.getName());
	
	static {  
		 httpclient =  new DefaultHttpClient();   
		 httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient); // 接受任何证书的浏览器客户端   
	} 
	
	/**
	 * 向指定URL发送GET方法的请求
	 * @param url 发送请求的URL带参数
	 * @return URL 所代表远程资源的响应结果
	 */
	public static Map<String, Object> sendGet(String url,String channelNo,String serviceCode,String serviceName) {	
		Map<String, Object> result=new HashMap<String, Object>();
		BufferedReader in = null;
		String content = null;
		String resultCode = "";	
		try {
			// 实例化HTTP方法
			log.error("---GetRequest>>>>"+url);
			HttpGet get=HttpClientConnectionManager.getGetMethod(url);
			HttpResponse response = httpclient.execute(get);
			String status=String.valueOf(response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
				resultCode = Constants.CODE_SUCC;
			}else{
				resultCode = status;
			}
			content = EntityUtils.toString(response.getEntity(), "utf-8");
			log.error("---GetResponse>>>>"+content);		
		}catch(Exception e){
			resultCode = Constants.CODE_FAIL;
			log.error("HttpRequest sendGet error:",e);
		} finally {
			if (in != null) {
				try {
					in.close();// 最后要关闭BufferedReader
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
			result.put("resultCode", resultCode);		
		}		
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * @param url 发送请求的 URL
	 * @param param 请求参数。
	 * @return 所代表远程资源的响应结果
	 */
	public static Map<String, Object> sendPost(String url, String serviceCode,String paramStr) {  
		Map<String, Object> result = new HashMap<String, Object>();
		HttpPost post = HttpClientConnectionManager.getPostMethod(url);
		HttpEntity entity = null;
		String resultCode = "";
		String response = "";		
		try {
			entity = new StringEntity(paramStr,ENCODING);
			post.setEntity(entity);
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  10000);//连接时间10s
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  20000);//数据传输时间20s
			HttpResponse httpresponse = httpclient.execute(post);
			entity = httpresponse.getEntity();
			response = EntityUtils.toString(entity, ENCODING);
			
			//log.error("PostResponse>>>>"+response);
			//返回成功
			if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultCode = Constants.CODE_SUCC;
			}else{
				resultCode = String.valueOf(httpresponse.getStatusLine().getStatusCode());
			}			
		} catch (Exception e) {
			resultCode = Constants.CODE_FAIL;
			log.error("HttpRequest sendPost error:",e);
		}finally{
			result.put("resultCode", resultCode);
			result.put("result", response);
		}
		return result;
	}

	public static String sendEopPost(String url, String paramStr, String appId, String appKey) {
		HttpPost post = new HttpPost(url); // 设置响应头信息
		HttpEntity entity = null;
		String response = "";		
		try {
			entity = new StringEntity(paramStr,ENCODING);
			post.addHeader("X-APP-ID", appId);
			post.addHeader("X-APP-KEY", appKey);
			post.addHeader("Content-Type", "application/json");
			post.setEntity(entity);
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  10000);//连接时间10s
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  20000);//数据传输时间20s
			HttpResponse httpresponse = httpclient.execute(post);
			entity = httpresponse.getEntity();
			response = EntityUtils.toString(entity, ENCODING);
			
			log.error("PostResponse>>>>"+response);
					
		} catch (Exception e) {
			log.error("HttpRequest sendPost error:",e);
		}
		return response;
	}    

	public HttpClient getHttpclient() {
		return httpclient;
	}
}