package com.hong.es;

import com.alibaba.fastjson.JSON;
import com.hong.es.entity.Book;
import com.hong.es.entity.to.Label;
import com.hong.es.entity.to.RelatedcompanyInfo;
import com.hong.es.entity.to.Warning;
import com.hong.es.service.BookService;
import com.hong.es.util.DateUtils;
import com.hong.es.util.EsClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.springframework.util.CollectionUtils;

/**
 * @author wanghong
 * @date 2019/08/22 22:35
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsDemoTests {
    @Autowired
    private BookService bookService;

   /* @Test
    public void getOne() {
        System.out.println(bookService.getById(1).toString());
    }

    @Test
    public void getAll() {
        List<Book> res = bookService.getAll();
        res.forEach(System.out::println);
    }

    @Test
    public void addOneTest() {
        bookService.putOne(new Book(1, 1, "格林童话"));
        bookService.putOne(new Book(2, 1, "美人鱼"));
    }

    @Test
    public void addBatchTest() {
        List<Book> list = new ArrayList<>();
        list.add(new Book(3, 1, "第一本书"));
        list.add(new Book(4, 1, "第二本书"));
        bookService.putList(list);
    }

    @Test
    public void deleteBatch() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        bookService.deleteBatch(list);
    }

    @Test
    public void deleteByQuery(){
        bookService.deleteByUserId(1);
    }
*/

    private static final List<String> labelL1List = new ArrayList<>(Arrays.asList("经营风险","治理和管理风险","财务风险","证券市场风险","信用风险","不可抗力风险"));
    private static final String INDEX = "companywarnings";

    @Test
    public void test() throws Exception{
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        List<Long> companyIds = new ArrayList<>();
        companyIds.add(114082L);
        boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.companyId", companyIds));
        query.filter(QueryBuilders.nestedQuery("relatedcompanyInfo", boolQuery, ScoreMode.Total));

        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse;
        List<Warning> warnings = new ArrayList<>();
        searchResponse = EsClient.client().search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse.status() == RestStatus.OK) {
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits();
            SearchHit[] searchHits = hits.getHits();
            Map<String, Object> sourceAsMap;
            for (SearchHit hit : searchHits) {
                sourceAsMap = hit.getSourceAsMap();
                Warning warning = JSON.parseObject(JSON.toJSONString(sourceAsMap), Warning.class);
                warnings.add(warning);
            }
        }
    }

    @Test
    public void test2() throws Exception{
        BoolQueryBuilder companyWarningQuery = QueryBuilders.boolQuery();
        List<Long> companyIds = new ArrayList<>();
        Long companyId = 114082L;
        companyIds.add(companyId);

        BoolQueryBuilder q0 = QueryBuilders.boolQuery();
        q0.must(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level1Name", labelL1List));

        BoolQueryBuilder q1 = QueryBuilders.boolQuery();
        q1.must(QueryBuilders.nestedQuery("relatedcompanyInfo.labels", q0, ScoreMode.Total));
        q1.must(QueryBuilders.termsQuery("relatedcompanyInfo.companyId", companyIds));

        companyWarningQuery.filter(QueryBuilders.nestedQuery("relatedcompanyInfo", q1, ScoreMode.Total));

        String beginTime = "2019-07-22";
        String endTime = "2019-10-22";
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("noticeDate");
        rangeQueryBuilder.lt(DateUtils.getISODateStr(DateUtils.getISODateTime(endTime + " 00:00:00").plusDays(1).minusHours(8L)));
        rangeQueryBuilder.gte(DateUtils.getISODateStr(DateUtils.getISODateTime(beginTime + " 00:00:00").minusHours(8L)));
        companyWarningQuery.filter(rangeQueryBuilder);

        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("dateHis")
                .minDocCount(0).format("yyyy-MM-dd").field("noticeDate")
                .dateHistogramInterval(DateHistogramInterval.days(1))
                .extendedBounds(new ExtendedBounds(beginTime,endTime))
                .subAggregation(AggregationBuilders.nested("companyInfoHits","relatedcompanyInfo")
                        .subAggregation(AggregationBuilders.filter("filterTerm",QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                .subAggregation(AggregationBuilders.nested("labelsHits","relatedcompanyInfo.labels")
                                        .subAggregation(AggregationBuilders.filter("filterTerm",QueryBuilders.termQuery("relatedcompanyInfo.labels.level1Name",labelL1List))
                                                .subAggregation(AggregationBuilders.terms("dailyHits").field("relatedcompanyInfo.labels.level1Name"))
                                        )
                                )
                        )
                );

 /*       NestedAggregationBuilder nestedStat =
                AggregationBuilders.nested("nestedStat","relatedcompanyInfo").
                        subAggregation(AggregationBuilders.filter("filterTerm", QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                .subAggregation(AggregationBuilders.terms("countLabels").field("relatedcompanyInfo.labels.level1Name"))).
                        subAggregation(AggregationBuilders.filter("nestedLabelStat",
                                QueryBuilders.boolQuery().must(QueryBuilders.termQuery(
                                        "relatedcompanyInfo.companyId",companyId))));*/

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(companyWarningQuery);
        searchSourceBuilder.aggregation(dateHis);
       // searchSourceBuilder.size(30);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse;

        List<Warning> warnings = new ArrayList<>();

        searchResponse = EsClient.client().search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse.status() == RestStatus.OK) {
            Aggregations aggregations = searchResponse.getAggregations();
            Histogram histograms =  aggregations.get("dateHis");
            List<? extends Histogram.Bucket> histogramsBuckets =  histograms.getBuckets();
            for(Histogram.Bucket bucket:histogramsBuckets){
                Nested date_histogram_hits_nested =  bucket.getAggregations().get("dateHistogramHits");
                ParsedFilter filter_term = date_histogram_hits_nested.getAggregations().get("filterTerm");
                ParsedLongTerms daily_hits = filter_term.getAggregations().get("dailyHits");
                List<? extends Terms.Bucket> daily_hits_Buckets =  daily_hits.getBuckets();
            }


            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            Map<String, Object> sourceAsMap;
            for (SearchHit hit : searchHits) {
                sourceAsMap = hit.getSourceAsMap();
                Warning warning = JSON.parseObject(JSON.toJSONString(sourceAsMap), Warning.class);
                System.out.println(warning.getId());
                List<RelatedcompanyInfo> list = warning.getRelatedcompanyInfo();
                if (!CollectionUtils.isEmpty(list)){
                    for (RelatedcompanyInfo c:list){
                        if (c.getCompanyId() == 114082L){
                            List<Label> labels = c.getLabels();
                            if (CollectionUtils.isEmpty(labels)){
                                System.out.println("=============FALSE=============");
                                return;
                            }
                            for (Label l:labels){
                                if (!labelL1List.contains(l.getLevel1Name())){
                                    System.out.println("=============FALSE=============");
                                    return;
                                }
                            }
                        }
                    }
                }
                warnings.add(warning);
            }
        }
        System.out.println(warnings.size());
        System.out.println("+++++++++++++++++++++++++++++++++++");
    }
}
