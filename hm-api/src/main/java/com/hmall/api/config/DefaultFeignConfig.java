package com.hmall.api.config;

import com.hmall.api.client.TradeClient;
import com.hmall.api.client.fallback.CartClientFallbackFactory;
import com.hmall.api.client.fallback.ItemClientFallbackFactory;
import com.hmall.api.client.fallback.TradeClientFallbackFactory;
import com.hmall.api.client.fallback.UserClientFallbackFactory;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL;
    }
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    requestTemplate.header("user-info", userId.toString());
                }
            }
        };
    }
    @Bean
    public ItemClientFallbackFactory itemClientFallbackFactory(){
        return new ItemClientFallbackFactory();
    }
    @Bean
    public CartClientFallbackFactory cartClientFallbackFactory(){
        return new CartClientFallbackFactory();
    }
    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){return new UserClientFallbackFactory();}
    @Bean
    public TradeClientFallbackFactory tradeClientFallbackFactory(){return new TradeClientFallbackFactory();}
}
