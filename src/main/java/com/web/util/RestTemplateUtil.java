package com.web.util;

import com.web.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by huangquanwei on
 * restTemplateUtils 高性能请求http https
 */
@Component
public class RestTemplateUtil {

    private static Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);
    private static final int CONNEC_TIMEOUT = 60000;//连接时间
    private static final int READ_TIMEOUT = 60000;//读取时间
    private static final int RETRY_COUNT = 1; //重试次数


    @Resource
    private RestTemplate restTemplate;

    /**
     * http 请求 GET
     *
     * @param url    地址
     * @param params 参数
     * @return String 类型
     */
    public ResponseEntity<String> getHttp(String url, Map<String, Object> params, Map<String, String> headersMap) {
        return this.getHttp(url, params, headersMap, CONNEC_TIMEOUT, READ_TIMEOUT, RETRY_COUNT);
    }

    /**
     * http 请求 GET
     *
     * @param url    地址
     * @param params 参数
     * @return String 类型
     */
    public ResponseEntity<String> getHttp(String url, Map<String, Object> params, Map<String, String> headersMap, Integer connecTimeout, Integer readTimeout, Integer retryCount) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connecTimeout);
        requestFactory.setReadTimeout(readTimeout);
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
        restTemplate.setErrorHandler(new org.springframework.web.client.DefaultResponseErrorHandler()); // 异常处理
        if (url.indexOf("https") == 0) {
            restTemplate.setRequestFactory(new HttpsClientRequestFactory()); // 绕过https
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        if (headersMap != null && headersMap.size() > 0) {
            requestHeaders.setAll(headersMap);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);
        url = expandURL(url, params);
        ResponseEntity<String> resEntity = null; // 返回值类型;
        for (int i = 1; i <= retryCount; i++) {
            try {
//                result = restTemplate.getForObject(url, String.class, params);
                resEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                return resEntity;
            } catch (Exception e) {
                log.error("-----------开始-----------重试count:{} ", i);
                e.printStackTrace();
            } finally {
                String result = "";
                if (resEntity != null) {
                    result = resEntity.getBody();
                }
                String line = System.getProperty("line.separator");
                log.info("调用接口Url:{}," + line + "调用接口入参:{}," + line + "调用接口回参:{}", url, JsonUtil.toStringNonNull(params), result);
            }
        }
        return null;
    }

