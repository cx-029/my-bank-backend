package com.mybank.backend.controller;

import com.mybank.backend.entity.Account;
import com.mybank.backend.entity.AccountLossReport;
import com.mybank.backend.entity.Customer;
import com.mybank.backend.entity.User;
import com.mybank.backend.service.AccountLossReportService;
import com.mybank.backend.service.AccountService;
import com.mybank.backend.service.CustomerService;
import com.mybank.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/account/loss")
public class AccountLossReportController {

    @Autowired
    private AccountLossReportService lossReportService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    // 挂失申请（不传accountId，只传type和reason）
    @PostMapping("/apply")
    public String apply(@RequestBody AccountLossReport req, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) return "用户不存在";

        // 通过 userId 查 customer
        Optional<Customer> customerOpt = customerService.getCustomerByUserId(user.getId());
        if (customerOpt.isEmpty()) return "客户不存在";
        Customer customer = customerOpt.get();

        // 通过 customerId 查 account（如有多卡，取第一个）
        Account account = accountService.getAccountByCustomerId(customer.getId());
        if (account == null) return "未找到账户信息";
        Long accountId = account.getId();

        boolean ok = lossReportService.applyLoss(accountId, req.getType(), req.getReason());
        return ok ? "挂失申请成功" : "当前账户已挂失，无法重复申请";
    }

    @PostMapping("/release")
    public String release(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) return "用户不存在";

        Optional<Customer> customerOpt = customerService.getCustomerByUserId(user.getId());
        if (customerOpt.isEmpty()) return "客户不存在";
        Customer customer = customerOpt.get();

        Account account = accountService.getAccountByCustomerId(customer.getId());
        if (account == null) return "未找到账户信息";
        Long accountId = account.getId();

        boolean ok = lossReportService.releaseLoss(accountId);
        return ok ? "挂失解除成功" : "未找到可解除的挂失记录";
    }

    @GetMapping("/list")
    public List<AccountLossReport> list(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) return List.of();

        Optional<Customer> customerOpt = customerService.getCustomerByUserId(user.getId());
        if (customerOpt.isEmpty()) return List.of();
        Customer customer = customerOpt.get();

        Account account = accountService.getAccountByCustomerId(customer.getId());
        if (account == null) return List.of();
        return lossReportService.getLossReports(account.getId());
    }
}