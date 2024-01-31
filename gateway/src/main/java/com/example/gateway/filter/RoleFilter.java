package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 权限判断
 */
@Component
public class RoleFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 当前请求的 URL
        String url = exchange.getRequest().getURI().getPath();
        if (url.contains("/category/add") ||
                url.contains("/category/del")) {
            // 要求必须要是管理员权限
            ServerHttpResponse response = exchange.getResponse();
            List<String> managers = response.getHeaders().get("manager");
            if (managers != null && managers.size() == 1 &&
                    Integer.parseInt(managers.get(0)) != 1) {
                // 非管理员
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
