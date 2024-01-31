package com.example.commentservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.commentservice.entity.Comment;
import com.example.commentservice.entity.vo.CommentVO;
import com.example.commentservice.service.IArticleService;
import com.example.commentservice.service.ICommentService;
import com.example.commentservice.service.IUserService;
import com.example.common.AjaxResult;
import org.apache.coyote.ajp.AjpMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IArticleService articleService;

    /**
     * 根据文章id，获取评论列表
     *
     * @return
     */
    @RequestMapping("/listbyaid")
    public AjaxResult listByAid(Long aid) {
        // 1.参数效验
        if (aid == null || aid <= 0) {
            return AjaxResult.fail(-1, "无效的文章ID");
        }
        // 2.查询数据库
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aid", aid);
        queryWrapper.orderByDesc(true, "cid");
        List<Comment> list = commentService.list(queryWrapper);
        List<CommentVO> result = new ArrayList<>();
        // 3.数据处理 -> uid 转换成 username
        list.stream().parallel().forEach(comment -> {
            CommentVO commentVO = new CommentVO();
            commentVO.setCid(comment.getCid());
            commentVO.setContent(comment.getContent());
            commentVO.setUid(comment.getUid());
            commentVO.setPid(comment.getPid());
            commentVO.setUsername(userService.getUserNameByUid(comment.getUid()));
            result.add(commentVO);
        });
        // 4.将结果返回给前端
        return AjaxResult.success(result);
    }

    @RequestMapping("/delbyaid")
    public boolean delByAid(Long aid) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aid", aid);
        return commentService.remove(queryWrapper);
    }

    @RequestMapping("/add")
    public AjaxResult add(Comment comment, @RequestHeader("uid") Long uid) {
        // 1.参数效验
        if (comment == null || !StringUtils.hasLength(comment.getContent()) ||
                comment.getAid() == null || comment.getAid() <= 0 ||
                uid == null || uid <= 0) {
            return AjaxResult.fail(-1, "参数错误！");
        }
        // 2.组装
        comment.setUid(uid);
        // 3.数据持久化
        boolean result = commentService.save(comment);
        // 4.返回结果
        return AjaxResult.success(result);
    }

    @RequestMapping("/del")
    public AjaxResult del(Long cid, @RequestHeader("uid") Long uid) {
        // 1.参数效验
        if (cid == null || uid == null || cid <= 0 || uid <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 2.判断评论的归属人
        Comment comment = commentService.getById(cid);
        if (comment == null || comment.getCid() <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        boolean isDel = false;
        // 2.1 评论 uid = 参数中的 uid -> 当前评论是“我”发的
        if (comment.getUid().equals(uid)) {
            isDel = true;
        } else {
            // 2.2 评论归属的文章中的作者 id = 参数中的 uid -> 文章属于“我”，这个文章下的评论可以删除
            Long artUid = articleService.getUidByAid(comment.getAid());
            if (artUid.equals(uid)) {
                isDel = true;
            }
        }
        if (!isDel) {
            // 无删除权限
            return AjaxResult.fail(-2,"无权删除！");
        }
        // 3.数据持久化
        boolean result = commentService.removeById(cid);
        // 4.讲结果返回给调用方
        return AjaxResult.success(result);
    }

}
