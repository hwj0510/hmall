package com.hmall.cart.listener;

import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.api.dto.OrderFormDTO;
import com.hmall.cart.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class CartClearListener {
    private final ICartService cartService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.clear.queue", durable = "true"),
            exchange = @Exchange(name = "trade.topic", type = "topic"),
            key = "order.create"
    ))
    public void listenCartClear(OrderFormDTO orderFormDTO) {
        // 从订单中提取商品ID，清理购物车
        List<Long> itemIds = orderFormDTO.getDetails().stream()
                .map(OrderDetailDTO::getItemId)
                .collect(Collectors.toList());
        cartService.removeByItemIds(itemIds);
    }
}
