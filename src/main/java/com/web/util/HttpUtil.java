package com.web.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.web.util.common.CommonParams;
  
/**
 * HTTP各请求工具类
 * @author WJZ
 * 2017-08-02
 */
@SuppressWarnings("deprecation")
public class HttpUtil { 
	private static Logger log = LoggerFactory.getLogger(HttpUtil.class.getName());
    private static PoolingHttpClientConnectionManager connMgr;  
    private static RequestConfig requestConfig;  
    private static final int MAX_TIMEOUT =60000;  

    static {  
        // 设置连接池  
        connMgr = new PoolingHttpClientConnectionManager();  
        // 设置连接池大小  
        connMgr.setMaxTotal(100);  
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());  
        
        RequestConfig.Builder configBuilder = RequestConfig.custom();  
        // 设置连接超时  
        configBuilder.setConnectTimeout(MAX_TIMEOUT);  
        // 设置读取超时  
        configBuilder.setSocketTimeout(MAX_TIMEOUT);  
        // 设置从连接池获取连接实例的超时  
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);  
        // 在提交请求之前 测试连接是否可用  
        configBuilder.setStaleConnectionCheckEnabled(true);  
        requestConfig = configBuilder.build();  
    }  
  
    /** 
     * 发送 GET 请求（HTTP），不带输入数据 
     * @param url 
     * @return 
     */  
    public static String doGet(String url) {  
        return doGet(url, new HashMap<String, Object>());  
    }  
  
    /** 
     * 发送 GET 请求（HTTP），K-V形式 
     * @param url 
     * @param params 
     * @return 
     */  
    @SuppressWarnings({ "resource" })
	public static String doGet(String url, Map<String, Object> params) {  
        String apiUrl = url;  
        StringBuffer param = new StringBuffer();  
        int i = 0;  
        for (String key : params.keySet()) {  
            if (i == 0)  
                param.append("?");  
            else  
                param.append("&");  
            param.append(key).append("=").append(params.get(key));  
            i++;  
        }  
        apiUrl += param;  
        String result = null;  
        HttpClient httpclient = new DefaultHttpClient();  
        try {  
            HttpGet httpPost = new HttpGet(apiUrl);  
            HttpResponse response = httpclient.execute(httpPost);  
            //int statusCode = response.getStatusLine().getStatusCode();  
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                InputStream instream = entity.getContent();  
                result = IOUtils.toString(instream, "UTF-8");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return result;  
    }  
  
    /** 
     * 发送 POST 请求（HTTP），不带输入数据 
     * @param apiUrl 
     * @return 
     * @throws Exception 
     */  
    public static String doPost(String apiUrl) throws Exception {  
        return doPost(apiUrl, new HashMap<String, Object>());  
    }  
  
    /** 
     * 发送 POST 请求（HTTP），K-V形式 
     * @param apiUrl API接口URL 
     * @param params 参数map 
     * @return 
     * @throws Exception 
     */  
    public static String doPost(String apiUrl, Map<String, Object> params) throws Exception {  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        String httpStr = null;  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {  
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());  
                pairList.add(pair);  
            }  
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));  
            response = httpClient.execute(httpPost);  
            HttpEntity entity = null;
            		
            if(200==response.getStatusLine().getStatusCode()){
    			entity = response.getEntity();
    		}else{
    			throw new Exception("调用远程地址失败");
    		}
            
            httpStr = EntityUtils.toString(entity, "UTF-8");  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
  
    /** 
     * 发送 POST 请求（HTTP），JSON形式 
     * @param apiUrl 
     * @param json json对象 
     * @return 
     */  
    public static String doPost(String apiUrl, Object json,Map<String, Object> otherParam) {  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        String httpStr = "";  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            //往header里面设置staffcode来控制灰度
            Header header=new BasicHeader("staffCode", (String) otherParam.get("staffCode"));
            httpPost.setHeader(header);
            Header header2=new BasicHeader("hlogcatg", "Y");
            httpPost.setHeader(header2);
            Header header3=new BasicHeader("hloggid", UUID.randomUUID().toString());
            httpPost.setHeader(header3);
            StringEntity stringEntity = new StringEntity(json.toString(),"UTF-8");//解决中文乱码问题  
            stringEntity.setContentEncoding("UTF-8");  
            stringEntity.setContentType("application/json");  
            httpPost.setEntity(stringEntity);  
            response = httpClient.execute(httpPost);  
            HttpEntity entity = response.getEntity();  
            
            //System.out.println(response.getStatusLine().getStatusCode());  
            
            if(response.getStatusLine().getStatusCode()==200){
            	httpStr = EntityUtils.toString(entity, "UTF-8");  
            }
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (response != null) {  
                try {  
                	//response.close();
            		//httpClient.close();
                	
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        
        return httpStr;  
    }  
    
    public static String doCatchPost(String apiUrl, Object json,Map<String, Object> otherParam) throws ClientProtocolException, IOException {  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        String httpStr = "";  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
        httpPost.setConfig(requestConfig);  
        //往header里面设置staffcode来控制灰度
        Header header=new BasicHeader("staffCode", (String) otherParam.get("staffCode"));
        httpPost.setHeader(header);
        Header header2=new BasicHeader("hlogcatg", "Y");
        httpPost.setHeader(header2);
        Header header3=new BasicHeader("hloggid", UUID.randomUUID().toString());
        httpPost.setHeader(header3);
        StringEntity stringEntity = new StringEntity(json.toString(),"UTF-8");//解决中文乱码问题  
        stringEntity.setContentEncoding("UTF-8");  
        stringEntity.setContentType("application/json");  
        httpPost.setEntity(stringEntity);  
        response = httpClient.execute(httpPost);  
        HttpEntity entity = response.getEntity();        
        if(response.getStatusLine().getStatusCode()==200){
        	httpStr = EntityUtils.toString(entity, "UTF-8");  
        }else{
        	httpStr = "接口异常";
        }
        if (response != null) {  
            try {  
            	//response.close();
        		//httpClient.close();
            	
                EntityUtils.consume(response.getEntity());  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return httpStr;  
    }  
    
    /** 
     * 发送 POST 请求（HTTP），XML格式数据 
     * @param apiUrl 
     * @param xml xml对象 
     * @return 
     */  
    public static String doPostXml(String apiUrl,Object xml) {  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        String httpStr = null;  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            StringEntity stringEntity = new StringEntity(xml.toString(),"UTF-8");//解决中文乱码问题  
            stringEntity.setContentEncoding("UTF-8");  
            stringEntity.setContentType("application/xml");  
            httpPost.setEntity(stringEntity);  
            response = httpClient.execute(httpPost);  
            HttpEntity entity = response.getEntity();  
            httpStr = EntityUtils.toString(entity, "UTF-8");  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
  
    /** 
     * 发送 SSL POST 请求（HTTPS），K-V形式 
     * @param apiUrl API接口URL 
     * @param params 参数map 
     * @return 
     */  
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {  
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
        String httpStr = null;  
  
        try {  
        	httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.setConfig(requestConfig);  
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());  
            for (Map.Entry<String, Object> entry : params.entrySet()) {  
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());  
                pairList.add(pair);  
            }  
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));  
            response = httpClient.execute(httpPost);  
            int statusCode = response.getStatusLine().getStatusCode();  
            if (statusCode != HttpStatus.SC_OK) {  
                return null;  
            }  
            HttpEntity entity = response.getEntity();  
            if (entity == null) {  
                return null;  
            }  
            httpStr = EntityUtils.toString(entity, "utf-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
  
    /** 
     * 发送 SSL POST 请求（HTTPS），JSON形式 
     * @param apiUrl API接口URL 
     * @param json JSON对象 
     * @return 
     */  
    public static String doPostSSL(String apiUrl, Object json) {  
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();  
        HttpPost httpPost = new HttpPost(apiUrl);  
        CloseableHttpResponse response = null;  
        String httpStr = null;  
  
        try {  
            httpPost.setConfig(requestConfig);  
            StringEntity stringEntity = new StringEntity(json.toString(),"UTF-8");//解决中文乱码问题  
            stringEntity.setContentEncoding("UTF-8");  
            stringEntity.setContentType("application/json");  
            httpPost.setEntity(stringEntity);  
            response = httpClient.execute(httpPost);  
            int statusCode = response.getStatusLine().getStatusCode();  
            if (statusCode != HttpStatus.SC_OK) {  
                return null;  
            }  
            HttpEntity entity = response.getEntity();  
            if (entity == null) {  
                return null;  
            }  
            httpStr = EntityUtils.toString(entity, "utf-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (response != null) {  
                try {  
                    EntityUtils.consume(response.getEntity());  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return httpStr;  
    }  
  
    /** 
     * 创建SSL安全连接 
     * 
     * @return 
     */  
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {  
        SSLConnectionSocketFactory sslsf = null;  
        try {  
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
  
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
                    return true;  
                }  
            }).build();  
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {  
  
                @Override  
                public boolean verify(String arg0, SSLSession arg1) {  
                    return true;  
                }  
  
                @Override  
                public void verify(String host, SSLSocket ssl) throws IOException {  
                }  
  
                @Override  
                public void verify(String host, X509Certificate cert) throws SSLException {  
                }  
  
                @Override  
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {  
                }  
            });  
        } catch (GeneralSecurityException e) {  
            e.printStackTrace();  
        }  
        return sslsf;  
    }  
	
	/**
	 * NameValuePair 传值 ? &形式
	 * @Title: sendUrlEncodedPost
	 * @param: @param url
	 * @param: @param formparams
	 * @param: @return
	 * @return String 返回类型
	 * @throws
	 */
	@SuppressWarnings("resource")
	public static Map<String, Object> sendUrlEncodedPost(String url,List<NameValuePair> formparams) {
		Map<String, Object> resultMap=new HashMap<String, Object>();
		resultMap.put("httpStatus", "400");
		resultMap.put("retSrc","");
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		try {
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(urlEntity);
			HttpResponse response = httpclient.execute(httpPost);
            InputStream ins = null;
            InputStreamReader inr = null;
            BufferedReader br = null;
            HttpEntity entity = response.getEntity();
            try {
            	resultMap.put("httpStatus", response.getStatusLine().getStatusCode());
            	
                ins = entity.getContent();
                
                //按指定的字符集构建文件流
                inr = new InputStreamReader(ins, "UTF-8");
                br = new BufferedReader(inr);
                
                StringBuffer sbf = new StringBuffer();
                String line = null;
                
                while ((line = br.readLine()) != null) {
                    sbf.append(line);
                }
                
                if(sbf!=null){
                	resultMap.put("retSrc",sbf.toString());
                }
            } catch (Exception e) {
               e.printStackTrace();
            } finally {
                if (br != null){br.close();}
                if (inr != null){inr.close();}
                if (ins != null){ins.close();}
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
		
		return resultMap;
	}
  
	public static String doHttpPost(String URL,String payStr) {
		byte[] payData = payStr.getBytes();
		InputStream instr = null;
		try {
			URL url = new URL(URL);
			HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestProperty("content-Type", "text/xml");
			urlCon.setRequestProperty("charset", "utf-8");
			urlCon.setRequestProperty("Content-length",String.valueOf(payData.length));
			DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
			printout.write(payData);
			printout.flush();
			printout.close();
			instr = urlCon.getInputStream();
			byte[] bis = IOUtils.toByteArray(instr);
			String responseString = new String(bis, "UTF-8");
			if (StringUtil.isEmptyStr(responseString)) {
				System.out.println("返回空");
			}
			System.out.println(responseString);
			return responseString;
		} catch (Exception e) {
			log.error("HttpRequest doHttpPost error:",e);
			return "";
		} finally {
			try {
				instr.close();
			} catch (Exception ex) {
				return "";
			}
		}
	}
	
	
	public static Map<String, Object> sendJsonPost(String url, String paramStr) {
        Map<String, Object> result = new HashMap<String, Object>();
        String resultCode = CommonParams.RESULT_FAIL;
        String response = "";
        try {
        	URL httpUrl = new URL(url); //url地址
        	HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
        	connection.setDoInput(true);
        	connection.setDoOutput(true);
        	connection.setRequestMethod("POST");
        	connection.setUseCaches(false);
        	connection.setInstanceFollowRedirects(true);
        	connection.setRequestProperty("Content-Type","application/json");
        	connection.connect();
        	try (OutputStream os = connection.getOutputStream()) {
        		os.write(paramStr.getBytes("UTF-8"));
        	}
        	try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        		String lines;
        		StringBuffer sbf = new StringBuffer();
        		while ((lines = reader.readLine()) != null) {
        			lines = new String(lines.getBytes(), "utf-8");
        			sbf.append(lines);
        		}
        		log.info("返回来的报文："+sbf.toString());
        		response = sbf.toString();
        		resultCode = CommonParams.RESULT_SUCC;
        	}
        	connection.disconnect();
        	result.put(CommonParams.RESULT_CODE_STR, resultCode);
    	    result.put("response", response);
        } catch (Exception e) {
        	e.printStackTrace();
        	result.put(CommonParams.RESULT_CODE_STR, "-1");
     	    result.put("response", response);
       }
        return result;
    }
	
    /** 
     * 测试方法 
     * @param args 
     */  
    public static void main(String[] args) throws Exception {  
//    	  // 加密
//        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        formparams.add(new BasicNameValuePair("client_id", "CLI_CRM_ITVJZ20170412"));
//        formparams.add(new BasicNameValuePair("redirect_uri", "https://vpn.un-net.com/BizHall/assist/addItv"));
//        formparams.add(new BasicNameValuePair("grant_type", "authorization_code"));
//        formparams.add(new BasicNameValuePair("code","2222"));
//        try {
//        	//10.40.250.30:8002
//            HttpUtil.sendUrlEncodedPost("http://10.183.4.203:9001/eam-apps/oauth/token", formparams);
//            
//        } catch (Exception e) {
//        	System.out.println(e);
//        }
//        
//        System.out.println("222");
//    	
    	try{

    		InetAddress myip= InetAddress.getLocalHost();

    		System.out.println("你的IP地址是："+myip.getHostAddress());

    		System.out.println("主机名为："+myip.getHostName()+"。");

    		}catch(Exception e){

    		e.printStackTrace();
    		}
    	String staffCode="liaomi";
    	Map<String, Object> a=new HashMap<String, Object>();
    	a.put("staffCode",staffCode);
    	
    	List<Map<String, Object>> c=new ArrayList<Map<String,Object>>();
    	c.add(a);
    	Map<String,Object> otherParam=new HashMap<String,Object>();
	otherParam.put("staffCode", staffCode);
    	//String b=HttpUtil.doPost("http://133.37.135.35:9007/sm-auth-service/service/sm_auth_getStaffBaseInfo",JsonUtil.toString(c));
    	String b=HttpUtil.doPost("http://133.38.38.42:8470/sm-auth-service/service/sm_auth_getStaffBaseInfo",JsonUtil.toString(c),otherParam);
    	//http://133.37.135.35:9007/sm-auth-service
    	System.out.println(b);
    }  
}
