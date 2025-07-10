package com.mybank.backend.service.impl;

<<<<<<< HEAD
import com.mybank.backend.entity.Customer;
import com.mybank.backend.entity.User;
import com.mybank.backend.repository.CustomerRepository;
import com.mybank.backend.repository.UserRepository;
import com.mybank.backend.service.CustomerService;
=======
import com.mybank.backend.entity.User;
import com.mybank.backend.repository.UserRepository;
>>>>>>> bbb1d15 (Initial commit)
import com.mybank.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
<<<<<<< HEAD
    @Autowired
    private CustomerService customerService;
=======
>>>>>>> bbb1d15 (Initial commit)

    @Override
    public String register(User user) {
        // 必填校验
        if (!StringUtils.hasText(user.getUsername()) ||
                !StringUtils.hasText(user.getPassword()) ||
                !StringUtils.hasText(user.getRealName()) ||
                !StringUtils.hasText(user.getEmail()) ||
                !StringUtils.hasText(user.getPhone())) {
            return "请填写所有必填项";
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "用户名已存在";
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "邮箱已注册";
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            return "手机号已注册";
        }
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 默认角色
<<<<<<< HEAD

        if (user.getRole() == null) user.setRole("customer");
        // 保存用户，并接收返回值
        User savedUser = userRepository.save(user);

        if ("customer".equals(savedUser.getRole())) {
            Customer customer = new Customer();
            customer.setUserId(savedUser.getId());        // 关键：外键关联
            customer.setName(savedUser.getUsername());    // 可用realName
            customer.setPhone(savedUser.getPhone());
            customer.setEmail(savedUser.getEmail());
            // 其它字段为空，后续完善
            customerService.saveCustomer(customer);
            userRepository.save(user);
        }
=======
        if (user.getRole() == null) user.setRole("customer");
        userRepository.save(user);
>>>>>>> bbb1d15 (Initial commit)
        return "success";
    }
}