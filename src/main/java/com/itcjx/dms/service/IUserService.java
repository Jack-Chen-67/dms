package com.itcjx.dms.service;

import com.itcjx.dms.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.dms.util.Result;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
public interface IUserService extends IService<User> {

    // 登录
    Result<String> login(String username, String password);
    // 获取用户信息
    Result<User> current(String token);
    // 注册
    Result<User> register(User user);

}
