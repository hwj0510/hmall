package com.hmall.api.client.fallback;

import com.hmall.api.client.CartClient;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

public class CartClientFallbackFactory implements FallbackFactory<CartClient> {
    @Override
    public CartClient create(Throwable cause) {
        return new CartClient() {
            @Override
            public void deleteCartItemByIds(List<Long> ids) {
                throw new RuntimeException("删除失败",cause);
            }
        };
    }
}