//    public String getHttp(String url, Map<String, Object> params, Integer connecTimeout, Integer readTimeout, Integer retryCount) {
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(connecTimeout);
//        requestFactory.setReadTimeout(readTimeout);
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
//        if (url.indexOf("https") == 0) {
//            restTemplate.setRequestFactory(new HttpsClientRequestFactory()); // 绕过https
//        }
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler()); //error处理
//        restTemplate.setRequestFactory(new HttpsClientRequestFactory()); // 绕过https
//        url = expandURL(url, params);
//        String result = null; // 返回值类型;
//        for (int i = 1; i <= retryCount; i++) {
//            try {
//                result = restTemplate.getForObject(url, String.class, params);
//                return result;
//            } catch (Exception e) {
//                log.error("-----------开始-----------重试count:{} ", i);
//                e.printStackTrace();
//            } finally {
//                log.info("调用接口Url:{}," + System.getProperty("line.separator") + "调用接口入参:{}," + System.getProperty("line.separator") + "调用接口回参:{}", url, JsonUtil.toStringNonNull(params), result);
//            }
//        }
//        return result;
//    }


    /**
     * http 普通请求 post
     *
     * @param url        地址
     * @param params     MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
     * @param headersMap header
     * @return String 类型
     */
    public String postHttp(String url, Map params, Map headersMap) {
        Map<String, Object> turnMap = new HashMap<String, Object>();
        turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_FAIL);
        turnMap.put(Constants.RESULT_MSG_STR, "调用接口失败");
          ResponseEntity<String> responseEntity = this.postHttp(url, params, headersMap, CONNEC_TIMEOUT, READ_TIMEOUT, RETRY_COUNT);
        if(responseEntity == null){
            turnMap.put(Constants.RESULT_CODE_STR, Constants.CODE_OTHER);
            turnMap.put(Constants.RESULT_MSG_STR, "调用远程接口异常");
            return JsonUtil.toStringNonNull(turnMap);
        }
        return responseEntity.getBody();
    }


    /**
     * http 普通请求 post
     *
     * @param url        地址
     * @param params     MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
     * @param headersMap header
     * @return String 类型
     */
    public ResponseEntity<String> postHttpForResponseEntity(String url, Map params, Map headersMap) {
        return this.postHttp(url, params, headersMap, CONNEC_TIMEOUT, READ_TIMEOUT, RETRY_COUNT);
    }

    /**
     * http 请求 post
     *
     * @param url           地址
     * @param params        参数
     * @param headersMap    header
     * @param connecTimeout 连接超时时间
     * @param readTimeout   读取数据超时时间
     * @param retryCount    重试次数
     * @return String 类型
     */
    public ResponseEntity<String> postHttp(String url, Map<String, Object> params, Map headersMap, Integer connecTimeout, Integer readTimeout, Integer retryCount) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(); // 时间函数
        requestFactory.setConnectTimeout(connecTimeout);
        requestFactory.setReadTimeout(readTimeout);
        //内部实际实现为 HttpClient
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
        if (url.indexOf("https") == 0) {
            restTemplate.setRequestFactory(new HttpsClientRequestFactory()); // 绕过https
        }
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler()); // 异常处理的headers error 处理
        // 设置·header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAll(headersMap);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, requestHeaders); // josn utf-8 格式
        ResponseEntity<String> responseEntity = null; // 返回值类型;
        for (int i = 1; i <= retryCount; i++) {
            try {
                responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
//                resEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                return responseEntity;
            } catch (Exception e) {
                log.error("responseEntity.getStatusCode():{} ", responseEntity == null ? null : responseEntity.getStatusCode());
                log.error("-----------开始-----------重试count:{} ", i);
                log.error("调用远程接口获得异常:", e);
                if (i == RETRY_COUNT) {
                    return null;
                }
            } finally {
                String line = System.getProperty("line.separator");
                log.info("调用接口Url:{}," + line + "调用接口入参:{}," + line + "调用接口回参:{}", url, JsonUtil.toStringNonNull(params), responseEntity == null ? null : responseEntity.getBody());
            }
        }
        return null;
    }


    /**
     * http 普通请求 post
     *
     * @param url        地址
     * @param params     MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
     * @param headersMap header
     * @return String 类型
     */
    public String postHttp(String url, MultiValueMap params, Map headersMap) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(); // 时间函数
        requestFactory.setConnectTimeout(CONNEC_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        //内部实际实现为 HttpClient
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler()); // 异常处理的headers error 处理
        // 设置·header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAll(headersMap);

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(params, requestHeaders); // josn utf-8 格式
        String result = null; // 返回值类型;
        for (int i = 1; i <= RETRY_COUNT; i++) {
            try {
                result = restTemplate.postForObject(url, requestEntity, String.class);
                return result;
            } catch (Exception e) {
                log.error("-----------开始-----------重试count:{} ", i);
                e.printStackTrace();

            }
        }
        return result;
    }


    /**
     * @param url        请求地址
     * @param params     请求 josn 格式参数
     * @param headersMap headers 头部需要参数
     * @return 返回string类型返回值
     */
    public String postHttps(String url, Map<String, Object> params, Map headersMap) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(); // 时间函数
        requestFactory.setConnectTimeout(CONNEC_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        //内部实际实现为 HttpClient
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
        restTemplate.setRequestFactory(new HttpsClientRequestFactory()); // 绕过https
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler()); // 异常处理的headers error 处理
        // 设置·header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAll(headersMap);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, requestHeaders); // josn utf-8 格式
        String result = null; // 返回值类型;
        for (int i = 1; i <= RETRY_COUNT; i++) {
            try {
                result = restTemplate.postForObject(url, requestEntity, String.class);
                return result;
            } catch (Exception e) {
                log.error("-----------开始-----------重试count:{} ", i);
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 加密参数类型请求  application/x-www-form-urlencoded
     * MultiValueMap<String, Object>
     * 采用 HttpEntity<MultiValueMap<String, Object>> 构造
     * http 请求 post
     *
     * @param url            地址
     * @param postParameters 普通post请求需要的参数
     * @param headersMap     header
     * @return String 类型
     */
    public String postHttpEncryption(String url, MultiValueMap<String, Object> postParameters, Map headersMap) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(); // 时间函数
        requestFactory.setConnectTimeout(CONNEC_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        //内部实际实现为 HttpClient
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler()); // 异常处理的headers error 处理
        // 设置·header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAll(headersMap);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(postParameters, requestHeaders);
        String result = null; // 返回值类型;
        for (int i = 1; i <= RETRY_COUNT; i++) {
            try {
                result = restTemplate.postForObject(url, requestEntity, String.class);
                return result;
            } catch (Exception e) {
                log.error("-----------开始-----------重试count:{} ", i);
                e.printStackTrace();
            }
        }
        return result;
    }


    private String expandURL(String url, Map<String, Object> jsonObject) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        Set<String> keys = jsonObject.keySet();
        for (String key : keys) {
            sb.append(key).append("=").append(jsonObject.get(key)).append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }


    /**
     * 出现异常，可自定义
     */
    private class DefaultResponseErrorHandler implements ResponseErrorHandler {


        /**
         * 对response进行判断，如果是异常情况，返回true
         */
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().value() != HttpServletResponse.SC_OK;
        }

        /**
         * 异常情况时的处理方法
         */
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getBody()));
            StringBuilder sb = new StringBuilder();
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            try {
                throw new Exception(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}