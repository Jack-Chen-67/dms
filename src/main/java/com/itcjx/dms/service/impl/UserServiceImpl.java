package com.itcjx.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itcjx.dms.entity.User;
import com.itcjx.dms.mapper.UserMapper;
import com.itcjx.dms.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.dms.util.JwtTokenUtil;
import com.itcjx.dms.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 登录
    @Override
    public Result<String> login(String username, String password) {
        //判空
        if(username == null || password == null || username.isEmpty() || password.isEmpty()){
            return Result.error(400, "用户名或密码不能为空");
        }
        //从数据库获取用户
        // 修改: 使用 QueryWrapper 构造查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if(user == null){
            return Result.error(400, "用户不存在");
        }
        //判断密码是否一致
        // 修改: 使用 BCryptPasswordEncoder 验证密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(!passwordEncoder.matches(password, user.getPassword())){
            return Result.error(400, "密码错误");
        }
        //生成token
        String token = jwtTokenUtil.generateToken(username, user.getId());
        return Result.success(token);
    }

    // 获取当前用户信息
    @Override
    public Result<User> current(String token) {
        if(token == null || token.isEmpty()){
            return Result.error(400, "token不能为空");
        }
        //从token中获取用户信息
        Long userId = jwtTokenUtil.getUserIdFromToken(token.trim());
        if(userId == null){
            return Result.error(400, "token无效");
        }
        User user = userMapper.selectById(userId);
        if(user == null){
            return Result.error(404, "用户不存在");
        }
        return Result.success(user);
    }

    // 注册
    @Override
    public Result<User> register(User user) {
        if(user.getUsername() == null || user.getPassword() == null || user.getUsername().isEmpty() || user.getPassword().isEmpty()){
            return Result.error(400, "用户名或密码不能为空");
        }
        //判断用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        if(existUser != null){
            return Result.error(409, "用户名已存在");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();//加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));//加密密码
        user.setStatus((byte) 1);//默认正常
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return Result.success(user);
    }

}