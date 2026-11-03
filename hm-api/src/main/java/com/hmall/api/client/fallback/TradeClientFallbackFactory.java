package com.hmall.api.client.fallback;

import com.hmall.api.client.TradeClient;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.api.dto.OrderFormDTO;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

public class TradeClientFallbackFactory implements FallbackFactory<TradeClient> {
    @Override
    public TradeClient create(Throwable cause) {
        return new TradeClient() {

            @Override
            public void markOrderPaySuccess(Long orderId) {
                throw new RuntimeException("支付失败",cause);
            }

            @Override
            public Long createOrder(OrderFormDTO orderFormDTO) {
                throw new RuntimeException("创建订单失败",cause);
            }

            @Override
            public List<OrderDetailDTO> queryOrderDetailsByOrderId(Long orderId) {
                throw new RuntimeException("查询订单失败",cause);
            }
        };
    }
}
