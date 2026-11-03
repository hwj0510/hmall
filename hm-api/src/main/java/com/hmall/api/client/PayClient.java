package com.hmall.api.client;

import com.hmall.api.client.fallback.PayClientFallbackFactory;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.api.dto.PayOrderFormDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "pay-service",fallbackFactory = PayClientFallbackFactory.class)
public interface PayClient {
    @PostMapping("{id}")
    public void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO);
    /**
     * 根据交易订单id查询支付单
     * @param id 业务订单id
     * @return 支付单信息
     */
    @GetMapping("/pay-orders/biz/{id}")
    PayOrderDTO queryPayOrderByBizOrderNo(@PathVariable("id") Long id);
}
