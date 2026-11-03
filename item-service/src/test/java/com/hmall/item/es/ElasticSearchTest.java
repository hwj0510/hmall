package com.hmall.item.es;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.ItemDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public class ElasticSearchTest {
    private RestHighLevelClient client;

    @Test
    void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("item");
        request.source()
                .query(QueryBuilders.matchAllQuery());
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        parse(response);
    }

    public void parse(SearchResponse response) throws IOException {
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println( total);
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits){
            String json = hit.getSourceAsString();
            ItemDoc bean = JSONUtil.toBean(json, ItemDoc.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (CollUtil.isEmpty(highlightFields)){
                HighlightField hf = highlightFields.get("name");
                if (hf != null){
                    String name = hf.getFragments()[0].string();
                    bean.setName(name);
                }
            }
            System.out.println(bean);
        }
    }

    @Test
    void testMatch() throws IOException {
        SearchRequest request = new SearchRequest("item");
        request.source().query(QueryBuilders.matchQuery("name", "小米"));
//        request.source().query(QueryBuilders.termQuery("name", "小米"));
//        request.source().query(QueryBuilders.rangeQuery("price").from(100).to(2000));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        parse(response);
    }
    @Test
    void testBool() throws IOException {
        SearchRequest request = new SearchRequest("item");
       request.source().query(QueryBuilders.boolQuery()
        .must(QueryBuilders.matchQuery("name", "脱脂牛奶"))
        .filter(QueryBuilders.termQuery("brand", "艾思达"))
        .filter(QueryBuilders.rangeQuery("price").lt(100000)));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        parse(response);
    }
    @Test
    void testPageAndSort() throws IOException {
        int pageNo = 1, pageSize = 5;

        // 1.创建Request
        SearchRequest request = new SearchRequest("item");
        // 2.组织请求参数
        // 2.1.搜索条件参数
        request.source().query(QueryBuilders.matchAllQuery());
        // 2.2.排序参数
        request.source().sort("price", SortOrder.ASC);
        // 2.3.分页参数
        request.source().from((pageNo - 1) * pageSize).size(pageSize);
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        parse(response);
    }
    @Test
    void testHighlight() throws IOException {
        SearchRequest request = new SearchRequest("item");
        request.source().query(QueryBuilders.matchQuery("name", "小米"));
        request.source().highlighter(SearchSourceBuilder.highlight().field("name"));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        parse( response);
    }


    @BeforeEach
    void setUp() {
        client =new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.23.128:9200")
        ));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (client != null){
            client.close();
        }

    }
}
