package com.hong.es.controller;

import com.hong.es.entity.to.BaseOutData;
import com.hong.es.entity.to.NewsAndAnnouncementInfoData;
import com.hong.es.util.Contants;
import com.hong.es.util.StringUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/exhibition/news")
public class CompanyExhibitionNewsController {

    @Autowired
    private RestHighLevelClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyExhibitionNewsController.class);


    //"新闻概览"
    @RequestMapping(value = "/newsOverview/{companyId}/{type}", method = RequestMethod.GET)
    public BaseOutData newsOverview(@PathVariable String companyId, @PathVariable String type)  {
        //全量搜索
        BaseOutData outData = new BaseOutData();
        SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        Map<String,Object> resultMap = new HashMap<>();

        int daysSpanCount = 0;

        //处理时间类型
        if("1".equals(type)){
            //一周
            daysSpanCount = -7;

        }else if("2".equals(type)){
            // 一个月
            daysSpanCount = -30;
        }else {
            //3个月
            daysSpanCount = -90;
        }

        String startDateStr ="now"+daysSpanCount+"d/d";

        BoolQueryBuilder boolMainQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolFilterQueryBuilder = QueryBuilders.boolQuery();
        //公司过滤
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("relatedcompyarray",QueryBuilders.termQuery("relatedcompyarray.company_id",companyId),ScoreMode.None);
        boolFilterQueryBuilder.must(nestedQueryBuilder);
        boolFilterQueryBuilder.must(QueryBuilders.termQuery("is_del","0"));
        boolFilterQueryBuilder.must(QueryBuilders.rangeQuery("notice_dt").gte(startDateStr).lte("now+3d/d"));
        boolMainQueryBuilder.filter(boolFilterQueryBuilder);

        searchSourceBuilder.query(boolMainQueryBuilder);
        searchSourceBuilder.size(1);
        TermsAggregationBuilder newsSourceTerm = AggregationBuilders.terms("news_sources").field("source_type");
        DateHistogramAggregationBuilder dateHis =  AggregationBuilders.dateHistogram("dateHis").minDocCount(0).format("yyyy-MM-dd").field("notice_dt").
                dateHistogramInterval( DateHistogramInterval.days(1)).extendedBounds(new ExtendedBounds(startDateStr,"now+3d/d")).
                subAggregation(AggregationBuilders.nested("date_histogram_hits", "relatedcompyarray").subAggregation(AggregationBuilders.filter("filter_term",
                        QueryBuilders.termQuery("relatedcompyarray.company_id",companyId))
                .subAggregation(AggregationBuilders.terms("daily_hits").field("relatedcompyarray.sentimental"))));
        NestedAggregationBuilder nested_stat =  AggregationBuilders.nested("nested_stat","relatedcompyarray").subAggregation(AggregationBuilders.filter("filter_term",
                QueryBuilders.termQuery("relatedcompyarray.company_id",companyId)).
                subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))).
                subAggregation(AggregationBuilders.filter("nested_negative_label_stat",
                       QueryBuilders.boolQuery().must(QueryBuilders.termQuery("relatedcompyarray.company_id",companyId)).must(QueryBuilders.rangeQuery("relatedcompyarray.sentimental").lt(0)) )
                .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").
                        subAggregation(AggregationBuilders.terms("count_l1").field("relatedcompyarray.label.label_l1"))));
        searchSourceBuilder.aggregation(nested_stat).aggregation(dateHis).aggregation(newsSourceTerm);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try{
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if(searchResponse.status()==RestStatus.OK){


                Map<String,Object> map = new HashMap<>();
                //新闻来源
                Terms terms = searchResponse.getAggregations().get("news_sources");
                List<? extends Terms.Bucket> buckets =  terms.getBuckets();
                Map<String,Object> newsSourcesMap = new HashMap<>();
                for(Terms.Bucket bucket:buckets){
                    newsSourcesMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }
                map.put("newsSources",newsSourcesMap);

                Histogram histograms =  searchResponse.getAggregations().get("dateHis");
                List<? extends Histogram.Bucket> histogramsBuckets =  histograms.getBuckets();
                Map<String,Object> dateHisList = new HashMap<>();
                Map<String,Object> singleDateHisMap = null;
                SearchHit[] searchHits = null;
                Map<String, Object> sourceAsMap = null;
                for(Histogram.Bucket bucket:histogramsBuckets){
                    singleDateHisMap = new HashMap<>();
                    singleDateHisMap.put("allCount",bucket.getDocCount());
                    Nested date_histogram_hits_nested =  bucket.getAggregations().get("date_histogram_hits");
                    ParsedFilter filter_term = date_histogram_hits_nested.getAggregations().get("filter_term");
                    ParsedLongTerms daily_hits = filter_term.getAggregations().get("daily_hits");
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

                Nested nested =  searchResponse.getAggregations().get("nested_stat");
                ParsedStringTerms count_l1  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("nested_negative_label_stat"))).getAggregations().get("nested_label_stat")).
                        getAggregations().get("count_l1");
                List<? extends Terms.Bucket> count_l1Buckets =  count_l1.getBuckets();
                Map<String,Object> labelMap = new HashMap<>();
                for(Terms.Bucket bucket:count_l1Buckets){
                    labelMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }

                map.put("countLabel",labelMap);

                ParsedLongTerms sentimental  = ((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).
                        getAggregations().get("count_sentimental");
                List<? extends Terms.Bucket> sentimentalBuckets =  sentimental.getBuckets();
                Map<String,Object> sentimentalMap = new HashMap<>();
                for(Terms.Bucket bucket:sentimentalBuckets){
                    sentimentalMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }

                sentimentalMap.put("all",(int) searchResponse.getHits().getTotalHits());
                map.put("sentimental",sentimentalMap);
                resultMap.put("result",map);


           }
        }catch (IOException e){
            LOGGER.info(e.getMessage());
            outData.setCode("1");
            outData.setMessage(e.getMessage());
        }
        outData.setCode("0");
        outData.setData(resultMap);
        return outData;
    }


   // "新闻详情",
    @RequestMapping(value = "/newsOrAnnouncementDetailView", method = RequestMethod.POST)
    public BaseOutData newsOrAnnouncementDetailView(@RequestBody NewsAndAnnouncementInfoData infoData)  {
        //全量搜索
        BaseOutData outData = new BaseOutData();
        SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        Map<String,Object> resultMap = new HashMap<>();

        int daysSpanCount = -90;

        BoolQueryBuilder boolMainQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder nestBoolQuery = QueryBuilders.boolQuery();

        //公司过滤 以下都用terms 兼容后面多选
        TermsQueryBuilder termCompanyQueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.company_id", infoData.getCompanyIdList());

        //情感过滤(包含正面、中性)
        if(!CollectionUtils.isEmpty(infoData.getSentimentalList())){
            TermsQueryBuilder termSentimentalQueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.sentimental",infoData.getSentimentalList());
            nestBoolQuery.must(termSentimentalQueryBuilder);
        }

        //一级标签
        if(!CollectionUtils.isEmpty(infoData.getLabel1List())||!CollectionUtils.isEmpty(infoData.getLabel2List())) {
            BoolQueryBuilder labelNestBoolQuery = QueryBuilders.boolQuery();
            if(!CollectionUtils.isEmpty(infoData.getLabel1List())){
                TermsQueryBuilder termLabel1QueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.label.label_l1", infoData.getLabel1List());
                labelNestBoolQuery.must(termLabel1QueryBuilder);
            }
            if(!CollectionUtils.isEmpty(infoData.getLabel2List())){
                TermsQueryBuilder termLabel1QueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.label.label_l2", infoData.getLabel2List());
                labelNestBoolQuery.must(termLabel1QueryBuilder);
            }

            NestedQueryBuilder labelNestedQueryBuilder = QueryBuilders.nestedQuery("relatedcompyarray.label",labelNestBoolQuery,ScoreMode.None);
            nestBoolQuery.must(labelNestedQueryBuilder);
        }

        nestBoolQuery.must(termCompanyQueryBuilder);

        NestedAggregationBuilder nested_stat = null;

        //过滤相关公司id为-1，自己本身，以及公司名称为空的关联企业
        BoolQueryBuilder filterCompanyBoolQuery = QueryBuilders.boolQuery();
        infoData.getCompanyIdList().add("-1");
        filterCompanyBoolQuery.mustNot(QueryBuilders.termsQuery("relatedcompyarray.company_id", infoData.getCompanyIdList() )).
                mustNot(QueryBuilders.termQuery("relatedcompyarray.company_nm.keyword", ""));

        //新闻
        if(infoData.getDataType()==0){

            //关联度 TODO
            RangeQueryBuilder relevancyQueryBuilder = QueryBuilders.rangeQuery("relatedcompyarray.relevance").gte((infoData.getRelevancyLevel()-1)*0.2);
            nestBoolQuery.must(relevancyQueryBuilder);

            nested_stat =  AggregationBuilders.nested("nested_stat","relatedcompyarray").subAggregation(AggregationBuilders.filter("filter_term",
                    nestBoolQuery).
                    subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                    .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").subAggregation(AggregationBuilders.terms("count_l1").field("relatedcompyarray.label.label_l1")))).
                    subAggregation(AggregationBuilders.filter("filter_company",
                            filterCompanyBoolQuery)
                            .subAggregation(AggregationBuilders.terms("mention_companies").field("relatedcompyarray.company_nm.keyword").size(10).shardSize(12).
                                    subAggregation(AggregationBuilders.min("min_company_id").field("relatedcompyarray.company_id"))));

        }else{
            //公告
            //提及本企业、本企业公告
            if(infoData.getAnnouncementType()==1){
                //本企业公告
                RangeQueryBuilder relevancyQueryBuilder = QueryBuilders.rangeQuery("relatedcompyarray.relevance").gte(1);
                nestBoolQuery.must(relevancyQueryBuilder);
            }else if(infoData.getAnnouncementType()==2){
                //提及本企业
                RangeQueryBuilder relevancyQueryBuilder = QueryBuilders.rangeQuery("relatedcompyarray.relevance").lt(1);
                nestBoolQuery.must(relevancyQueryBuilder);
            }



            //是否选择一级标签
            nested_stat =  AggregationBuilders.nested("nested_stat","relatedcompyarray").subAggregation(AggregationBuilders.filter("filter_company",
                    filterCompanyBoolQuery)
                    .subAggregation(AggregationBuilders.terms("mention_companies").field("relatedcompyarray.company_nm.keyword").size(10).shardSize(12).
                            subAggregation(AggregationBuilders.min("min_company_id").field("relatedcompyarray.company_id"))));
            if(CollectionUtils.isEmpty(infoData.getLabel1List())&&CollectionUtils.isEmpty(infoData.getLabel2List())){
                //一级标签和二级标签同一层级聚合
                nested_stat.subAggregation(AggregationBuilders.filter("filter_term",
                        nestBoolQuery).
                        subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                        .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").subAggregation(AggregationBuilders.terms("count_l1").
                                field("relatedcompyarray.label.label_l1")).subAggregation(AggregationBuilders.terms("count_l2").
                                field("relatedcompyarray.label.label_l2").size(50))));

            }else if(CollectionUtils.isEmpty(infoData.getLabel1List())&&!CollectionUtils.isEmpty(infoData.getLabel2List())){
                //二级标签勾选，一级标签跟着改变
                nested_stat.subAggregation(AggregationBuilders.filter("filter_term",
                        nestBoolQuery).
                        subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                        .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").subAggregation(AggregationBuilders.filter("count_l1",
                                QueryBuilders.termsQuery("relatedcompyarray.label.label_l2",infoData.getLabel2List()))
                                .subAggregation(AggregationBuilders.terms("l1").
                                field("relatedcompyarray.label.label_l1"))).subAggregation(AggregationBuilders.terms("count_l2").
                                field("relatedcompyarray.label.label_l2").size(50))));

            }else if(!CollectionUtils.isEmpty(infoData.getLabel1List())){
                //一级标签勾选，二级标签改变
                nested_stat.subAggregation(AggregationBuilders.filter("filter_term",
                        nestBoolQuery).
                        subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                        .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").subAggregation(AggregationBuilders.filter("count_l2",
                                QueryBuilders.termsQuery("relatedcompyarray.label.label_l1",infoData.getLabel1List()))
                                .subAggregation(AggregationBuilders.terms("l2").
                                        field("relatedcompyarray.label.label_l2").size(50))).subAggregation(AggregationBuilders.terms("count_l1").
                                field("relatedcompyarray.label.label_l1"))));
            }

        }

        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("relatedcompyarray",nestBoolQuery,ScoreMode.None);
        TermQueryBuilder termDataTypeQueryBuilder = QueryBuilders.termQuery("data_type",infoData.getDataType());

        //关键字过滤
        if(!StringUtil.isEmpty(infoData.getKeyword())){
            TermQueryBuilder termKeywordQueryBuilder = QueryBuilders.termQuery("subject_words.word",infoData.getKeyword());
            NestedQueryBuilder nestedKeywordQueryBuilder = QueryBuilders.nestedQuery("subject_words",termKeywordQueryBuilder,ScoreMode.None);
            boolMainQueryBuilder.must(nestedKeywordQueryBuilder);
        }
        //时间3个月内新闻或公告
        RangeQueryBuilder noticeDtQueryBuilder = QueryBuilders.rangeQuery("notice_dt").gte("now-90d/d")
                .lte("now+3d/d");

        boolMainQueryBuilder.must(nestedQueryBuilder).must(termDataTypeQueryBuilder).must(noticeDtQueryBuilder).must(QueryBuilders.termQuery("is_del","0"));

        //来源过滤
        if(!StringUtil.isEmpty(infoData.getSourceType())){
            boolMainQueryBuilder.mustNot(QueryBuilders.termQuery("source_type",infoData.getSourceType()));

        }

        searchSourceBuilder.query(boolMainQueryBuilder);

        searchSourceBuilder.sort("notice_dt",SortOrder.DESC);
        //分页
        int pageSize = infoData.getPageSize()==0?10:infoData.getPageSize();
        int page = infoData.getPage()==0?1:infoData.getPage();
        searchSourceBuilder.from(pageSize*(page-1));
        searchSourceBuilder.size(pageSize);



        //词云聚合
        NestedAggregationBuilder words_cloud_stat =  AggregationBuilders.nested("words_cloud_stat","subject_words").subAggregation(AggregationBuilders.terms("words_cloud").
                        field("subject_words.word").size(50).shardSize(55).order(BucketOrder.aggregation("sum_value",false)).subAggregation(AggregationBuilders.sum("sum_value").field("subject_words.value")));

        searchSourceBuilder.aggregation(nested_stat).aggregation(words_cloud_stat);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try{
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if(searchResponse.status()==RestStatus.OK){

                Map<String,Object> map = new HashMap<>();

                //主体
                SearchHits hits = searchResponse.getHits();
                long totalHits = hits.getTotalHits();
                outData.setCount((int) totalHits);
                SearchHit[] searchHits = hits.getHits();
                List<Map<String, Object>> mapList = new ArrayList<>();
                Map<String, Object> sourceAsMap = null;
                for (SearchHit hit : searchHits) {
                    // do something with the SearchHit
                    sourceAsMap = hit.getSourceAsMap();
                    mapList.add(sourceAsMap);


                }
                map.put("newsOrAnnouncementArray",mapList);

                //词云
                Nested words_cloud_stat_nested =  searchResponse.getAggregations().get("words_cloud_stat");
                ParsedStringTerms words_cloud =  words_cloud_stat_nested.getAggregations().get("words_cloud");
                List<? extends Terms.Bucket> words_cloudBuckets = words_cloud.getBuckets();
                List<Map<String,Object>> wordsCloudList = new ArrayList<>();
                Map<String,Object> wordsCloudMap = null;
                for(Terms.Bucket bucket:words_cloudBuckets){
                    wordsCloudMap= new HashMap<>();
                    wordsCloudMap.put("name",String.valueOf(bucket.getKey()));
                    wordsCloudMap.put("value",((ParsedSum)bucket.getAggregations().get("sum_value")).getValue());
                    wordsCloudList.add(wordsCloudMap);
                }
                map.put("wordCloud",wordsCloudList);

                Nested nested =  searchResponse.getAggregations().get("nested_stat");
                ParsedStringTerms count_l1 = null;
                ParsedStringTerms count_l2 = null;
                if(infoData.getDataType()==0){
                    count_l1  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                            getAggregations().get("count_l1");

                }
                else{
                    if(CollectionUtils.isEmpty(infoData.getLabel1List())&&CollectionUtils.isEmpty(infoData.getLabel2List())){

                        count_l1  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l1");


                        count_l2  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l2");

                    }else if(CollectionUtils.isEmpty(infoData.getLabel1List())&&!CollectionUtils.isEmpty(infoData.getLabel2List())){
                        ParsedFilter pf =   ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l1");
                         count_l1  = pf.getAggregations().get("l1");
                         count_l2  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l2");

                    }else if(!CollectionUtils.isEmpty(infoData.getLabel1List())){
                        count_l1  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l1");


                        ParsedFilter pf =   ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l2");
                        count_l2  = pf.getAggregations().get("l2");
                    }

                    List<? extends Terms.Bucket> count_l2Buckets =  count_l2.getBuckets();
                    Map<String,Object> label2Map = new HashMap<>();
                    for(Terms.Bucket bucket:count_l2Buckets){
                        label2Map.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                    }

                    map.put("countLabel2",label2Map);
                }

                List<? extends Terms.Bucket> count_l1Buckets =  count_l1.getBuckets();
                Map<String,Object> labelMap = new HashMap<>();
                for(Terms.Bucket bucket:count_l1Buckets){
                    labelMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }

                map.put("countLabel",labelMap);

                ParsedLongTerms sentimental  = ((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).
                        getAggregations().get("count_sentimental");
                List<? extends Terms.Bucket> sentimentalBuckets =  sentimental.getBuckets();
                Map<String,Object> sentimentalMap = new HashMap<>();
                for(Terms.Bucket bucket:sentimentalBuckets){
                    sentimentalMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }
                map.put("sentimental",sentimentalMap);

                //提及的企业
                ParsedStringTerms mention_companies  = ((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_company"))).
                        getAggregations().get("mention_companies");

                List<? extends Terms.Bucket> mention_companiesBuckets =  mention_companies.getBuckets();
                List<Map<String,Object>> mentionCompanyList = new ArrayList<>();
                Map<String,Object> mentionCompanies = null;
                BigDecimal bd = null;
                for(Terms.Bucket bucket:mention_companiesBuckets){
                    mentionCompanies = new HashMap<>();
                    mentionCompanies.put("companyNm",String.valueOf(bucket.getKey()));
                    mentionCompanies.put("mentionCount",bucket.getDocCount());
                    bd = new BigDecimal(((ParsedMin)bucket.getAggregations().get("min_company_id")).getValue());
                    mentionCompanies.put("companyId",bd.toPlainString());
                    mentionCompanyList.add(mentionCompanies);
                }

                map.put("mentionCompanies",mentionCompanyList);


                resultMap.put("result",map);


            }
        }catch (IOException e){
            LOGGER.info(e.getMessage());
            outData.setCode("1");
            outData.setMessage(e.getMessage());
        }
        outData.setCode("0");
        outData.setData(resultMap);
        return outData;
    }

    /**
     * 关联方舆情
     * @param infoData
     * @return
     */
    //@ApiOperation(value = "关联方舆情",httpMethod = "POST")
    @RequestMapping(value = "/relatedPartyDetailView", method = RequestMethod.POST)
    public BaseOutData relatedPartyDetailView(@RequestBody NewsAndAnnouncementInfoData infoData)  {
        //全量搜索
        BaseOutData outData = new BaseOutData();
        if(CollectionUtils.isEmpty(infoData.getCompanyIdList())){
            outData.setCode("0");
            Map<String,Object> map = new HashMap<>();
            Map<String,Object> resultMap = new HashMap<>();
            map.put("newsOrAnnouncementArray",new ArrayList<>());
            map.put("countLabel2",new HashMap<>());
            map.put("countLabel",new HashMap<>());
            map.put("sentimental",new HashMap<>());

            resultMap.put("result",map);
            outData.setData(resultMap);
            return outData;
        }

        SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        Map<String,Object> resultMap = new HashMap<>();


        BoolQueryBuilder boolMainQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder nestBoolQuery = QueryBuilders.boolQuery();

        //公司过滤 以下都用terms 兼容后面多选
        TermsQueryBuilder termCompanyQueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.company_id", infoData.getCompanyIdList());

        //情感过滤(包含正面、中性)
        if(!CollectionUtils.isEmpty(infoData.getSentimentalList())){
            TermsQueryBuilder termSentimentalQueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.sentimental",infoData.getSentimentalList());
            nestBoolQuery.must(termSentimentalQueryBuilder);
        }

        //一级标签
        if(!CollectionUtils.isEmpty(infoData.getLabel1List())||!CollectionUtils.isEmpty(infoData.getLabel2List())) {
            BoolQueryBuilder labelNestBoolQuery = QueryBuilders.boolQuery();
            if(!CollectionUtils.isEmpty(infoData.getLabel1List())){
                TermsQueryBuilder termLabel1QueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.label.label_l1", infoData.getLabel1List());
                labelNestBoolQuery.must(termLabel1QueryBuilder);
            }
            if(!CollectionUtils.isEmpty(infoData.getLabel2List())){
                TermsQueryBuilder termLabel1QueryBuilder = QueryBuilders.termsQuery("relatedcompyarray.label.label_l2", infoData.getLabel2List());
                labelNestBoolQuery.must(termLabel1QueryBuilder);
            }

            NestedQueryBuilder labelNestedQueryBuilder = QueryBuilders.nestedQuery("relatedcompyarray.label",labelNestBoolQuery,ScoreMode.None);
            nestBoolQuery.must(labelNestedQueryBuilder);
        }

        nestBoolQuery.must(termCompanyQueryBuilder);

        NestedAggregationBuilder nested_stat = null;
        //新闻
        if(infoData.getDataType()==0){

            //关联度 三星以及三星以上的新闻TODO
            RangeQueryBuilder relevancyQueryBuilder = QueryBuilders.rangeQuery("relatedcompyarray.relevance").gte((infoData.getRelevancyLevel()-1)*0.2);
            nestBoolQuery.must(relevancyQueryBuilder);

            nested_stat =  AggregationBuilders.nested("nested_stat","relatedcompyarray").subAggregation(AggregationBuilders.filter("filter_term",
                    nestBoolQuery).
                    subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                    .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").
                            subAggregation(AggregationBuilders.terms("count_l1").field("relatedcompyarray.label.label_l1"))));

        }else{
            //公告
            //本企业公告
            RangeQueryBuilder relevancyQueryBuilder = QueryBuilders.rangeQuery("relatedcompyarray.relevance").gte(1);
            nestBoolQuery.must(relevancyQueryBuilder);

            //是否选择一级标签
            nested_stat =  AggregationBuilders.nested("nested_stat","relatedcompyarray");
            if(CollectionUtils.isEmpty(infoData.getLabel1List())&&CollectionUtils.isEmpty(infoData.getLabel2List())){
                //一级标签和二级标签同一层级聚合
                nested_stat.subAggregation(AggregationBuilders.filter("filter_term",
                        nestBoolQuery).
                        subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                        .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").subAggregation(AggregationBuilders.terms("count_l1").
                                field("relatedcompyarray.label.label_l1")).subAggregation(AggregationBuilders.terms("count_l2").
                                field("relatedcompyarray.label.label_l2").size(50))));

            }else if(CollectionUtils.isEmpty(infoData.getLabel1List())&&!CollectionUtils.isEmpty(infoData.getLabel2List())){
                //二级标签勾选，一级标签跟着改变
                nested_stat.subAggregation(AggregationBuilders.filter("filter_term",
                        nestBoolQuery).
                        subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                        .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").subAggregation(AggregationBuilders.filter("count_l1",
                                QueryBuilders.termsQuery("relatedcompyarray.label.label_l2",infoData.getLabel2List()))
                                .subAggregation(AggregationBuilders.terms("l1").
                                        field("relatedcompyarray.label.label_l1"))).subAggregation(AggregationBuilders.terms("count_l2").
                                field("relatedcompyarray.label.label_l2").size(50))));

            }else if(!CollectionUtils.isEmpty(infoData.getLabel1List())){
                //一级标签勾选，二级标签改变
                nested_stat.subAggregation(AggregationBuilders.filter("filter_term",
                        nestBoolQuery).
                        subAggregation(AggregationBuilders.terms("count_sentimental").field("relatedcompyarray.sentimental"))
                        .subAggregation( AggregationBuilders.nested("nested_label_stat","relatedcompyarray.label").subAggregation(AggregationBuilders.filter("count_l2",
                                QueryBuilders.termsQuery("relatedcompyarray.label.label_l1",infoData.getLabel1List()))
                                .subAggregation(AggregationBuilders.terms("l2").
                                        field("relatedcompyarray.label.label_l2").size(50))).subAggregation(AggregationBuilders.terms("count_l1").
                                field("relatedcompyarray.label.label_l1"))));
            }

        }

        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("relatedcompyarray",nestBoolQuery,ScoreMode.None);
        TermQueryBuilder termDataTypeQueryBuilder = QueryBuilders.termQuery("data_type",infoData.getDataType());

        //时间3个月内新闻或公告
        RangeQueryBuilder noticeDtQueryBuilder = QueryBuilders.rangeQuery("notice_dt").gte("now-90d/d")
                .lte("now+3d/d");

        boolMainQueryBuilder.must(nestedQueryBuilder).must(termDataTypeQueryBuilder).must(noticeDtQueryBuilder).must(QueryBuilders.termQuery("is_del","0"));;

        //来源过滤
        if(!StringUtil.isEmpty(infoData.getSourceType())){
            boolMainQueryBuilder.mustNot(QueryBuilders.termQuery("source_type",infoData.getSourceType()));

        }
        searchSourceBuilder.query(boolMainQueryBuilder);

        searchSourceBuilder.sort("notice_dt",SortOrder.DESC);
        //分页
        int pageSize = infoData.getPageSize()==0?10:infoData.getPageSize();
        int page = infoData.getPage()==0?1:infoData.getPage();
        searchSourceBuilder.from(pageSize*(page-1));
        searchSourceBuilder.size(pageSize);

        searchSourceBuilder.aggregation(nested_stat);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try{
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if(searchResponse.status()==RestStatus.OK){

                Map<String,Object> map = new HashMap<>();

                //主体
                SearchHits hits = searchResponse.getHits();
                long totalHits = hits.getTotalHits();
                outData.setCount((int) totalHits);
                SearchHit[] searchHits = hits.getHits();
                List<Map<String, Object>> mapList = new ArrayList<>();
                Map<String, Object> sourceAsMap = null;
                for (SearchHit hit : searchHits) {
                    // do something with the SearchHit
                    sourceAsMap = hit.getSourceAsMap();
                    mapList.add(sourceAsMap);


                }
                map.put("newsOrAnnouncementArray",mapList);

                Nested nested =  searchResponse.getAggregations().get("nested_stat");
                ParsedStringTerms count_l1 = null;
                ParsedStringTerms count_l2 = null;
                if(infoData.getDataType()==0){
                    count_l1  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                            getAggregations().get("count_l1");

                }
                else{
                    if(CollectionUtils.isEmpty(infoData.getLabel1List())&&CollectionUtils.isEmpty(infoData.getLabel2List())){

                        count_l1  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l1");


                        count_l2  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l2");

                    }else if(CollectionUtils.isEmpty(infoData.getLabel1List())&&!CollectionUtils.isEmpty(infoData.getLabel2List())){
                        ParsedFilter pf =   ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l1");
                        count_l1  = pf.getAggregations().get("l1");
                        count_l2  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l2");

                    }else if(!CollectionUtils.isEmpty(infoData.getLabel1List())){
                        count_l1  = ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l1");


                        ParsedFilter pf =   ((ParsedNested)((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).getAggregations().get("nested_label_stat")).
                                getAggregations().get("count_l2");
                        count_l2  = pf.getAggregations().get("l2");
                    }

                    List<? extends Terms.Bucket> count_l2Buckets =  count_l2.getBuckets();
                    Map<String,Object> label2Map = new HashMap<>();
                    for(Terms.Bucket bucket:count_l2Buckets){
                        label2Map.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                    }

                    map.put("countLabel2",label2Map);
                }

                List<? extends Terms.Bucket> count_l1Buckets =  count_l1.getBuckets();
                Map<String,Object> labelMap = new HashMap<>();
                for(Terms.Bucket bucket:count_l1Buckets){
                    labelMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }

                map.put("countLabel",labelMap);

                ParsedLongTerms sentimental  = ((ParsedFilter)((Aggregation)nested.getAggregations().get("filter_term"))).
                        getAggregations().get("count_sentimental");
                List<? extends Terms.Bucket> sentimentalBuckets =  sentimental.getBuckets();
                Map<String,Object> sentimentalMap = new HashMap<>();
                for(Terms.Bucket bucket:sentimentalBuckets){
                    sentimentalMap.put(String.valueOf(bucket.getKey()),bucket.getDocCount());
                }
                map.put("sentimental",sentimentalMap);

                resultMap.put("result",map);
            }
        }catch (IOException e){
            LOGGER.info(e.getMessage());
            outData.setCode("1");
            outData.setMessage(e.getMessage());
        }
        outData.setCode("0");
        outData.setData(resultMap);
        return outData;
    }

    /**
     * 获得1级标签列表
     *
     * @return
     */
    //@ApiOperation(value = "获得1级标签列表",httpMethod = "GET")
    @RequestMapping(value = "/getLabel1", method = RequestMethod.GET)
    public BaseOutData getAllLabel1() {
        BaseOutData outData = new BaseOutData();
        SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("is_del","0"));
        searchSourceBuilder.size(0);
        NestedAggregationBuilder aggBuilder = AggregationBuilders.nested("nested_stat", "relatedcompyarray")
                .subAggregation(AggregationBuilders.nested("nested_label_stat", "relatedcompyarray.label")
                        .subAggregation(AggregationBuilders.terms("count_l1").field("relatedcompyarray.label.label_l1")));

        searchSourceBuilder.aggregation(aggBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        List<String> label1s = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        try{
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if(searchResponse.status()==RestStatus.OK){
                ParsedStringTerms count_l1  = ((ParsedNested)((ParsedNested)(searchResponse.getAggregations().get("nested_stat"))).getAggregations().get("nested_label_stat")).
                        getAggregations().get("count_l1");
                List<? extends Terms.Bucket> count_l1Buckets =  count_l1.getBuckets();
                for(Terms.Bucket bucket:count_l1Buckets){
                    label1s.add(String.valueOf(bucket.getKey()));
                }
            }
        }catch (IOException e){
            LOGGER.info(e.getMessage());
            outData.setCode("1");
            outData.setMessage(e.getMessage());
        }
        resultMap.put("result", label1s);
        outData.setCode("0");
        outData.setData(resultMap);
        return outData;
    }

   
}