package com.itcjx.dms.controller;

import com.itcjx.dms.entity.User;
import com.itcjx.dms.service.impl.UserServiceImpl;
import com.itcjx.dms.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;
    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        return userServiceImpl.login(user.getUsername(), user.getPassword());
    }

    @GetMapping("current")
    public Result<User> current(@RequestHeader("Authorization") String token) {
        //去掉Bearer
        token = token.substring(7);
        return userServiceImpl.current(token);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        return userServiceImpl.register(user);
    }
}
