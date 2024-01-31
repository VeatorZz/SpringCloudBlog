package com.example.userservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Userinfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 3069969422083334769L;

    @TableId(type = IdType.AUTO)
    private int uid;
    private String username;
    private String password;
    private String email;
    private String photo;
    private String createtime;
    private String updatetime;
    private int manager;
}
