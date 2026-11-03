package com.hmall.item.es;

import org.apache.http.HttpHost;
import org.apache.lucene.index.Term;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class ElasticAgg {
    private RestHighLevelClient client;
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
    @Test
    void testAgg() throws IOException {
        SearchRequest request = new SearchRequest("item");
        request.source().size(0);
        request.source().aggregation(AggregationBuilders.terms("brandAgg").field("brand.keyword").size(10));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Aggregations agg = response.getAggregations();
        Terms brandAgg = agg.get("brandAgg");
        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
        for (Terms.Bucket bucket : buckets){
            String brand = bucket.getKeyAsString();
            System.out.println(brand);
            long docCount = bucket.getDocCount();
            System.out.println(docCount);
        }


    }
}
