package com.web.common;

import java.net.URI;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.web.bean.HttpSvrInitParam;
import com.web.util.inter.AbortConnectionThread;
import com.web.util.inter.AbortConnectionThreadNew;

@SuppressWarnings("deprecation")
public class HttpService {

	private CloseableHttpClient httpClient = null;

	private static HttpService instance = null;

	private static int timeout = 60000;
	private static int maxTotal = 50;
	private static int maxPerRoute = 30;
	private static String url;
	
	private static String cfgFileName;  //默认配置文件
	private static String urlStr;
	private ResourceBundle prb = null;

	private HttpService(HttpSvrInitParam initParam)throws Exception {
		cfgFileName = initParam.getCfgFileName();
		//未设置配置文件默认读取
		if(null==cfgFileName ||"".equals(cfgFileName)){
			cfgFileName = "ec-ca";  
		}
		prb = ResourceBundle.getBundle(cfgFileName);
		initConfig();
		initHttpClient();
	}

	private static synchronized void newInstance(HttpSvrInitParam initParam)throws Exception {
		if (null == instance) {
			instance = new HttpService(initParam);
		}
	}

	public static HttpService getInstance(HttpSvrInitParam initParam)throws Exception {
		urlStr = initParam.getUrlStr();
		if (null == instance) {
			newInstance(initParam);
		}
		return instance;
	}
	public String call(String params,String method) throws Exception {
		//TODO 有池管理时是否还需要关闭当前连接
		if (null == params) {
			return null;
		}
		url = prb.getString(urlStr);
		if (StringUtils.isBlank(url)) {
			return null;
		}
		String callurl=url+method;
		HttpEntity entity = null;
		//设置协议，端口，地址
		URI uri = new URIBuilder()
//	    	.setScheme("http")
	    	.setPort(80)
	    	.setPath(callurl.trim())
	    	.build();
		HttpPost post = new HttpPost(uri);
		//entity = new StringEntity(params, MDA.DEFAULT_ENCODING);
		entity = new StringEntity(params, "UTF-8");
		post.setEntity(entity);

		CloseableHttpResponse response = httpClient.execute(post);
		if(200==response.getStatusLine().getStatusCode()){
	        entity = response.getEntity();
		}else{
			post.abort();
			throw new Exception("地址["+callurl+"]请求失败,请检查配置"+cfgFileName+".properties");
		}
		
		return EntityUtils.toString(entity, "UTF-8");
	}

	private static void initConfig()throws Exception {
		ResourceBundle prb = ResourceBundle.getBundle(cfgFileName);
		String timeOutStr = prb.getString("service.timeOut");
		if (StringUtils.isNotBlank(timeOutStr)) {
			timeout = Integer.valueOf(timeOutStr.trim());
		}

		String maxTotalStr = prb.getString("service.maxTotal");
		if (StringUtils.isNotBlank(maxTotalStr)) {
			maxTotal = Integer.parseInt(maxTotalStr.trim());
		}

		String maxPerRouteStr = prb.getString("service.maxPerRoute");
		if (StringUtils.isNotBlank(maxPerRouteStr)) {
			maxPerRoute = Integer.parseInt(maxPerRouteStr.trim());
		}
	}

	private void initHttpClient()throws Exception {
//		HttpClientConnectionManager manager = new BasicHttpClientConnectionManager();
		//自定义的socket工厂类可以和指定的协议（Http、Https）联系起来，用来创建自定义的连接管理器。
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())//通用socket工厂
				.build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
		AbortConnectionThreadNew act = new AbortConnectionThreadNew(cm);
        act.start();
        //设置超时时间
      	RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        httpClient= HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
		
        //未找到协议版本设置的地方
		/*HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);*/
	}

}
