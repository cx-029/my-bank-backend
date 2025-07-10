package com.mybank.backend.service;

import com.mybank.backend.entity.User;

public interface UserService {
    /**
     * @return 注册成功返回"success"，否则返回错误信息
     */
    String register(User user);
}