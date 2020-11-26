package com.atguigu.gulimall.test.beidou;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * jdk https call
 */
public class JdkDemo {

    public static void main(String[] args) throws Exception {
        httpsCall("https://testopen.95155.com/save/apis/login", false);
        httpsCall("https://www.baidu.com", true);
    }

    /**
     * https调用
     *
     * @param url    url
     * @param strict 严格模式 true 校验证书等；false 不校验
     */
    public static void httpsCall(String url, boolean strict) throws Exception {
        if (!strict) {
            // 信任证书
            trustAllCerts();
            // 信任域名
            trustAllHosts();
        }
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        System.out.printf("Response - code: %s, message: %s, body: %s\n", conn.getResponseCode(), conn.getResponseMessage(), result);
    }

    /**
     * 信任所有证书
     */
    private static void trustAllCerts() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    /**
     * 信任所有域名
     */
    private static void trustAllHosts() {
        HostnameVerifier trustAllHosts = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(trustAllHosts);
    }
}
