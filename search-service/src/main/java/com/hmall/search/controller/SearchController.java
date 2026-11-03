package com.hmall.search.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.api.dto.ItemDTO;
import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.po.ItemDoc;
import com.hmall.search.domain.po.query.ItemPageQuery;
import com.hmall.search.service.IItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final IItemService itemService;
    private final RestHighLevelClient  client =new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.23.128:9200")));

    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO search(ItemPageQuery query) throws IOException {
        if (query.getPageSize() < 20) {
            query.setPageSize(20);
        }
        SearchRequest request = new SearchRequest("item");
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        if (StrUtil.isNotBlank(query.getKey())) {
            bool.must(QueryBuilders.matchQuery("name", query.getKey()));
        }
        if (StrUtil.isNotBlank(query.getBrand())) {
            bool.filter(QueryBuilders.matchQuery("brand", query.getBrand()));
        }
        if (StrUtil.isNotBlank(query.getCategory())) {
            bool.filter(QueryBuilders.matchQuery("category", query.getCategory()));
        }
        if (query.getMaxPrice() != null) {
            bool.filter(QueryBuilders.rangeQuery("price").lte(query.getMaxPrice()));
        }
        if (query.getMinPrice() != null) {
            bool.filter(QueryBuilders.rangeQuery("price").gte(query.getMinPrice()));
        }
        request.source().query(bool).size(query.getPageSize()).from(query.from());
        //排序
        List<OrderItem> orders = query.toMpPage("update_time", false).getOrders();
        for (OrderItem orderItem : orders) {
            request.source().sort(orderItem.getColumn(), orderItem.isAsc() ? SortOrder.ASC : SortOrder.DESC);
        }
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        long pages = (total + query.getPageSize() - 1) / query.getPageSize();
        SearchHit[] searchHits = hits.getHits();
        ArrayList<ItemDoc> itemDocs = new ArrayList<>();
        if (searchHits != null) {
            for (SearchHit hitsHit : searchHits) {
                itemDocs.add(JSONUtil.toBean(hitsHit.getSourceAsString(), ItemDoc.class));
            }
        }
        return PageDTO.of(new Page<>(query.getPageNo(), query.getPageSize(), total), itemDocs);
    }

    @ApiOperation("搜索商品")
    @GetMapping("/{id}")
    public ItemDTO search(@PathVariable ("id") Long id) throws IOException {
        GetRequest request = new GetRequest("item");
        request.id(id.toString());
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String source = response.getSourceAsString();
        ItemDoc bean = JSONUtil.toBean(source, ItemDoc.class);
        return BeanUtil.copyProperties(bean,ItemDTO.class);
    }
}
