package com.example.articleservice.service.impl;

import com.example.articleservice.entity.Category;
import com.example.articleservice.mapper.CategoryMapper;
import com.example.articleservice.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
