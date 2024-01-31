package com.example.commentservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("art-service-blog")
public interface IArticleService {
    @RequestMapping("/art/getuidbyaid")
    Long getUidByAid(@RequestParam("aid")Long aid);
}
