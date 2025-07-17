package com.mybank.backend.controller;

import com.mybank.backend.entity.Admin;
import com.mybank.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/admin/profile")
public class AdminProfileController {
    @Autowired
    private AdminService adminService;

    @GetMapping
    public Admin getProfile(Principal principal) {
        String username = principal.getName();
        return adminService.findByUsername(username);
    }

    @PutMapping
    public Admin updateProfile(Principal principal, @RequestBody Admin adminReq) {
        String username = principal.getName();
        return adminService.updateProfile(username, adminReq);
    }
}