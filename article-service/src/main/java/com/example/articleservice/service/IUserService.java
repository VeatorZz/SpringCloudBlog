package com.example.articleservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service-blog")
public interface IUserService {

    @RequestMapping("/user/getnamebyuid")
    public String getUserNameByUid(@RequestParam("uid") Long uid);

}
