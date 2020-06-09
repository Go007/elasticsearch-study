package com.hong.es;

/**
 * @author wanghong
 * @date 2019/12/26 16:37
 **/

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hong.es.entity.to.Warning;
import com.hong.es.util.DateUtils;
import com.hong.es.util.EsClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NewsSearchBo {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsSearchBo.class);

    private static final String INDEX = "companywarnings";

    @Test
    public void execute() {
        EsWarningParameter parameter = new EsWarningParameter();
        parameter.setCompanyId(Arrays.asList(514332L));
        parameter.setDataType(Arrays.asList(0, 1));
        parameter.setStartNoticeDate("2019-09-26");
        parameter.setEndNoticeDate("2019-12-26");
        parameter.setRelevanceName(Arrays.asList("高度相关", "紧密相关", "一般相关", "较弱相关", "弱相关"));
        parameter.setPage(1);
        parameter.setSize(10);

        BaseOutData outData = new BaseOutData();

        PageVO<Warning> page = new PageVO<>();

        if (CollectionUtils.isEmpty(parameter.getCompanyId())) {
            outData.setCode("0");
            outData.setMessage("返回成功!");
            page.setTotalCount(0L);
            outData.setData(page);
            return;
        }

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        if (CollectionUtils.isNotEmpty(parameter.getSwIndustryId())
                || CollectionUtils.isNotEmpty(parameter.getCsrcIndustryId())
                || CollectionUtils.isNotEmpty(parameter.getExposure()) || parameter.getIssueBonds() != null
                || parameter.getNewOTCMarket() != null || parameter.getShareType() != null
                || parameter.getPpType() != null || CollectionUtils.isNotEmpty(parameter.getOrgFormIds())
                || CollectionUtils.isNotEmpty(parameter.getRelevance())
                || CollectionUtils.isNotEmpty(parameter.getSentimental())
                || CollectionUtils.isNotEmpty(parameter.getImportance())
                || CollectionUtils.isNotEmpty(parameter.getCompanyId())
                || StringUtils.isNotBlank(parameter.getKeyword())
                || CollectionUtils.isNotEmpty(parameter.getRelevanceName())) {

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (CollectionUtils.isNotEmpty(parameter.getSwIndustryId())) {
                boolQuery
                        .must(QueryBuilders.termsQuery("relatedcompanyInfo.swIndustryId", parameter.getSwIndustryId()));
            }

            if (CollectionUtils.isNotEmpty(parameter.getCsrcIndustryId())) {
                boolQuery.must(
                        QueryBuilders.termsQuery("relatedcompanyInfo.csrcIndustryId", parameter.getCsrcIndustryId()));
            }

            if (CollectionUtils.isNotEmpty(parameter.getExposure())) {
                boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.exposureId", parameter.getExposure()));
            }

            if (parameter.getIssueBonds() != null) {
                boolQuery.must(QueryBuilders.termQuery("relatedcompanyInfo.isBond", parameter.getIssueBonds()));
            }

            if (parameter.getNewOTCMarket() != null) {
                boolQuery.must(QueryBuilders.termQuery("relatedcompanyInfo.newOtcMarket", parameter.getNewOTCMarket()));
            }

            if (parameter.getShareType() != null) {
                boolQuery.must(QueryBuilders.termQuery("relatedcompanyInfo.shareType", parameter.getShareType()));
            }

            if (parameter.getPpType() != null) {
                boolQuery.must(QueryBuilders.termQuery("relatedcompanyInfo.ppType", parameter.getPpType()));
            }

            if (CollectionUtils.isNotEmpty(parameter.getOrgFormIds())) {
                boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.orgFormId", parameter.getOrgFormIds()));
            }

            if (CollectionUtils.isNotEmpty(parameter.getRelevance())) {
                boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.relevance", parameter.getRelevance()));
            }

            if (CollectionUtils.isNotEmpty(parameter.getRelevanceName())) {
                List<String> relNameList = parameter.getRelevanceName();
                if (relNameList.contains("弱相关")) {
                    boolQuery.should(QueryBuilders.rangeQuery("relatedcompanyInfo.relevance").lt(0.2));
                }
                if (relNameList.contains("较弱相关")) {
                    boolQuery.should(QueryBuilders.rangeQuery("relatedcompanyInfo.relevance").from(0.2).to(0.4, false));
                }
                if
                (relNameList.contains("一般相关")) {
                    boolQuery.should(QueryBuilders.rangeQuery("relatedcompanyInfo.relevance").from(0.4).to(0.6, false));
                }
                if
                (relNameList.contains("紧密相关")) {
                    boolQuery.should(QueryBuilders.rangeQuery("relatedcompanyInfo.relevance").from(0.6).to(0.8, false));
                }
                if
                (relNameList.contains("高度相关")) {
                    boolQuery.should(QueryBuilders.rangeQuery("relatedcompanyInfo.relevance").gte(0.8));
                }
            }

            if (CollectionUtils.isNotEmpty(parameter.getSentimental())) {
                boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.sentimental", parameter.getSentimental()));
            }

            if (CollectionUtils.isNotEmpty(parameter.getImportance())) {
                boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.importance", parameter.getImportance()));
            }

            if (CollectionUtils.isNotEmpty(parameter.getCompanyId())) {
                boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.companyId", parameter.getCompanyId()));
            }

            if (StringUtils.isNotBlank(parameter.getKeyword())) {
                BoolQueryBuilder boolFuzzyQuery = QueryBuilders.boolQuery();
                boolFuzzyQuery.should(QueryBuilders.wildcardQuery("relatedcompanyInfo.companyName",
                        "*" + parameter.getKeyword() + "*"));
                boolFuzzyQuery.should(
                        QueryBuilders.wildcardQuery("relatedcompanyInfo.title", "*" + parameter.getKeyword() + "*"));
                boolQuery.filter(boolFuzzyQuery);

            }

            setNestedCondition(boolQuery, parameter);

            query.filter(QueryBuilders.nestedQuery("relatedcompanyInfo", boolQuery, ScoreMode.Total));

        } else {
            setNestedCondition(query, parameter);
        }

        if (CollectionUtils.isNotEmpty(parameter.getPublishSite())) {
            query.filter(QueryBuilders.termsQuery("publishSite", parameter.getPublishSite()));
        }

        if (CollectionUtils.isNotEmpty(parameter.getWarningRegulationSid())) {
            query.filter(QueryBuilders.termsQuery("warningRegulationSid", parameter.getWarningRegulationSid()));
        }

        if (CollectionUtils.isNotEmpty(parameter.getDataType())) {
            query.filter(QueryBuilders.termsQuery("dataType", parameter.getDataType()));
        }

        if (CollectionUtils.isNotEmpty(parameter.getTypeName())) {
            query.filter(QueryBuilders.termsQuery("typeName", parameter.getTypeName()));
        }

        if (CollectionUtils.isNotEmpty(parameter.getTypeCode())) {
            query.filter(QueryBuilders.termsQuery("typeCode", parameter.getTypeCode()));
        }

        if (parameter.getEndNoticeDate() != null || parameter.getStartNoticeDate() != null) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("noticeDate");
            if (parameter.getEndNoticeDate() != null) {
                // 时间范围查询，截止时间只包含日期时，加一天然后调整es自身的时差问题，需减去8小时
                // 当截止时间带时分秒时，直接减es自身时差八小时8小时
                rangeQueryBuilder.lt(DateUtils.getISODateStr(parameter.getEndNoticeDate().length() == 10
                        ? DateUtils.getISODateTime(parameter.getEndNoticeDate() + " 00:00:00").plusDays(1)
                        .minusHours(8L)
                        : DateUtils.getISODateTime(parameter.getEndNoticeDate()).minusHours(8L)));
            }

            if (parameter.getStartNoticeDate() != null) {
                rangeQueryBuilder.gte(DateUtils.getISODateStr(DateUtils.getISODateTime(
                        parameter.getStartNoticeDate().length() == 10 ? parameter.getStartNoticeDate() + " 00:00:00"
                                : parameter.getStartNoticeDate())
                        .minusHours(8L)));
            }

            query.filter(rangeQueryBuilder);
        }

        // 默认排序字段与排序方向
        String sortField = "noticeDate";
        SortOrder sortOrder = SortOrder.DESC;

        if (StringUtils.isNotBlank(parameter.getSortField())) {
            sortField = parameter.getSortField();
        }

        if (parameter.getSortDirection() != null) {
            sortOrder = parameter.getSortDirection() == -1 ? SortOrder.DESC : SortOrder.ASC;
        }

        FieldSortBuilder fsb = SortBuilders.fieldSort(sortField);
        fsb.missing("_last");// null排在最后
        fsb.order(sortOrder);

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        int curPage = parameter.getPage() == null ? 1 : parameter.getPage();
        int rows = parameter.getSize() == null ? 10 : parameter.getSize();

        int start = rows * (curPage - 1);
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(rows);

        searchSourceBuilder.query(query);
        searchSourceBuilder.sort(fsb);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse;

        try {
            List<Warning> warnings = new ArrayList<>();
            searchResponse = EsClient.client().search(searchRequest, RequestOptions.DEFAULT);
            // List<SearchHit> searchHits = scrollSearchAll(EsClient.client(), searchRequest);

            if (searchResponse.status() == RestStatus.OK) {
                // if (CollectionUtils.isNotEmpty(searchHits)) {

                SearchHits hits = searchResponse.getHits();
                long totalHits = hits.getTotalHits();
                page.setTotalCount(totalHits);
                SearchHit[] searchHits = hits.getHits();

                Map<String, Object> sourceAsMap;
                for (SearchHit hit : searchHits) {
                    sourceAsMap = hit.getSourceAsMap();
                    Warning warning = JSON.parseObject(JSON.toJSONString(sourceAsMap), Warning.class);
                    // warning.setNoticeDateStr(DateUtil.dateToStr(warning.getNoticeDate()));

                    warnings.add(warning);

/*                    List<String> relNameList = parameter.getRelevanceName();
                    if (CollectionUtils.isEmpty(relNameList)) {
                        warnings.add(warning);
                    }else {
                        List<RelatedcompanyInfo> relComList = warning.getRelatedcompanyInfo();
                        if (CollectionUtils.isNotEmpty(relComList)) {
                            for (RelatedcompanyInfo com : relComList) {
                                if (com.getCompanyId().equals(parameter.getCompanyId().get(0))) {
                                    Double relevancy = com.getRelevance();
                                    String stars = "";
                                    if (relevancy < 0.2) {
                                        stars = "弱相关";
                                    } else if (relevancy >= 0.2 && relevancy < 0.4) {
                                        stars = "较弱相关";
                                    } else if (relevancy >= 0.4 && relevancy < 0.6) {
                                        stars = "一般相关";
                                    } else if (relevancy >= 0.6 && relevancy < 0.8) {
                                        stars = "紧密相关";
                                    } else if (relevancy >= 0.8) {
                                        stars = "高度相关";
                                    }

                                    if (relNameList.contains(stars)) {
                                        warnings.add(warning);
                                    }
                                    break;
                                }
                            }
                        }

                    }*/

                }

                //  page.setTotalCount((long) warnings.size());
                // 手动分页
                //  int start = rows * (curPage - 1);
                //  int end = (start + rows) > warnings.size() ? warnings.size() : start + rows;
                //  warnings = warnings.subList(start, end);
            }

            page.setItems(warnings);
            page.setPage(parameter.getPage());
            page.setSize(parameter.getSize());
            // page.setTotalCount(page.getTotalCount() == null ? 0 : page.getTotalCount());

            System.out.println("======================");
            System.out.println(page);

            outData.setCode("0");
            outData.setMessage("返回成功!");
            outData.setData(page);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            outData.setCode("1");
            outData.setMessage("系统报错!" + e.getMessage());
        }

    }

    private void setNestedCondition(BoolQueryBuilder query, EsWarningParameter parameter) {
        if (CollectionUtils.isNotEmpty(parameter.getLevel1()) || CollectionUtils.isNotEmpty(parameter.getLevel2())
                || CollectionUtils.isNotEmpty(parameter.getLevel3())) {

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            if (CollectionUtils.isNotEmpty(parameter.getLevel1())) {
                if (parameter.getLevel1().contains("-1")) {
                    BoolQueryBuilder bqb = QueryBuilders.boolQuery();
                    bqb.should(QueryBuilders.boolQuery()
                            .mustNot(QueryBuilders.existsQuery("relatedcompanyInfo.labels.level1")));
                    bqb.should(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level1", parameter.getLevel1()));
                    boolQuery.must(bqb);
                } else {
                    boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level1", parameter.getLevel1()));
                }
            }

            if (CollectionUtils.isNotEmpty(parameter.getLevel2())) {
                if (parameter.getLevel2().contains("-1")) {
                    BoolQueryBuilder bqb = QueryBuilders.boolQuery();
                    bqb.should(QueryBuilders.boolQuery()
                            .mustNot(QueryBuilders.existsQuery("relatedcompanyInfo.labels.level2")));
                    bqb.should(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level2", parameter.getLevel2()));
                    boolQuery.must(bqb);
                } else {
                    boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level2", parameter.getLevel2()));
                }
            }

            if (CollectionUtils.isNotEmpty(parameter.getLevel3())) {
                // 部分标签体系只有二级标签，当选中全部二级和三级标签时，这部分只有二级标签的数据无法查询出来，此处需要做特殊处理
                if (parameter.getLevel3().contains("-1")) {
                    BoolQueryBuilder bqb = QueryBuilders.boolQuery();
                    bqb.should(QueryBuilders.boolQuery()
                            .mustNot(QueryBuilders.existsQuery("relatedcompanyInfo.labels.level3")));
                    bqb.should(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level3", parameter.getLevel3()));
                    boolQuery.must(bqb);
                } else {
                    boolQuery.must(QueryBuilders.termsQuery("relatedcompanyInfo.labels.level3", parameter.getLevel3()));
                }
            }

            query.must(QueryBuilders.nestedQuery("relatedcompanyInfo.labels", boolQuery, ScoreMode.Total));

        }

    }

    /**
     * 使用游标获取全部结果，返回SearchHit集合
     *
     * @param restHighLevelClient
     * @param searchRequest
     * @return
     * @throws IOException
     */
    public static List<SearchHit> scrollSearchAll(RestHighLevelClient restHighLevelClient, SearchRequest searchRequest) throws IOException {
        // 初始化 scroll
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1));// 设定滚动时间间隔
        searchRequest.scroll(scroll);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT); //restHighLevelClient.search(searchRequest);
        // scrollId: DnF1ZXJ5VGhlbkZldGNoBQAAAAAABHpEFnQxRkFsSE1zUkhDV2tFM0dYYkFyMkEAAAAAAAR6RRZ0MUZBbEhNc1JIQ1drRTNHWGJBcjJBAAAAAAAEekYWdDFGQWxITXNSSENXa0UzR1hiQXIyQQAAAAAABHpHFnQxRkFsSE1zUkhDV2tFM0dYYkFyMkEAAAAAAAR6SBZ0MUZBbEhNc1JIQ1drRTNHWGJBcjJB
        String scrollId = searchResponse.getScrollId();
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<SearchHit> resultSearchHit = new ArrayList<>();

        // 遍历搜索命中的数据,直到没有数据
        while (hits != null && hits.length > 0) {
            for (SearchHit hit : hits) {
                resultSearchHit.add(hit);
            }
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(scroll);
            SearchResponse searchScrollResponse = restHighLevelClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);// restHighLevelClient.searchScroll(searchScrollRequest);
            scrollId = searchScrollResponse.getScrollId();
            hits = searchScrollResponse.getHits().getHits();
        }
        //及时清除es快照，释放资源
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        System.out.println(clearScrollResponse.isSucceeded());
        return resultSearchHit;
    }

    public static JSONObject scrollSearchPage(RestHighLevelClient restHighLevelClient, SearchRequest searchRequest, String scrollId) throws Exception {
        JSONObject jsonObject = new JSONObject();
        List<Warning> list = new ArrayList<>();

        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1));
        SearchHit[] hits;

        // 第一次查询
        if (StringUtils.isEmpty(scrollId)) {
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT); //restHighLevelClient.search(searchRequest);
            hits = searchResponse.getHits().getHits();
            for (SearchHit hit : hits) {
                list.add(JSON.parseObject(hit.getSourceAsString(), Warning.class));
            }

            jsonObject.put("scrollId", searchResponse.getScrollId());
            jsonObject.put("data", list);
            return jsonObject;
        }

        // 非首次查询,使用 游标
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
        searchScrollRequest.scroll(scroll);
        SearchResponse searchScrollResponse = restHighLevelClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
        hits = searchScrollResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            list.add(JSON.parseObject(hit.getSourceAsString(), Warning.class));
        }

        jsonObject.put("scrollId", searchScrollResponse.getScrollId());
        jsonObject.put("data", list);

