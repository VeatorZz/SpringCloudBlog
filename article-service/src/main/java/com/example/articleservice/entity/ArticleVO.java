package com.example.articleservice.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ArticleVO extends Article implements Serializable  {
    @Serial
    private static final long serialVersionUID = -851452554138346002L;
    private Long cid;
    private String username;
    private String category;
}
