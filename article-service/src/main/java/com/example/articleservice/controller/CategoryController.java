package com.example.articleservice.controller;


import com.example.articleservice.entity.Category;
import com.example.articleservice.service.ICategoryService;
import com.example.common.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @RequestMapping("/add")
    public AjaxResult add(Category category) {
        // 1.非空效验
        if (category == null || !StringUtils.hasLength(category.getName())) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 2.调用 Service 进行持久化
        boolean result = categoryService.save(category);
        // 3.将结果返回给前端
        return AjaxResult.success("", result);
    }

    @RequestMapping("/list")
    public AjaxResult list() {
        List<Category> list = categoryService.list();
        // 倒序排列
        Collections.reverse(list);
        return AjaxResult.success(list);
    }

    @RequestMapping("/del")
    public AjaxResult del(Integer cid) {
        // 1.参数效验
        if (cid == null || cid <= 0) {
            return AjaxResult.fail(-1,"非法参数！");
        }
        // 2.执行持久化操作
        boolean result = categoryService.removeById(cid);
        return AjaxResult.success(result);
    }

}