//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//        System.out.println(clearScrollResponse.isSucceeded());

        return jsonObject;
    }

    public static List<Warning> pageQuery(int pageNum, int pageSize, QueryBuilder queryBuilder) throws Exception {
        List<Warning> list = new ArrayList<>();
        if (pageNum < 0) {
            return list;
        }

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);

        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1));
        searchRequest.scroll(scroll);
        SearchResponse searchResponse = EsClient.client().search(searchRequest, RequestOptions.DEFAULT);
        long total = searchResponse.getHits().getTotalHits();
        if (pageNum >= total) {
            return list;
        }

        SearchHit[] hits = null;
        if (pageNum == 1) {
            hits = searchResponse.getHits().getHits();
        } else {
            String scrollId = searchResponse.getScrollId();
            SearchScrollRequest searchScrollRequest = null;
            for (int i = 1; i < pageNum; i++) {
                searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(scroll);
                searchResponse = EsClient.client().scroll(searchScrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
            }

            hits = searchResponse.getHits().getHits();
        }

        for (SearchHit hit : hits) {
            list.add(JSON.parseObject(hit.getSourceAsString(), Warning.class));
        }

        return list;
    }

    /**
     * 改进版:减少 scroll 循环次数
     *
     * @param pageNum
     * @param pageSize
     * @param queryBuilder
     * @return
     * @throws Exception
     */
    public static List<Warning> pageQuery2(int pageNum, int pageSize, QueryBuilder queryBuilder) throws Exception {
        List<Warning> list = new ArrayList<>();
        if (pageNum < 1) {
            return list;
        }

        int pageNewSize = pageNum == 1 ? pageSize : (pageNum - 1) * pageSize;
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(pageNewSize);
        searchRequest.source(searchSourceBuilder);

        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1));
        searchRequest.scroll(scroll);
        SearchResponse searchResponse = EsClient.client().search(searchRequest, RequestOptions.DEFAULT);
        long total = searchResponse.getHits().getTotalHits();
        if (pageNum >= total) {
            return list;
        }

        SearchHit[] hits = null;
        if (pageNum == 1) {
            hits = searchResponse.getHits().getHits();
        } else {
            SearchScrollRequest searchScrollRequest = null;
            searchScrollRequest = new SearchScrollRequest(searchResponse.getScrollId());
            searchScrollRequest.scroll(scroll);
            searchResponse = EsClient.client().scroll(searchScrollRequest, RequestOptions.DEFAULT);

            hits = searchResponse.getHits().getHits();
        }

        for (SearchHit hit : hits) {
            list.add(JSON.parseObject(hit.getSourceAsString(), Warning.class));
        }

        return list;
    }


}

