package com.hong.es.controller;

import com.hong.es.entity.to.BaseOutData;
import com.hong.es.entity.to.NewsOut;
import com.hong.es.entity.to.NewsSearchConditon;
import com.hong.es.util.Contants;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNested;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
//@Api(description = "新闻搜索接口")
@RequestMapping(value = "/company/news/search")
public class CompanyNewsSearchController {

    @Autowired
    private RestHighLevelClient client;
    private static final Logger logger = LoggerFactory.getLogger(CompanyNewsSearchController.class);

    private static final List<String> labelL1List = new ArrayList<>(Arrays.asList("经营风险","治理和管理风险","财务风险","证券市场风险","信用风险","不可抗力风险"));
    private static final List<String> sentimentalList = new ArrayList<>(Arrays.asList("正面","中性","一般负面","重点负面","严重负面"));

    /**
     * 新闻详情(新闻详情页)
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/detail/{newsId}", method = RequestMethod.GET)
    public BaseOutData getDetail(@PathVariable String newsId) {
        BaseOutData outData = new BaseOutData();
        Map data = new HashMap();
        try {
            SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //构建bool查询
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            boolBuilder.must(QueryBuilders.termQuery("_id", newsId));
            boolBuilder.must(QueryBuilders.termQuery("is_del", "0"));

            searchSourceBuilder.query(boolBuilder);
            //请求
            searchRequest.source(searchSourceBuilder);
            //调用
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.status() == RestStatus.OK) {
                SearchHits hits = searchResponse.getHits();
                SearchHit[] searchHits = hits.getHits();
                Map<String, Object> sourceAsMap;
                for (SearchHit hit : searchHits) {
                    sourceAsMap = hit.getSourceAsMap();
                    NewsOut newsOut = this.newsToObject(sourceAsMap);

                    List<Map<String, Object>> recommendNews;
                    //根据关联企业获取相关新闻
                    List<String> companyIdList = new ArrayList<>();
                    for (Map<String, Object> company : newsOut.getRelatedcompyarray()) {
                        if (company.get("company_id")!=null)
                            companyIdList.add(company.get("company_id").toString());
                    }
                    recommendNews = this.getRecommendNews(companyIdList,newsOut.getInfoSid());
                    if ((recommendNews == null || recommendNews.size() == 0) && newsOut.getSubjectWords()!=null) {
                        //根据关键字获取相关新闻
                        List<String> subjectWordsList = new ArrayList<>();
                        for (Map<String, Object> subjectWords : newsOut.getSubjectWords()) {
                            subjectWordsList.add(subjectWords.get("word").toString());
                        }
                        recommendNews = this.getRecommendNews(subjectWordsList);
                    }
                    newsOut.setRecommendNews(recommendNews);
                    data.put("result", newsOut);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            outData.setCode("1");
            outData.setMessage("系统报错!" + e.getMessage());
            return outData;
        }
        outData.setCode("0");
        outData.setMessage("返回成功!");
        outData.setData(data);
        return outData;
    }
    //@ApiOperation(value = "时间轴接口",httpMethod = "POST")
    @RequestMapping(value = "/timerShaft", method = RequestMethod.POST)
    public BaseOutData timerShaft(@RequestBody Map<String,String> inData) {
        //获取参数
        String companyId = inData.get("companyId");
        String type = inData.get("type");//1：全部 2：正面 3：负面
        int curPage = StringUtils.isNotBlank(inData.get("curPage"))?Integer.valueOf(inData.get("curPage")):1;
        int rowNum = StringUtils.isNotBlank(inData.get("rowNum"))?Integer.valueOf(inData.get("rowNum")):10;
        int start = rowNum * curPage;
        int end = start-rowNum+1;
        String startStr = start == 0 ? "now-10d/d" : "now-" + start + "d/d";
        String endStr = end == 1 ? "now+3d/d" : "now-" + end + "d/d";

        BaseOutData outData = new BaseOutData();
        Map data = new HashMap();
        List<Map<String, Object>> returnList = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //构建bool查询
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            boolBuilder.must(QueryBuilders.nestedQuery("relatedcompyarray", QueryBuilders.termQuery("relatedcompyarray.company_id", companyId), ScoreMode.None));
            boolBuilder.must(QueryBuilders.rangeQuery("notice_dt").gte(startStr).lte(endStr));
            //过滤删除的文档
            boolBuilder.mustNot(QueryBuilders.termQuery("is_del", "1"));
            searchSourceBuilder.query(boolBuilder);
            //构建聚合
            String[] includes = {"info_sid", "data_title", "notice_dt", "data_type","src_url"};
            AggregationBuilder aggregationBuilder;
            if (type.equals("1")) {
                aggregationBuilder = AggregationBuilders.dateHistogram("dateHis").field("notice_dt").dateHistogramInterval(DateHistogramInterval.days(1)).
                        format("yyyy-MM-dd").minDocCount(1).
                        subAggregation(AggregationBuilders.nested("relatedcompyarrayNested", "relatedcompyarray").
                                subAggregation(AggregationBuilders.filter("filter_term", QueryBuilders.termQuery("relatedcompyarray.company_id", companyId)).
                                        subAggregation(AggregationBuilders.range("daily_positive").field("relatedcompyarray.sentimental").keyed(true).addUnboundedFrom("positive", 1).
                                                subAggregation(AggregationBuilders.reverseNested("backToTop").
                                                        subAggregation(AggregationBuilders.topHits("final_hit").fetchSource(includes, null).size(5).
                                                                sort(SortBuilders.fieldSort("relatedcompyarray.relevance").order(SortOrder.DESC).setNestedPath("relatedcompyarray").setNestedFilter(QueryBuilders.termQuery("relatedcompyarray.company_id",companyId))).
                                                                sort(SortBuilders.fieldSort("notice_dt").order(SortOrder.DESC))))).
                                        subAggregation(AggregationBuilders.range("daily_negative").field("relatedcompyarray.sentimental").keyed(true).addUnboundedTo("negative", 0).
                                                subAggregation(AggregationBuilders.nested("nested_label", "relatedcompyarray.label").
                                                        subAggregation(AggregationBuilders.terms("aggs_lable1").field("relatedcompyarray.label.label_l1").size(20).
                                                                subAggregation(AggregationBuilders.reverseNested("backToTop").
                                                                        subAggregation(AggregationBuilders.topHits("final_hit").fetchSource(includes, null).size(5).
                                                                                sort(SortBuilders.fieldSort("relatedcompyarray.sentimental").order(SortOrder.ASC).setNestedSort(new NestedSortBuilder("relatedcompyarray").setFilter(QueryBuilders.termQuery("relatedcompyarray.company_id",companyId)))).
                                                                                sort(SortBuilders.fieldSort("relatedcompyarray.relevance").order(SortOrder.DESC).setNestedSort(new NestedSortBuilder("relatedcompyarray").setFilter(QueryBuilders.termQuery("relatedcompyarray.company_id",companyId)))).
                                                                                sort(SortBuilders.fieldSort("notice_dt").order(SortOrder.DESC)))))))));
            } else if (type.equals("2")) {
                aggregationBuilder = AggregationBuilders.dateHistogram("dateHis").field("notice_dt").dateHistogramInterval(DateHistogramInterval.days(1)).
                        format("yyyy-MM-dd").minDocCount(1).
                        subAggregation(AggregationBuilders.nested("relatedcompyarrayNested", "relatedcompyarray").
                                subAggregation(AggregationBuilders.filter("filter_term", QueryBuilders.termQuery("relatedcompyarray.company_id", companyId)).
                                        subAggregation(AggregationBuilders.range("daily_positive").field("relatedcompyarray.sentimental").keyed(true).addUnboundedFrom("positive", 1).
                                                subAggregation(AggregationBuilders.reverseNested("backToTop").
                                                        subAggregation(AggregationBuilders.topHits("final_hit").fetchSource(includes, null).size(5).
                                                                sort(SortBuilders.fieldSort("relatedcompyarray.relevance").order(SortOrder.DESC).setNestedSort(new NestedSortBuilder("relatedcompyarray").setFilter(QueryBuilders.termQuery("relatedcompyarray.company_id",companyId)))).
                                                                sort(SortBuilders.fieldSort("notice_dt").order(SortOrder.DESC)))))));
            } else {
                aggregationBuilder = AggregationBuilders.dateHistogram("dateHis").field("notice_dt").dateHistogramInterval(DateHistogramInterval.days(1)).
                        format("yyyy-MM-dd").minDocCount(1).
                        subAggregation(AggregationBuilders.nested("relatedcompyarrayNested", "relatedcompyarray").
                                subAggregation(AggregationBuilders.filter("filter_term", QueryBuilders.termQuery("relatedcompyarray.company_id", companyId)).
                                        subAggregation(AggregationBuilders.range("daily_negative").field("relatedcompyarray.sentimental").keyed(true).addUnboundedTo("negative", 0).
                                                subAggregation(AggregationBuilders.nested("nested_label", "relatedcompyarray.label").
                                                        subAggregation(AggregationBuilders.terms("aggs_lable1").field("relatedcompyarray.label.label_l1").size(20).
                                                                subAggregation(AggregationBuilders.reverseNested("backToTop").
                                                                        subAggregation(AggregationBuilders.topHits("final_hit").fetchSource(includes, null).size(5).
                                                                                sort(SortBuilders.fieldSort("relatedcompyarray.sentimental").order(SortOrder.ASC).setNestedSort(new NestedSortBuilder("relatedcompyarray").setFilter(QueryBuilders.termQuery("relatedcompyarray.company_id",companyId)))).
                                                                                sort(SortBuilders.fieldSort("relatedcompyarray.relevance").order(SortOrder.DESC).setNestedSort(new NestedSortBuilder("relatedcompyarray").setFilter(QueryBuilders.termQuery("relatedcompyarray.company_id",companyId)))).
                                                                                sort(SortBuilders.fieldSort("notice_dt").order(SortOrder.DESC)))))))));
            }

            //不返回记录
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggregationBuilder);
            //请求
            searchRequest.source(searchSourceBuilder);
            //调用
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.status() == RestStatus.OK) {
                Histogram histograms =  searchResponse.getAggregations().get("dateHis");
                List<? extends Histogram.Bucket> histogramsBuckets = histograms.getBuckets();
                for (Histogram.Bucket bucket : histogramsBuckets) {
                    Map histogramMap = new HashMap();
                    Map sentimentalMap = new HashMap();
                    Nested relatedcompyarrayNested = bucket.getAggregations().get("relatedcompyarrayNested");
                    Filter filter_term = relatedcompyarrayNested.getAggregations().get("filter_term");
                    if (type.equals("1")) {
                        this.getPositiveDate(filter_term,sentimentalMap);
                        this.getNegativeDate(filter_term,sentimentalMap);
                    } else if (type.equals("2")) {
                        this.getPositiveDate(filter_term,sentimentalMap);
                    } else {
                        this.getNegativeDate(filter_term,sentimentalMap);
                    }
                    if (sentimentalMap.get("positive") != null || sentimentalMap.get("negative") != null) {
                        histogramMap.put(bucket.getKeyAsString(), sentimentalMap);
                        returnList.add(histogramMap);
                    }
                }
                data.put("result", returnList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            outData.setCode("1");
            outData.setMessage("系统报错!");
            return outData;
        }
        outData.setCode("0");
        outData.setMessage("返回成功!");
        outData.setData(data);
        return outData;
    }

    /***
     * 负面数据
     * @param filter_term
     * @param sentimentalMap
     */
    public void getNegativeDate(Filter filter_term,Map sentimentalMap) {
        Range daily_negative = filter_term.getAggregations().get("daily_negative");
        List<? extends Range.Bucket> negativeBucket = daily_negative.getBuckets();
        Nested nested_label = negativeBucket.get(0).getAggregations().get("nested_label");
        Terms aggs_lable1 = nested_label.getAggregations().get("aggs_lable1");
        List<? extends Terms.Bucket> label1Buckets = aggs_lable1.getBuckets();
        Map label1Map = new HashMap();
        for (Terms.Bucket label1Bucket : label1Buckets) {
            long docCount = label1Bucket.getDocCount();
            ReverseNested backToTop = label1Bucket.getAggregations().get("backToTop");
            TopHits final_hit = backToTop.getAggregations().get("final_hit");
            SearchHits hits = final_hit.getHits();
            SearchHit[] searchHits = hits.getHits();
            Map label1StaMap = new HashMap();
            List<Map<String, Object>> hitList = new ArrayList<>();
            for (SearchHit hit : searchHits) {
                Map<String,Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, Object> hitMap = new HashMap<>();
                hitMap.put("infoSid", sourceAsMap.get("info_sid"));
                hitMap.put("dataTitle", sourceAsMap.get("data_title"));
                hitMap.put("noticeDt", sourceAsMap.get("notice_dt"));
                hitMap.put("dataType", sourceAsMap.get("data_type"));
                hitMap.put("srcUrl", sourceAsMap.get("src_url"));
                hitList.add(hitMap);
            }
            label1StaMap.put("count", hits.getTotalHits());
            label1StaMap.put("list", hitList);
            label1Map.put(label1Bucket.getKey(), label1StaMap);
        }
        if (label1Buckets.size() > 0) {
            sentimentalMap.put("negative", label1Map);
        }
    }

