package com.liuyun.github.http;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpUtils3 {

    /** HTTP客户端 */
    private static CloseableHttpClient httpClient;

    static {
        try {
            //获取连接管理器
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
            //设置默认连接配置
            connManager.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Consts.UTF_8).build());
            //设置连接池最大连接数
            connManager.setMaxTotal(300);
            //设置单路由最大连接数
            connManager.setDefaultMaxPerRoute(300);
            //设置默认Socket配置
            connManager.setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).build());

            //获取SSL上下文
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build();
            //获取请求配置
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000).setSocketTimeout(10000).build();

            //获取HTTP客户端
            httpClient = HttpClients.custom().setConnectionManager(connManager)
                                             .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                                             .setDefaultRequestConfig(requestConfig).build();
        } catch (Exception e) {
            log.error("创建httpClient失败", e);
        }
    }

    public static String doPost(String url, List<NameValuePair> params) {
        return doPost(url, params, null);
    }

    public static String doPost(String url, Map<String, Object> params) {
        return doPost(url, params, null);
    }

    public static String doPost(String url, String params) {
        return doPost(url, params, null);
    }


    public static String doPost(String url, List<NameValuePair> params, List<NameValuePair> headers) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            }
            if (headers != null && !headers.isEmpty()) {
                httpPost.setHeaders(buildHeaderArray(headers));
            }
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                result = EntityUtils.toString(entity);
            }
            return result;
        } catch (Exception e) {
            log.error(String.format("url=%s, params=%s", url, params), e);
            return null;
        } finally {
            httpPost.releaseConnection();
        }
    }

    public static String doPost(String url, Map<String, Object> params, List<NameValuePair> headers) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                JSONObject jsonParam = new JSONObject();
                for (Map.Entry entry : params.entrySet()) {
                    jsonParam.put((String) entry.getKey(), entry.getValue());
                }
                StringEntity entity = new StringEntity(jsonParam.toString(), "UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            if (headers != null && !headers.isEmpty()) {
                httpPost.setHeaders(buildHeaderArray(headers));
            }
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                result = EntityUtils.toString(entity);
            }
            return result;
        } catch (Exception e) {
            log.error(String.format("url=%s, params=%s", url, params), e);
            return null;
        } finally {
            httpPost.releaseConnection();
        }
    }

    public static String doPost(String url, String paramJson, List<NameValuePair> headers) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        try {
            if (paramJson != null && !paramJson.isEmpty()) {
                StringEntity entity = new StringEntity(paramJson, "UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            if (headers == null) {
                headers = Lists.newArrayList();
            }
            headers.add(new BasicNameValuePair("Content-Type", "application/json; charset=utf-8"));
            if (headers != null && !headers.isEmpty()) {
                httpPost.setHeaders(buildHeaderArray(headers));
            }
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                result = EntityUtils.toString(entity);
            }
            return result;
        } catch (Exception e) {
            log.error(String.format("url=%s, paramJson=%s", url, paramJson, e));
            return null;
        } finally {
            httpPost.releaseConnection();
        }
    }

    public static String doGet(String url) {
        return doGet(url, "");
    }

    public static String doGet(String url, List<NameValuePair> params) {
        return doGet(url, params, null);
    }

    public static String doGet(String url, List<NameValuePair> params, List<NameValuePair> headers) {
        StringBuilder paramStr = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            paramStr.append(params.get(i).getName()).append("=").append(params.get(i).getValue());
            if(i < params.size() - 1) {
                paramStr.append("&");
            }
        }
        return doGet(url, paramStr.toString(), headers);
    }

    public static String doGet(String url, String params) {
        return doGet(url, params, null);
    }

    public static String doGet(String url, String params, List<NameValuePair> headers) {
        String result = null;
        url += "?" + params;
        url = url.replaceAll(" ", "%20");
        HttpGet httpGet = new HttpGet(url);
        try {
            if (headers != null && !headers.isEmpty()) {
                httpGet.setHeaders(buildHeaderArray(headers));
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
            return result;
        } catch (Exception e) {
            log.error(String.format("url=%s, params=%s", url, params), e);
            return null;
        } finally {
            httpGet.releaseConnection();
        }
    }

    public static byte[] doGetByte(String url) {
        return doGetByte(url, null);
    }

    public static byte[] doGetByte(String url, String params) {
        return doGetByte(url, params, null);
    }

    public static byte[] doGetByte(String url, String params, List<NameValuePair> headers) {
        byte[] result = null;
        url += "?" + params;
        url = url.replaceAll(" ", "%20");
        HttpGet httpGet = new HttpGet(url);
        try {
            if (headers != null && !headers.isEmpty()) {
                httpGet.setHeaders(buildHeaderArray(headers));
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toByteArray(entity);
            }
            return result;
        } catch (Exception e) {
            log.error(String.format("url=%s, params=%s", url, params), e);
            return null;
        } finally {
            httpGet.releaseConnection();
        }
    }
    
    public static Header[] buildHeaderArray(List<NameValuePair> headerList) {
        Header[] headers = new Header[headerList.size()];
        for (int i = 0; i < headerList.size(); i++) {
            headers[i] = new BasicHeader(headerList.get(i).getName(), headerList.get(i).getValue());
        }
        return headers;
    }
    
}
