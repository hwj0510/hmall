package com.hmall.item.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(properties = "spring.profiles.active=local")
@Slf4j
public class ElasticDocTest {
    private RestHighLevelClient client;
    @Autowired
    private IItemService itemService;
//    新增和全量修改
    @Test
    void testIndexDoc() throws IOException {
        Item item = itemService.getById(577967L);
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
//        itemDoc.setPrice(71301);
        IndexRequest request=new IndexRequest("item").id(itemDoc.getId());
        request.source(JSONUtil.toJsonStr(itemDoc),XContentType.JSON);
//        IndexResponse res = client.index(request, RequestOptions.DEFAULT);
        client.index(request,RequestOptions.DEFAULT);
//        System.out.println( res);
    }
    @Test
    void testDeleteDoc() throws IOException {
       DeleteRequest request =new DeleteRequest("item","577967");
       client.delete(request,RequestOptions.DEFAULT);
    }
    @Test
    void testGetDoc() throws IOException {
        GetRequest request =new GetRequest("item","577967");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        ItemDoc itemDoc = JSONUtil.toBean(json, ItemDoc.class);
        System.out.println(itemDoc);
    }
    //局部更新
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request =new UpdateRequest("item","577967");
        request.doc("price","71302");
        client.update(request,RequestOptions.DEFAULT);
    }
    // 批量更新
    @Test
    void testBulkDoc() throws IOException {
        BulkRequest request =new BulkRequest();
        request.add(new IndexRequest("item").id("577967").source("json",XContentType.JSON));
        client.bulk(request,RequestOptions.DEFAULT);
    }
    @Test
    void testLoadItemDocs() throws IOException{
        int pageNo = 1;
        int size = 1000;
        while (true) {
            Page<Item> page = itemService
                    .lambdaQuery()
                    .eq(Item::getStatus, 1)
                    .page(Page.of(pageNo, size));
            List<Item> records = page.getRecords();
            if (CollUtil.isEmpty(page.getRecords())) {
                return;
            }
            log.info("total:{}", page.getTotal());
            BulkRequest request = new BulkRequest();
            for (Item item : records) {
                request.add(new IndexRequest("item")
                        .id(item.getId().toString())
                        .source(JSONUtil.toJsonStr(BeanUtil.copyProperties(item, ItemDoc.class)), XContentType.JSON));
            }
            client.bulk(request, RequestOptions.DEFAULT);
            pageNo++;
        }
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
