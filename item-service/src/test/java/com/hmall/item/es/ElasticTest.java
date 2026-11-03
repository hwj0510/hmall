package com.hmall.item.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

public class ElasticTest {
    private RestHighLevelClient client;
    static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"image\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"sold\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"commentCount\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"isAD\":{\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"updateTime\":{\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
    @Test
    void testConnection() throws IOException {
        System.out.println(client);
    }
    @Test
    void TestCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("item");
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    @Test
    void testGetIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("item");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println( exists);
    }

    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("item");
        client.indices().delete(request, RequestOptions.DEFAULT);
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
