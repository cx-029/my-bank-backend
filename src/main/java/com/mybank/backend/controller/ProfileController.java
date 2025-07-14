package com.mybank.backend.controller;

import com.mybank.backend.entity.Customer;
import com.mybank.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private CustomerService customerService;

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
                origin.setIdNumber(CustomerService.encryptIdNumber(dto.idNumber));
            }
            customerService.saveCustomer(origin);
            return CustomerDTO.from(origin);
        }
        return null;
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
            dto.idNumber = maskIdNumber(CustomerService.decryptIdNumber(c.getIdNumber()));
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
            return id.substring(0,3) + "***********" + id.substring(id.length()-3);
        }
    }
}