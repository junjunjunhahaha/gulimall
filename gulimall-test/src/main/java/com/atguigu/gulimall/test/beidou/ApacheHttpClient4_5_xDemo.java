package com.atguigu.gulimall.test.beidou;

import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Apache HttpClient 4.5.x https call
 */
public class ApacheHttpClient4_5_xDemo {

    public static final char[] digital = "0123456789ABCDEF".toCharArray();

    public static void main(String[] args) throws Exception {
        httpsCall("https://zhiyunopenapi.95155.com/save/apis/vHisTrack24", false);
    }

    public static void httpsCall(String url, boolean strict) throws Exception {
        HttpClientBuilder builder = HttpClients.custom();
        if (!strict) {
            trustAllCerts(builder);
        }
        CloseableHttpClient httpClient = builder.build();
        String key ="834e5fd2-8da3-46ab-9218-e5dd8eb5fb0d";
        Map<String,String> map = new HashMap<>();
        map.put("token","08fe9f18-4e1c-49b0-ac4b-daf8ec8b7481");
        map.put("cid","57769bbf-f7ea-4cbd-a7ae-155d45c2808e");
        map.put("vclN","苏GA7098");
        map.put("qryBtm","2020-11-20");
        map.put("qryEtm","2020-11-20");

        HttpPost httpPost = new HttpPost(url);
        String sign = signWithParamsOnly(map, key);
        map.put("sign", sign);
        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        CloseableHttpResponse response = httpClient.execute(httpPost);
        StatusLine statusLine = response.getStatusLine();
        System.out.printf("Response - code: %s, message: %s, body: %s\n", statusLine.getStatusCode(), statusLine.getReasonPhrase(),
                EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
    }

    private static void trustAllCerts(HttpClientBuilder httpClientBuilder) throws Exception {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(null, new TrustAllStrategy());
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
        httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
    }

    public static String signWithParamsOnly(Map<String, String> params, String secretKey)
            throws UnsupportedEncodingException {
        List paramValueList = new ArrayList();
        if (params != null) {
            for (Map.Entry entry : params.entrySet()) {
                paramValueList.add((String)entry.getKey() + (String)entry.getValue());
            }
        }
        Collections.sort(paramValueList);
        String[] datas = new String[paramValueList.size()];
        paramValueList.toArray(datas);
            byte[] signature = hmacSha1(datas, secretKey.getBytes("utf-8"));
        return encodeHexStr(signature);
    }



    public static String encodeHexStr(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] result = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            result[(i * 2)] = digital[((bytes[i] & 0xF0) >> 4)];
            result[(i * 2 + 1)] = digital[(bytes[i] & 0xF)];
        }
        return new String(result);
    }


    public static byte[] hmacSha1(String[] datas, byte[] key) {
        Mac mac = null;
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            String[] arrayOfString = datas;
            int j = datas.length; for (int i = 0; i < j; i++) {
                String data = arrayOfString[i];
                mac.update(data.getBytes("UTF-8"));
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return mac.doFinal();
    }
}
