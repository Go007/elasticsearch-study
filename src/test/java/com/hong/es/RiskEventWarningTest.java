package com.hong.es;

import com.hong.es.util.DateUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @Description: 风险事件预警ES查询
 * @Author wanghong
 * @Date 2020/12/21 10:17
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskEventWarningTest {

    private static final String INDEX_RISK_EVENT_DETAIL = "risk_event_detail";
    private static final String INDEX_COMPANY_RISK_EVENT = "company_risk_event";

    private static final String HOST = "172.16.79.58";
    private static final int PORT = 9200;
    private static final int MAX_CONN_PER_ROUTE = 300;
    private static final int MAX_CONN_TOTAL = 500;

    @Test
    public void pageList() throws Exception {
        RiskEventWarningParameter param = new RiskEventWarningParameter();
        param.setCompanyName("建设集团");
        param.setEventRank("高");
        param.setEventDateBegin("2019-01-01");
        param.setEventDateEnd("2020-12-31");

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(param.getEventTitle())) {
            BoolQueryBuilder boolFuzzyQuery = QueryBuilders.boolQuery();
            boolFuzzyQuery.should(QueryBuilders.wildcardQuery("event_title", "*" + param.getEventTitle() + "*"));
            boolQuery.filter(boolFuzzyQuery);
        }
        if (StringUtils.isNotEmpty(param.getCompanyName())) {
            BoolQueryBuilder boolFuzzyQuery = QueryBuilders.boolQuery();
            boolFuzzyQuery.should(QueryBuilders.wildcardQuery("company_name", "*" + param.getCompanyName() + "*"));
            boolQuery.filter(boolFuzzyQuery);
        }
        if (StringUtils.isNotEmpty(param.getEventRank())) {
            boolQuery.filter(QueryBuilders.termsQuery("event_rank", param.getEventRank()));
        }
        if (StringUtils.isNotEmpty(param.getEventDateBegin()) || StringUtils.isNotEmpty(param.getEventDateEnd())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("event_date");
            if (StringUtils.isNotEmpty(param.getEventDateEnd())) {
                rangeQueryBuilder.lt(DateUtils.getISODateStr(param.getEventDateEnd().length() == 10
                        ? DateUtils.getISODateTime(param.getEventDateEnd() + " 00:00:00").plusDays(1)
                        .minusHours(8L)
                        : DateUtils.getISODateTime(param.getEventDateEnd()).minusHours(8L)));
            }
            if (StringUtils.isNotEmpty(param.getEventDateBegin())) {
                rangeQueryBuilder.gte(DateUtils.getISODateStr(DateUtils.getISODateTime(
                        param.getEventDateBegin().length() == 10 ? param.getEventDateBegin() + " 00:00:00"
                                : param.getEventDateBegin())
                        .minusHours(8L)));
            }
            boolQuery.filter(rangeQueryBuilder);
        }

        RestHighLevelClient client = buildClient();
        String sortField = "event_date";
        SortOrder sortOrder = SortOrder.DESC;
        FieldSortBuilder fsb = SortBuilders.fieldSort(sortField);
        fsb.missing("_last");// null排在最后
        fsb.order(sortOrder);
        SearchRequest searchRequest = new SearchRequest(INDEX_COMPANY_RISK_EVENT);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        int curPage = param.getPage() == null ? 1 : param.getPage();
        int rows = param.getSize() == null ? 10 : param.getSize();
        int start = rows * (curPage - 1);
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(rows);
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.sort(fsb);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse.status() != RestStatus.OK) {
            return;
        }

        SearchHits hits = searchResponse.getHits();
        // long totalHits = hits.getTotalHits();
        // System.out.println(totalHits);
        // 使用 jest 解决 因 ES server版本与 RestHighLevelClient 不匹配取法获取 total
        JestClient jestClient = buildJestClient();
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(INDEX_COMPANY_RISK_EVENT).build();
        JestResult jestResult = jestClient.execute(search);
        if (jestResult.isSucceeded()) {
            long total = jestResult.getJsonObject().getAsJsonObject("hits").getAsJsonObject("total").get("value").getAsLong();
            System.out.println(total);
        }

        SearchHit[] searchHits = hits.getHits();
        Map<String, Object> sourceAsMap;
        for (SearchHit hit : searchHits) {
            sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }

    private class RiskEventWarningParameter {
        private Long rowSid;
        private String eventTitle;
        private String companyId;
        private String companyName;
        private String eventRank;
        private String eventDateBegin;
        private String eventDateEnd;
        private Integer page;
        private Integer size = 10;

        public String getEventTitle() {
            return eventTitle;
        }

        public void setEventTitle(String eventTitle) {
            this.eventTitle = eventTitle;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getEventRank() {
            return eventRank;
        }

        public void setEventRank(String eventRank) {
            this.eventRank = eventRank;
        }

        public String getEventDateBegin() {
            return eventDateBegin;
        }

        public void setEventDateBegin(String eventDateBegin) {
            this.eventDateBegin = eventDateBegin;
        }

        public String getEventDateEnd() {
            return eventDateEnd;
        }

        public void setEventDateEnd(String eventDateEnd) {
            this.eventDateEnd = eventDateEnd;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }
    }

    public RestHighLevelClient buildClient() {
        RestHighLevelClient client = null;
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

        return client;
    }

    public JestClient buildJestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://" + HOST + ":" + PORT)
                .connTimeout(60000).readTimeout(60000).multiThreaded(true).build());
        return factory.getObject();
    }

}
