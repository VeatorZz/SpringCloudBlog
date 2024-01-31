package com.example.articleservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("comment-service-blog")
public interface ICommentService {
    @RequestMapping("/comment/delbyaid")
    boolean delByAid(@RequestParam("aid")Long aid);
}
