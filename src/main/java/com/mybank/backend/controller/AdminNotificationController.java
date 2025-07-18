package com.mybank.backend.controller;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    // 分页+条件查询通知，参数都可选
    @GetMapping
    public Page<Notification> listNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return notificationService.getNotificationsPaged(pageable, title, author);
    }

    // 创建通知
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification, Principal principal) {
        notification.setCreatedAt(new Date());
        notification.setAuthor(principal.getName());
        return notificationService.saveNotification(notification);
    }

    // 更新通知
    @PutMapping("/{id}")
    public Notification updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        notification.setId(id);
        return notificationService.updateNotification(notification);
    }

    // 删除通知
    @DeleteMapping("/{id}")
    public boolean deleteNotification(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }

    // 查询单条通知
    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable Long id) {
        return notificationService.getNotificationDetail(id);
    }
}