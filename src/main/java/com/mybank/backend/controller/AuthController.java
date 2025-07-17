package com.mybank.backend.controller;

import com.mybank.backend.entity.User;
import com.mybank.backend.repository.UserRepository;
import com.mybank.backend.service.FaceRecognitionService;
import com.mybank.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @PostMapping("/face-login")
    public ResponseEntity<?> faceLogin(@RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        String username = faceRecognitionService.recognize(base64Image); // 需确保返回用户名
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                String token = jwtUtil.generateToken(username, userOpt.get().getRole());
                return ResponseEntity.ok(Map.of("token", token, "role", userOpt.get().getRole()));
            } else {
                return ResponseEntity.ok(Map.of("error", "用户不存在"));
            }
        } else {
            return ResponseEntity.ok(Map.of("error", "人脸识别失败"));
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        Optional<User> optionalUser = userRepository.findByUsername(username);
        Map<String, Object> res = new HashMap<>();

        if (optionalUser.isPresent()) {
            String dbPassword = optionalUser.get().getPassword();
            boolean matchResult = passwordEncoder.matches(password, dbPassword);
            if (matchResult) {
                String role = optionalUser.get().getRole();
                String token = jwtUtil.generateToken(username, role); // 生成带role的token
                res.put("token", token);
                res.put("username", username);
                res.put("role", role); // 返回role
            } else {
                res.put("error", "用户名或密码错误");
            }
        } else {
            res.put("error", "用户名或密码错误");
        }
        return res;
    }
}