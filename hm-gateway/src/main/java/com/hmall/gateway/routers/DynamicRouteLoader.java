package com.hmall.gateway.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicRouteLoader {
    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;

    private final String dateId = "gateway-router.json";
    private final String group = "DEFAULT_GROUP";
    private final Set<String> routeIds = new HashSet<>();

    @PostConstruct
    public void innitRouteConfig() throws Exception {
        log.info("开始加载路由配置");
        String configAndSignListener = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dateId, group, 3000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }
                    // 监听器后的操作
                    @Override
                    public void receiveConfigInfo(String configAndSignListener) {
                        updateConfigInfo(configAndSignListener);
                    }
                });
        //第一次启动时，更新路由配置
        updateConfigInfo(configAndSignListener);
    }

    private void updateConfigInfo(String configAndSignListener) {
        log.debug("监听路由配置",configAndSignListener);
        List<RouteDefinition> list = JSONUtil.toList(configAndSignListener, RouteDefinition.class);
        //删除路由表
        for (String routeId : routeIds){
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe(); // 删除路由表
        }
        routeIds.clear();

        for (RouteDefinition routeDefinition : list){
            //更新路由
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            //记录路由id便于后续删除
            routeIds.add(routeDefinition.getId());
        }
    }
}
