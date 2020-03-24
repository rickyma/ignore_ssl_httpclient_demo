package com.mhx;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author MHX
 * @date 2018/10/10
 */
public class HttpClientTest {

    private static CloseableHttpClient buildSSLCloseableHttpClient() throws Exception {
        // ignore certificate verification
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true).build();

        // ignore hostname verification
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslConnectionFactory).build();
        HttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);

        int timeout = 5;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .build();
        return HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(config).build();
    }

    public static void main(String[] args) throws Exception {
        HttpGet httpGet = new HttpGet(args[0]);
        try (CloseableHttpClient httpClient = buildSSLCloseableHttpClient(); CloseableHttpResponse response = httpClient.execute(httpGet)) {
            List<String> strings = IOUtils.readLines(response.getEntity().getContent(), Charset.defaultCharset());
            strings.forEach(line -> System.out.println(line));
        }
    }
}
