package com.hmall.api.client.fallback;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.api.dto.PayOrderFormDTO;
import org.springframework.cloud.openfeign.FallbackFactory;

public class PayClientFallbackFactory implements FallbackFactory<PayClient> {
    @Override
    public PayClient create(Throwable cause) {
        return new PayClient() {

            @Override
            public void tryPayOrderByBalance(Long id, PayOrderFormDTO payOrderFormDTO) {
                throw new RuntimeException("支付失败",cause);
            }

            @Override
            public PayOrderDTO queryPayOrderByBizOrderNo(Long id) {
                return null;
            }
        };
    }
}
