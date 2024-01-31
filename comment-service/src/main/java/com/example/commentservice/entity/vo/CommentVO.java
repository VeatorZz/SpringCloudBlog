package com.example.commentservice.entity.vo;

import com.example.commentservice.entity.Comment;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CommentVO extends Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 4916826272412840058L;
    private String username;
}
