package com.hmall.api.client;

import com.hmall.api.client.fallback.TradeClientFallbackFactory;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.api.dto.OrderFormDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "trade-service",fallbackFactory = TradeClientFallbackFactory.class)
public interface TradeClient {
    @PutMapping("orders/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId);
    @PostMapping("/orders")
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO);
    @GetMapping("/orders/{orderId}/details")
    public List<OrderDetailDTO> queryOrderDetailsByOrderId(@PathVariable("orderId") Long orderId);
}
