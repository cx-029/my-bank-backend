package com.mybank.backend.service.impl;

import com.mybank.backend.entity.Admin;
import com.mybank.backend.repository.AdminRepository;
import com.mybank.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }

    @Override
    public String register(Admin admin) {
        // 必填校验
        if (!StringUtils.hasText(admin.getUsername()) ||
                !StringUtils.hasText(admin.getPassword()) ||
                !StringUtils.hasText(admin.getRealName()) ||
                !StringUtils.hasText(admin.getEmail()) ||
                !StringUtils.hasText(admin.getPhone())) {
            return "请填写所有必填项";
        }
        if (adminRepository.findByUsername(admin.getUsername()).isPresent()) {
            return "用户名已存在";
        }
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            return "邮箱已注册";
        }
        if (adminRepository.findByPhone(admin.getPhone()).isPresent()) {
            return "手机号已注册";
        }
        // 密码加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        if (admin.getRole() == null) admin.setRole("admin");
        adminRepository.save(admin);
        return "success";
    }

    @Override
    public Admin updateProfile(String username, Admin adminReq) {
        Admin origin = findByUsername(username);
        if (origin == null) return null;
        origin.setRealName(adminReq.getRealName());
        origin.setEmail(adminReq.getEmail());
        origin.setPhone(adminReq.getPhone());
        origin.setPhotoUrl(adminReq.getPhotoUrl());
        // 其他字段可根据需求补充
        adminRepository.save(origin);
        return origin;
    }
}