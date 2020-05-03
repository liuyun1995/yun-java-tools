package com.liuyun.github.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.Map;

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
            //获取请求配置
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(10000)
                    .setConnectTimeout(10000).setSocketTimeout(10000).build();

            //获取HTTP客户端
            httpClient = HttpClients.custom().setConnectionManager(connManager)
                                             .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                                             .setDefaultRequestConfig(requestConfig).build();
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

    public static class GetBuilder {

        private CloseableHttpClient httpClient;
        private String url;
        private Map<String, String> paramMap = Maps.newLinkedHashMap();
        private Map<String, String> headerMap = Maps.newLinkedHashMap();

        public GetBuilder(CloseableHttpClient httpClient) {
            this.httpClient = httpClient;
        }

        public GetBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public GetBuilder addParam(String key, String value) {
            this.paramMap.put(key, value);
            return this;
        }

        public GetBuilder addParam(List<NameValuePair> params) {
            params.forEach((t)->this.paramMap.put(t.getName(), t.getValue()));
            return this;
        }

        public GetBuilder addParam(Map<String, String> params) {
            this.paramMap.putAll(params);
            return this;
        }

        public GetBuilder addHeader(String key, String value) {
            this.headerMap.put(key, value);
            return this;
        }

        public GetBuilder addHeader(List<NameValuePair> headers) {
            headers.forEach((t)->this.headerMap.put(t.getName(), t.getValue()));
            return this;
        }

        public GetBuilder addHeader(Map<String, String> headers) {
            this.headerMap.putAll(headers);
            return this;
        }

        public String doGet() {
            String result = null;
            HttpGet httpGet = new HttpGet(this.url + buildParams(paramMap));
            try {
                if(headerMap != null && !headerMap.isEmpty()) {
                    httpGet.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity);
                }
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpGet.releaseConnection();
            }
        }

        public HttpEntity doGetEntity() {
            HttpEntity result = null;
            HttpGet httpGet = new HttpGet(this.url + buildParams(paramMap));
            try {
                if(headerMap != null && !headerMap.isEmpty()) {
                    httpGet.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    result = response.getEntity();
                }
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpGet.releaseConnection();
            }
        }

        public byte[] doGetByte() {
            byte[] result = null;
            HttpGet httpGet = new HttpGet(this.url + buildParams(paramMap));
            try {
                if(headerMap != null && !headerMap.isEmpty()) {
                    httpGet.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toByteArray(entity);
                }
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpGet.releaseConnection();
            }
        }
    }

    public static class PostBuilder {

        private CloseableHttpClient httpClient;
        private String url;
        private HttpEntity httpEntity;
        private Map<String, String> headerMap = Maps.newLinkedHashMap();

        public PostBuilder (CloseableHttpClient httpClient) {
            this.httpClient = httpClient;
        }

        public PostBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public PostBuilder setParam(String paramJson) {
            StringEntity stringEntity = new StringEntity(paramJson, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            this.httpEntity = stringEntity;
            return this;
        }

        public PostBuilder setParam(List<NameValuePair> params) {
            this.httpEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            return this;
        }

        public PostBuilder addHeader(String key, String value) {
            this.headerMap.put(key, value);
            return this;
        }

        public PostBuilder addHeader(List<NameValuePair> headers) {
            headers.forEach((t)->this.headerMap.put(t.getName(), t.getValue()));
            return this;
        }

        public PostBuilder addHeader(Map<String, String> headers) {
            this.headerMap.putAll(headers);
            return this;
        }

        public String doPost() {
            String result = null;
            HttpPost httpPost = new HttpPost(this.url);
            try {
                if(httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if (headerMap != null && !headerMap.isEmpty()) {
                    httpPost.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    result = EntityUtils.toString(entity);
                }
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpPost.releaseConnection();
            }
        }

        public HttpEntity doPostEntity() {
            HttpEntity result = null;
            HttpPost httpPost = new HttpPost(this.url);
            try {
                if(httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if (headerMap != null && !headerMap.isEmpty()) {
                    httpPost.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    result = httpResponse.getEntity();
                }
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpPost.releaseConnection();
            }
        }

        public byte[] doPostByte() {
            byte[] result = null;
            HttpPost httpPost = new HttpPost(this.url);
            try {
                if(httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if (headerMap != null && !headerMap.isEmpty()) {
                    httpPost.setHeaders(buildHeaders(headerMap));
                }
                CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    result = EntityUtils.toByteArray(entity);
                }
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            } finally {
                httpPost.releaseConnection();
            }
        }

    }

}
