package com.atguigu.gulimall.test.beidou;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * spring 4.3.17(Apache HttpClient 4.5.x built-in) https call
 * <p>
 * spring RestTemplate 请求证书过期的网站不会出错
 */
public class RestTemplate4_xDemo {

    public static void main(String[] args) throws Exception {
        httpsCall("https://www.baidu.com", true);
        httpsCall("https://testopen.95155.com/save/apis/login", true);
    }

    public static void httpsCall(String url, boolean strict) throws Exception {
        HttpClientBuilder builder = HttpClients.custom();
        if (!strict) {
            trustAllCerts(builder);
        }
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(builder.build());
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        System.out.printf("Response - code: %s, message: %s, body: %s\n",
                response.getStatusCodeValue(), response.getStatusCode().getReasonPhrase(), response.getBody());
    }

    private static void trustAllCerts(HttpClientBuilder httpClientBuilder) throws Exception {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(null, new TrustAllStrategy());
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
        httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
    }
}
