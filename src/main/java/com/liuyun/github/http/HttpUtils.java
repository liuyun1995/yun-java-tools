package com.liuyun.github.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

@Slf4j
public class HttpUtils {

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

            //获取请求配置构建器
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            requestConfigBuilder.setConnectionRequestTimeout(20000);
            requestConfigBuilder.setConnectTimeout(20000);
            requestConfigBuilder.setSocketTimeout(20000);

            //获取HttpClient构建器
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setConnectionManager(connManager);
            httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext));
            httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
            //获取HTTP客户端
            httpClient = httpClientBuilder.build();
        } catch (Exception e) {
            log.error("初始化HttpClient失败", e);
        }
    }

    public static GetBuilder httpGet() {
        return new GetBuilder(httpClient);
    }

    public static PostBuilder httpPost() {
        return new PostBuilder(httpClient);
    }

    private static String buildParams(Map<String, String> paramMap) {
        StringBuilder param = new StringBuilder();
        paramMap.forEach((k,v)->{
            if(param.length() == 0) {
                param.append("?");
            } else if (param.length() > 0) {
                param.append("&");
            }
            param.append(k).append("=").append(v);
        });
        return param.toString();
    }

    private static Header[] buildHeaders(Map<String, String> headerMap) {
        List<Header> list = Lists.newArrayList();
        headerMap.forEach((k,v)->list.add(new BasicHeader(k, v)));
        return list.toArray(new Header[]{});
    }

    public static class HttpBuilder<T> {

        protected CloseableHttpClient httpClient;
        protected String url;
        protected Map<String, String> headerMap = Maps.newLinkedHashMap();
        protected Map<String, String> paramMap = Maps.newLinkedHashMap();
        protected HttpEntity httpEntity;

        public HttpBuilder (CloseableHttpClient httpClient) {
            this.httpClient = httpClient;
        }

        public T setUrl(String url) {
            this.url = url;
            return (T)this;
        }

        public T addHeader(String key, String value) {
            this.headerMap.put(key, value);
            return (T)this;
        }

        public T addHeader(List<NameValuePair> headers) {
            headers.forEach((t)->this.headerMap.put(t.getName(), t.getValue()));
            return (T)this;
        }

        public T addHeader(Map<String, String> headers) {
            this.headerMap.putAll(headers);
            return (T)this;
        }

        public T addParam(String key, String value) {
            this.paramMap.put(key, value);
            return (T)this;
        }

        public T addParam(List<NameValuePair> params) {
            params.forEach((t)->this.paramMap.put(t.getName(), t.getValue()));
            return (T)this;
        }

        public T addParam(Map<String, String> params) {
            this.paramMap.putAll(params);
            return (T)this;
        }

        public T jsonParam(String paramJson) {
            StringEntity stringEntity = new StringEntity(paramJson, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            this.httpEntity = stringEntity;
            return (T)this;
        }

        public T fromParam(List<NameValuePair> params) {
            this.httpEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            return (T)this;
        }

    }

    public static class GetBuilder extends HttpBuilder<GetBuilder> {

        public GetBuilder(CloseableHttpClient httpClient) {
            super(httpClient);
        }

        public HttpEntity doGetEntity() {
            HttpGet httpGet = new HttpGet(this.url + buildParams(paramMap));
            try {
                if(headerMap != null && !headerMap.isEmpty()) {
                    httpGet.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse response = httpClient.execute(httpGet);
                return response == null ? null : response.getEntity();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpGet.releaseConnection();
            }
        }

        public String doGet() {
            try {
                HttpEntity httpEntity = doGetEntity();
                if(httpEntity != null) {
                    return EntityUtils.toString(httpEntity);
                }
                return null;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }

        public byte[] doGetByte() {
            try {
                HttpEntity httpEntity = doGetEntity();
                if(httpEntity != null) {
                    return EntityUtils.toByteArray(httpEntity);
                }
                return null;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
    }

    public static class PostBuilder extends HttpBuilder<PostBuilder> {

        public PostBuilder (CloseableHttpClient httpClient) {
            super(httpClient);
        }

        public HttpEntity doPostEntity() {
            HttpPost httpPost = new HttpPost(this.url + buildParams(paramMap));
            try {
                if(httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if (headerMap != null && !headerMap.isEmpty()) {
                    httpPost.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse response = httpClient.execute(httpPost);
                return response == null ? null : response.getEntity();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpPost.releaseConnection();
            }
        }

        public String doPost() {
            try {
                HttpEntity httpEntity = doPostEntity();
                if(httpEntity != null) {
                    return EntityUtils.toString(httpEntity);
                }
                return null;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }

        public byte[] doPostByte() {
            try {
                HttpEntity httpEntity = doPostEntity();
                if(httpEntity != null) {
                    return EntityUtils.toByteArray(httpEntity);
                }
                return null;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
    }

}
