package com.hmall.trade.listener;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trade.constant.MqConstants;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {
    private final IOrderService orderService    ;
    private final PayClient payClient;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstants.DELAY_ORDER_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(name = MqConstants.DELAY_EXCHANGE_NAME, type = "direct",delayed = "true"),
            key = "delay.order.query"
    ))
    public void handleOrderDelayMessage(Long orderId) {
       //查询订单
        Order order = orderService.getById(orderId);
        //检查订单状态
        if (order == null || order.getStatus() != 1){
            return;
        }
        //如果订单未支付，则查询流水情况
        PayOrderDTO payOrder = payClient.queryPayOrderByBizOrderNo(orderId);
        //判断是否支付
        if (payOrder != null && payOrder.getStatus()==3) {

            //已支付,修改订单状态
            orderService.markOrderPaySuccess(orderId);
            //未支付, 关闭订单，恢复库存
        }else{
                orderService.cancelOrder(orderId);
            }
    }
}
