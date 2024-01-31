package com.example.articleservice.service.impl;

import com.example.articleservice.entity.Article;
import com.example.articleservice.mapper.ArticleMapper;
import com.example.articleservice.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lei
 * @since 2023-12-06
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

}
