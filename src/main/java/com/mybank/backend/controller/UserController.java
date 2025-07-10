package com.mybank.backend.controller;

import com.mybank.backend.entity.User;
import com.mybank.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        String result = userService.register(user);
        Map<String, Object> resp = new HashMap<>();
        if ("success".equals(result)) {
            resp.put("msg", "注册成功");
        } else {
            resp.put("error", result);
        }
        return resp;
    }
}