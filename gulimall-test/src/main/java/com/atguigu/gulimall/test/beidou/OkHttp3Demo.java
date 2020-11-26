package com.atguigu.gulimall.test.beidou;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * okhttp3 https call
 * <p>
 * okhttp3 请求证书过期的网站不会出错
 */
public class OkHttp3Demo {

    public static void main(String[] args) throws Exception {
       // httpsCall("https://www.baidu.com", true);
        httpsCall("https://openapi-test.sinoiov.cn/save/apis/login", false);
    }

    public static void httpsCall(String url, boolean strict) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (!strict) {
            trustAllCertsAndHosts(builder);
        }
        OkHttpClient httpClient = builder.build();
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        String result = response.body() != null ? response.body().string() : null;
        System.out.printf("Response - code: %s, message: %s, body: %s\n", response.code(), response.message(), result);
    }

    /**
     * 信任所有证书和域名
     */
    private static void trustAllCertsAndHosts(OkHttpClient.Builder builder) throws Exception {
        TrustAllManager trustAllManager = new TrustAllManager();
        TrustManager[] trustAllCerts = new TrustManager[]{trustAllManager};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        builder.sslSocketFactory(sc.getSocketFactory(), trustAllManager);

        HostnameVerifier trustAllHosts = (hostname, session) -> true;
        builder.hostnameVerifier(trustAllHosts);
    }

    private static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
