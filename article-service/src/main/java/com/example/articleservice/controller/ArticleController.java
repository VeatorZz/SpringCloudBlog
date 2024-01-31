package com.example.articleservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.articleservice.entity.Article;
import com.example.articleservice.entity.ArticleCategory;
import com.example.articleservice.entity.ArticleVO;
import com.example.articleservice.entity.Category;
import com.example.articleservice.service.*;
import com.example.common.AjaxResult;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/art")
public class ArticleController {
    @Value("${page.pageSize:2}")
    private int pageSize;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private IArticleCategoryService articleCategoryService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ICommentService commentService;

    @Transactional
    @RequestMapping("/add")
    public AjaxResult add(ArticleVO articleVO,
                          @RequestHeader(value = "uid", required = false) Long uid) {
        // 1.非空效验
        if (articleVO == null || !StringUtils.hasLength(articleVO.getTitle()) ||
                !StringUtils.hasLength(articleVO.getContent()) ||
                articleVO.getCid() <= 0 || uid == null || uid <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        articleVO.setUid(uid);
        // 2.文章持久化
        boolean result = articleService.save(articleVO);
        if (result) {
            ArticleCategory articleCategory = new ArticleCategory();
            articleCategory.setAid(articleVO.getAid());
            articleCategory.setCid(articleVO.getCid());
            // 3.文章和分类管理表中添加数据
            boolean finalResult = articleCategoryService.save(articleCategory);
            // 4.将结果返回给前端
            return AjaxResult.success(finalResult);
        }
        return AjaxResult.fail(-2, "未知错误！");
    }

    @RequestMapping("/getbyid")
    public AjaxResult getById(Long aid) {
        // 1.非空效验
        if (aid == null || aid <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 2.查询文章信息
        Article article = articleService.getById(aid);
        if (article == null || article.getAid() <= 0) {
            return AjaxResult.fail(-2, "数据库没有此文章！");
        }
        ArticleVO articleVO = new ArticleVO();
        articleVO.setAid(article.getAid());
        articleVO.setTitle(article.getTitle());
        articleVO.setContent(article.getContent());
        articleVO.setUid(article.getUid());
        // 3.查询文章对应的 cid（分类id）
        QueryWrapper<ArticleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aid", aid);
        ArticleCategory articleCategory = articleCategoryService.getOne(queryWrapper);
        if (articleCategory != null) {
            // 3.1 组装 cid
            articleVO.setCid(articleCategory.getCid());
            // 3.2 组装 category
            Category category = categoryService.getById(articleCategory.getCid());
            if (category != null && category.getCid() > 0) {
                articleVO.setCategory(category.getName());
            }
        }
        // 4.组装作者名
        String username = userService.getUserNameByUid(article.getUid());
        articleVO.setUsername(username);
        // 5.数据组合，返回给调用方
        return AjaxResult.success(articleVO);
    }


    @Transactional
    @RequestMapping("/update")
    public AjaxResult update(ArticleVO articleVO,
                             @RequestHeader(value = "uid", required = false) Long uid) {
        // 1.非空效验
        if (articleVO == null || !StringUtils.hasLength(articleVO.getTitle()) ||
                !StringUtils.hasLength(articleVO.getContent()) ||
                articleVO.getCid() <= 0 || uid == null || uid <= 0 ||
                articleVO.getAid() == null || articleVO.getAid() <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        articleVO.setUid(uid);
        // 2.文章持久化
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("aid", articleVO.getAid());
        boolean result = articleService.update(articleVO, updateWrapper);
        if (result) {
            // 3.删除原来的关联数据
            QueryWrapper<ArticleCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("aid", articleVO.getAid());
            ArticleCategory articleCategory = articleCategoryService.getOne(queryWrapper);
            if (articleCategory != null && articleCategory.getAcid() > 0) {
                // 存储关联关系
                articleCategoryService.removeById(articleCategory.getAcid());
            }
            articleCategory = new ArticleCategory();
            articleCategory.setAid(articleVO.getAid());
            articleCategory.setCid(articleVO.getCid());
            // 4.文章和分类管理表中添加数据
            boolean finalResult = articleCategoryService.save(articleCategory);
            // 5.将结果返回给前端
            return AjaxResult.success(finalResult);
        }
        return AjaxResult.fail(-2, "未知错误！");
    }

    @RequestMapping("/getuserart")
    public AjaxResult getUserArticleList(Long uid) {
        // 1.参数效验
        if (uid == null || uid <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 2.查询用户的所有文章
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.orderByDesc(true, "aid");
        List<Article> list = articleService.list(queryWrapper);
        return AjaxResult.success(list);
    }

    /**
     * 删除文章和当前文章下的所有评论信息
     *
     * @param aid
     * @param uid
     * @return
     */
    @GlobalTransactional
    @RequestMapping("/delbyaid")
    public AjaxResult delByAid(Long aid,
                               @RequestHeader(value = "uid", required = false)
                               Long uid) {
        // 1.参数效验
        if (aid == null || aid <= 0 || uid == null || uid <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 2.构建删除条件
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aid", aid);
        queryWrapper.eq("uid", uid); // 文章归属权的判断
        boolean result = articleService.remove(queryWrapper);
        if (result) {
            // 文章删除成功之后，删除此文章下的所有评论信息
            result = commentService.delByAid(aid);
        }
        return AjaxResult.success(result);
    }

    @RequestMapping("/getlistbypage")
    public AjaxResult getListByPage(Integer pIndex) {
        if (pIndex == null || pIndex <= 0) {
            // 参数的校正
            pIndex = 1;
        }
        Page page = new Page(pIndex, pageSize);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(true, "aid");
        Page<Article> result = articleService.page(page, queryWrapper);
        return AjaxResult.success(result);
    }

    @RequestMapping("/getuidbyaid")
    public Long getUidByAid(Long aid) {
        Article article = articleService.getById(aid);
        if (article == null || article.getAid() <= 0) {
            return 0L;
        }
        return article.getUid();
    }

}
