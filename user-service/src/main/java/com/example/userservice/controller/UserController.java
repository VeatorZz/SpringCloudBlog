package com.example.userservice.controller;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.AjaxResult;
import com.example.common.AppVariable;
import com.example.common.PasswordUtil;
import com.example.userservice.entity.Userinfo;
import com.example.userservice.entity.vo.UserinfoVO;
import com.example.userservice.service.UserService;
import com.example.userservice.utils.MyJWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册方法
     *
     * @param userinfo
     * @return
     */
    @RequestMapping("/add")
    public AjaxResult add(Userinfo userinfo) {
        // 1.非空判断
        if (userinfo == null || !StringUtils.hasLength(userinfo.getUsername()) ||
                !StringUtils.hasLength(userinfo.getPassword())) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 密码加盐处理
        userinfo.setPassword(PasswordUtil.encrypt(userinfo.getPassword()));
        // 2.调用 UserService 进行逻辑处理
        boolean result = userService.save(userinfo);
        // 3.将结果返回给调用方
        if (result) {
            return AjaxResult.success("注册成功", true);
        }
        return AjaxResult.fail(-2, "未知错误！");
    }

    /**
     * 登录方法
     */
    @RequestMapping("/login")
    public AjaxResult login(String username, String password) {
        // 1.非空判断
        if (!StringUtils.hasLength(username) ||
                !StringUtils.hasLength(password)) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 2.根据[用户名]查询数据库中的 Userinfo 对象
        QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username); // 等于 where username=#{username}
        Userinfo userinfo = userService.getOne(queryWrapper); // 执行查询操作
        if (userinfo == null || userinfo.getUid() <= 0) {
            // 根据用户名查询失败
            return AjaxResult.fail(-2, "用户名或密码输入错误！");
        }
        // 3.进行密码的比较
        if (PasswordUtil.decrypt(password, userinfo.getPassword())) {
            return AjaxResult.fail(-2, "用户名或密码输入错误！");
        }
        // 4.登录成功，使用 JWT 生成 Token
        Map<String, Object> payload = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("uid", userinfo.getUid());
                put("manager", userinfo.getManager());
                // JWT 过期时间为 15 天
                put("exp", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 15);
            }
        };
        String token = JWTUtil.createToken(payload, AppVariable.JWT_KEY.getBytes());
        HashMap<String, Object> result = new HashMap<>() {{
            put("token", token);
            put("uid", userinfo.getUid());
            put("username", userinfo.getUsername());
            put("manager", userinfo.getManager());
        }};
        return AjaxResult.success(result);
    }

    @RequestMapping("/getbyuid")
    public AjaxResult getByUid(Long uid,
                               @RequestHeader(value = AppVariable.TOKEN_KEY,
                                       required = false) String token) {
        // 1.参数效验
        if (uid == null || uid <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 2.根据 uid 查询用户信息
        Userinfo userinfo = userService.getById(uid);
        if (userinfo == null && userinfo.getUid() <= 0) {
            return AjaxResult.fail(-1, "非法参数！");
        }
        // 3.将登录用户 id 赋值给结果对象
        Long loginUid = MyJWTUtils.getUidByToken(token);
        // 4.将结果对象返回给调用者
        UserinfoVO userinfoVO = new UserinfoVO();
        userinfoVO.setUsername(userinfo.getUsername());
        userinfoVO.setEmail(userinfo.getEmail());
        userinfoVO.setLoginUid(loginUid);
        return AjaxResult.success(userinfoVO);
    }

    @RequestMapping("/getnamebyuid")
    public String getUserNameByUid(Long uid) {
        if (uid != null && uid > 0) {
            Userinfo userinfo = userService.getById(uid);
            if (userinfo != null && userinfo.getUid() > 0) {
                return userinfo.getUsername();
            }
        }
        return "";
    }

}
