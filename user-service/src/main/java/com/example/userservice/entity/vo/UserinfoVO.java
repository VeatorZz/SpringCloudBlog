package com.example.userservice.entity.vo;

import com.example.userservice.entity.Userinfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserinfoVO extends Userinfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6162584852365329609L;
    private Long loginUid;
}
