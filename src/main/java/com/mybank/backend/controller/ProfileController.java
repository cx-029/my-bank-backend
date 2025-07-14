package com.mybank.backend.controller;

import com.mybank.backend.entity.Customer;
import com.mybank.backend.service.CustomerService;
import com.mybank.backend.service.FaceRecognitionService;
import com.mybank.backend.util.IdNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

/**
 * 身份证加密存储，不用DTO，直接用Customer实体
 * 增加日志，排查人脸识别接口解密身份证号末位错误问题
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @GetMapping
    public Customer getProfile(Principal principal) {
        String realName = principal.getName();
        Optional<Customer> customerOpt = customerService.getCustomerByName(realName);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            // 解密身份证号，前端展示时用全*号
            String decryptedId = IdNumberUtil.decryptIdNumber(customer.getIdNumber());
            customer.setIdNumber(maskIdNumber(decryptedId));
            return customer;
        }
        return null;
    }

    @PutMapping
    public Customer updateProfile(Principal principal, @RequestBody Customer customerReq) {
        String realName = principal.getName();
        Optional<Customer> dbCustomer = customerService.getCustomerByName(realName);

        if (dbCustomer.isPresent()) {
            Customer origin = dbCustomer.get();
            origin.setGender(customerReq.getGender());
            origin.setBirthday(customerReq.getBirthday());
            origin.setAddress(customerReq.getAddress());
            origin.setPhone(customerReq.getPhone());
            origin.setEmail(customerReq.getEmail());
            origin.setPhotoUrl(customerReq.getPhotoUrl());
            // 仅当前端提交了新身份证号才加密更新
            if (customerReq.getIdNumber() != null && !customerReq.getIdNumber().isEmpty()
                    && !isAllStars(customerReq.getIdNumber())) {
                String encrypted = IdNumberUtil.encryptIdNumber(customerReq.getIdNumber());
                origin.setIdNumber(encrypted);
            }
            customerService.saveCustomer(origin);

            // 返回时只展示全*号
            String decryptedId = IdNumberUtil.decryptIdNumber(origin.getIdNumber());
            origin.setIdNumber(maskIdNumber(decryptedId));
            return origin;
        }
        return null;
    }

    /**
     * 人脸识别后返回真实身份证号接口
     * 前端传base64人脸图片，识别通过且user_id==当前用户则返回真实身份证号
     */
    @PostMapping("/id-number")
    public ResponseEntity<?> getIdNumberAfterFace(@RequestBody FaceDTO dto, Principal principal) {
        String recognizedUser = faceRecognitionService.recognize(dto.base64Image);
        String loginName = principal.getName();
        if (recognizedUser != null && recognizedUser.equals(loginName)) {
            Optional<Customer> customerOpt = customerService.getCustomerByName(loginName);
            if (customerOpt.isPresent()) {
                String encryptedId = customerOpt.get().getIdNumber();
                String idNumber = IdNumberUtil.decryptIdNumber(encryptedId);
                // 后端
                return ResponseEntity.ok(Map.of("idNumber", idNumber));
            }
            return ResponseEntity.status(404).body("用户不存在");
        }
        return ResponseEntity.status(403).body("人脸识别失败或不匹配");
    }

    // DTO用于接收前端base64人脸图片
    public static class FaceDTO {
        public String base64Image;
    }

    // 全*号遮掩
    private static String maskIdNumber(String id) {
        if (id == null || id.isEmpty()) return "******************";
        return "*".repeat(id.length());
    }

    // 判断前端是否提交的是全星号（前端未修改身份证号时会提交全*号）
    private static boolean isAllStars(String id) {
        return id != null && !id.isEmpty() && id.chars().allMatch(ch -> ch == '*');
    }
}