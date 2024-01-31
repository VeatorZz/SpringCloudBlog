package com.example.commentservice.service.impl;

import com.example.commentservice.entity.Comment;
import com.example.commentservice.mapper.CommentMapper;
import com.example.commentservice.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
