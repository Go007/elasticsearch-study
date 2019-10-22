package com.hong.es.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
/**
 * @author wanghong
 * @date 2019/10/22 15:18
 **/
public class EsClient {
    private static final String HOST = "172.16.32.35";
    private static final int PORT = 9200;

    private static final int MAX_CONN_PER_ROUTE = 300;
    private static final int MAX_CONN_TOTAL = 500;

    private static RestHighLevelClient client = null;

    static {
        try {
            if (client != null) {
                client.close();
            }

            HttpHost httpHost = new HttpHost(HOST, PORT);
            RestClientBuilder builder = RestClient.builder(httpHost)
                    .setHttpClientConfigCallback(clientBuilder->{
                        clientBuilder.setMaxConnPerRoute(MAX_CONN_PER_ROUTE).setMaxConnTotal(MAX_CONN_TOTAL);
                        return clientBuilder;
                    });
            client = new RestHighLevelClient(builder);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static RestHighLevelClient client(){
        return client;
    }
}
