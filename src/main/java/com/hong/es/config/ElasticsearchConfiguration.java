package com.hong.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ElasticsearchConfiguration implements FactoryBean<RestHighLevelClient>, InitializingBean, DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

    //ES地址
    @Value("${spring.data.elasticsearch.host}")
    private String host;
    //ES端口
    @Value("${spring.data.elasticsearch.port}")
    private int port;
    //ES用户名
   // @Value("${spring.data.elasticsearch.username}")
    private String username;
    //ES密码
    //@Value("${spring.data.elasticsearch.password}")
    private String password;

    //client单个地址的最大连接数
    @Value("${spring.data.elasticsearch.maxConnPerRoute}")
    private int maxConnPerRoute;
    //client最大连接数
    @Value("${spring.data.elasticsearch.maxConnTotal}")
    private int maxConnTotal;

    //Java Low Level REST Client （要想使用高版本client必须依赖低版本的client）
    //private RestClient client;
    //Java High Level REST Client （高版本client）
    private RestHighLevelClient restHighLevelClient;


    //销毁方法
    @Override
    public void destroy() throws Exception {
        try {
            LOGGER.info("Closing elasticSearch client");
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (final Exception e) {
            LOGGER.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public RestHighLevelClient getObject() throws Exception {
        return restHighLevelClient;
    }

    @Override
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        buildClient();
    }

    //初始化client
    protected void buildClient() {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(username, password));
//        client = RestClient.builder(new HttpHost(host, port))
//                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                    @Override
//                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                    }
//                })
//                .build();
        //restHighLevelClient = new RestHighLevelClient(client);
//        restHighLevelClient = new RestHighLevelClient(
//                RestClient.builder(Gethosts()
//                        ));
        RestClientBuilder builder = RestClient.builder(Gethosts())
                .setHttpClientConfigCallback(clientBuilder->{
                    clientBuilder.setMaxConnPerRoute(maxConnPerRoute).setMaxConnTotal(maxConnTotal);
                    return clientBuilder;
                });
        restHighLevelClient = new RestHighLevelClient(builder);
    }

    public HttpHost[] Gethosts(){
        String[] hosts = host.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        HttpHost httpHost = null;
        for (int i=0;i< hosts.length;i++
                ) {
            httpHost = new HttpHost(hosts[i], port, "http");
            httpHosts[i]=httpHost;
        }
        return httpHosts;
    }

}
