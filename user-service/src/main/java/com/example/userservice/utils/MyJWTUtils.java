package com.example.userservice.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import org.springframework.util.StringUtils;

public class MyJWTUtils {

    /**
     * 根据 JWT 中的 Token 获取用户 uid
     *
     * @param token
     * @return
     */
    public static Long getUidByToken(String token) {
        if (StringUtils.hasLength(token)) {
            JWT jwt = null;
            try {
                jwt = JWTUtil.parseToken(token);
                return Long.parseLong(jwt.getPayload("uid").toString());
            } catch (Exception e) {
            }
        }
        return 0L;
    }

}
