package com.mybank.backend.controller;

import com.mybank.backend.entity.AccountLossReport;
import com.mybank.backend.entity.Account;
import com.mybank.backend.service.AccountLossReportService;
import com.mybank.backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/loss")
public class AdminAccountLossReportController {

    @Autowired
    private AccountLossReportService lossReportService;

    @Autowired
    private AccountRepository accountRepository;

    // 新增接口
    @GetMapping("/count")
    public long getPendingLossCount() {
        return lossReportService.countLatestByStatus("待处理"); // "待处理"为你系统的待审批状态
    }

    // 分页查询挂失记录，保证最新的挂失记录在最前面（createdAt倒序）
    @GetMapping("/list")
    public Page<AccountLossReport> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String status
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (accountId != null && status != null && !status.isEmpty()) {
            return lossReportService.findByAccountIdAndStatus(accountId, status, pageRequest);
        } else if (accountId != null) {
            return lossReportService.findByAccountId(accountId, pageRequest);
        } else if (status != null && !status.isEmpty()) {
            return lossReportService.findByStatus(status, pageRequest);
        } else {
            return lossReportService.findAll(pageRequest);
        }
    }

    // 挂失审批（挂失、冻结、销户、正常），加限制
    @PostMapping("/approve")
    public String approve(@RequestParam Long reportId, @RequestParam String action) {
        AccountLossReport report = lossReportService.findById(reportId);
        if (report == null) return "挂失记录不存在";
        Account account = accountRepository.findById(report.getAccountId()).orElse(null);
        if (account == null) return "账户不存在";

        // 销户后禁止操作
        if ("销户".equals(account.getStatus())) {
            return "该账户已销户，无法进行操作";
        }
        // 只能操作最新一次挂失
        AccountLossReport latest = lossReportService.findLatestByAccountId(account.getId());
        if (latest == null || !latest.getId().equals(report.getId())) {
            return "只能操作该账户最后一次挂失记录";
        }

        switch (action) {
            case "挂失":
                account.setStatus("挂失");
                report.setStatus("挂失");
                break;
            case "冻结":
                account.setStatus("冻结");
                report.setStatus("冻结");
                break;
            case "销户":
                account.setStatus("销户");
                report.setStatus("销户");
                break;
            case "正常":
                account.setStatus("正常");
                report.setStatus("正常");
                break;
            default:
                return "非法操作";
        }
        accountRepository.save(account);
        lossReportService.save(report);
        return "操作成功";
    }

    // 删除挂失记录（建议前端只允许删除最后一次挂失记录）
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        lossReportService.deleteById(id);
    }

    // 修改挂失记录（只能改最后一次挂失记录，且accountId等不可改）
    @PutMapping("/{id}")
    public AccountLossReport update(@PathVariable Long id, @RequestBody AccountLossReport report) {
        AccountLossReport old = lossReportService.findById(id);
        if (old == null) throw new RuntimeException("记录不存在");
        // 只能允许最后一次挂失记录修改
        AccountLossReport latest = lossReportService.findLatestByAccountId(old.getAccountId());
        if (latest == null || !latest.getId().equals(id)) {
            throw new RuntimeException("只能修改该账户最后一次挂失记录");
        }
        old.setStatus(report.getStatus());
        old.setReason(report.getReason());
        // 修改类型和处理时间
        if (!"挂失".equals(report.getStatus())) { // 处理完成
            old.setType("已处理");
            old.setResolvedAt(java.time.LocalDateTime.now());
        }
        return lossReportService.save(old);
    }
}