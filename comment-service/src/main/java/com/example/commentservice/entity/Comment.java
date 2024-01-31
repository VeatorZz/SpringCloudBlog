package com.example.commentservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "cid", type = IdType.AUTO)
    private Long cid;

    /**
     * 评论内容
     */
    @TableField("`content`")
    private String content;

    /**
     * 发表时间
     */
    private LocalDateTime createtime;

    /**
     * 评论人
     */
    private Long uid;

    /**
     * 回复评论id，如果没有则为0
     */
    private Long pid;

    /**
     * 发布评论所用时间
     */
    private Integer usetime;

    private Long aid;

}
