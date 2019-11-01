package com.hong.es;

import com.alibaba.fastjson.JSON;
import com.hong.es.entity.Book;
import com.hong.es.entity.CommonCountVO;
import com.hong.es.entity.EsEntity;
import com.hong.es.entity.to.Label;
import com.hong.es.entity.to.RelatedcompanyInfo;
import com.hong.es.entity.to.Warning;
import com.hong.es.service.BookService;
import com.hong.es.util.DateUtils;
import com.hong.es.util.EsClient;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.springframework.util.CollectionUtils;

import static com.hong.es.util.EsClient.client;

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
        Long companyId = 114082L;
        String beginTime = "2019-09-28";
        String endTime = "2019-10-28";

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.filter(buildRangeQueryBuilder("noticeDate",beginTime,endTime));
        query.must(buildNestedQueryBuilder("relatedcompanyInfo","relatedcompanyInfo.companyId",companyId));

        DateHistogramAggregationBuilder dateHis =
                AggregationBuilders.dateHistogram("dateHis").minDocCount(0).format("yyyy-MM-dd").field("noticeDate")
                        .dateHistogramInterval(DateHistogramInterval.days(1))
                        .extendedBounds(new ExtendedBounds(beginTime,endTime))
                        .subAggregation(AggregationBuilders.nested("dateHistogramHits", "relatedcompanyInfo")
                                .subAggregation(AggregationBuilders.filter("filterTerm",QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                        .subAggregation(AggregationBuilders.nested("labelsHits","relatedcompanyInfo.labels")
                                                .subAggregation(AggregationBuilders.terms("dailyHits").field("relatedcompanyInfo.labels.level1Name"))
                                        )
                                )
                        );

        NestedAggregationBuilder nestedStat =
                AggregationBuilders.nested("nestedStat","relatedcompanyInfo")
                        .subAggregation(
                                AggregationBuilders.filter("filterTerm", QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                        .subAggregation(
                                                AggregationBuilders.filter(
                                                        "nestedNegativeLabelStat", QueryBuilders.boolQuery().must(QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId)).must(QueryBuilders.rangeQuery("relatedcompanyInfo.sentimental").lt(0))
                                                )
                                                        .subAggregation(
                                                                AggregationBuilders.nested("allLabels","relatedcompanyInfo.labels")
                                                                        .subAggregation(
                                                                                AggregationBuilders.terms("countLabel").field("relatedcompanyInfo.labels.level1Name")
                                                                        )
                                                        )
                                        )
                        );

        NestedAggregationBuilder nested_stat =
                AggregationBuilders.nested("nested_stat","relatedcompanyInfo")
                        .subAggregation(
                                AggregationBuilders.filter("filter_term", QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                        .subAggregation(
                                                AggregationBuilders.terms("count_sentimental").field("relatedcompanyInfo.sentimental")
                                        )
                        )
                        .subAggregation(
                                AggregationBuilders.filter(
                                        "nested_negative_label_stat", QueryBuilders.boolQuery().must(QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId)).must(QueryBuilders.rangeQuery("relatedcompanyInfo.sentimental").lt(0))
                                )
                                 .subAggregation(
                                         AggregationBuilders.nested("nested_label_stat","relatedcompanyInfo.labels")
                                                 .subAggregation(
                                                         AggregationBuilders.terms("count_l1").field("relatedcompanyInfo.labels.level1Name")
                                                 )
                                 )
                        );

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchSourceBuilder.size(1);
        searchSourceBuilder.aggregation(dateHis).aggregation(nestedStat).aggregation(nested_stat);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
        List<CommonCountVO> countVOList = new ArrayList<>();
        if (searchResponse.status() == RestStatus.OK) {
            Aggregations aggregations = searchResponse.getAggregations();
            Histogram histograms =  aggregations.get("dateHis");
            List<? extends Histogram.Bucket> histogramsBuckets =  histograms.getBuckets();

            CommonCountVO countVO = null;

            for(Histogram.Bucket bucket:histogramsBuckets){
                Nested date_histogram_hits_nested =  bucket.getAggregations().get("dateHistogramHits");
                ParsedFilter filter_term = date_histogram_hits_nested.getAggregations().get("filterTerm");
                Nested date_label = filter_term.getAggregations().get("labelsHits");
                ParsedStringTerms daily_hits = date_label.getAggregations().get("dailyHits");
                List<? extends Terms.Bucket> daily_hits_Buckets =  daily_hits.getBuckets();

                for (Terms.Bucket  daily_hits_Bucket:daily_hits_Buckets) {
                    countVO = new CommonCountVO();
                    countVO.setKey(bucket.getKeyAsString());
                    String type = daily_hits_Bucket.getKey().toString();
                    countVO.setType(type);
                    countVO.setValue(BigDecimal.valueOf(daily_hits_Bucket.getDocCount()));
                    countVOList.add(countVO);
                }

                List<String> list = daily_hits_Buckets.stream().map(b -> b.getKey().toString()).collect(Collectors.toList());
                List<String> list2 = labelL1List.stream().filter(l -> !list.contains(l)).collect(Collectors.toList());
                for (String label:list2){
                    countVO = new CommonCountVO();
                    countVO.setKey(bucket.getKeyAsString());
                    countVO.setType(label);
                    countVO.setValue(BigDecimal.valueOf(0L));
                    countVOList.add(countVO);
                }
            }

            Nested nested =  aggregations.get("nestedStat");
            ParsedFilter parsedFilter0 =nested.getAggregations().get("filterTerm");
            ParsedFilter parsedFilter = parsedFilter0.getAggregations().get("nestedNegativeLabelStat");
            ParsedNested nestedLabel  =  parsedFilter.getAggregations().get("allLabels");
            ParsedStringTerms label = nestedLabel.getAggregations().get("countLabel");
            List<? extends Terms.Bucket> sentimentalBuckets =  label.getBuckets();
            Map<String,Object> labelMap = new HashMap<>();
            for(Terms.Bucket bucket:sentimentalBuckets){
                labelMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
            }

            Long labelCount = labelMap.values().stream().mapToLong(s -> (Long)s).sum();
            Long all = searchResponse.getHits().getTotalHits();
            labelMap.put("其他",all - labelCount);
            labelMap.put("all",all);

            System.out.println(labelMap);

            Nested nested2 =  aggregations.get("nested_stat");
            ParsedStringTerms count_l1  = ((ParsedNested)((ParsedFilter)(nested2.getAggregations().get("nested_negative_label_stat")))
                    .getAggregations().get("nested_label_stat")).getAggregations().get("count_l1");
            List<? extends Terms.Bucket> count_l1Buckets =  count_l1.getBuckets();
            Map<String,Object> labelMap2 = new HashMap<>();
            for(Terms.Bucket bucket:count_l1Buckets){
                labelMap2.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
            }

            System.out.println("++===++=========");
            System.out.println(labelMap2);
            System.out.println("==================");

            ParsedLongTerms sentimental  = ((ParsedFilter)(nested2.getAggregations().get("filter_term"))).getAggregations().get("count_sentimental");
            List<? extends Terms.Bucket> sentimentalBuckets2 =  sentimental.getBuckets();
            Map<String,Object> sentimentalMap = new HashMap<>();
            for(Terms.Bucket bucket:sentimentalBuckets2){
                sentimentalMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
            }
            sentimentalMap.put("all",(int) searchResponse.getHits().getTotalHits());
            System.out.println(sentimentalMap);
        }

        System.out.println("=========================");
        countVOList.forEach(s -> System.out.println(s));
        System.out.println("=================");
        Map<String,List<CommonCountVO>> map = countVOList.stream().collect(Collectors.groupingBy(CommonCountVO::getType));
        List<CommonCountVO> voList = new ArrayList<>(countVOList.size());
        for (Map.Entry<String,List<CommonCountVO>> vo:map.entrySet()){
            vo.getValue().sort(Comparator.comparing(CommonCountVO::getKey));
            voList.addAll(vo.getValue());
        }
        voList.forEach(c -> System.out.println(c));
        System.out.println(countVOList.size());
    }

    @Test
    public void test00() throws Exception{
        /**
         *  以下是通过oracle数据库完成于test()同样功能的操作
         *  相关表结构见 resources/sql/NEWS_LABEL_JX.sql
         *    ID    NEWS_BASICINFO_SID   COMPANY_ID  LABEL        IS_DEL       CREATE_DT                                 UPDT_DT
         * 51000009704	580865	          512900	信用风险			0	18-7月 -18 04.12.16.000000 下午	18-7月 -18 04.12.16.000000 下午
         * 51000009705	597522	          29709	治理和管理风险			0	18-7月 -18 04.12.16.000000 下午	18-7月 -18 04.12.16.000000 下午
         * 51000009706	1039948	         309905	经营风险			0	18-7月 -18 04.12.16.000000 下午	18-7月 -18 04.12.16.000000 下午
         * 51000009707	114098	         350756	财务风险			0	18-7月 -18 04.12.16.000000 下午	18-7月 -18 04.12.16.000000 下午
         * 51000009708	158916	         450814	治理和管理风险			0	18-7月 -18 04.12.16.000000 下午	18-7月 -18 04.12.16.000000 下午
         * 51000009709	1510368	         83463	不可抗力风险			0	18-7月 -18 04.12.16.000000 下午	18-7月 -18 04.12.16.000000 下午
         */
/*Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("companyId", companyId);
        paramMap.put("beginTime", inDto.getBeginTime());
        paramMap.put("endTime", inDto.getEndTime());
        List<NewsLabelJX> labelList = oracleDao.findList(COUNT_NEWS_LABEL, paramMap);

        if (CollectionUtils.isNotEmpty(labelList)) {
            List<NewsLabelJX> labels = labelList.stream().filter(s -> labelL1List.contains(s.getLabel())).collect(Collectors.toList());
            Map<String, Long> labelMap = labels.stream().collect(Collectors.groupingBy(NewsLabelJX::getLabel,Collectors.counting()));
            labelMap.put("其他", Long.valueOf(labelList.size() - labels.size()));
            labelMap.put("totalCount", Long.valueOf(labelList.size()));
            map.put("countLabel",labelMap);

            Comparator<NewsLabelJX> comparator = new Comparator<NewsLabelJX>() {
                @Override
                public int compare(NewsLabelJX o1, NewsLabelJX o2) {
                    return o1.getUpdateDate().compareTo(o2.getUpdateDate());
                }

            };

            Map<String,List<NewsLabelJX>> newsLabelJXMap = labels.stream().collect(Collectors.groupingBy(NewsLabelJX::getLabel));
            for(Map.Entry<String, List<NewsLabelJX>> entry:newsLabelJXMap.entrySet()) {
                String key = entry.getKey();
                List<NewsLabelJX> data = entry.getValue();
                data.sort(comparator);
                for(NewsLabelJX ss:data) {
                    ss.setUpdateDateStr(DateUtil.dateToStr(ss.getUpdateDate(), "yyyy-MM-dd"));
                }

                Map<String,Long> dateMap = data.stream().collect(Collectors.groupingBy(NewsLabelJX::getUpdateDateStr,Collectors.counting()));
                Date beginDate = DateUtil.strToDate(inDto.getBeginTime(), "yyyy-MM-dd");
                Date endDate = DateUtil.strToDate(inDto.getEndTime(), "yyyy-MM-dd");
                while(beginDate.compareTo(endDate) <=0) {
                    CommonCountVO vo = new CommonCountVO();
                    vo.setType(key);
                    String curDate = DateUtil.dateToStr(beginDate, "yyyy-MM-dd");
                    vo.setKey(curDate);
                    if(!dateMap.containsKey(curDate)) {
                        vo.setValue(BigDecimal.valueOf(0));
                    }else {
                        vo.setValue(BigDecimal.valueOf(dateMap.get(curDate)));
                    }

                    voList.add(vo);
                    beginDate = DateUtil.plus(beginDate);
                }
            }
        }*/
    }

    @Test
    public void test2() throws Exception{
        BoolQueryBuilder companyWarningQuery = QueryBuilders.boolQuery();
        List<Long> companyIds = new ArrayList<>();
        Long companyId = 114082L;
        companyIds.add(companyId);
        String beginTime = "2019-07-22";
        String endTime = "2019-10-22";

        BoolQueryBuilder q0 = QueryBuilders.boolQuery();
        q0.must(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level1Name", labelL1List));

        BoolQueryBuilder q1 = QueryBuilders.boolQuery();
        q1.must(QueryBuilders.nestedQuery("relatedcompanyInfo.labels", q0, ScoreMode.Total));
        q1.must(QueryBuilders.termsQuery("relatedcompanyInfo.companyId", companyIds));

        companyWarningQuery.filter(QueryBuilders.nestedQuery("relatedcompanyInfo", q1, ScoreMode.Total));
        companyWarningQuery.filter(buildRangeQueryBuilder("noticeDate",beginTime,endTime));

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(companyWarningQuery);
       // searchSourceBuilder.size(30);
        searchRequest.source(searchSourceBuilder);

        List<Warning> warnings = new ArrayList<>();
        SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse.status() == RestStatus.OK) {
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
                        if (c.getCompanyId() == companyId){
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

    /**
     * Elasticsearch之mapping的写入、查看与修改: https://www.cnblogs.com/zlslch/p/6474424.html
     * Elasticsearch之settings
     * curl -XGET "http://172.16.32.35:9200/companywarnings/_mapping?pretty"
     * curl -XGET "http://172.16.32.35:9200/companywarnings/_settings?pretty"
     */
    @Test
    public void test03(){
        // 1. 列出查询条件
        Long companyId = 114082L;
        String beginTime = "2019-07-22";
        String endTime = "2019-10-22";

        // 2.根据查询条件/查询或聚合需求构造QueryBuilder
        BoolQueryBuilder boolFilterQueryBuilder = QueryBuilders.boolQuery();
        boolFilterQueryBuilder.filter(buildRangeQueryBuilder("noticeDate",beginTime,endTime));
        boolFilterQueryBuilder.must(buildNestedQueryBuilder("relatedcompanyInfo","relatedcompanyInfo.companyId",companyId));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolFilterQueryBuilder);
        searchSourceBuilder.size(1);

        NestedAggregationBuilder nestedStat =
                AggregationBuilders.nested("nestedStat","relatedcompanyInfo")
                        .subAggregation(AggregationBuilders.filter("filterTerm", QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                .subAggregation(AggregationBuilders.terms("countSentimental").field("relatedcompanyInfo.sentimental"))
                        )
                        .subAggregation(AggregationBuilders.filter("nestedNegativeLabelStat",
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                        .must(QueryBuilders.rangeQuery("relatedcompanyInfo.sentimental").lt(0))
                                )
                        );

        DateHistogramAggregationBuilder dateHis =
                AggregationBuilders.dateHistogram("dateHis").minDocCount(0).format("yyyy-MM-dd").field("noticeDate")
                        .dateHistogramInterval(DateHistogramInterval.days(1))
                        .extendedBounds(new ExtendedBounds(beginTime,endTime))
                        .subAggregation(AggregationBuilders.nested("dateHistogramHits", "relatedcompanyInfo")
                                .subAggregation(AggregationBuilders.filter("filterTerm",QueryBuilders.termQuery("relatedcompanyInfo.companyId",companyId))
                                        .subAggregation(AggregationBuilders.terms("dailyHits").field("relatedcompanyInfo.sentimental"))
                                )
                        );

        TermsAggregationBuilder newsSourceTerm = AggregationBuilders.terms("newsSources").field("typeCode");

       // searchSourceBuilder.aggregation(nestedStat).aggregation(dateHis).aggregation(newsSourceTerm);
        // 这里经测试,跟aggregation的先后顺序无关,彼此是相互独立的
        searchSourceBuilder.aggregation(newsSourceTerm).aggregation(nestedStat).aggregation(dateHis);

        SearchRequest searchRequest = new SearchRequest(INDEX);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;

        try {
            searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
            if(searchResponse.status()==RestStatus.OK){
                Map<String,Object> map = new HashMap<>();
                Aggregations aggregations = searchResponse.getAggregations();
                Terms terms = aggregations.get("newsSources");
                List<? extends Terms.Bucket> buckets =  terms.getBuckets();
                Map<String,Object> newsSourcesMap = new HashMap<>();
                for(Terms.Bucket bucket:buckets){
                    newsSourcesMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }
                map.put("newsSources",newsSourcesMap);


                Histogram histograms =  aggregations.get("dateHis");
                List<? extends Histogram.Bucket> histogramsBuckets =  histograms.getBuckets();
                Map<String,Object> dateHisList = new HashMap<>();
                Map<String,Object> singleDateHisMap = null;

                for(Histogram.Bucket bucket:histogramsBuckets){
                    singleDateHisMap = new HashMap<>();
                    singleDateHisMap.put("allCount",bucket.getDocCount());
                    Nested date_histogram_hits_nested =  bucket.getAggregations().get("dateHistogramHits");
                    ParsedFilter filter_term = date_histogram_hits_nested.getAggregations().get("filterTerm");
                    ParsedLongTerms daily_hits = filter_term.getAggregations().get("dailyHits");
                    List<? extends Terms.Bucket> daily_hits_Buckets =  daily_hits.getBuckets();
                    long negativeCount = 0;

                    for (int i = 0, daily_hits_bucketsSize = daily_hits_Buckets.size(); i < daily_hits_bucketsSize; i++) {
                        Terms.Bucket daily_hits_Bucket = daily_hits_Buckets.get(i);
                        int v = Integer.valueOf(daily_hits_Bucket.getKey().toString());
                        if (v < 0) {
                            negativeCount = negativeCount + daily_hits_Bucket.getDocCount();
                        }
                    }
                    singleDateHisMap.put("negativeCount", negativeCount);
                    dateHisList.put(String.valueOf(bucket.getKeyAsString()),singleDateHisMap);

                }
                map.put("dateHis",dateHisList);

                Nested nested =  aggregations.get("nestedStat");
                ParsedLongTerms sentimental  = ((ParsedFilter)(nested.getAggregations().get("filterTerm"))).
                        getAggregations().get("countSentimental");
                List<? extends Terms.Bucket> sentimentalBuckets =  sentimental.getBuckets();
                Map<String,Object> sentimentalMap = new HashMap<>();
                for(Terms.Bucket bucket:sentimentalBuckets){
                    sentimentalMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }

                sentimentalMap.put("all",(int) searchResponse.getHits().getTotalHits());
                map.put("sentimental",sentimentalMap);

                System.out.println(JSON.toJSONString(map));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private QueryBuilder buildRangeQueryBuilder(String keyWord,String beginTime,String endTime){
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(keyWord);
        rangeQueryBuilder.lt(DateUtils.getISODateStr(DateUtils.getISODateTime(endTime + " 00:00:00").plusDays(1).minusHours(8L)));
        rangeQueryBuilder.gte(DateUtils.getISODateStr(DateUtils.getISODateTime(beginTime + " 00:00:00").minusHours(8L)));
        return rangeQueryBuilder;
    }

    private QueryBuilder buildNestedQueryBuilder(String path, String queryTerm,Object termValue){
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(path,QueryBuilders.termQuery(queryTerm,termValue),ScoreMode.None);
        return nestedQueryBuilder;
    }

    /**
     * Elasticsearch 数据以json格式导出到本地磁盘文件
     * 数据量如果过大,则分页导出
     */
    @Test
    public void exportEsData(){
        String filePath = "D:\\es\\";
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        int pageSize = 10000;
        long begin = System.currentTimeMillis();
        for (int i = 1;i<= 10;i++){
            searchSourceBuilder.from(pageSize*(i-1));
            searchSourceBuilder.size(pageSize);
            searchRequest.source(searchSourceBuilder);
            BufferedWriter bw = null;
            try {
                SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
                String indexPath = filePath + "es" + i + ".json";
                bw = new BufferedWriter(new FileWriter(indexPath,true));
                if (searchResponse.status() == RestStatus.OK) {
                    SearchHits hits = searchResponse.getHits();
                    // long total = hits.getTotalHits(); 查看总记录条数
                    SearchHit[] searchHits = hits.getHits();
                    for (SearchHit hit : searchHits) {
                        String json = hit.getSourceAsString();
                        if (StringUtils.isNotEmpty(json)){
                            bw.write(json);
                            bw.write("\r\n");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (bw != null){
                    try {
                        bw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        System.out.println("耗时:" + (System.currentTimeMillis() - begin) + "ms");
    }

    /**
     * json格式的数据导入到Elasticsearch
     *  分页导入
     */
    @Test
    public void importData(){
        RestHighLevelClient client = EsClient.client();
        String filePath = "D:\\es\\";
        BufferedReader br = null;
        BulkRequest request = null;
        String json = null;
        for (int i = 1;i<= 10;i++){
            try {
                br = new BufferedReader(new FileReader(filePath + "es" + i + ".json"));
                request = new BulkRequest();
                while ((json = br.readLine()) != null){
                    request.add(new IndexRequest(INDEX).source(json, XContentType.JSON));
                }
                client.bulk(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
