package com.example.gateway.filter;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.example.common.AppVariable;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 登录过滤器（登录判断）
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    // 排除登录验证的 URL 地址
    private String[] skipAuthUrls = {
            "/user/add", "/user/login", "/user/getbyuid",
            "/art/getuserart", "/art/getlistbypage",
            "/art/getbyid","/comment/listbyaid"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 当前请求的 URL
        String url = exchange.getRequest().getURI().getPath();
        for (String item : skipAuthUrls) {
            if (item.equals(url)) {
                // 继续往下走
                return chain.filter(exchange);
            }
        }
        ServerHttpResponse response = exchange.getResponse();
        // 登录判断
        List<String> tokens =
                exchange.getRequest().getHeaders().get(AppVariable.TOKEN_KEY);
        if (tokens == null || tokens.size() == 0) {
            // 当前未登录
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // token 有值
        String token = tokens.get(0);
        // JWT 效验 token 是否有效
        boolean result = false;
        try {
            result = JWTUtil.verify(token, AppVariable.JWT_KEY.getBytes());
        } catch (Exception e) {
            result = false;
        }
        JWT jwt = null;
        if (!result) {
            // 无效 token
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        } else { // 判断 token 是否过期
            jwt = JWTUtil.parseToken(token);
            // 得到过期时间
            Object expObj = jwt.getPayload("exp");
            if (expObj == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            long exp = Long.parseLong(expObj.toString());
            if (System.currentTimeMillis() > exp) {
                // token 过期
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }
        // 将 token -> uid、manager 存到 header
        String uid = jwt.getPayload("uid").toString();
        String manager = jwt.getPayload("manager").toString();
        response.getHeaders().set("uid", uid);
        response.getHeaders().set("manager", manager);
        // 修改原始 request 添加 header
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("uid", uid)
                .header("manager", manager).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        // 值越小越早执行
        return 1;
    }
}
