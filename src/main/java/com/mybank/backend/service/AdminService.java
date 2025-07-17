package com.mybank.backend.service;

import com.mybank.backend.entity.Admin;

public interface AdminService {
    Admin findByUsername(String username);
    String register(Admin admin);
    Admin updateProfile(String username, Admin adminReq);
}