package com.hong.es.util;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsUtils {
    private static final Logger logger = LoggerFactory.getLogger(EsUtils.class);

    /***
     * 展台相关接口
     * @param client
     * @param index
     * @param companyId
     * @return
     * @throws Exception
     */
    public static Object getJsonInfo(RestHighLevelClient client,String index,String companyId) throws Exception{
        Object data=null;
        if (StringUtils.isNotBlank(index) && StringUtils.isNotBlank(companyId)) {
            try {
                //从es里面查询
                SearchRequest searchRequest = new SearchRequest(index);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("company_id", companyId);
                searchSourceBuilder.query(termQueryBuilder);
                searchRequest.source(searchSourceBuilder);
                //调用
                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
                if(searchResponse.status()== RestStatus.OK){
                    SearchHits hits = searchResponse.getHits();
                    SearchHit[] searchHits = hits.getHits();
                    Map<String, Object> sourceAsMap;
                    for (SearchHit hit : searchHits) {
                        sourceAsMap = hit.getSourceAsMap();
                        data = sourceAsMap.get("json_info");
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return data;
    }

    /***
     * 获取高级搜索查询条件
     * @param client
     * @param index
     * @param columnName
     * @return
     * @throws Exception
     */
    public static Object getCondition(RestHighLevelClient client,String index,String columnName) throws Exception{
        Object data=null;
        try {
            //调用
            SearchResponse searchResponse = client.search(new SearchRequest(index), RequestOptions.DEFAULT);
            if(searchResponse.status()== RestStatus.OK){
                SearchHits hits = searchResponse.getHits();
                SearchHit[] searchHits = hits.getHits();
                Map<String, Object> sourceAsMap;
                for (SearchHit hit : searchHits) {
                    sourceAsMap = hit.getSourceAsMap();
                    data = sourceAsMap.get(columnName);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return data;
    }

    /**
     * 查询 适用单个或多个精确匹配
     */
    public static List<Map<String,Object>> getDocInfo(RestHighLevelClient client,String index, Map<String,Object> termsMap) throws Exception{
        List<Map<String, Object>> mapList = new ArrayList<>();
        //从es里面查询
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, Object> map:termsMap.entrySet()) {
            queryBuilder.filter(QueryBuilders.termsQuery(map.getKey(), map.getValue()));
        }
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        //调用
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        if(searchResponse.status()== RestStatus.OK){
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            Map<String,Object> map = new HashMap<>();

            Map<String, Object> sourceAsMap = null;
            for (SearchHit hit : searchHits) {
                sourceAsMap = hit.getSourceAsMap();
                mapList.add(sourceAsMap);
            }
        }
        return mapList;
    }

    public static String parseHighlightFields(SearchHit hit,String fieldName){
        String str = "";
        if (hit.getHighlightFields() != null && hit.getHighlightFields().get(fieldName)!=null) {
            Text[] fragments = hit.getHighlightFields().get(fieldName).getFragments();
            for (Text text : fragments) {
                str+=text;
            }
        }
        return str;
    }

    public static List<String> getHighlightFields(SearchHit hit,String fieldName){
        List<String> list = new ArrayList<>();
        if (hit.getHighlightFields() != null && hit.getHighlightFields().get(fieldName)!=null) {
            Text[] fragments = hit.getHighlightFields().get(fieldName).getFragments();
            for (Text text : fragments) {
                list.add(text.toString());
            }
        }
        return list;
    }
}
