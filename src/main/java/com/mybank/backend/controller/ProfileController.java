package com.mybank.backend.controller;

import com.mybank.backend.entity.Customer;
import com.mybank.backend.service.CustomerService;
import com.mybank.backend.service.FaceRecognitionService;
import com.mybank.backend.util.IdNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @GetMapping
    public CustomerDTO getProfile(Principal principal) {
        String realName = principal.getName();
        Optional<Customer> customerOpt = customerService.getCustomerByName(realName);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return CustomerDTO.from(customer);
        }
        return null;
    }

    @PutMapping
    public CustomerDTO updateProfile(Principal principal, @RequestBody CustomerDTO dto) {
        String realName = principal.getName();
        Optional<Customer> dbCustomer = customerService.getCustomerByName(realName);

        if (dbCustomer.isPresent()) {
            Customer origin = dbCustomer.get();
            origin.setGender(dto.gender);
            origin.setBirthday(dto.birthday);
            origin.setAddress(dto.address);
            origin.setPhone(dto.phone);
            origin.setEmail(dto.email);
            origin.setPhotoUrl(dto.photoUrl);
            if (dto.idNumber != null && !dto.idNumber.isEmpty()) {
                origin.setIdNumber(IdNumberUtil.encryptIdNumber(dto.idNumber));
            }
            customerService.saveCustomer(origin);
            return CustomerDTO.from(origin);
        }
        return null;
    }

    /**
     * 人脸识别后返回真实身份证号接口
     * 前端传base64人脸图片，识别通过且user_id==当前用户则返回真实身份证号
     */
    @PostMapping("/id-number")
    public ResponseEntity<?> getIdNumberAfterFace(@RequestBody FaceDTO dto, Principal principal) {
        // 人脸识别服务，返回user_id
        String recognizedUser = faceRecognitionService.recognize(dto.base64Image);
        String loginName = principal.getName();
        if (recognizedUser != null && recognizedUser.equals(loginName)) {
            Optional<Customer> customerOpt = customerService.getCustomerByName(loginName);
            if (customerOpt.isPresent()) {
                String idNumber = IdNumberUtil.decryptIdNumber(customerOpt.get().getIdNumber());
                return ResponseEntity.ok(idNumber); // 返回真实身份证号
            }
            return ResponseEntity.status(404).body("用户不存在");
        }
        return ResponseEntity.status(403).body("人脸识别失败或不匹配");
    }

    // DTO用于接收前端base64人脸图片
    public static class FaceDTO {
        public String base64Image;
    }

    // DTO用于安全数据交换
    public static class CustomerDTO {
        public Long id;
        public String name;
        public String gender;
        public String idNumber; // 脱敏返回，提交时明文
        public java.util.Date birthday;
        public String address;
        public String phone;
        public String email;
        public String photoUrl;

        public static CustomerDTO from(Customer c) {
            CustomerDTO dto = new CustomerDTO();
            dto.id = c.getId();
            dto.name = c.getName();
            dto.gender = c.getGender();
            dto.idNumber = maskIdNumber(IdNumberUtil.decryptIdNumber(c.getIdNumber()));
            dto.birthday = c.getBirthday();
            dto.address = c.getAddress();
            dto.phone = c.getPhone();
            dto.email = c.getEmail();
            dto.photoUrl = c.getPhotoUrl();
            return dto;
        }

        // 脱敏身份证号
        private static String maskIdNumber(String id) {
            if (id == null || id.length() < 8) return "********";
            return id.substring(0, 3) + "***********" + id.substring(id.length() - 3);
        }
    }
}