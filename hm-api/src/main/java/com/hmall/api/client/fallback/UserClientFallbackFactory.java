package com.hmall.api.client.fallback;

import com.hmall.api.client.UserClient;
import org.springframework.cloud.openfeign.FallbackFactory;

public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public void deductMoney(String pw, Integer amount) {
                throw new RuntimeException("扣减失败,余额不足",cause);
            }
        };
    }
}
