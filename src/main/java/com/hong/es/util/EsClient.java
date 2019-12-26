package com.hong.es.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wanghong
 * @date 2019/10/22 15:18
 **/
public class EsClient {

     private static final String HOST = "172.16.32.35";

   // private static final String HOST = "192.168.157.129";

    private static final int PORT = 9200;

    private static final int MAX_CONN_PER_ROUTE = 300;
    private static final int MAX_CONN_TOTAL = 500;

    private static RestHighLevelClient client = null;

    private static final String INDEX = "companywarnings";

    static {
        try {
            if (client != null) {
                client.close();
            }

            HttpHost httpHost = new HttpHost(HOST, PORT);
            RestClientBuilder builder = RestClient.builder(httpHost)
                    .setHttpClientConfigCallback(clientBuilder -> {
                        clientBuilder.setMaxConnPerRoute(MAX_CONN_PER_ROUTE).setMaxConnTotal(MAX_CONN_TOTAL);
                        return clientBuilder;
                    });
            client = new RestHighLevelClient(builder);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static RestHighLevelClient client() {
        return client;
    }

    /**
     * Description: 删除index
     *
     * @param index index
     * @return void
     */
    public static void deleteIndex(String index) {
        try {
            client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createIndex() {
        try {
            CreateIndexRequest request = new CreateIndexRequest(INDEX);
            InputStream in = EsClient.class.getClassLoader().getResourceAsStream("es/index_source.json");
            String index_source = readFile(in);
            request.source(index_source, XContentType.JSON);
            CreateIndexResponse response = client().indices().create(request, RequestOptions.DEFAULT);
            if (!response.isAcknowledged()) {
                throw new RuntimeException("初始化失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(InputStream in) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 新增，修改文档
     *
     * @param indexName 索引
     * @param type      mapping type
     * @param id        文档id
     * @param jsonStr   文档数据
     */
    public void addData(String indexName, String type, String id, String jsonStr) {
        try {
            // 1、创建索引请求  //索引  // mapping type  //文档id
            IndexRequest request = new IndexRequest(indexName, type, id);     //文档id
            // 2、准备文档数据
            // 直接给JSON串
            request.source(jsonStr, XContentType.JSON);
            //4、发送请求
            IndexResponse indexResponse = null;
            try {
                // 同步方式
                indexResponse = client.index(request);
            } catch (ElasticsearchException e) {
                // 捕获，并处理异常
                //判断是否版本冲突、create但文档已存在冲突
                if (e.status() == RestStatus.CONFLICT) {
                    System.out.println("冲突了，请在此写冲突处理逻辑！" + e.getDetailedMessage());
                }
            }
            //5、处理响应
            if (indexResponse != null) {
                String index1 = indexResponse.getIndex();
                String type1 = indexResponse.getType();
                String id1 = indexResponse.getId();
                long version1 = indexResponse.getVersion();
                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    System.out.println("新增文档成功!" + index1 + type1 + id1 + version1);
                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    System.out.println("修改文档成功!");
                }
                // 分片处理信息
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    System.out.println("分片处理信息.....");
                }
                // 如果有分片副本失败，可以获得失败原因信息
                if (shardInfo.getFailed() > 0) {
                    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                        String reason = failure.reason();
                        System.out.println("副本失败原因：" + reason);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量插入ES
     *
     * @param indexName 索引
     * @param type      类型
     * @param idName    id名称
     * @param list      数据集合
     */
    public static void bulkDate(String indexName, String type, String idName, List<Map<String, Object>> list) {
        try {
            if (null == list || list.size() <= 0) {
                return;
            }
            if (StringUtils.isBlank(indexName) || StringUtils.isBlank(idName) || StringUtils.isBlank(type)) {
                return;
            }
            BulkRequest request = new BulkRequest();
            for (Map<String, Object> map : list) {
                if (map.get(idName) != null) {
                    request.add(new IndexRequest(indexName, type, String.valueOf(map.get(idName)))
                            .source(map, XContentType.JSON));
                }
            }
            // 2、可选的设置
           /*
           request.timeout("2m");
           request.setRefreshPolicy("wait_for");
           request.waitForActiveShards(2);
           */
            //3、发送请求
            // 同步请求
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            //4、处理响应
            if (bulkResponse != null) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        //TODO 新增成功的处理
                        System.out.println("新增成功,{}" + indexResponse.toString());
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        //TODO 修改成功的处理
                        System.out.println("修改成功,{}" + updateResponse.toString());
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                        //TODO 删除成功的处理
                        System.out.println("删除成功,{}" + deleteResponse.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: 搜索
     * @param index   index
     * @param builder 查询参数
     * @param c       结果类对象
     * @return java.util.ArrayList
     */
    public static <T> List<T> search(String index, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