    /***
     * 正面数据
     * @param filter_term
     * @param sentimentalMap
     */
    public void getPositiveDate(Filter filter_term,Map sentimentalMap) {
        Map positiveMap = new HashMap();
        List<Map<String, Object>> hitList = new ArrayList<>();
        Range daily_positive = filter_term.getAggregations().get("daily_positive");
        List<? extends Range.Bucket> positiveBuckets = daily_positive.getBuckets();
        ReverseNested backToTop = positiveBuckets.get(0).getAggregations().get("backToTop");
        TopHits final_hit = backToTop.getAggregations().get("final_hit");
        SearchHits hits = final_hit.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            Map<String,Object> sourceAsMap = hit.getSourceAsMap();
            Map<String, Object> hitMap = new HashMap<>();
            hitMap.put("infoSid", sourceAsMap.get("info_sid"));
            hitMap.put("dataTitle", sourceAsMap.get("data_title"));
            hitMap.put("noticeDt", sourceAsMap.get("notice_dt"));
            hitMap.put("dataType", sourceAsMap.get("data_type"));
            hitMap.put("srcUrl", sourceAsMap.get("src_url"));
            hitList.add(hitMap);
        }
        if (hits.getTotalHits() > 0) {
            positiveMap.put("count", hits.getTotalHits());
            positiveMap.put("list", hitList);
            sentimentalMap.put("positive", positiveMap);
        }
    }

    /**
     * 设置bool查询
     * @param boolBuilder
     * @param condition
     */
