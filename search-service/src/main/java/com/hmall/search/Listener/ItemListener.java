package com.hmall.search.Listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.search.domain.po.ItemDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemListener {
    private final RestHighLevelClient client =new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.23.128:9200")));
    private final ItemClient itemClient;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.index.queue",durable = "true"),
            exchange = @Exchange(name = "search.direct",type = ExchangeTypes.DIRECT),
            key = {"item.index"}
    ))
    public void listenerItemIndex(Long id) throws IOException {
        ItemDTO itemDTO = itemClient.queryItemById(id);
        if (itemDTO == null){
            return;
        }
        ItemDoc itemDoc = BeanUtil.copyProperties(itemDTO, ItemDoc.class);
        String json = JSONUtil.toJsonStr(itemDTO);
        IndexRequest request = new IndexRequest("item").id(itemDoc.getId().toString());
        request.source(json, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "search.direct",type = ExchangeTypes.DIRECT),
            key = {"item.delete"}
    ))
    public void listenerItemDelete(Long id) throws IOException{
        DeleteRequest request=new DeleteRequest("item", id.toString());
        client.delete(request, RequestOptions.DEFAULT);
    }
}