//    public void setBoolBuilder(BoolQueryBuilder boolBuilder, NewsSearchConditon condition) {
//        boolBuilder.should(QueryBuilders.multiMatchQuery(condition.getKeyword(),"data_title").minimumShouldMatch("75%"));
//        boolBuilder.should(QueryBuilders.nestedQuery("relatedcompyarray", QueryBuilders.boolQuery().
//                        should(this.getBoolQueryBuilder(condition,QueryBuilders.termQuery("relatedcompyarray.company_nm.keyword", condition.getKeyword()).boost(9))).
//                        should(this.getBoolQueryBuilder(condition,QueryBuilders.matchPhraseQuery("relatedcompyarray.company_nm",condition.getKeyword()).boost(6)))
//                , ScoreMode.Avg));
//    }

    /***
     * 构建FunctionScoreQueryBuilder
     * @param boolBuilder
     * @return
     */
    public FunctionScoreQueryBuilder getFunctionScoreQueryBuilder(BoolQueryBuilder boolBuilder) {
        //构建functions
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[3];
        //利用高斯函数重新计算得分
        ScoreFunctionBuilder<GaussDecayFunctionBuilder> gaussDecayFunctionBuilder = new GaussDecayFunctionBuilder("notice_dt", "now", "20d", "1d");
        gaussDecayFunctionBuilder.setWeight(70);
        FunctionScoreQueryBuilder.FilterFunctionBuilder gauss = new FunctionScoreQueryBuilder.FilterFunctionBuilder(gaussDecayFunctionBuilder);
        filterFunctionBuilders[0]= gauss;
        //当天数据加权重
        ScoreFunctionBuilder<WeightBuilder> scoreFunctionBuilder = new WeightBuilder();
        scoreFunctionBuilder.setWeight(1.5f);
        FunctionScoreQueryBuilder.FilterFunctionBuilder today = new FunctionScoreQueryBuilder.FilterFunctionBuilder
                (QueryBuilders.rangeQuery("notice_dt").gte("now-1d"),scoreFunctionBuilder);
        filterFunctionBuilders[1]= today;
        //之前得分为零的，最后得分仍然是零
        ScoreFunctionBuilder<ScriptScoreFunctionBuilder> scriptScoreFunctionBuilder = new ScriptScoreFunctionBuilder(new Script("return _score  == 0 ? 0 : 1.0"));
        FunctionScoreQueryBuilder.FilterFunctionBuilder script = new FunctionScoreQueryBuilder.FilterFunctionBuilder(scriptScoreFunctionBuilder);
        filterFunctionBuilders[2]= script;

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(boolBuilder, filterFunctionBuilders).
                boostMode(CombineFunction.SUM).scoreMode(FunctionScoreQuery.ScoreMode.MULTIPLY);
        return functionScoreQueryBuilder;
    }

    /**
     * 新闻推荐
     * @param subjectWords
     * @return
     */
    public List<Map<String,Object>> getRecommendNews(List<String> subjectWords) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            TermsQueryBuilder wordTermsQueryBuilder = QueryBuilders.termsQuery("subject_words.word", subjectWords);
            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("subject_words", wordTermsQueryBuilder, ScoreMode.Avg);
            searchSourceBuilder.query(nestedQueryBuilder);
            searchSourceBuilder.size(5);
            //请求
            searchRequest.source(searchSourceBuilder);
            //调用
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.status() == RestStatus.OK) {
                SearchHits hits = searchResponse.getHits();
                SearchHit[] searchHits = hits.getHits();
                Map<String, Object> sourceAsMap;
                for (SearchHit hit : searchHits) {
                    sourceAsMap = hit.getSourceAsMap();
                    NewsOut newsOut = this.newsToObject(sourceAsMap);
                    Map map = new HashMap();
                    map.put("infoSid", newsOut.getInfoSid());
                    map.put("dataTitle", newsOut.getDataTitle());
                    map.put("sourceType", newsOut.getSourceType());
                    map.put("noticeDt", newsOut.getNoticeDt());
                    list.add(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 新闻推荐
     * @param companyIds
     * @param newsId
     * @return
     */
    public List<Map<String,Object>> getRecommendNews(List<String> companyIds,String newsId) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(Contants.ES_INDEX_NEWS_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.nestedQuery("relatedcompyarray", QueryBuilders.termsQuery("relatedcompyarray.company_id", companyIds), ScoreMode.Avg)).
                    mustNot(QueryBuilders.termQuery("info_sid", newsId));
            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.size(5);
            //排序
            searchSourceBuilder.sort(SortBuilders.fieldSort("notice_dt").order(SortOrder.DESC));
            //请求
            searchRequest.source(searchSourceBuilder);
            //调用
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.status() == RestStatus.OK) {
                SearchHits hits = searchResponse.getHits();
                SearchHit[] searchHits = hits.getHits();
                Map<String, Object> sourceAsMap;
                for (SearchHit hit : searchHits) {
                    sourceAsMap = hit.getSourceAsMap();
                    NewsOut newsOut = this.newsToObject(sourceAsMap);
                    Map map = new HashMap();
                    map.put("infoSid", newsOut.getInfoSid());
                    map.put("dataTitle", newsOut.getDataTitle());
                    map.put("sourceType", newsOut.getSourceType());
                    map.put("noticeDt", newsOut.getNoticeDt());
                    list.add(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public NewsOut newsToObject(Map<String, Object> sourceAsMap) {
        NewsOut newsOut = new NewsOut();
        if (sourceAsMap.get("relatedcompyarray") != null){
            List<Map<String,Object>> relatedCompanyList = (List<Map<String,Object>>)sourceAsMap.get("relatedcompyarray");
            //根据关联度排序
            Collections.sort(relatedCompanyList, (Comparator<Map>) (arg0, arg1) -> Float.valueOf(arg1.get("relevance").toString()).compareTo(Float.valueOf(arg0.get("relevance").toString())));
            //处理label
            for (Map<String, Object> company : relatedCompanyList) {
                List tempLabelList = new ArrayList();
                if (company.get("label") instanceof List) {
                    List<Map<String,Object>> labelList = (List) company.get("label");
                    for (Map<String, Object> label : labelList) {
                        Map tempLabel = new HashMap();
                        if (label.get("label_l1") == null) {
                            tempLabel.put("label_l1", "");
                        } else {
                            tempLabel.put("label_l1", label.get("label_l1"));
                        }

                        if (label.get("label_l2") == null) {
                            tempLabel.put("label_l2", new ArrayList());
                        }else if (label.get("label_l2") instanceof String) {
                            tempLabel.put("label_l2", new ArrayList());
                        } else if (label.get("label_l2") instanceof List) {
                            List label_l2 = (List) label.get("label_l2");
                            if (label_l2.size() == 1 && label_l2.get(0).equals("")) {
                                tempLabel.put("label_l2", new ArrayList());
                            } else {
                                tempLabel.put("label_l2", label.get("label_l2"));
                            }
                        } else {
                            tempLabel.put("label_l2", label.get("label_l2"));
                        }
                        tempLabelList.add(tempLabel);
                    }
                }
                company.put("label", tempLabelList);
            }
            newsOut.setRelatedcompyarray(relatedCompanyList);
        }
        if (sourceAsMap.get("subject_words") != null){
            newsOut.setSubjectWords((List<Map<String,Object>>)sourceAsMap.get("subject_words"));
        }
        if (sourceAsMap.get("source_type") != null){
            newsOut.setSourceType(String.valueOf(sourceAsMap.get("source_type")));
        }
        if (sourceAsMap.get("data_type") != null){
            newsOut.setDataType(String.valueOf(sourceAsMap.get("data_type")));
        }
        if(null != sourceAsMap.get("data_title")) {
            newsOut.setDataTitle(String.valueOf(sourceAsMap.get("data_title")));
        }
        if(null != sourceAsMap.get("author")) {
            newsOut.setAuthor(String.valueOf(sourceAsMap.get("author")));
        }
        if(null != sourceAsMap.get("src_url")) {
            newsOut.setSrcUrl(String.valueOf(sourceAsMap.get("src_url")));
        }
        if(null != sourceAsMap.get("content")) {
            newsOut.setContent(String.valueOf(sourceAsMap.get("content")));
        }
        if(null != sourceAsMap.get("sim_hash")) {
            newsOut.setSimHash(String.valueOf(sourceAsMap.get("sim_hash")));
        }
        if(null != sourceAsMap.get("publish_site")) {
            newsOut.setPublishSite(String.valueOf(sourceAsMap.get("publish_site")));
        }
        if (sourceAsMap.get("notice_dt") != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            newsOut.setNoticeDt(String.valueOf(sourceAsMap.get("notice_dt")));
        }
        if(null != sourceAsMap.get("info_cd")) {
            newsOut.setInfoCd(String.valueOf(sourceAsMap.get("info_cd")));
        }
        if(null != sourceAsMap.get("info_sid")) {
            newsOut.setInfoSid(String.valueOf(sourceAsMap.get("info_sid")));
        }
        return newsOut;
    }


//    public BoolQueryBuilder getBoolQueryBuilder(NewsSearchConditon condition, QueryBuilder queryBuilder) {
//        BoolQueryBuilder relatedcompyarrayBoolQueryBuilder = this.handleCompyarrayFilter(condition);
//        if (relatedcompyarrayBoolQueryBuilder.must().size() == 0) {
//            relatedcompyarrayBoolQueryBuilder = QueryBuilders.boolQuery();
//        }
//        relatedcompyarrayBoolQueryBuilder.must(queryBuilder);
//        return relatedcompyarrayBoolQueryBuilder;
//    }

    public void conditionFilter(BoolQueryBuilder boolBuilder, NewsSearchConditon condition) throws ParseException {
        //过滤企业名称为空的新闻
        boolBuilder.mustNot(QueryBuilders.nestedQuery("relatedcompyarray", QueryBuilders.termQuery("relatedcompyarray.company_nm.keyword", ""), ScoreMode.None));
        //过滤已删除的记录
        boolBuilder.mustNot(QueryBuilders.termQuery("is_del", "1"));
        //时间过滤，notice_dt要小于now+3d/d
        boolBuilder.filter(QueryBuilders.rangeQuery("notice_dt").lte("now+3d/d"));
        //媒体类型
        if(StringUtils.isNotEmpty(condition.getMediaType())) {
            String[] mediaTypeArr = condition.getMediaType()!=null?condition.getMediaType().split(","):null;
            String[] mediaTypeQuery = new String[mediaTypeArr.length];
            for (int i = 0; i < mediaTypeArr.length; i++) {
                if (mediaTypeArr[i].equals("新闻")) {
                    mediaTypeQuery[i] = "0";
                } else {
                    mediaTypeQuery[i] = "1";
                }
            }
            boolBuilder.filter(QueryBuilders.termsQuery("data_type", mediaTypeQuery));
        }

        //发布时间
        if(StringUtils.isNotEmpty(condition.getPublishTime())) {
            String[] foundDtArr = condition.getPublishTime().split("-");
            String startDateStr = foundDtArr[0];
            String endDateStr = foundDtArr[1];
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = formatter.parse(startDateStr);
            Date endTime = formatter.parse(endDateStr);

            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("notice_dt");
            rangeQueryBuilder.gte(sdf.format(startTime));
            rangeQueryBuilder.lte(sdf.format(endTime));
            boolBuilder.filter(rangeQueryBuilder);
        }

        //过滤relatedcompyarray里面的字段
        if (StringUtils.isNotEmpty(condition.getRelateCompanyType()) || StringUtils.isNotEmpty(condition.getSentimental()) || StringUtils.isNotEmpty(condition.getRelevance()) || StringUtils.isNotEmpty(condition.getLabelL1()) || StringUtils.isNotEmpty(condition.getLabelL2())) {
            boolBuilder.filter(QueryBuilders.nestedQuery("relatedcompyarray", this.handleCompyarrayFilter(condition), ScoreMode.Avg));
        }
    }

    public BoolQueryBuilder handleCompyarrayFilter(NewsSearchConditon condition) {

        /***************************************path:relatedcompyarray.label 过滤Start*****************************************************/
        BoolQueryBuilder labelBoolQueryBuilder = QueryBuilders.boolQuery();
        //一级风险标签
        if(StringUtils.isNotEmpty(condition.getLabelL1())) {
            String[] labelL1Arr = condition.getLabelL1()!=null?condition.getLabelL1().split(","):null;
            for (int i = 0; labelL1Arr != null && i < labelL1Arr.length; i++) {
                if (labelL1Arr[i].equals("无标签")) {
                    labelL1Arr[i] = "";
                    break;
                }
            }
            labelBoolQueryBuilder.must(QueryBuilders.termsQuery("relatedcompyarray.label.label_l1", labelL1Arr));
        }
        //二级风险标签
        if(StringUtils.isNotEmpty(condition.getLabelL2())) {
            String[] labelL2Arr = condition.getLabelL2()!=null?condition.getLabelL2().split(","):null;
            for (int i = 0; labelL2Arr != null && i < labelL2Arr.length; i++) {
                if (labelL2Arr[i].equals("无标签")) {
                    labelL2Arr[i] = "";
                    break;
                }
            }
            labelBoolQueryBuilder.must(QueryBuilders.termsQuery("relatedcompyarray.label.label_l2", labelL2Arr));
        }

        /***************************************path:relatedcompyarray.label 过滤End*****************************************************/
        /***************************************path:relatedcompyarray 过滤Start*****************************************************/
        BoolQueryBuilder relatedcompyarrayBoolQueryBuilder = QueryBuilders.boolQuery();
        //关联企业类型
        if (StringUtils.isNotEmpty(condition.getRelateCompanyType())) {
            String[] companyTypeArr = condition.getRelateCompanyType()!=null?condition.getRelateCompanyType().split(","):null;
            relatedcompyarrayBoolQueryBuilder.must(QueryBuilders.termsQuery("relatedcompyarray.company_type", companyTypeArr));
        }
        //正负面
        if (StringUtils.isNotEmpty(condition.getSentimental())) {
            String[] sentimentalArr = condition.getSentimental()!=null?condition.getSentimental().split(","):null;
            String[] sentimentalQuery = new String[sentimentalArr.length];
            for (int i=0;i<sentimentalArr.length;i++) {
                if (sentimentalArr[i].equals("严重负面")) {
                    sentimentalQuery[i] = "-3";
                } else if (sentimentalArr[i].equals("重点负面")) {
                    sentimentalQuery[i] = "-2";
                }else if (sentimentalArr[i].equals("一般负面")) {
                    sentimentalQuery[i] = "-1";
                }else if (sentimentalArr[i].equals("中性")) {
                    sentimentalQuery[i] = "0";
                } else if (sentimentalArr[i].equals("正面")) {
                    sentimentalQuery[i] = "1";
                } else {
                    sentimentalQuery[i] = "";
                }
            }
            relatedcompyarrayBoolQueryBuilder.must(QueryBuilders.termsQuery("relatedcompyarray.sentimental", sentimentalQuery));
        }
        //关联度
        if (StringUtils.isNotEmpty(condition.getRelevance())) {
            String[] relevanceArr = condition.getRelevance().split("-");
            String start = relevanceArr[0];
            String end = relevanceArr[1];
            relatedcompyarrayBoolQueryBuilder.must(QueryBuilders.rangeQuery("relatedcompyarray.relevance").gte(start).lte(end));
        }
        //一级标签和二级标签
        if (StringUtils.isNotEmpty(condition.getLabelL1()) || StringUtils.isNotEmpty(condition.getLabelL2())) {
            relatedcompyarrayBoolQueryBuilder.must(QueryBuilders.nestedQuery("relatedcompyarray.label",labelBoolQueryBuilder,ScoreMode.Avg));
        }
        /***************************************path:relatedcompyarray 过滤End*****************************************************/
        return relatedcompyarrayBoolQueryBuilder;
    }

    /***
     * 统计传递关于企业的参数的个数
     * @param condition
     * @return
     */
    public int companyConditionCount(NewsSearchConditon condition) {
        int i=0;
        //企业类型
        if (StringUtils.isNotBlank(condition.getRelateCompanyType())) {
            i+=1;
        }
        if (StringUtils.isNotBlank(condition.getLabelL1())) {
            i+=1;
        }
        if (StringUtils.isNotBlank(condition.getLabelL2())) {
            i+=1;
        }
        if (StringUtils.isNotBlank(condition.getSentimental())) {
            i+=1;
        }
        return i;
    }

    /***
     * 新闻关联的企业必须满足所有的条件，才能返回true
     * @param company
     * @param condition
     * @return
     */
    public boolean companyFilter(Map<String, Object> company,NewsSearchConditon condition) {
        String[] relateCompanyTypeArr;
        String[] labelL1Arr;
        String[] labelL2Arr;
        String[] sentimentalArr;
        Map<String, Boolean> conditionMap = new HashMap();
        //企业类型
        if (StringUtils.isNotBlank(condition.getRelateCompanyType())) {
            conditionMap.put("company_type", false);
            relateCompanyTypeArr = condition.getRelateCompanyType().split(",");
            List companyTypeList;
            if (company.get("company_type") instanceof List) {
                companyTypeList = (List) company.get("company_type");
                if (companyTypeList != null && companyTypeList.size() > 0) {
                    for (int i = 0; i < relateCompanyTypeArr.length; i++) {
                        if (companyTypeList.contains(relateCompanyTypeArr[i])) {
                            conditionMap.put("company_type", true);
                            break;
                        }
                    }
                }
            }
        }
        //标签
        List<Map<String, Object>> labelList = null;
        if (company.get("label") instanceof List) {
            labelList = (List) company.get("label");
        }
        //一级标签 内部or
        if (StringUtils.isNotBlank(condition.getLabelL1())) {
            conditionMap.put("label_l1", false);
            labelL1Arr = condition.getLabelL1().split(",");
            if (labelList != null) {
                for (Map<String, Object> label : labelList) {
                    if (label.get("label_l1") instanceof String) {
                        String labelL1 = (String) label.get("label_l1");
                        for (int i = 0; i < labelL1Arr.length; i++) {
                            if (labelL1.equals(labelL1Arr[i])) {
                                conditionMap.put("label_l1", true);
                                break;
                            }
                        }
                        if (conditionMap.get("label_l1")) {
                            break;
                        }
                    }
                }
            }
        }
        //二级标签 内部or
        if (StringUtils.isNotBlank(condition.getLabelL2())) {
            conditionMap.put("label_l2", false);
            labelL2Arr = condition.getLabelL2().split(",");
            if (labelList != null) {
                for (Map<String, Object> label : labelList) {
                    if (label.get("label_l2") instanceof List) {
                        List<String> labelL2List = (List) label.get("label_l2");
                        for (String labelL2 : labelL2List) {
                            for (int i = 0; i < labelL2Arr.length; i++) {
                                if (labelL2.equals(labelL2Arr[i])) {
                                    conditionMap.put("label_l2", true);
                                    break;
                                }
                            }
                            if (conditionMap.get("label_l2")) {
                                break;
                            }
                        }
                        if (conditionMap.get("label_l2")) {
                            break;
                        }
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(condition.getSentimental())) {
            conditionMap.put("sentimental", false);
            sentimentalArr = condition.getSentimental().split(",");
            if (company.get("sentimental") instanceof String) {
                String sentimental = (String) company.get("sentimental");
                if (Long.valueOf(sentimental) == -3) {
                    sentimental = "严重负面";
                } else if (Long.valueOf(sentimental) == -2) {
                    sentimental = "重点负面";
                } else if (Long.valueOf(sentimental) == -1) {
                    sentimental = "一般负面";
                } else if (Long.valueOf(sentimental) == 0) {
                    sentimental = "中性";
                } else if (Long.valueOf(sentimental) == 1) {
                    sentimental = "正面";
                } else {
                    sentimental = "无标签";
                }
                for (int i = 0; i < sentimentalArr.length; i++) {
                    if (sentimental.equals(sentimentalArr[i])) {
                        conditionMap.put("sentimental", true);
                        break;
                    }
                }
            }
        }
        //遍历conditionMap ,满足 and 要求
        boolean result = true;
        int count = 0;
        for (Map.Entry<String, Boolean> entry : conditionMap.entrySet()) {
            if (!entry.getValue()) {
                result = false;
                break;
            }
            count++;
        }
        if (count == 0) {
            result = false;
        }
        return result;
    }

    /***
     * 在已有的有序条件中加上es中增多的条件
     * @param orderedConditionList
     * @param esCondition
     */
    public List<String> handleConditonSort(List<String> orderedConditionList,String esCondition) {
        synchronized (this) {
            if (!orderedConditionList.contains(esCondition)) {
                orderedConditionList.add(esCondition);
            }
        }
        return orderedConditionList;
    }

    /***
     * 在有序的条件上删除多余的（es中没有的）条件
     * @param orderedConditionList
     * @param esConditionList
     * @return
     */
    public List<String> handleConditonSort(List<String> orderedConditionList,List esConditionList) {
        synchronized (this) {
            for (int i=0;i<orderedConditionList.size();i++) {
                if (!esConditionList.contains(orderedConditionList.get(i))) {
                    orderedConditionList.remove(i);
                }
            }
        }
        return orderedConditionList;
    }

    public List<Map<String, Object>> addCountInfo(List<String> orderedConditionList,List<? extends Terms.Bucket> buckets) {
        List<Map<String, Object>> countList = new ArrayList<>();
        for (String condition : orderedConditionList) {
            Map map = new HashMap();
            map.put("name", condition);
            for (Terms.Bucket bucket : buckets) {
                if (bucket.getKey().equals(condition)) {
                    map.put("count", bucket.getDocCount());
                    Terms labelL2 = bucket.getAggregations().get("labelL2");
                    List labelL2List = new ArrayList();
                    for (Terms.Bucket labelL2Bucket : labelL2.getBuckets()) {
                        if (StringUtils.isNotBlank(labelL2Bucket.getKeyAsString())) {
                            Map labelL2Map = new HashMap();
                            labelL2Map.put("name", labelL2Bucket.getKey());
                            labelL2Map.put("count", labelL2Bucket.getDocCount());
                            labelL2List.add(labelL2Map);
                        }
                    }
                    map.put("labelL2Agg", labelL2List);
                    break;
                }
            }
            countList.add(map);
        }
        return countList;
    }
    public List<Map<String, Object>> addCountInfoSen(List<String> orderedConditionList,List<? extends Terms.Bucket> buckets) {
        List<Map<String, Object>> countList = new ArrayList<>();
        for (String condition : orderedConditionList) {
            Map map = new HashMap();
            long temp;
            if (condition.equals("严重负面")) {
                temp = -3;
            } else if (condition.equals("重点负面")) {
                temp = -2;
            } else if (condition.equals("一般负面")) {
                temp = -1;
            } else if (condition.equals("中性")) {
                temp = 0;
            } else{
                temp = 1;
            }
            for (Terms.Bucket bucket : buckets) {
                if (Long.valueOf(bucket.getKey().toString())==temp) {
                    map.put("name", condition);
                    map.put("count", bucket.getDocCount());
                    countList.add(map);
                    break;
                }
            }
        }
        return countList;
    }
}
